package codexlatinus.compiler.visualizacion;

import codexlatinus.CodexLatinusBaseListener;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.ErrorNode;
import java.util.*;

public class ParseTraceListener extends CodexLatinusBaseListener
{
    private final List<String> pilaReglas = new ArrayList<>();
    private final List<PasoPila> pasos = new ArrayList<>();

    @Override
    public void enterEveryRule(ParserRuleContext ctx)
    {
        String nombreRegla = ctx.getClass().getSimpleName().replace("Context", "");
        pilaReglas.add(nombreRegla);
        pasos.add(new PasoPila(pilaReglas, "push " + nombreRegla));
    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx)
    {
        String nombreRegla = ctx.getClass().getSimpleName().replace("Context", "");
        if (!pilaReglas.isEmpty())
        {
            pilaReglas.remove(pilaReglas.size() - 1);
        }
        pasos.add(new PasoPila(pilaReglas, "pop " + nombreRegla));
    }
    @Override
    public void visitTerminal(TerminalNode nodo)
    {
        pasos.add(new PasoPila(pilaReglas, "shift: " + nodo.getText()));
    }
    @Override
    public void visitErrorNode(ErrorNode nodo)
    {
        pasos.add(new PasoPila(pilaReglas, "error: " + nodo.getText()));
    }

    public List<PasoPila> getPasos()
    {
        return pasos;
    }
}