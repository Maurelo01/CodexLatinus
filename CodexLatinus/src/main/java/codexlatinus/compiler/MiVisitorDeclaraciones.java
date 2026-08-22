package codexlatinus.compiler;

import codexlatinus.CodexLatinusParser;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class MiVisitorDeclaraciones extends MiVisitorBase
{

    @Override
    public Object visitPrograma(CodexLatinusParser.ProgramaContext ctx)
    {
        return visitChildren(ctx);
    }

    @Override
    public Object visitSeccionDeclaraciones(CodexLatinusParser.SeccionDeclaracionesContext ctx)
    {
        return visitChildren(ctx);
    }

    @Override
    public Object visitSeccionVariables(CodexLatinusParser.SeccionVariablesContext ctx)
    {
        return visitChildren(ctx);
    }

    @Override
    public Object visitDefinicionEstructura(CodexLatinusParser.DefinicionEstructuraContext ctx)
    {
        String nombre = ctx.ID().getText();
        if (estructuras.containsKey(nombre))
        {
            error("La estructura " + nombre + " ya está definida", ctx.start);
            return null;
        }
        InfoEstructura info = new InfoEstructura(nombre);
        for (CodexLatinusParser.AtributoEstructuraContext attrCtx : ctx.atributoEstructura())
        {
            String nombreAtributo = attrCtx.ID().getText();
            String tipoAtributo;
            if (attrCtx.SERIES() != null) 
            {
                tipoAtributo = "series " + textoCompletoTipo(attrCtx.tipo());
            }
            else
            {
                tipoAtributo = textoCompletoTipo(attrCtx.tipo());
            }
            if (info.atributos.containsKey(nombreAtributo))
            {
                error("Atributo " + nombreAtributo + " duplicado en la estructura " + nombre, attrCtx.start);
            }
            else
            {
                info.atributos.put(nombreAtributo, tipoAtributo);
            }
        }
        estructuras.put(nombre, info);
        return null;
    }

    @Override
    public Object visitDeclaracionVariable(CodexLatinusParser.DeclaracionVariableContext ctx)
    {
        String id = ctx.ID().getText();
        String tipoTexto = textoCompletoTipo(ctx.tipo());
        Simbolo.Tipo tipoEnum = mapearTipo(tipoTexto);
        boolean esGlobal = tabla.getAmbitoActual().getNombre().equals("global");
        Object valor = null;
        if (ctx.valorInicial() != null)
        {
            String tipoExpr = (String) visit(ctx.valorInicial());
            if (tipoExpr != null && !esCompatible(tipoTexto, tipoExpr))
            {
                error(String.format("Tipo incompatible en la declaración de %s: se esperaba %s pero se obtuvo %s", id, tipoTexto, tipoExpr), ctx.start);
            }
        }

        if (!tabla.agregar(new Simbolo(id, tipoEnum, valor, esGlobal)))
        {
            error("La variable " + id + " ya está declarada en este ámbito", ctx.start);
        }
        return null;
    }

    @Override
    public Object visitDeclaracionBoolSinTipo(CodexLatinusParser.DeclaracionBoolSinTipoContext ctx)
    {
        String id = ctx.ID().getText();
        boolean esGlobal = tabla.getAmbitoActual().getNombre().equals("global");
        if (!tabla.agregar(new Simbolo(id, Simbolo.Tipo.BOOL, null, esGlobal)))
        {
            error("La variable booleana " + id + " ya está declarada en este ámbito", ctx.start);
        }
        return null;
    }

    @Override
    public Object visitDeclaracionArray(CodexLatinusParser.DeclaracionArrayContext ctx)
    {
        String id = ctx.ID().getText();
        String tipoBase = textoCompletoTipo(ctx.tipo());
        String tipoCompleto = "series " + tipoBase;
        boolean esGlobal = tabla.getAmbitoActual().getNombre().equals("global");
        Simbolo.Tipo tipoEnum = Simbolo.Tipo.ARREGLO;
        Object valor = tipoBase;
        String tipoTamaño = (String) visit(ctx.expresion());
        if (!"numerus".equals(tipoTamaño))
        {
            error("El tamaño del arreglo " + id + " debe ser de tipo numerus", ctx.expresion().start);
        }
        Integer tamaño = evaluarEntero(ctx.expresion());
        if (tamaño != null && tamaño <= 0)
        {
            error("El tamaño del arreglo " + id + " debe ser mayor que 0", ctx.expresion().start);
        }
        if (ctx.arr_valores() != null)
        {
            List<CodexLatinusParser.ValorArrayContext> valores = ctx.arr_valores().valorArray();
            if (tamaño != null && valores.size() != tamaño)
            {
                error("El arreglo " + id + " espera " + tamaño + " valores, pero se proporcionaron " + valores.size(), ctx.arr_valores().start);
            }
            for (CodexLatinusParser.ValorArrayContext v : valores)
            {
                validarValorArray(v, tipoBase, ctx.arr_valores().start);
            }
        }

        Simbolo simbolo = new Simbolo(id, tipoEnum, valor, esGlobal);
        simbolo.setTamaño(tamaño);
        if (!tabla.agregar(simbolo))
        {
            error("El arreglo " + id + " ya está declarado en este ámbito", ctx.start);
        }
        return null;
    }

    @Override
    public Object visitDeclaracionArrayBooleano(CodexLatinusParser.DeclaracionArrayBooleanoContext ctx)
    {
        String id = ctx.ID().getText();
        String tipoCompleto = "series bool";
        boolean esGlobal = tabla.getAmbitoActual().getNombre().equals("global");
        Simbolo.Tipo tipoEnum = Simbolo.Tipo.ARREGLO;
        Object valor = "bool";
        String tipoTamaño = (String) visit(ctx.expresion());
        if (!"numerus".equals(tipoTamaño))
        {
            error("El tamaño del arreglo booleano " + id + " debe ser de tipo numerus", ctx.expresion().start);
        }

        Integer tamaño = evaluarEntero(ctx.expresion());
        if (tamaño != null && tamaño <= 0)
        {
            error("El tamaño del arreglo booleano " + id + " debe ser mayor que 0", ctx.expresion().start);
        }
        if (ctx.arr_valores() != null)
        {
            List<CodexLatinusParser.ValorArrayContext> valores = ctx.arr_valores().valorArray();
            if (tamaño != null && valores.size() != tamaño)
            {
                error("El arreglo booleano " + id + " espera " + tamaño + " valores, pero se proporcionaron " + valores.size(), ctx.arr_valores().start);
            }
            for (CodexLatinusParser.ValorArrayContext v : valores)
            {
                validarValorArray(v, "bool", ctx.arr_valores().start);
            }
        }
        Simbolo simbolo = new Simbolo(id, tipoEnum, valor, esGlobal);
        simbolo.setTamaño(tamaño);
        if (!tabla.agregar(simbolo))
        {
            error("El arreglo booleano " + id + " ya está declarado en este ámbito", ctx.start);
        }
        return null;
    }

    @Override
    public Object visitDeclaracionEstructura(CodexLatinusParser.DeclaracionEstructuraContext ctx)
    {
        String idVar = ctx.ID().getText();
        String tipoEstructura = ctx.tipo().getText();
        boolean esGlobal = tabla.getAmbitoActual().getNombre().equals("global");
        InfoEstructura info = estructuras.get(tipoEstructura);
        if (info == null)
        {
            error("La estructura " + tipoEstructura + " no está definida", ctx.start);
            return null;
        }
        Map<String, String> atributosProporcionados = new LinkedHashMap<>();
        Map<String, Integer> tamañosAtributos = new LinkedHashMap<>();
        if (ctx.estructuraInicial() != null)
        {
            CodexLatinusParser.EstructuraInicialContext estructuraCtx = ctx.estructuraInicial();
            if (estructuraCtx.atributos_valores() != null)
            {
                for (CodexLatinusParser.Atributo_valorContext atriValor : estructuraCtx.atributos_valores().atributo_valor())
                {
                    String nombreAtributo = atriValor.ID().getText();
                    String tipoAtributo = (String) visit(atriValor.valorAtributo());
                    atributosProporcionados.put(nombreAtributo, tipoAtributo);
                    if (atriValor.valorAtributo().ID() != null && atriValor.valorAtributo().NUMERO() != null)
                    {
                        try
                        {
                            int tamaño = Integer.parseInt(atriValor.valorAtributo().NUMERO().getText());
                            tamañosAtributos.put(nombreAtributo, tamaño);
                        }
                        catch (NumberFormatException e) {}
                    }
                }
            }
        }

        for (Map.Entry<String, String> requerido : info.atributos.entrySet())
        {
            String nombreAtrib = requerido.getKey();
            String tipoEsperado = requerido.getValue();
            if (!atributosProporcionados.containsKey(nombreAtrib))
            {
                error("Falta el atributo obligatorio " + nombreAtrib + " en la inicialización de " + idVar, ctx.start);
            }
            else
            {
                String tipoReal = atributosProporcionados.get(nombreAtrib);
                if (tipoReal != null && !esCompatible(tipoEsperado, tipoReal))
                {
                    error(String.format("Tipo incompatible para el atributo %s de %s: se esperaba %s pero se obtuvo %s", nombreAtrib, tipoEstructura, tipoEsperado, tipoReal), ctx.start);
                }
            }
        }
        for (String nombreAtrib : atributosProporcionados.keySet())
        {
            if (!info.atributos.containsKey(nombreAtrib))
            {
                error("El atributo " + nombreAtrib + " no existe en la estructura " + tipoEstructura, ctx.start);
            }
        }
        Simbolo simbolo = new Simbolo(idVar, Simbolo.Tipo.ESTRUCTURA, tipoEstructura, esGlobal);
        for (Map.Entry<String, Integer> entry : tamañosAtributos.entrySet())
        {
            simbolo.setTamañoAtributo(entry.getKey(), entry.getValue());
        }
        if (!tabla.agregar(simbolo))
        {
            error("La variable " + idVar + " ya está declarada en este ámbito", ctx.start);
        }
        return null;
    }

    @Override
    public Object visitValorAtributo(CodexLatinusParser.ValorAtributoContext ctx)
    {
        if (ctx.expresion() != null)
        {
            return visit(ctx.expresion());
        }
        if (ctx.ID() != null && ctx.NUMERO() != null)
        {
            return "numerus";
        }
        if (ctx.estructuraAnonima() != null)
        {
            return ctx.getText();
        }
        if (ctx.arr_valores() != null)
        {
            return "series";
        }
        return null;
    }
}