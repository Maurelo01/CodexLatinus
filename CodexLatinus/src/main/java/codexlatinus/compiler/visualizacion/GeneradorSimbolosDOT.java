package codexlatinus.compiler.visualizacion;

import codexlatinus.compiler.symbol.Ambito;
import codexlatinus.compiler.symbol.TablaSimbolos;

public class GeneradorSimbolosDOT
{
    public String generarDOT(TablaSimbolos tabla)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph TablaSimbolos {\n");
        sb.append("  rankdir=TB;\n");
        sb.append("  node [shape=box];\n");
        for (Ambito ambito : tabla.getTodosLosAmbitos())
        {
            String nombre = ambito.getNombre();
            int numSimbolos = ambito.getSimbolos().size();
            sb.append(String.format("  \"%s\" [label=\"Ámbito: %s\\nSímbolos: %d\"];\n",
                    nombre, nombre, numSimbolos));
        }
        for (Ambito ambito : tabla.getTodosLosAmbitos())
        {
            for (Ambito hijo : ambito.getHijos())
            {
                sb.append(String.format("  \"%s\" -> \"%s\";\n", ambito.getNombre(), hijo.getNombre()));
            }
        }
        sb.append("}\n");
        return sb.toString();
    }
}