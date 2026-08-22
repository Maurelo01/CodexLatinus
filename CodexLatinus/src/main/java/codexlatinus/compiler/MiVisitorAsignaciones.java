package codexlatinus.compiler;

import codexlatinus.CodexLatinusParser;

public abstract class MiVisitorAsignaciones extends MiVisitorDeclaraciones
{

    @Override
    public Object visitAsigVariable(CodexLatinusParser.AsigVariableContext ctx)
    {
        String id = ctx.ID().getText();
        Simbolo simbolo = tabla.buscar(id);

        if (simbolo == null)
        {
            error("Variable " + id + " no declarada", ctx.start);
            return null;
        }
        String tipoExpr = (String) visit(ctx.expresion());
        String tipoVariable = obtenerTipoCompleto(simbolo);
        if (tipoExpr != null && !esCompatible(tipoVariable, tipoExpr))
        {
            error(String.format("Tipo incompatible en asignación a %s: se esperaba %s pero se obtuvo %s", id, tipoVariable, tipoExpr), ctx.start);
        }
        return null;
    }

    @Override
    public Object visitAsigArreglo(CodexLatinusParser.AsigArregloContext ctx)
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
        String tipoIndice = (String) visit(ctx.expresion(0));
        if (!"numerus".equals(tipoIndice))
        {
            error("El índice para asignar en el arreglo debe ser numerus, se obtuvo " + tipoIndice, ctx.start);
        }
        verificarRangoArray(simbolo, ctx.expresion(0), ctx.start);
        String tipoBase = (String) simbolo.getValor();
        Object resultadoExpr = visit(ctx.expresion(1)); 
        String tipoExpr = (resultadoExpr != null) ? resultadoExpr.toString() : null;
        if (tipoExpr != null && !esCompatible(tipoBase, tipoExpr))
        {
            error(String.format("Tipo incompatible al asignar a posición del arreglo %s: se esperaba %s pero se obtuvo %s", id, tipoBase, tipoExpr), ctx.start);
        }
        return null;
    }

    @Override
    public Object visitAsigAtributo(CodexLatinusParser.AsigAtributoContext ctx)
    {
        String idVariable = ctx.ID(0).getText();
        String nombreAtributo = ctx.ID(1).getText();
        Simbolo simbolo = tabla.buscar(idVariable);
        if (simbolo == null)
        {
            error("Variable " + idVariable + " no declarada", ctx.start);
            return null;
        }
        String tipoVar = obtenerTipoCompleto(simbolo);
        if (tipoVar == null) return null;
        InfoEstructura info = estructuras.get(tipoVar);
        if (info == null)
        {
            error("La variable " + idVariable + " no es una estructura", ctx.start);
            return null;
        }
        String tipoAtributo = info.atributos.get(nombreAtributo);
        if (tipoAtributo == null)
        {
            error("La estructura " + tipoVar + " no tiene un atributo " + nombreAtributo, ctx.start);
            return null;
        }
        String tipoExpr = (String) visit(ctx.expresion());
        if (tipoExpr != null && !esCompatible(tipoAtributo, tipoExpr))
        {
            error(String.format("Tipo incompatible al asignar al atributo %s: se esperaba %s pero se obtuvo %s", nombreAtributo, tipoAtributo, tipoExpr), ctx.start);
        }
        return null;
    }

    @Override
    public Object visitAsigAtributoArray(CodexLatinusParser.AsigAtributoArrayContext ctx)
    {
        String idVariable = ctx.ID(0).getText();
        String nombreAtributo = ctx.ID(1).getText();
        Simbolo simbolo = tabla.buscar(idVariable);
        if (simbolo == null)
        {
            error("Variable " + idVariable + " no declarada", ctx.start);
            return null;
        }
        String tipoVar = obtenerTipoCompleto(simbolo);
        InfoEstructura info = estructuras.get(tipoVar);
        if (info == null)
        {
            error("La variable " + idVariable + " no es una estructura", ctx.start);
            return null;
        }
        String tipoAtributo = info.atributos.get(nombreAtributo);
        if (tipoAtributo == null)
        {
            error("La estructura " + tipoVar + " no tiene un atributo " + nombreAtributo, ctx.start);
            return null;
        }
        if (!esArray(tipoAtributo))
        {
            error("El atributo " + nombreAtributo + " no es un arreglo", ctx.start);
            return null;
        }
        String tipoIndice = (String) visit(ctx.expresion(0));
        if (!"numerus".equals(tipoIndice))
        {
            error("El índice del arreglo debe ser numerus, se obtuvo " + tipoIndice, ctx.start);
            return null;
        }
        Integer tamanoAtributo = simbolo.getTamañoAtributo(nombreAtributo);
        if (tamanoAtributo != null)
        {
            verificarRangoArrayAtributo(simbolo, nombreAtributo, tamanoAtributo, ctx.expresion(0), ctx.start);
        }
        String tipoBase = tipoBaseDeArray(tipoAtributo);
        Object resultadoExpr = visit(ctx.expresion(1)); 
        String tipoExpr = (resultadoExpr != null) ? resultadoExpr.toString() : null;
        if (tipoExpr != null && !esCompatible(tipoBase, tipoExpr))
        {
            error(String.format("Tipo incompatible al asignar al arreglo %s: se esperaba %s pero se obtuvo %s", nombreAtributo, tipoBase, tipoExpr), ctx.start);
        }
        return null;
    }

    @Override
    public Object visitAsigArrayEstructura(CodexLatinusParser.AsigArrayEstructuraContext ctx)
    {
        String idArray = ctx.ID().getText();
        Simbolo simbolo = tabla.buscar(idArray);
        if (simbolo == null)
        {
            error("Arreglo " + idArray + " no declarado", ctx.start);
            return null;
        }

        if (simbolo.getTipo() != Simbolo.Tipo.ARREGLO)
        {
            error("" + idArray + " no es un arreglo", ctx.start);
            return null;
        }
        String tipoIndice = (String) visit(ctx.expresion());
        if (!"numerus".equals(tipoIndice))
        {
            error("El índice del arreglo debe ser numerus, se obtuvo " + tipoIndice, ctx.start);
        }
        verificarRangoArray(simbolo, ctx.expresion(), ctx.start);
        String tipoBase = (String) simbolo.getValor();
        InfoEstructura info = estructuras.get(tipoBase);
        if (info == null)
        {
            error("El arreglo " + idArray + " no es de un tipo estructura válido", ctx.start);
            return null;
        }
        validarEstructuraAnonima(ctx.estructuraAnonima(), info, ctx.start);
        return null;
    }
}