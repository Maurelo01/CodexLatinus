package codexlatinus.compiler.semantic;

import codexlatinus.CodexLatinusBaseVisitor;
import codexlatinus.CodexLatinusParser;
import codexlatinus.compiler.symbol.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.antlr.v4.runtime.Token;

public abstract class MiVisitorBase extends CodexLatinusBaseVisitor<Object>
{
    protected final TablaSimbolos tabla = new TablaSimbolos();
    protected int erroresSemanticos = 0;
    protected final Map<String, InfoFuncion> funciones = new HashMap<>();
    protected final Map<String, InfoEstructura> estructuras = new HashMap<>();
    protected String tipoRetornoEsperadoActual = null;
    protected int dentroDeCiclo = 0;
    protected final List<String> listaErrores = new ArrayList<>();
    
    protected static class InfoFuncion
    {
        String nombre;
        String tipoRetorno;
        List<Parametro> parametros;
        InfoFuncion(String nombre, String tipoRetorno)
        {
            this.nombre = nombre;
            this.tipoRetorno = tipoRetorno;
            this.parametros = new ArrayList<>();
        }
    }

    protected static class Parametro
    {
        String nombre;
        String tipo;
    }

    protected static class InfoEstructura
    {
        String nombre;
        Map<String, String> atributos;
        InfoEstructura(String nombre)
        {
            this.nombre = nombre;
            this.atributos = new LinkedHashMap<>();
        }
    }

    protected void error(String mensaje, Token token)
    {
        erroresSemanticos++;
        String msgError = String.format("Error Semántico en línea %d, columna %d: %s", token.getLine(), token.getCharPositionInLine() + 1, mensaje);
        listaErrores.add(msgError);
    }
    
    public List<String> getListaErrores()
    {
        return listaErrores;
    }

    protected Simbolo.Tipo mapearTipo(String tipoTexto)
    {
        return switch (tipoTexto)
        {
            case "numerus" -> Simbolo.Tipo.NUMERUS;
            case "textum" -> Simbolo.Tipo.TEXTUM;
            case "decimalis" -> Simbolo.Tipo.DECIMALIS;
            case "littera" -> Simbolo.Tipo.LITTERA;
            case "bool" -> Simbolo.Tipo.BOOL;
            default -> Simbolo.Tipo.ESTRUCTURA;
        };
    }

    protected String textoCompletoTipo(CodexLatinusParser.TipoContext ctx)
    {
        if (ctx == null) return "desconocido";
        if (ctx.tipoSimple() != null)
        {
            return ctx.tipoSimple().getText();
        }
        else
        {
            return "series " + ctx.tipoSimple().getText();
        }
    }

    protected boolean esTipoPrimitivo(String tipo)
    {
        return switch (tipo)
        {
            case "bool", "littera", "numerus", "decimalis", "textum" -> true;
            default -> false;
        };
    }

    protected boolean esArray(String tipo)
    {
        return tipo != null && tipo.startsWith("series ");
    }

    protected String tipoBaseDeArray(String tipo)
    {
        if (esArray(tipo))
        {
            return tipo.substring("series ".length());
        }
        return tipo;
    }

    protected int nivelTipo(String tipo)
    {
        if (tipo == null) return 0;
        return switch (tipo)
        {
            case "bool" -> 1;
            case "littera" -> 2;
            case "numerus" -> 3;
            case "decimalis" -> 4;
            case "textum" -> 5;
            default -> 0;
        };
    }

    protected String obtenerTipoCompleto(Simbolo simbolo)
    {
        if (simbolo.getTipo() == Simbolo.Tipo.ARREGLO)
        {
            return "series " + simbolo.getValor();
        }
        else if (simbolo.getTipo() == Simbolo.Tipo.ESTRUCTURA)
        {
            return (String) simbolo.getValor();
        }
        else
        {
            return simbolo.getTipo().name().toLowerCase();
        }
    }

    protected boolean esCompatible(String tipoEsperado, String tipoObtenido)
    {
        if (tipoEsperado.equals(tipoObtenido)) return true;
        if ("decimalis".equals(tipoEsperado) && "numerus".equals(tipoObtenido)) return true;
        int nivelEsperado = nivelTipo(tipoEsperado);
        int nivelObtenido = nivelTipo(tipoObtenido);
        return nivelEsperado >= nivelObtenido && nivelObtenido > 0 && nivelEsperado > 0;
    }

    protected void validarEstructuraAnonima(CodexLatinusParser.EstructuraAnonimaContext strucAnonima, InfoEstructura info, Token token)
    {
        Map<String, String> proporcionados = new LinkedHashMap<>();
        if (strucAnonima.atributos_valores() != null)
        {
            for (CodexLatinusParser.Atributo_valorContext av : strucAnonima.atributos_valores().atributo_valor())
            {
                String nombre = av.ID().getText();
                String tipo = (String) visit(av.valorAtributo());
                proporcionados.put(nombre, tipo);
            }
        }

        for (Map.Entry<String, String> requerido : info.atributos.entrySet())
        {
            if (!proporcionados.containsKey(requerido.getKey()))
            {
                error("Falta el atributo obligatorio " + requerido.getKey() + " en la estructura anónima", token);
            }
            else if (proporcionados.get(requerido.getKey()) != null && !esCompatible(requerido.getValue(), proporcionados.get(requerido.getKey())))
            {
                error(String.format("Tipo incompatible en atributo %s: se esperaba %s pero se obtuvo %s", requerido.getKey(), requerido.getValue(), proporcionados.get(requerido.getKey())), token);
            }
        }

        for (String nombre : proporcionados.keySet())
        {
            if (!info.atributos.containsKey(nombre))
            {
                error("Atributo desconocido " + nombre + " en la estructura anónima", token);
            }
        }
    }

    protected boolean analizarBloque(List<CodexLatinusParser.InstruccionContext> instrucciones, boolean esFuncionConRetorno)
    {
        boolean retornaSiempre = false;
        for (CodexLatinusParser.InstruccionContext instruccion : instrucciones)
        {
            if (retornaSiempre)
            {
                error("Código inalcanzable después de una instrucción reddere", instruccion.start);
                visit(instruccion);
                continue;
            }
            if (instruccion.retorno() != null)
            {
                visit(instruccion.retorno());
                retornaSiempre = true;
            }
            else if (instruccion.condicional() != null)
            {
                Object resultado = visit(instruccion.condicional());
                if (resultado instanceof Boolean)
                {
                    retornaSiempre = (Boolean) resultado;
                }
            }
            else if (instruccion.bucle() != null)
            {
                visit(instruccion.bucle());
            }
            else
            {
                visit(instruccion);
            }
        }
        return retornaSiempre;
    }

    protected boolean analizarCondicional(CodexLatinusParser.CondicionalContext ctx)
    {
        List<CodexLatinusParser.BloqueContext> bloques = ctx.bloque();
        int numeroCondiciones = ctx.expresion().size();
        int numeroBloques = bloques.size();
        boolean siRetorna = analizarBloque(bloques.get(0).instruccion(), false);
        boolean hayElseDefinitivo = (numeroBloques > numeroCondiciones);
        if (hayElseDefinitivo)
        {
            boolean elseRetorna = analizarBloque(bloques.get(bloques.size() - 1).instruccion(), false);
            for (int i = 1; i < bloques.size() - 1; i++)
            {
                analizarBloque(bloques.get(i).instruccion(), false);
            }
            return siRetorna && elseRetorna;
        }
        else
        {
            for (int i = 1; i < bloques.size(); i++)
            {
                analizarBloque(bloques.get(i).instruccion(), false);
            }
            return false;
        }
    }

    protected Integer evaluarEntero(CodexLatinusParser.ExpresionContext ctx)
    {
        if (ctx == null) return null;
        if (ctx instanceof CodexLatinusParser.ToFactorContext)
        {
            return evaluarFactor(((CodexLatinusParser.ToFactorContext) ctx).factor());
        }
        else if (ctx instanceof CodexLatinusParser.SumaRestaContext)
        {
            CodexLatinusParser.SumaRestaContext s = (CodexLatinusParser.SumaRestaContext) ctx;
            Integer izq = evaluarEntero(s.expresion(0));
            Integer der = evaluarEntero(s.expresion(1));
            if (izq != null && der != null)
            {
                if (s.MAS() != null) return izq + der;
                if (s.MENOS() != null) return izq - der;
            }
            return null;
        }
        else if (ctx instanceof CodexLatinusParser.MultDivContext)
        {
            CodexLatinusParser.MultDivContext m = (CodexLatinusParser.MultDivContext) ctx;
            Integer izq = evaluarEntero(m.expresion(0));
            Integer der = evaluarEntero(m.expresion(1));
            if (izq != null && der != null)
            {
                if (m.POR() != null) return izq * der;
                if (m.DIVISION() != null)
                {
                    if (der == 0) return null;
                    return izq / der;
                }
                if (m.MODULO() != null)
                {
                    if (der == 0) return null;
                    return izq % der;
                }
            }
            return null;
        }
        return null;
    }

    protected Integer evaluarFactor(CodexLatinusParser.FactorContext ctx)
    {
        if (ctx == null) return null;
        if (ctx instanceof CodexLatinusParser.NumLiteralContext)
        {
            String texto = ((CodexLatinusParser.NumLiteralContext) ctx).NUMERO().getText();
            try
            {
                return Integer.parseInt(texto);
            }
            catch (NumberFormatException e)
            {
                return null;
            }
        }
        else if (ctx instanceof CodexLatinusParser.ParentesisContext)
        {
            return evaluarEntero(((CodexLatinusParser.ParentesisContext) ctx).expresion());
        }
        else if (ctx instanceof CodexLatinusParser.DecLiteralContext)
        {
            String texto = ((CodexLatinusParser.DecLiteralContext) ctx).DECIMALES().getText();
            try
            {
                double decimal = Double.parseDouble(texto);
                if (decimal == Math.floor(decimal)) return (int) decimal;
            }
            catch (NumberFormatException e) {}
        }
        else if (ctx instanceof CodexLatinusParser.NegacionUnariaContext)
        {
            Integer valor = evaluarFactor(((CodexLatinusParser.NegacionUnariaContext) ctx).factor());
            if (valor != null)
            {
                return -valor;
            }
        }
        return null;
    }

    protected void verificarRangoArray(Simbolo simbolo, CodexLatinusParser.ExpresionContext indiceCtx, Token token)
    {
        if (simbolo.getTamaño() == null) return;
        Integer tamano = simbolo.getTamaño();
        Integer indice = evaluarEntero(indiceCtx);
        if (indice != null && (indice < 0 || indice >= tamano))
        {
            error("Índice fuera de rango: el arreglo " + simbolo.getId() + " tiene tamaño " + tamano + " pero se accedió con índice " + indice, token);
        }
    }

    protected void validarValorArray(CodexLatinusParser.ValorArrayContext valorArr, String tipoBase, Token token)
    {
        if (valorArr.estructuraAnonima() != null)
        {
            InfoEstructura info = estructuras.get(tipoBase);
            if (info == null)
            {
                error("El tipo base " + tipoBase + " no es una estructura definida", token);
            }
            else
            {
                validarEstructuraAnonima(valorArr.estructuraAnonima(), info, token);
            }
        }
        else if (valorArr.expresion() != null)
        {
            String tipoExpr = (String) visit(valorArr.expresion());
            if (tipoExpr != null && !esCompatible(tipoBase, tipoExpr))
            {
                error(String.format("Tipo incompatible en inicialización del arreglo: se esperaba %s pero se obtuvo %s", tipoBase, tipoExpr), valorArr.start);
            }
        }
    }

    protected void validarIncrementoVariable(String id, Token token)
    {
        Simbolo simbolo = tabla.buscar(id);
        if (simbolo == null)
        {
            error("Variable " + id + " no declarada", token);
            return;
        }
        String tipo = obtenerTipoCompleto(simbolo);
        if (!esTipoPrimitivo(tipo) || !tipo.equals("numerus") && !tipo.equals("decimalis"))
        {
            error("El operador de incremento/decremento solo es válido para tipos numéricos (numerus o decimalis)", token);
        }
    }
    
    protected void verificarRangoArrayAtributo(Simbolo simbolo, String nombreAtributo, Integer tamano, CodexLatinusParser.ExpresionContext indiceCtx, Token token)
    {
        if (tamano == null) return;
        Integer indice = evaluarEntero(indiceCtx);
        if (indice != null && (indice < 0 || indice >= tamano))
        {
            error("Índice fuera de rango: el arreglo " + nombreAtributo + " de " + simbolo.getId() + " tiene tamaño " + tamano + " pero se accedió con índice " + indice, token);
        }
    }

    public int getErroresSemanticos()
    {
        return erroresSemanticos;
    }
    
    public TablaSimbolos getTablaSimbolos()
    {
        return tabla;
    }
}