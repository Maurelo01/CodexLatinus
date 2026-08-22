package codexlatinus.compiler.visualizacion;

import org.antlr.v4.runtime.tree.ParseTree;

public class GeneradorASTDOT
{
    private int nodoId = 0;
    public String generarDOT(ParseTree arbol)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph AST {\n");
        sb.append("  rankdir=TB;\n");
        sb.append("  node [shape=box, style=rounded];\n");
        recorrer(arbol, null, sb);
        sb.append("}\n");
        return sb.toString();
    }

    private void recorrer(ParseTree nodo, String padreId, StringBuilder sb)
    {
        int actual = nodoId++;
        String etiqueta = nodo.getText().replace("\\", "\\\\").replace("\"", "\\\"");
        sb.append(String.format("  nodo%d [label=\"%s\"];\n", actual, etiqueta));
        if (padreId != null)
        {
            sb.append(String.format("  %s -> nodo%d;\n", padreId, actual));
        }
        for (int i = 0; i < nodo.getChildCount(); i++)
        {
            recorrer(nodo.getChild(i), "nodo" + actual, sb);
        }
    }
}