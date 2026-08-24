package codexlatinus.compiler.visualizacion;

import codexlatinus.compiler.symbol.Ambito;
import codexlatinus.compiler.symbol.Simbolo;
import codexlatinus.compiler.symbol.TablaSimbolos;

public class GeneradorSimbolosDOT
{
    public String generarDOT(TablaSimbolos tabla)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph TablaSimbolos {\n");
        sb.append("  rankdir=TB;\n");
        sb.append("  node [shape=none];\n");
        for (Ambito ambito : tabla.getTodosLosAmbitos())
        {
            String nombreAmbito = ambito.getNombre();
            sb.append(String.format("  \"%s\" [label=<<TABLE BORDER=\"1\" CELLBORDER=\"1\" CELLSPACING=\"0\">\n", nombreAmbito));
            sb.append(String.format("    <TR><TD COLSPAN=\"3\" BGCOLOR=\"#D3D3D3\"><B>Ámbito: %s</B></TD></TR>\n", nombreAmbito));
            sb.append("    <TR><TD><B>ID</B></TD><TD><B>Tipo</B></TD><TD><B>Valor/Referencia</B></TD></TR>\n");
            for (Simbolo simbolo : ambito.getSimbolos().values())
            {
                String valorStr = simbolo.getValor() != null ? simbolo.getValor().toString() : "Sin inicializar";
                valorStr = valorStr.replace("<", "&lt;").replace(">", "&gt;");
                sb.append(String.format("    <TR><TD>%s</TD><TD>%s</TD><TD>%s</TD></TR>\n", simbolo.getId(), simbolo.getTipo().toString(), valorStr));
            }
            sb.append("  </TABLE>>];\n");
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