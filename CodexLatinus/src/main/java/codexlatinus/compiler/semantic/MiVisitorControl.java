package codexlatinus.compiler.semantic;

import codexlatinus.CodexLatinusParser;
import codexlatinus.compiler.symbol.*;

public abstract class MiVisitorControl extends MiVisitorExpresiones
{

    @Override
    public Object visitDefinicionFuncion(CodexLatinusParser.DefinicionFuncionContext ctx)
    {
        String nombreFuncion = ctx.ID().getText();
        String tipoRetorno;
        if (ctx.RATIO() != null)
        {
            tipoRetorno = textoCompletoTipo(ctx.tipo());
        }
        else
        {
            tipoRetorno = "void";
        }
        if (funciones.containsKey(nombreFuncion))
        {
            error("La función " + nombreFuncion + " ya está definida", ctx.start);
            return null;
        }
        InfoFuncion funcion = new InfoFuncion(nombreFuncion, tipoRetorno);
        funciones.put(nombreFuncion, funcion);
        tabla.ingresarOtroAmbito("función " + nombreFuncion);
        if (ctx.parametros() != null)
        {
            for (CodexLatinusParser.ParametroContext parametro : ctx.parametros().parametro())
            {
                String nombreParam = parametro.ID().getText();
                String tipoParam = textoCompletoTipo(parametro.tipo());
                Parametro param = new Parametro();
                param.nombre = nombreParam;
                param.tipo = tipoParam;
                funcion.parametros.add(param);
                if (!tabla.agregar(new Simbolo(nombreParam, mapearTipo(tipoParam), null, false)))
                {
                    error("El parámetro " + nombreParam + " ya está declarado en la función", parametro.start);
                }
            }
        }
        if (ctx.seccionVariables() != null)
        {
            visit(ctx.seccionVariables());
        }
        this.tipoRetornoEsperadoActual = tipoRetorno;
        boolean retornoObligatorio = !tipoRetorno.equals("void");
        boolean retornaSiempre = analizarBloque(ctx.instruccion(), retornoObligatorio);
        if (retornoObligatorio && !retornaSiempre)
        {
            error("La función " + nombreFuncion + " no siempre retorna un valor", ctx.stop);
        }
        this.tipoRetornoEsperadoActual = null;
        tabla.salirAmbito();
        return null;
    }

    @Override
    public Object visitRetorno(CodexLatinusParser.RetornoContext ctx)
    {
        if (tipoRetornoEsperadoActual == null)
        {
            error("Instrucción reddere fuera de una función permitida", ctx.start);
            return null;
        }

        String tipoRetornado = "void";
        if (ctx.expresion() != null)
        {
            Object res = visit(ctx.expresion());
            tipoRetornado = (res != null) ? res.toString() : "desconocido";
        }

        if (!tipoRetornado.equals(tipoRetornoEsperadoActual) && !esCompatible(tipoRetornoEsperadoActual, tipoRetornado))
        {
            error("Tipo de retorno incorrecto. Se esperaba " + tipoRetornoEsperadoActual + " pero se obtuvo " + tipoRetornado, ctx.start);
        }
        return null;
    }

    @Override
    public Object visitCondicional(CodexLatinusParser.CondicionalContext ctx)
    {
        for (CodexLatinusParser.ExpresionContext exprCtx : ctx.expresion())
        {
            String tipoCondicion = (String) visit(exprCtx);
            if (!"bool".equals(tipoCondicion))
            {
                error("Error de Flujo: La condición debe ser estrictamente booleana, se obtuvo " + tipoCondicion, exprCtx.start);
            }
        }
        return analizarCondicional(ctx);
    }

    @Override
    public Object visitBucleDum(CodexLatinusParser.BucleDumContext ctx)
    {
        String tipoCondicion = (String) visit(ctx.expresion());
        if (!"bool".equals(tipoCondicion))
        {
            error("Error de Flujo: La condición del ciclo dum debe ser booleana", ctx.start);
        }
        dentroDeCiclo++;
        if (ctx.bloque() != null)
        {
            analizarBloque(ctx.bloque().instruccion(), false);
        }
        dentroDeCiclo--;
        return null;
    }

    @Override
    public Object visitBucleFacere(CodexLatinusParser.BucleFacereContext ctx)
    {
        String tipoCondicion = (String) visit(ctx.expresion());
        if (!"bool".equals(tipoCondicion))
        {
            error("Error de Flujo: La condición del ciclo facere debe ser booleana", ctx.start);
        }
        dentroDeCiclo++;
        if (ctx.bloque() != null)
        {
            analizarBloque(ctx.bloque().instruccion(), false);
        }
        dentroDeCiclo--;
        return null;
    }
    
    @Override
    public Object visitBuclePer(CodexLatinusParser.BuclePerContext ctx)
    {
        tabla.ingresarOtroAmbito("per");
        if (ctx.declaracion() != null)
        {
            visit(ctx.declaracion());
        }
        if (ctx.expresion() != null)
        {
            String tipoCondicion = (String) visit(ctx.expresion());
            if (!"bool".equals(tipoCondicion))
            {
                error("Error de Flujo: La condición del ciclo per debe ser booleana", ctx.start);
            }
        }
        if (ctx.actualizacion() != null)
        {
            visit(ctx.actualizacion());
        }
        dentroDeCiclo++;
        if (ctx.bloque() != null)
        {
            analizarBloque(ctx.bloque().instruccion(), false);
        }
        dentroDeCiclo--;
        tabla.salirAmbito();
        return null;
    }
    
    @Override
    public Object visitInterrupcion(CodexLatinusParser.InterrupcionContext ctx)
    {
        if (dentroDeCiclo == 0)
        {
            error("La instrucción perge o interrumpe solo puede usarse dentro de un ciclo", ctx.start);
        }
        return null;
    }

    @Override
    public Object visitLectura(CodexLatinusParser.LecturaContext ctx)
    {
        if (ctx.ID() != null)
        {
            String id = ctx.ID().getText();
            Simbolo simbolo = tabla.buscar(id);
            if (simbolo == null)
            {
                error("La variable " + id + " no está declarada para la lectura", ctx.start);
            }
            else if (!"textum".equals(obtenerTipoCompleto(simbolo)))
            {
                error("La variable de lectura " + id + " debe ser de tipo textum", ctx.start);
            }
        }
        return null;
    }
    
    @Override
    public Object visitActualizacion(CodexLatinusParser.ActualizacionContext ctx)
    {
        if (ctx.asignacion() != null)
        {
            visit(ctx.asignacion());
        }
        else
        {
            String id = ctx.ID().getText();
            validarIncrementoVariable(id, ctx.start);
        }
        return null;
    }
    
    @Override
    public Object visitIncrementoVariable(CodexLatinusParser.IncrementoVariableContext ctx)
    {
        String id = ctx.ID().getText();
        validarIncrementoVariable(id, ctx.start);
        return null;
    }

    @Override
    public Object visitIncrementoArray(CodexLatinusParser.IncrementoArrayContext ctx)
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
        String tipoBase = (String) simbolo.getValor();
        if (!esTipoPrimitivo(tipoBase) || !tipoBase.equals("numerus") && !tipoBase.equals("decimalis"))
        {
            error("El operador de incremento/decremento solo es válido para tipos numéricos (numerus o decimalis)", ctx.start);
        }
        return null;
    }

    @Override
    public Object visitIncrementoAtributo(CodexLatinusParser.IncrementoAtributoContext ctx)
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
        if (!esTipoPrimitivo(tipoAtributo) || !tipoAtributo.equals("numerus") && !tipoAtributo.equals("decimalis"))
        {
            error("El operador de incremento/decremento solo es válido para tipos numéricos (numerus o decimalis)", ctx.start);
        }
        return null;
    }
}