package codexlatinus.compiler.translation;

import codexlatinus.CodexLatinusBaseVisitor;
import codexlatinus.CodexLatinusParser;
import org.antlr.v4.runtime.tree.TerminalNode;

public class PigLatinTraductor extends CodexLatinusBaseVisitor<String>
{
    private String traducirPalabra(String palabra)
    {
        if (palabra.isEmpty()) return palabra;
        if (!palabra.matches("[a-zA-Z_][a-zA-Z0-9_]*"))
        {
            return palabra;
        }
        char primero = palabra.charAt(0);
        if (esVocal(primero))
        {
            return palabra + "way";
        }
        else
        {
            int indice = 0;
            while (indice < palabra.length() && !esVocal(palabra.charAt(indice)))
            {
                indice++;
            }
            if (indice == 0) return palabra + "way";
            return palabra.substring(indice) + palabra.substring(0, indice) + "ay";
        }
    }

    private boolean esVocal(char c)
    {
        return "aeiouAEIOU".indexOf(c) != -1;
    }

    // SECCIONES
    @Override
    public String visitPrograma(CodexLatinusParser.ProgramaContext ctx)
    {
        StringBuilder sb = new StringBuilder();
        if (ctx.seccionDeclaraciones() != null) sb.append(visit(ctx.seccionDeclaraciones()));
        if (ctx.seccionFunciones() != null) sb.append(visit(ctx.seccionFunciones()));
        if (ctx.seccionCodigo() != null) sb.append(visit(ctx.seccionCodigo()));
        sb.append(traducirPalabra("FINIS")).append(";\n");
        return sb.toString();
    }

    @Override
    public String visitSeccionDeclaraciones(CodexLatinusParser.SeccionDeclaracionesContext ctx)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(traducirPalabra("VARIABILES")).append(">\n");
        for (var decl : ctx.declaracion()) sb.append(visit(decl)).append("\n");
        for (var def : ctx.definicionEstructura()) sb.append(visit(def)).append("\n");
        return sb.toString();
    }

    @Override
    public String visitSeccionFunciones(CodexLatinusParser.SeccionFuncionesContext ctx)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(traducirPalabra("MUNERA")).append(">\n");
        for (var func : ctx.definicionFuncion()) sb.append(visit(func)).append("\n");
        return sb.toString();
    }

    @Override
    public String visitSeccionCodigo(CodexLatinusParser.SeccionCodigoContext ctx)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(traducirPalabra("MAIOR")).append(">\n");
        for (var instr : ctx.instruccion()) sb.append(visit(instr)).append("\n");
        return sb.toString();
    }

    // ESTRUCTURAS
    @Override
    public String visitDefinicionEstructura(CodexLatinusParser.DefinicionEstructuraContext ctx) 
    {
        StringBuilder sb = new StringBuilder();
        sb.append(traducirPalabra("structura")).append(" ").append(traducirPalabra(ctx.ID().getText())).append(" {\n");
        for (var attr : ctx.atributoEstructura()) sb.append(visit(attr)).append("\n");
        sb.append("} ").append(traducirPalabra("finis")).append(";\n");
        return sb.toString();
    }

    @Override
    public String visitAtributoEstructura(CodexLatinusParser.AtributoEstructuraContext ctx)
    {
        StringBuilder sb = new StringBuilder();
        if (ctx.SERIES() != null) sb.append(traducirPalabra("series")).append(" ");
        else sb.append(traducirPalabra("esto")).append(" ");
        sb.append(traducirPalabra(ctx.ID().getText())).append(" : ");
        sb.append(visit(ctx.tipo())).append(" ");
        // Separador ; o ,
        if (ctx.PUNTOYCOMA() != null) sb.append(";");
        else sb.append(",");
        return sb.toString();
    }

    // FUNCIONES
    @Override
    public String visitDefinicionFuncion(CodexLatinusParser.DefinicionFuncionContext ctx)
    {
        StringBuilder sb = new StringBuilder();
        if (ctx.ACTIO() != null)
        {
            sb.append(traducirPalabra("actio")).append(" ");
        }
        else
        {
            sb.append(traducirPalabra("ratio")).append(" ").append(visit(ctx.tipo())).append(" ");
        }
        sb.append(traducirPalabra(ctx.ID().getText())).append("(");
        if (ctx.parametros() != null) sb.append(visit(ctx.parametros()));
        sb.append(") {\n");
        if (ctx.seccionVariables() != null) sb.append(visit(ctx.seccionVariables())).append("\n");
        for (var instr : ctx.instruccion()) sb.append(visit(instr)).append("\n");
        sb.append("} ").append(traducirPalabra("finis")).append(";\n");
        return sb.toString();
    }

    @Override
    public String visitSeccionVariables(CodexLatinusParser.SeccionVariablesContext ctx)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(traducirPalabra("VARIABILES")).append("[\n");
        for (var decl : ctx.declaracion()) sb.append(visit(decl)).append("\n");
        for (var def : ctx.definicionEstructura()) sb.append(visit(def)).append("\n");
        sb.append("]");
        return sb.toString();
    }

    @Override
    public String visitParametros(CodexLatinusParser.ParametrosContext ctx)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ctx.parametro().size(); i++)
        {
            if (i > 0) sb.append(", ");
            sb.append(visit(ctx.parametro(i)));
        }
        return sb.toString();
    }

    @Override
    public String visitParametro(CodexLatinusParser.ParametroContext ctx)
    {
        return traducirPalabra("esto") + " " + traducirPalabra(ctx.ID().getText()) + " : " + visit(ctx.tipo());
    }

    // DECLARACIONES
    @Override
    public String visitDeclaracionVariable(CodexLatinusParser.DeclaracionVariableContext ctx)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(traducirPalabra("esto")).append(" ").append(traducirPalabra(ctx.ID().getText())).append(" : ");
        sb.append(visit(ctx.tipo()));
        if (ctx.valorInicial() != null) sb.append(" ").append(visit(ctx.valorInicial()));
        sb.append(";");
        return sb.toString();
    }

    @Override
    public String visitDeclaracionBoolSinTipo(CodexLatinusParser.DeclaracionBoolSinTipoContext ctx)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(traducirPalabra("esto")).append(" ").append(traducirPalabra(ctx.ID().getText())).append(" : ");
        if (ctx.VERUM() != null) sb.append(traducirPalabra("verum"));
        else sb.append(traducirPalabra("falsus"));
        sb.append(";");
        return sb.toString();
    }

    @Override
    public String visitDeclaracionEstructura(CodexLatinusParser.DeclaracionEstructuraContext ctx)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(traducirPalabra("esto")).append(" ").append(traducirPalabra(ctx.ID().getText())).append(" : ");
        sb.append(traducirPalabra(ctx.tipo().getText())).append(" ");
        sb.append(visit(ctx.estructuraInicial()));
        return sb.toString();
    }

    @Override
    public String visitDeclaracionArray(CodexLatinusParser.DeclaracionArrayContext ctx)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(traducirPalabra("series")).append(" ").append(traducirPalabra(ctx.ID().getText())).append("[");
        sb.append(visit(ctx.expresion())).append("] : ");
        sb.append(visit(ctx.tipo()));
        if (ctx.arr_valores() != null) sb.append(" ").append(visit(ctx.arr_valores()));
        sb.append(";");
        return sb.toString();
    }

    @Override
    public String visitDeclaracionArrayBooleano(CodexLatinusParser.DeclaracionArrayBooleanoContext ctx)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(traducirPalabra("series")).append(" ").append(traducirPalabra(ctx.ID().getText())).append("[");
        sb.append(visit(ctx.expresion())).append("] : ");
        sb.append(visit(ctx.arr_valores())).append(";");
        return sb.toString();
    }

    @Override
    public String visitEstructuraInicial(CodexLatinusParser.EstructuraInicialContext ctx)
    {
        return "{ " + visit(ctx.atributos_valores()) + " }";
    }

    @Override
    public String visitArr_valores(CodexLatinusParser.Arr_valoresContext ctx)
    {
        StringBuilder sb = new StringBuilder("{ ");
        for (int i = 0; i < ctx.valorArray().size(); i++)
        {
            if (i > 0) sb.append(", ");
            sb.append(visit(ctx.valorArray(i)));
        }
        sb.append(" }");
        return sb.toString();
    }

    @Override
    public String visitValorInicial(CodexLatinusParser.ValorInicialContext ctx)
    {
        return visit(ctx.expresion());
    }

    @Override
    public String visitValorArray(CodexLatinusParser.ValorArrayContext ctx)
    {
        if (ctx.expresion() != null) return visit(ctx.expresion());
        else return visit(ctx.estructuraAnonima());
    }

    @Override
    public String visitAtributos_valores(CodexLatinusParser.Atributos_valoresContext ctx)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ctx.atributo_valor().size(); i++)
        {
            if (i > 0) sb.append(", ");
            sb.append(visit(ctx.atributo_valor(i)));
        }
        return sb.toString();
    }

    @Override
    public String visitAtributo_valor(CodexLatinusParser.Atributo_valorContext ctx)
    {
        return traducirPalabra(ctx.ID().getText()) + " : " + visit(ctx.valorAtributo());
    }

    @Override
    public String visitValorAtributo(CodexLatinusParser.ValorAtributoContext ctx)
    {
        if (ctx.expresion() != null) return visit(ctx.expresion());
        if (ctx.ID() != null) return traducirPalabra(ctx.ID().getText()) + "[" + traducirPalabra(ctx.NUMERO().getText()) + "]";
        if (ctx.estructuraAnonima() != null) return visit(ctx.estructuraAnonima());
        return visit(ctx.arr_valores());
    }

    // INSTRUCCIONES
    @Override
    public String visitInstruccion(CodexLatinusParser.InstruccionContext ctx)
    {
        if (ctx.asignacion() != null) return visit(ctx.asignacion());
        if (ctx.incremento() != null) return visit(ctx.incremento());
        if (ctx.expresion() != null) return visit(ctx.expresion());
        if (ctx.condicional() != null) return visit(ctx.condicional());
        if (ctx.bucle() != null) return visit(ctx.bucle());
        if (ctx.interrupcion() != null) return visit(ctx.interrupcion());
        if (ctx.retorno() != null) return visit(ctx.retorno());
        if (ctx.impresion() != null) return visit(ctx.impresion());
        if (ctx.lectura() != null) return visit(ctx.lectura());
        return "";
    }
    
    @Override
    public String visitAsigVariable(CodexLatinusParser.AsigVariableContext ctx)
    {
        return traducirPalabra(ctx.ID().getText()) + " = " + visit(ctx.expresion()) + ";";
    }

    @Override
    public String visitAsigArreglo(CodexLatinusParser.AsigArregloContext ctx)
    {
        return traducirPalabra(ctx.ID().getText()) + "[" + visit(ctx.expresion(0)) + "] = " + visit(ctx.expresion(1)) + ";";
    }

    @Override
    public String visitAsigAtributo(CodexLatinusParser.AsigAtributoContext ctx)
    {
        return traducirPalabra(ctx.ID(0).getText()) + "." + traducirPalabra(ctx.ID(1).getText()) + " = " + visit(ctx.expresion()) + ";";
    }

    @Override
    public String visitAsigAtributoArray(CodexLatinusParser.AsigAtributoArrayContext ctx)
    {
        return traducirPalabra(ctx.ID(0).getText()) + "." + traducirPalabra(ctx.ID(1).getText()) + "[" + visit(ctx.expresion(0)) + "] = " + visit(ctx.expresion(1)) + ";";
    }

    @Override
    public String visitAsigArrayEstructura(CodexLatinusParser.AsigArrayEstructuraContext ctx)
    {
        return traducirPalabra(ctx.ID().getText()) + "[" + visit(ctx.expresion()) + "] = " + visit(ctx.estructuraAnonima()) + ";";
    }

    @Override
    public String visitEstructuraAnonima(CodexLatinusParser.EstructuraAnonimaContext ctx)
    {
        return "{ " + visit(ctx.atributos_valores()) + " }";
    }

    @Override
    public String visitRetorno(CodexLatinusParser.RetornoContext ctx)
    {
        StringBuilder sb = new StringBuilder(traducirPalabra("reddere"));
        if (ctx.expresion() != null) sb.append(" ").append(visit(ctx.expresion()));
        sb.append(";");
        return sb.toString();
    }

    @Override
    public String visitImpresion(CodexLatinusParser.ImpresionContext ctx)
    {
        StringBuilder sb = new StringBuilder("%OINK ");
        for (int i = 0; i < ctx.expresion().size(); i++)
        {
            if (i > 0) sb.append(" %OINK ");
            sb.append(visit(ctx.expresion(i)));
        }
        sb.append(";");
        return sb.toString();
    }

    @Override
    public String visitLectura(CodexLatinusParser.LecturaContext ctx)
    {
        if (ctx.ID() != null) return traducirPalabra(ctx.ID().getText()) + " %OINK_OINK";
        else return "%OINK_OINK";
    }

    @Override
    public String visitIncrementoVariable(CodexLatinusParser.IncrementoVariableContext ctx)
    {
        return traducirPalabra(ctx.ID().getText()) + (ctx.MAS_ABREVIADO() != null ? "++;" : "--;");
    }

    @Override
    public String visitIncrementoArray(CodexLatinusParser.IncrementoArrayContext ctx)
    {
        return traducirPalabra(ctx.ID().getText()) + "[" + visit(ctx.expresion()) + "]" + (ctx.MAS_ABREVIADO() != null ? "++;" : "--;");
    }

    @Override
    public String visitIncrementoAtributo(CodexLatinusParser.IncrementoAtributoContext ctx)
    {
        return traducirPalabra(ctx.ID(0).getText()) + "." + traducirPalabra(ctx.ID(1).getText()) + (ctx.MAS_ABREVIADO() != null ? "++;" : "--;");
    }

    // EXPRESIONES
    @Override
    public String visitSumaResta(CodexLatinusParser.SumaRestaContext ctx)
    {
        return visit(ctx.expresion()) + (ctx.MAS() != null ? " + " : " - ") + visit(ctx.termino());
    }

    @Override
    public String visitComparacion(CodexLatinusParser.ComparacionContext ctx)
    {
        return visit(ctx.expresion()) + " " + ctx.getChild(1).getText() + " " + visit(ctx.termino());
    }

    @Override
    public String visitIgualdad(CodexLatinusParser.IgualdadContext ctx)
    {
        return visit(ctx.expresion()) + (ctx.IGUALIGUAL() != null ? " == " : " != ") + visit(ctx.termino());
    }

    @Override
    public String visitAndLogico(CodexLatinusParser.AndLogicoContext ctx)
    {
        return visit(ctx.expresion()) + " && " + visit(ctx.termino());
    }

    @Override
    public String visitOrLogico(CodexLatinusParser.OrLogicoContext ctx)
    {
        return visit(ctx.expresion()) + " || " + visit(ctx.termino());
    }

    @Override
    public String visitToTermino(CodexLatinusParser.ToTerminoContext ctx)
    {
        return visit(ctx.termino());
    }

    @Override
    public String visitMultDiv(CodexLatinusParser.MultDivContext ctx)
    {
        return visit(ctx.termino()) + (ctx.POR() != null ? " * " : " / ") + visit(ctx.factor());
    }

    @Override
    public String visitToFactor(CodexLatinusParser.ToFactorContext ctx)
    {
        return visit(ctx.factor());
    }

    // FACTORES
    @Override
    public String visitNumLiteral(CodexLatinusParser.NumLiteralContext ctx)
    {
        return ctx.NUMERO().getText();
    }

    @Override
    public String visitDecLiteral(CodexLatinusParser.DecLiteralContext ctx)
    {
        return ctx.DECIMALES().getText();
    }

    @Override
    public String visitTextLiteral(CodexLatinusParser.TextLiteralContext ctx)
    {
        return ctx.TEXTO().getText();
    }

    @Override
    public String visitCharLiteral(CodexLatinusParser.CharLiteralContext ctx)
    {
        return ctx.CARACTER().getText();
    }

    @Override
    public String visitTrueLiteral(CodexLatinusParser.TrueLiteralContext ctx)
    {
        return traducirPalabra("verum");
    }

    @Override
    public String visitFalseLiteral(CodexLatinusParser.FalseLiteralContext ctx)
    {
        return traducirPalabra("falsus");
    }

    @Override
    public String visitNegacion(CodexLatinusParser.NegacionContext ctx)
    {
        return traducirPalabra("non") + " " + visit(ctx.factor());
    }

    @Override
    public String visitVariable(CodexLatinusParser.VariableContext ctx)
    {
        return traducirPalabra(ctx.ID().getText());
    }

    @Override
    public String visitAccesoArray(CodexLatinusParser.AccesoArrayContext ctx)
    {
        return traducirPalabra(ctx.ID().getText()) + "[" + visit(ctx.expresion()) + "]";
    }

    @Override
    public String visitLlamadaFuncion(CodexLatinusParser.LlamadaFuncionContext ctx)
    {
        StringBuilder sb = new StringBuilder(traducirPalabra(ctx.ID().getText())).append("(");
        if (ctx.argumentos() != null) sb.append(visit(ctx.argumentos()));
        sb.append(")");
        return sb.toString();
    }

    @Override
    public String visitParentesis(CodexLatinusParser.ParentesisContext ctx)
    {
        return "(" + visit(ctx.expresion()) + ")";
    }

    @Override
    public String visitAccesoAtributo(CodexLatinusParser.AccesoAtributoContext ctx)
    {
        return visit(ctx.factor()) + "." + traducirPalabra(ctx.ID().getText());
    }

    @Override
    public String visitAccesoAtributoArray(CodexLatinusParser.AccesoAtributoArrayContext ctx)
    {
        return visit(ctx.factor()) + "." + traducirPalabra(ctx.ID().getText()) + "[" + visit(ctx.expresion()) + "]";
    }

    // Arg
    @Override
    public String visitArgumentos(CodexLatinusParser.ArgumentosContext ctx)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ctx.expresion().size(); i++)
        {
            if (i > 0) sb.append(", ");
            sb.append(visit(ctx.expresion(i)));
        }
        return sb.toString();
    }

    // CONDICIONALES/BUCLES
    @Override
    public String visitCondicional(CodexLatinusParser.CondicionalContext ctx)
    {
        StringBuilder sb = new StringBuilder(traducirPalabra("si")).append(" (");
        sb.append(visit(ctx.expresion(0))).append(") {\n");
        sb.append(visit(ctx.bloque(0))).append("\n}");
        int numCondiciones = ctx.expresion().size();
        int numBloques = ctx.bloque().size();
        for (int i = 1; i < numCondiciones; i++)
        {
            sb.append(" ").append(traducirPalabra("aliter")).append(" (");
            sb.append(visit(ctx.expresion(i))).append(") {\n");
            sb.append(visit(ctx.bloque(i))).append("\n}");
        }
        if (numBloques > numCondiciones)
        {
            int indiceElse = numBloques - 1;
            sb.append(" ").append(traducirPalabra("aliter")).append(" {\n");
            sb.append(visit(ctx.bloque(indiceElse))).append("\n}");
        }
        sb.append(" ").append(traducirPalabra("finis")).append(";\n");
        return sb.toString();
    }

    @Override
    public String visitBloque(CodexLatinusParser.BloqueContext ctx)
    {
        StringBuilder sb = new StringBuilder();
        for (var instr : ctx.instruccion())
        {
            sb.append(visit(instr)).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String visitBucleDum(CodexLatinusParser.BucleDumContext ctx)
    {
        return traducirPalabra("dum") + " (" + visit(ctx.expresion()) + ") {\n" + visit(ctx.bloque()) + "\n} " + traducirPalabra("finis") + ";";
    }

    @Override
    public String visitBucleFacere(CodexLatinusParser.BucleFacereContext ctx)
    {
        return traducirPalabra("facere") + " {\n" + visit(ctx.bloque()) + "\n} " + traducirPalabra("dum") + " (" + visit(ctx.expresion()) + ");";
    }

    @Override
    public String visitBuclePer(CodexLatinusParser.BuclePerContext ctx)
    {
        StringBuilder sb = new StringBuilder(traducirPalabra("per")).append(" (");
        sb.append(visit(ctx.declaracion())).append(" ");
        sb.append(visit(ctx.expresion())).append("; ");
        if (ctx.actualizacion() != null) sb.append(visit(ctx.actualizacion()));
        sb.append(") {\n").append(visit(ctx.bloque())).append("\n}");
        return sb.toString();
    }

    @Override
    public String visitActualizacion(CodexLatinusParser.ActualizacionContext ctx)
    {
        if (ctx.asignacion() != null) return visit(ctx.asignacion());
        else return traducirPalabra(ctx.ID().getText()) + (ctx.MAS_ABREVIADO() != null ? "++" : "--");
    }

    @Override
    public String visitInterrupcion(CodexLatinusParser.InterrupcionContext ctx)
    {
        return traducirPalabra(ctx.getChild(0).getText()) + ";";
    }

    // TIPOS
    @Override
    public String visitTipo(CodexLatinusParser.TipoContext ctx)
    {
        if (ctx.SERIES() != null) return traducirPalabra("series") + " " + visit(ctx.tipoSimple());
        else return visit(ctx.tipoSimple());
    }

    @Override
    public String visitTipoSimple(CodexLatinusParser.TipoSimpleContext ctx)
    {
        return traducirPalabra(ctx.getText());
    }
}