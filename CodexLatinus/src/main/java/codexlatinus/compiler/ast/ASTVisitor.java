package codexlatinus.compiler.ast;

import codexlatinus.CodexLatinusBaseVisitor;
import codexlatinus.CodexLatinusParser;

public class ASTVisitor extends CodexLatinusBaseVisitor<NodoAST>
{
    // PROGRAMA Y SECCIONES
    @Override
    public NodoAST visitPrograma(CodexLatinusParser.ProgramaContext ctx)
    {
        NodoAST raiz = new NodoAST("Programa");
        if (ctx.seccionDeclaraciones() != null)
        {
            raiz.agregarHijo(visit(ctx.seccionDeclaraciones()));
        }
        if (ctx.seccionFunciones() != null)
        {
            raiz.agregarHijo(visit(ctx.seccionFunciones()));
        }
        if (ctx.seccionCodigo() != null)
        {
            raiz.agregarHijo(visit(ctx.seccionCodigo()));
        }
        return raiz;
    }
    @Override
    public NodoAST visitSeccionDeclaraciones(CodexLatinusParser.SeccionDeclaracionesContext ctx)
    {
        NodoAST nodo = new NodoAST("DeclaracionesGlobales");
        for (int i = 1; i < ctx.getChildCount(); i++) 
        {
            NodoAST hijo = visit(ctx.getChild(i));
            if (hijo != null) nodo.agregarHijo(hijo);
        }
        return nodo;
    }
    @Override
    public NodoAST visitSeccionFunciones(CodexLatinusParser.SeccionFuncionesContext ctx)
    {
        NodoAST nodo = new NodoAST("Funciones");
        for (var func : ctx.definicionFuncion())
        {
            nodo.agregarHijo(visit(func));
        }
        return nodo;
    }
    @Override
    public NodoAST visitSeccionCodigo(CodexLatinusParser.SeccionCodigoContext ctx)
    {
        NodoAST nodo = new NodoAST("Main");
        for (var instr : ctx.instruccion())
        {
            nodo.agregarHijo(visit(instr));
        }
        return nodo;
    }

    // ESTRUCTURAS
    @Override
    public NodoAST visitDefinicionEstructura(CodexLatinusParser.DefinicionEstructuraContext ctx)
    {
        NodoAST nodo = new NodoAST("Estructura", ctx.ID().getText());
        for (var attr : ctx.atributoEstructura())
        {
            nodo.agregarHijo(visit(attr));
        }
        return nodo;
    }
    @Override
    public NodoAST visitAtributoEstructura(CodexLatinusParser.AtributoEstructuraContext ctx)
    {
        NodoAST nodo = new NodoAST("Atributo", ctx.ID().getText());
        if (ctx.tipo() != null)
        {
            nodo.agregarHijo(new NodoAST("Tipo", ctx.tipo().getText()));
        }
        else
        {
            nodo.agregarHijo(new NodoAST("Tipo", "bool"));
        }
        if (ctx.SERIES() != null)
        {
            nodo.agregarHijo(new NodoAST("EsArreglo"));
        }
        return nodo;
    }
    @Override
    public NodoAST visitAsigAtributoEstructura(CodexLatinusParser.AsigAtributoEstructuraContext ctx)
    {
        NodoAST nodo = new NodoAST("AsignacionAtributoEstructura");
        nodo.agregarHijo(new NodoAST("Objeto", ctx.ID(0).getText()));
        nodo.agregarHijo(new NodoAST("Atributo", ctx.ID(1).getText()));
        nodo.agregarHijo(visit(ctx.estructuraAnonima()));
        return nodo;
    }

    @Override
    public NodoAST visitAsigAtributoArrayEstructura(CodexLatinusParser.AsigAtributoArrayEstructuraContext ctx)
    {
        NodoAST nodo = new NodoAST("AsignacionAtributoArrayEstructura");
        nodo.agregarHijo(new NodoAST("Objeto", ctx.ID(0).getText()));
        nodo.agregarHijo(new NodoAST("Atributo", ctx.ID(1).getText()));
        nodo.agregarHijo(visit(ctx.expresion())); // indice
        nodo.agregarHijo(visit(ctx.estructuraAnonima())); // valor
        return nodo;
    }
    

    // FUNCIONES
    @Override
    public NodoAST visitDefinicionFuncion(CodexLatinusParser.DefinicionFuncionContext ctx)
    {
        String tipoFuncion = (ctx.ACTIO() != null) ? "actio" : "ratio";
        NodoAST nodo = new NodoAST("Funcion", ctx.ID().getText());
        nodo.agregarHijo(new NodoAST("TipoFuncion", tipoFuncion));
        if (ctx.RATIO() != null)
        {
            nodo.agregarHijo(new NodoAST("TipoRetorno", ctx.tipo().getText()));
        }
        if (ctx.parametros() != null)
        {
            nodo.agregarHijo(visit(ctx.parametros()));
        }
        if (ctx.seccionVariables() != null)
        {
            nodo.agregarHijo(visit(ctx.seccionVariables()));
        }
        for (var instruccion : ctx.instruccion())
        {
            nodo.agregarHijo(visit(instruccion));
        }

        return nodo;
    }
    @Override
    public NodoAST visitParametros(CodexLatinusParser.ParametrosContext ctx)
    {
        NodoAST nodo = new NodoAST("Parametros");
        for (var parametro : ctx.parametro())
        {
            nodo.agregarHijo(visit(parametro));
        }
        return nodo;
    }
    @Override
    public NodoAST visitParametro(CodexLatinusParser.ParametroContext ctx)
    {
        NodoAST nodo = new NodoAST("Parametro", ctx.ID().getText());
        nodo.agregarHijo(new NodoAST("Tipo", ctx.tipo().getText()));
        return nodo;
    }

    @Override
    public NodoAST visitSeccionVariables(CodexLatinusParser.SeccionVariablesContext ctx)
    {
        NodoAST nodo = new NodoAST("VariablesLocales");
        for (int i = 2; i < ctx.getChildCount() - 1; i++) 
        {
            NodoAST hijo = visit(ctx.getChild(i));
            if (hijo != null) nodo.agregarHijo(hijo);
        }
        return nodo;
    }

    // DECLARACIONES
    @Override
    public NodoAST visitDeclaracionVariable(CodexLatinusParser.DeclaracionVariableContext ctx)
    {
        NodoAST nodo = new NodoAST("DeclaracionVariable");
        nodo.agregarHijo(new NodoAST("ID", ctx.ID().getText()));
        nodo.agregarHijo(new NodoAST("Tipo", ctx.tipo().getText()));
        if (ctx.valorInicial() != null)
        {
            nodo.agregarHijo(visit(ctx.valorInicial()));
        }
        return nodo;
    }

    @Override
    public NodoAST visitDeclaracionBoolSinTipo(CodexLatinusParser.DeclaracionBoolSinTipoContext ctx)
    {
        NodoAST nodo = new NodoAST("DeclaracionBooleana");
        nodo.agregarHijo(new NodoAST("ID", ctx.ID().getText()));
        nodo.agregarHijo(new NodoAST("Valor", (ctx.VERUM() != null) ? "verum" : "falsus"));
        return nodo;
    }
    @Override
    public NodoAST visitDeclaracionEstructura(CodexLatinusParser.DeclaracionEstructuraContext ctx)
    {
        NodoAST nodo = new NodoAST("DeclaracionEstructura");
        nodo.agregarHijo(new NodoAST("ID", ctx.ID().getText()));
        nodo.agregarHijo(new NodoAST("Tipo", ctx.tipo().getText()));
        if (ctx.estructuraInicial() != null)
        {
            nodo.agregarHijo(visit(ctx.estructuraInicial()));
        }
        return nodo;
    }

    @Override
    public NodoAST visitDeclaracionArray(CodexLatinusParser.DeclaracionArrayContext ctx)
    {
        NodoAST nodo = new NodoAST("DeclaracionArray");
        nodo.agregarHijo(new NodoAST("ID", ctx.ID().getText()));
        nodo.agregarHijo(visit(ctx.expresion()));  // tamaño
        nodo.agregarHijo(new NodoAST("Tipo", ctx.tipo().getText()));
        if (ctx.arr_valores() != null)
        {
            nodo.agregarHijo(visit(ctx.arr_valores()));
        }
        return nodo;
    }
    @Override
    public NodoAST visitDeclaracionArrayBooleano(CodexLatinusParser.DeclaracionArrayBooleanoContext ctx)
    {
        NodoAST nodo = new NodoAST("DeclaracionArrayBooleano");
        nodo.agregarHijo(new NodoAST("ID", ctx.ID().getText()));
        nodo.agregarHijo(visit(ctx.expresion())); // tamaño
        if (ctx.arr_valores() != null)
        {
            nodo.agregarHijo(visit(ctx.arr_valores()));
        }
        return nodo;
    }
    @Override
    public NodoAST visitEstructuraInicial(CodexLatinusParser.EstructuraInicialContext ctx)
    {
        NodoAST nodo = new NodoAST("InicializacionEstructura");
        if (ctx.atributos_valores() != null)
        {
            nodo.agregarHijo(visit(ctx.atributos_valores()));
        }
        return nodo;
    }
    
    @Override
    public NodoAST visitEstructuraAnonima(CodexLatinusParser.EstructuraAnonimaContext ctx)
    {
        NodoAST nodo = new NodoAST("EstructuraAnonima");
        if (ctx.atributos_valores() != null)
        {
            nodo.agregarHijo(visit(ctx.atributos_valores()));
        }
        return nodo;
    }

    @Override
    public NodoAST visitArr_valores(CodexLatinusParser.Arr_valoresContext ctx)
    {
        NodoAST nodo = new NodoAST("ValoresArray");
        for (var valor : ctx.valorArray())
        {
            nodo.agregarHijo(visit(valor));
        }
        return nodo;
    }
    @Override
    public NodoAST visitValorInicial(CodexLatinusParser.ValorInicialContext ctx)
    {
        return visit(ctx.expresion());
    }

    @Override
    public NodoAST visitValorArray(CodexLatinusParser.ValorArrayContext ctx)
    {
        if (ctx.expresion() != null)
        {
            return visit(ctx.expresion());
        }
        else
        {
            return visit(ctx.estructuraAnonima());
        }
    }
    @Override
    public NodoAST visitAtributos_valores(CodexLatinusParser.Atributos_valoresContext ctx)
    {
        NodoAST nodo = new NodoAST("Atributos");
        for (var av : ctx.atributo_valor())
        {
            nodo.agregarHijo(visit(av));
        }
        return nodo;
    }

    @Override
    public NodoAST visitAtributo_valor(CodexLatinusParser.Atributo_valorContext ctx)
    {
        NodoAST nodo = new NodoAST("AtributoValor", ctx.ID().getText());
        nodo.agregarHijo(visit(ctx.valorAtributo()));
        return nodo;
    }
    @Override
    public NodoAST visitValorAtributo(CodexLatinusParser.ValorAtributoContext ctx)
    {
        if (ctx.expresion() != null) return visit(ctx.expresion());
        if (ctx.ID() != null) return new NodoAST("TamanoArray", ctx.NUMERO().getText());
        if (ctx.estructuraAnonima() != null) return visit(ctx.estructuraAnonima());
        if (ctx.arr_valores() != null) return visit(ctx.arr_valores());
        return new NodoAST("ValorDesconocido");
    }

    // INSTRUCCIÓN GENERICA
    @Override
    public NodoAST visitInstruccion(CodexLatinusParser.InstruccionContext ctx)
    {
        if (ctx.asignacion() != null) return visit(ctx.asignacion());
        if (ctx.incremento() != null)
        {
            NodoAST nodo = new NodoAST("InstruccionIncremento");
            nodo.agregarHijo(visit(ctx.incremento()));
            return nodo;
        }
        if (ctx.expresion() != null)
        {
            NodoAST nodo = new NodoAST("InstruccionExpresion");
            nodo.agregarHijo(visit(ctx.expresion()));
            return nodo;
        }
        if (ctx.condicional() != null) return visit(ctx.condicional());
        if (ctx.bucle() != null) return visit(ctx.bucle());
        if (ctx.interrupcion() != null) return visit(ctx.interrupcion());
        if (ctx.retorno() != null) return visit(ctx.retorno());
        if (ctx.impresion() != null) return visit(ctx.impresion());
        if (ctx.lectura() != null) return visit(ctx.lectura());
        return new NodoAST("InstruccionDesconocida");
    }

    // ASIGNACIONES
    @Override
    public NodoAST visitAsigVariable(CodexLatinusParser.AsigVariableContext ctx)
    {
        NodoAST nodo = new NodoAST("Asignacion");
        nodo.agregarHijo(new NodoAST("ID", ctx.ID().getText()));
        nodo.agregarHijo(visit(ctx.expresion()));
        return nodo;
    }
    @Override
    public NodoAST visitAsigArreglo(CodexLatinusParser.AsigArregloContext ctx)
    {
        NodoAST nodo = new NodoAST("AsignacionArray");
        nodo.agregarHijo(new NodoAST("ID", ctx.ID().getText()));
        nodo.agregarHijo(visit(ctx.expresion(0)));  // indice
        nodo.agregarHijo(visit(ctx.expresion(1)));  // valor
        return nodo;
    }

    @Override
    public NodoAST visitAsigAtributo(CodexLatinusParser.AsigAtributoContext ctx)
    {
        NodoAST nodo = new NodoAST("AsignacionAtributo");
        nodo.agregarHijo(new NodoAST("Objeto", ctx.ID(0).getText()));
        nodo.agregarHijo(new NodoAST("Atributo", ctx.ID(1).getText()));
        nodo.agregarHijo(visit(ctx.expresion()));
        return nodo;
    }

    @Override
    public NodoAST visitAsigAtributoArray(CodexLatinusParser.AsigAtributoArrayContext ctx)
    {
        NodoAST nodo = new NodoAST("AsignacionAtributoArray");
        nodo.agregarHijo(new NodoAST("Objeto", ctx.ID(0).getText()));
        nodo.agregarHijo(new NodoAST("Atributo", ctx.ID(1).getText()));
        nodo.agregarHijo(visit(ctx.expresion(0)));  // indice
        nodo.agregarHijo(visit(ctx.expresion(1)));  // valor
        return nodo;
    }
    @Override
    public NodoAST visitAsigArrayEstructura(CodexLatinusParser.AsigArrayEstructuraContext ctx)
    {
        NodoAST nodo = new NodoAST("AsignacionArrayEstructura");
        nodo.agregarHijo(new NodoAST("ID", ctx.ID().getText()));
        nodo.agregarHijo(visit(ctx.expresion()));  // indice
        nodo.agregarHijo(visit(ctx.estructuraAnonima()));
        return nodo;
    }

    // INCREMENTO/DECREMENTO 
    @Override
    public NodoAST visitIncrementoVariable(CodexLatinusParser.IncrementoVariableContext ctx)
    {
        return new NodoAST((ctx.MAS_ABREVIADO() != null) ? "Incremento" : "Decremento", ctx.ID().getText());
    }
    @Override
    public NodoAST visitIncrementoArray(CodexLatinusParser.IncrementoArrayContext ctx)
    {
        NodoAST nodo = new NodoAST((ctx.MAS_ABREVIADO() != null) ? "IncrementoArray" : "DecrementoArray");
        nodo.agregarHijo(new NodoAST("ID", ctx.ID().getText()));
        nodo.agregarHijo(visit(ctx.expresion()));
        return nodo;
    }
    @Override
    public NodoAST visitIncrementoAtributo(CodexLatinusParser.IncrementoAtributoContext ctx)
    {
        NodoAST nodo = new NodoAST((ctx.MAS_ABREVIADO() != null) ? "IncrementoAtributo" : "DecrementoAtributo");
        nodo.agregarHijo(new NodoAST("Objeto", ctx.ID(0).getText()));
        nodo.agregarHijo(new NodoAST("Atributo", ctx.ID(1).getText()));
        return nodo;
    }

    @Override
    public NodoAST visitRetorno(CodexLatinusParser.RetornoContext ctx)
    {
        NodoAST nodo = new NodoAST("Retorno");
        if (ctx.expresion() != null)
        {
            nodo.agregarHijo(visit(ctx.expresion()));
        }
        return nodo;
    }
    @Override
    public NodoAST visitImpresion(CodexLatinusParser.ImpresionContext ctx)
    {
        NodoAST nodo = new NodoAST("Impresion");
        for (var expr : ctx.expresion())
        {
            nodo.agregarHijo(visit(expr));
        }
        return nodo;
    }

    @Override
    public NodoAST visitLectura(CodexLatinusParser.LecturaContext ctx)
    {
        NodoAST nodo = new NodoAST("Lectura");
        if (ctx.ID() != null)
        {
            nodo.agregarHijo(new NodoAST("ID", ctx.ID().getText()));
        }
        return nodo;
    }

    // CONDICIONALES Y BUCLES 
    @Override
    public NodoAST visitCondicional(CodexLatinusParser.CondicionalContext ctx)
    {
        NodoAST nodo = new NodoAST("Condicional");
        int numCondiciones = ctx.expresion().size();
        int numBloques = ctx.bloque().size();
        for (int i = 0; i < numCondiciones; i++)
        {
            NodoAST rama = new NodoAST((i == 0) ? "Si" : "AliterSi");
            rama.agregarHijo(visit(ctx.expresion(i)));
            rama.agregarHijo(visit(ctx.bloque(i)));
            nodo.agregarHijo(rama);
        }
        if (numBloques > numCondiciones)
        {
            NodoAST sino = new NodoAST("Aliter");
            sino.agregarHijo(visit(ctx.bloque(numBloques - 1)));
            nodo.agregarHijo(sino);
        }
        return nodo;
    }

    @Override
    public NodoAST visitBloque(CodexLatinusParser.BloqueContext ctx)
    {
        NodoAST nodo = new NodoAST("Bloque");
        for (var instr : ctx.instruccion())
        {
            nodo.agregarHijo(visit(instr));
        }
        return nodo;
    }
    @Override
    public NodoAST visitBucleDum(CodexLatinusParser.BucleDumContext ctx)
    {
        NodoAST nodo = new NodoAST("BucleDum");
        nodo.agregarHijo(visit(ctx.expresion()));
        nodo.agregarHijo(visit(ctx.bloque()));
        return nodo;
    }

    @Override
    public NodoAST visitBucleFacere(CodexLatinusParser.BucleFacereContext ctx)
    {
        NodoAST nodo = new NodoAST("BucleFacere");
        nodo.agregarHijo(visit(ctx.bloque()));
        nodo.agregarHijo(visit(ctx.expresion()));
        return nodo;
    }
    @Override
    public NodoAST visitBuclePer(CodexLatinusParser.BuclePerContext ctx)
    {
        NodoAST nodo = new NodoAST("BuclePer");
        if (ctx.declaracion() != null) nodo.agregarHijo(visit(ctx.declaracion()));
        if (ctx.expresion() != null) nodo.agregarHijo(visit(ctx.expresion()));
        if (ctx.actualizacion() != null) nodo.agregarHijo(visit(ctx.actualizacion()));
        nodo.agregarHijo(visit(ctx.bloque()));
        return nodo;
    }

    @Override
    public NodoAST visitActualizacion(CodexLatinusParser.ActualizacionContext ctx)
    {
        if (ctx.asignacion() != null) return visit(ctx.asignacion());
        return new NodoAST((ctx.MAS_ABREVIADO() != null) ? "Incremento" : "Decremento", ctx.ID().getText());
    }
    @Override
    public NodoAST visitInterrupcion(CodexLatinusParser.InterrupcionContext ctx)
    {
        return new NodoAST("Interrupcion", ctx.getChild(0).getText());
    }

    // EXPRESIONES
    @Override
    public NodoAST visitSumaResta(CodexLatinusParser.SumaRestaContext ctx)
    {
        NodoAST nodo = new NodoAST(ctx.MAS() != null ? "+" : "-");
        nodo.agregarHijo(visit(ctx.expresion(0)));
        nodo.agregarHijo(visit(ctx.expresion(1)));
        return nodo;
    }
    @Override
    public NodoAST visitComparacion(CodexLatinusParser.ComparacionContext ctx)
    {
        NodoAST nodo = new NodoAST(ctx.getChild(1).getText());
        nodo.agregarHijo(visit(ctx.expresion(0)));
        nodo.agregarHijo(visit(ctx.expresion(1)));
        return nodo;
    }
    @Override
    public NodoAST visitIgualdad(CodexLatinusParser.IgualdadContext ctx)
    {
        NodoAST nodo = new NodoAST(ctx.IGUALIGUAL() != null ? "==" : "!=");
        nodo.agregarHijo(visit(ctx.expresion(0)));
        nodo.agregarHijo(visit(ctx.expresion(1)));
        return nodo;
    }

    @Override
    public NodoAST visitAndLogico(CodexLatinusParser.AndLogicoContext ctx)
    {
        NodoAST nodo = new NodoAST("&&");
        nodo.agregarHijo(visit(ctx.expresion(0)));
        nodo.agregarHijo(visit(ctx.expresion(1)));
        return nodo;
    }

    @Override
    public NodoAST visitOrLogico(CodexLatinusParser.OrLogicoContext ctx)
    {
        NodoAST nodo = new NodoAST("||");
        nodo.agregarHijo(visit(ctx.expresion(0)));
        nodo.agregarHijo(visit(ctx.expresion(1)));
        return nodo;
    }
    @Override
    public NodoAST visitMultDiv(CodexLatinusParser.MultDivContext ctx)
    {
        String operador = ctx.POR() != null ? "*" : (ctx.DIVISION() != null ? "/" : "%");
        NodoAST nodo = new NodoAST(operador);
        nodo.agregarHijo(visit(ctx.expresion(0)));
        nodo.agregarHijo(visit(ctx.expresion(1)));
        return nodo;
    }

    @Override
    public NodoAST visitToFactor(CodexLatinusParser.ToFactorContext ctx)
    {
        return visit(ctx.factor());
    }

    // FACTORES
    @Override
    public NodoAST visitNegacionUnaria(CodexLatinusParser.NegacionUnariaContext ctx)
    {
        NodoAST nodo = new NodoAST("NegacionUnaria");
        nodo.agregarHijo(visit(ctx.factor()));
        return nodo;
    }
    @Override
    public NodoAST visitNumLiteral(CodexLatinusParser.NumLiteralContext ctx)
    {
        return new NodoAST("Numero", ctx.NUMERO().getText());
    }
    @Override
    public NodoAST visitDecLiteral(CodexLatinusParser.DecLiteralContext ctx)
    {
        return new NodoAST("Decimal", ctx.DECIMALES().getText());
    }
    @Override
    public NodoAST visitTextLiteral(CodexLatinusParser.TextLiteralContext ctx)
    {
        return new NodoAST("Texto", ctx.TEXTO().getText());
    }
    @Override
    public NodoAST visitCharLiteral(CodexLatinusParser.CharLiteralContext ctx)
    {
        return new NodoAST("Caracter", ctx.CARACTER().getText());
    }

    @Override
    public NodoAST visitTrueLiteral(CodexLatinusParser.TrueLiteralContext ctx)
    {
        return new NodoAST("Booleano", "verum");
    }
    @Override
    public NodoAST visitFalseLiteral(CodexLatinusParser.FalseLiteralContext ctx)
    {
        return new NodoAST("Booleano", "falsus");
    }

    @Override
    public NodoAST visitNegacion(CodexLatinusParser.NegacionContext ctx)
    {
        NodoAST nodo = new NodoAST("Negacion");
        nodo.agregarHijo(visit(ctx.factor()));
        return nodo;
    }
    @Override
    public NodoAST visitVariable(CodexLatinusParser.VariableContext ctx)
    {
        return new NodoAST("Variable", ctx.ID().getText());
    }
    @Override
    public NodoAST visitAccesoArray(CodexLatinusParser.AccesoArrayContext ctx)
    {
        NodoAST nodo = new NodoAST("AccesoArray");
        nodo.agregarHijo(new NodoAST("ID", ctx.ID().getText()));
        nodo.agregarHijo(visit(ctx.expresion()));
        return nodo;
    }
    @Override
    public NodoAST visitLlamadaFuncion(CodexLatinusParser.LlamadaFuncionContext ctx)
    {
        NodoAST nodo = new NodoAST("LlamadaFuncion", ctx.ID().getText());
        if (ctx.argumentos() != null)
        {
            nodo.agregarHijo(visit(ctx.argumentos()));
        }
        return nodo;
    }

    @Override
    public NodoAST visitParentesis(CodexLatinusParser.ParentesisContext ctx)
    {
        NodoAST nodo = new NodoAST("Parentesis");
        nodo.agregarHijo(visit(ctx.expresion()));
        return nodo;
    }
    @Override
    public NodoAST visitAccesoAtributo(CodexLatinusParser.AccesoAtributoContext ctx)
    {
        NodoAST nodo = new NodoAST("AccesoAtributo");
        nodo.agregarHijo(visit(ctx.factor()));
        nodo.agregarHijo(new NodoAST("Atributo", ctx.ID().getText()));
        return nodo;
    }

    @Override
    public NodoAST visitAccesoAtributoArray(CodexLatinusParser.AccesoAtributoArrayContext ctx)
    {
        NodoAST nodo = new NodoAST("AccesoAtributoArray");
        nodo.agregarHijo(visit(ctx.factor()));
        nodo.agregarHijo(new NodoAST("Atributo", ctx.ID().getText()));
        nodo.agregarHijo(visit(ctx.expresion()));
        return nodo;
    }
    @Override
    public NodoAST visitArgumentos(CodexLatinusParser.ArgumentosContext ctx)
    {
        NodoAST nodo = new NodoAST("Argumentos");
        for (var expr : ctx.expresion())
        {
            nodo.agregarHijo(visit(expr));
        }
        return nodo;
    }

    // TIPOS
    @Override
    public NodoAST visitTipo(CodexLatinusParser.TipoContext ctx)
    {
        if (ctx.SERIES() != null)
        {
            NodoAST nodo = new NodoAST("TipoArray");
            nodo.agregarHijo(new NodoAST("TipoBase", ctx.tipoSimple().getText()));
            return nodo;
        } else
        {
            return new NodoAST("Tipo", ctx.tipoSimple().getText());
        }
    }
    @Override
    public NodoAST visitTipoSimple(CodexLatinusParser.TipoSimpleContext ctx)
    {
        return new NodoAST("Tipo", ctx.getText());
    }
}