package codexlatinus.compiler.visualizacion;

import codexlatinus.CodexLatinusBaseListener;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.ErrorNode;
import java.util.*;

public class ParseTraceListener extends CodexLatinusBaseListener
{
    private final List<String> pilaReglas = new ArrayList<>();
    private final List<String> log = new ArrayList<>();
    private final List<PasoPila> pasos = new ArrayList<>();

    @Override
    public void enterEveryRule(ParserRuleContext ctx)
    {
        String nombreRegla = ctx.getClass().getSimpleName().replace("Context", "");
        pilaReglas.add(nombreRegla);
        log.add("push " + nombreRegla);
        guardarPaso();
    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx)
    {
        String nombreRegla = ctx.getClass().getSimpleName().replace("Context", "");
        if (!pilaReglas.isEmpty())
        {
            pilaReglas.remove(pilaReglas.size() - 1);
        }
        log.add("pop " + nombreRegla);
        guardarPaso();
    }

    @Override
    public void visitTerminal(TerminalNode nodo)
    {
        log.add("shift: " + nodo.getText());
        guardarPaso();
    }

    @Override
    public void visitErrorNode(ErrorNode nodo)
    {
        log.add("error: " + nodo.getText());
        guardarPaso();
    }
    private void guardarPaso()
    {
        pasos.add(new PasoPila(new ArrayList<>(pilaReglas), log));
    }

    public List<PasoPila> getPasos()
    {
        return pasos;
    }
}