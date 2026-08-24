package codexlatinus.compiler.visualizacion;

import codexlatinus.compiler.ast.NodoAST;

public class GeneradorASTDOT
{
    private int nodoId = 0;

    public String generarDOT(NodoAST arbol)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph AST {\n");
        sb.append("  rankdir=TB;\n");
        sb.append("  node [shape=box, style=rounded, fontname=\"Helvetica\"];\n");
        if (arbol != null)
        {
            recorrer(arbol, null, sb);
        }
        sb.append("}\n");
        return sb.toString();
    }

    private void recorrer(NodoAST nodo, String padreId, StringBuilder sb)
    {
        if (nodo == null) return;
        int actual = nodoId++;
        String etiqueta = nodo.getValor() != null ? nodo.getEtiqueta() + "\\n" + escapar(nodo.getValor()) : escapar(nodo.getEtiqueta());
        sb.append(String.format("  nodo%d [label=\"%s\"];\n", actual, etiqueta));
        if (padreId != null)
        {
            sb.append(String.format("  %s -> nodo%d;\n", padreId, actual));
        }
        for (NodoAST hijo : nodo.getHijos())
        {
            recorrer(hijo, "nodo" + actual, sb);
        }
    }

    private String escapar(String texto)
    {
        if (texto == null) return "";
        return texto .replace("\\", "\\\\") .replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}