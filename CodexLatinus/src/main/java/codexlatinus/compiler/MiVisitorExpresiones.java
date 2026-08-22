package codexlatinus.compiler;

import codexlatinus.CodexLatinusParser;
import java.util.ArrayList;
import java.util.List;

public abstract class MiVisitorExpresiones extends MiVisitorAsignaciones
{
    // EXPRESIONES
    @Override
    public Object visitSumaResta(CodexLatinusParser.SumaRestaContext ctx)
    {
        String tipoIzq = (String) visit(ctx.expresion());
        String tipoDer = (String) visit(ctx.termino());
        if (tipoIzq == null || tipoDer == null) return null;
        if ("textum".equals(tipoIzq) || "textum".equals(tipoDer))
        {
            if (ctx.MENOS() != null)
            {
                error("El tipo textum no soporta la operación de resta", ctx.start);
                return null;
            }
            return "textum";
        }

        if (!esTipoPrimitivo(tipoIzq) || !esTipoPrimitivo(tipoDer) ||
            "textum".equals(tipoIzq) || "textum".equals(tipoDer) ||
            "bool".equals(tipoIzq) || "bool".equals(tipoDer) ||
            "littera".equals(tipoIzq) || "littera".equals(tipoDer))
        {
            error("Operación aritmética no válida entre " + tipoIzq + " y " + tipoDer, ctx.start);
            return null;
        }
        int nivelIzq = nivelTipo(tipoIzq);
        int nivelDer = nivelTipo(tipoDer);
        return nivelIzq >= nivelDer ? tipoIzq : tipoDer;
    }

    @Override
    public Object visitComparacion(CodexLatinusParser.ComparacionContext ctx)
    {
        String tipoIzq = (String) visit(ctx.expresion());
        String tipoDer = (String) visit(ctx.termino());
        if (tipoIzq == null || tipoDer == null) return null;
        if ("textum".equals(tipoIzq) || "textum".equals(tipoDer) ||
            "bool".equals(tipoIzq) || "bool".equals(tipoDer) ||
            "littera".equals(tipoIzq) || "littera".equals(tipoDer))
        {
            error("Comparación no válida entre " + tipoIzq + " y " + tipoDer, ctx.start);
            return null;
        }

        return "bool";
    }

    @Override
    public Object visitIgualdad(CodexLatinusParser.IgualdadContext ctx)
    {
        String tipoIzq = (String) visit(ctx.expresion());
        String tipoDer = (String) visit(ctx.termino());
        if (tipoIzq == null || tipoDer == null) return null;
        if (!tipoIzq.equals(tipoDer))
        {
            boolean ambosNumericos = esTipoPrimitivo(tipoIzq) && esTipoPrimitivo(tipoDer) &&
                                     (tipoIzq.equals("numerus") || tipoIzq.equals("decimalis")) &&
                                     (tipoDer.equals("numerus") || tipoDer.equals("decimalis"));
            if (!ambosNumericos)
            {
                error("No se pueden comparar tipos incompatibles: " + tipoIzq + " y " + tipoDer, ctx.start);
                return null;
            }
        }
        return "bool";
    }
    
    @Override
    public Object visitAndLogico(CodexLatinusParser.AndLogicoContext ctx)
    {
        String tipoIzq = (String) visit(ctx.expresion());
        String tipoDer = (String) visit(ctx.termino());
        if (!"bool".equals(tipoIzq) || !"bool".equals(tipoDer))
        {
            error("El operador && requiere operandos booleanos", ctx.start);
            return null;
        }
        return "bool";
    }

    @Override
    public Object visitOrLogico(CodexLatinusParser.OrLogicoContext ctx)
    {
        String tipoIzq = (String) visit(ctx.expresion());
        String tipoDer = (String) visit(ctx.termino());
        if (!"bool".equals(tipoIzq) || !"bool".equals(tipoDer))
        {
            error("El operador || requiere operandos booleanos", ctx.start);
            return null;
        }
        return "bool";
    }

    @Override
    public Object visitMultDiv(CodexLatinusParser.MultDivContext ctx)
    {
        String tipoIzq = (String) visit(ctx.termino());
        String tipoDer = (String) visit(ctx.factor());
        if (!esTipoPrimitivo(tipoIzq) || !esTipoPrimitivo(tipoDer) ||
            "textum".equals(tipoIzq) || "textum".equals(tipoDer) ||
            "bool".equals(tipoIzq) || "bool".equals(tipoDer) ||
            "littera".equals(tipoIzq) || "littera".equals(tipoDer))
        {
            error("Operación aritmética no válida entre " + tipoIzq + " y " + tipoDer, ctx.start);
            return null;
        }

        int nivelIzq = nivelTipo(tipoIzq);
        int nivelDer = nivelTipo(tipoDer);
        return nivelIzq >= nivelDer ? tipoIzq : tipoDer;
    }
    
    @Override
    public Object visitToTermino(CodexLatinusParser.ToTerminoContext ctx)
    {
        return visit(ctx.termino());
    }

    @Override
    public Object visitToFactor(CodexLatinusParser.ToFactorContext ctx)
    {
        return visit(ctx.factor());
    }

    // Factores
    @Override
    public Object visitNumLiteral(CodexLatinusParser.NumLiteralContext ctx)
    {
        return "numerus";
    }

    @Override
    public Object visitDecLiteral(CodexLatinusParser.DecLiteralContext ctx)
    {
        return "decimalis";
    }

    @Override
    public Object visitTextLiteral(CodexLatinusParser.TextLiteralContext ctx)
    {
        return "textum";
    }

    @Override
    public Object visitCharLiteral(CodexLatinusParser.CharLiteralContext ctx)
    {
        return "littera";
    }

    @Override
    public Object visitTrueLiteral(CodexLatinusParser.TrueLiteralContext ctx)
    {
        return "bool";
    }

    @Override
    public Object visitFalseLiteral(CodexLatinusParser.FalseLiteralContext ctx)
    {
        return "bool";
    }

    @Override
    public Object visitNegacion(CodexLatinusParser.NegacionContext ctx)
    {
        String tipo = (String) visit(ctx.factor());
        if (!"bool".equals(tipo))
        {
            error("El operador non requiere un operando booleano", ctx.start);
            return null;
        }
        return "bool";
    }

    @Override
    public Object visitVariable(CodexLatinusParser.VariableContext ctx)
    {
        String id = ctx.ID().getText();
        Simbolo simbolo = tabla.buscar(id);
        if (simbolo == null)
        {
            error("Variable " + id + " no declarada", ctx.start);
            return null;
        }
        return obtenerTipoCompleto(simbolo);
    }

    @Override
    public Object visitAccesoArray(CodexLatinusParser.AccesoArrayContext ctx)
    {
        String id = ctx.ID().getText();
        Simbolo simbolo = tabla.buscar(id);
        if (simbolo == null)
        {
            error("Arreglo " + id + " no declarado", ctx.start);
            return null;
        }
        if (simbolo.getTipo() != Simbolo.Tipo.ARREGLO)
        {
            error("" + id + " no es un arreglo", ctx.start);
            return null;
        }
        String tipoIndice = (String) visit(ctx.expresion());
        if (!"numerus".equals(tipoIndice))
        {
            error("El índice del arreglo debe ser numerus, se obtuvo " + tipoIndice, ctx.start);
        }
        verificarRangoArray(simbolo, ctx.expresion(), ctx.start);
        return (String) simbolo.getValor();
    }

    @Override
    public Object visitLlamadaFuncion(CodexLatinusParser.LlamadaFuncionContext ctx)
    {
        String nombre = ctx.ID().getText();
        InfoFuncion funcion = funciones.get(nombre);
        if (funcion == null)
        {
            error("La función " + nombre + " no está definida", ctx.start);
            return null;
        }
        List<CodexLatinusParser.ExpresionContext> args;
        if (ctx.argumentos() != null)
        {
            args = ctx.argumentos().expresion();
        }
        else
        {
            args = new ArrayList<>();
        }

        if (args.size() != funcion.parametros.size())
        {
            error(String.format("La función %s espera %d parámetros, pero se le pasaron %d", nombre, funcion.parametros.size(), args.size()), ctx.start);
            return null;
        }

        for (int i = 0; i < args.size(); i++)
        {
            String tipoArg = (String) visit(args.get(i));
            if (tipoArg != null && !esCompatible(funcion.parametros.get(i).tipo, tipoArg))
            {
                error(String.format("El parámetro %d de %s espera %s pero se obtuvo %s", i + 1, nombre, funcion.parametros.get(i).tipo, tipoArg), args.get(i).start);
            }
        }
        return funcion.tipoRetorno; 
    }

    @Override
    public Object visitParentesis(CodexLatinusParser.ParentesisContext ctx)
    {
        return visit(ctx.expresion());
    }

    @Override
    public Object visitAccesoAtributo(CodexLatinusParser.AccesoAtributoContext ctx)
    {
        String tipoBase = (String) visit(ctx.factor());
        String nombreAtributo = ctx.ID().getText();
        if (tipoBase == null) return null;
        InfoEstructura info = estructuras.get(tipoBase);
        if (info == null)
        {
            error("El tipo " + tipoBase + " no es una estructura definida", ctx.start);
            return null;
        }
        String tipoAtributo = info.atributos.get(nombreAtributo);
        if (tipoAtributo == null)
        {
            error("La estructura " + tipoBase + " no tiene un atributo llamado " + nombreAtributo, ctx.start);
            return null;
        }
        return tipoAtributo;
    }

    @Override
    public Object visitAccesoAtributoArray(CodexLatinusParser.AccesoAtributoArrayContext ctx)
    {
        String tipoBase = (String) visit(ctx.factor());
        String nombreAtributo = ctx.ID().getText();
        if (tipoBase == null) return null;
        if (esArray(tipoBase))
        {
            tipoBase = tipoBaseDeArray(tipoBase);
        }
        InfoEstructura info = estructuras.get(tipoBase);
        if (info == null)
        {
            error("El tipo " + tipoBase + " no es una estructura definida", ctx.start);
            return null;
        }
        String tipoAtributo = info.atributos.get(nombreAtributo);
        if (tipoAtributo == null)
        {
            error("La estructura " + tipoBase + " no tiene un atributo llamado " + nombreAtributo, ctx.start);
            return null;
        }
        String tipoIndice = (String) visit(ctx.expresion());
        if (!"numerus".equals(tipoIndice))
        {
            error("El índice del arreglo debe ser numerus, se obtuvo " + tipoIndice, ctx.start);
            return null;
        }
        if (esArray(tipoAtributo))
        {
            if (ctx.factor() instanceof CodexLatinusParser.VariableContext)
            {
                String idVar = ((CodexLatinusParser.VariableContext) ctx.factor()).ID().getText();
                Simbolo simboloVar = tabla.buscar(idVar);
                if (simboloVar != null)
                {
                    Integer tamañoAtributo = simboloVar.getTamañoAtributo(nombreAtributo);
                    if (tamañoAtributo != null)
                    {
                        verificarRangoArrayAtributo(simboloVar, nombreAtributo, tamañoAtributo, ctx.expresion(), ctx.start);
                    }
                }
            }
            return tipoBaseDeArray(tipoAtributo);
        }
        return tipoAtributo;
    }
}