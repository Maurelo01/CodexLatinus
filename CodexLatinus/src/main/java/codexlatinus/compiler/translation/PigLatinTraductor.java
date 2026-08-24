package codexlatinus.compiler.translation;

import codexlatinus.compiler.ast.NodoAST;

public class PigLatinTraductor
{
    private String traducirPalabra(String palabra)
   
    {
        if (palabra == null || palabra.isEmpty()) return palabra;
        if (!palabra.matches("[a-zA-Z_][a-zA-Z0-9_]*")) return palabra;
        char primero = palabra.charAt(0);
        if ("aeiouAEIOU".indexOf(primero) != -1)
        {
            return palabra + "way";
        }
        else
        {
            int indice = 0;
            while (indice < palabra.length() && "aeiouAEIOU".indexOf(palabra.charAt(indice)) == -1)
            {
                indice++;
            }
            if (indice == 0) return palabra + "way";
            return palabra.substring(indice) + palabra.substring(0, indice) + "ay";
        }
    }

    private String traducirTipo(NodoAST nodoTipo)
    {
        if (nodoTipo == null) return "";
        if (nodoTipo.getEtiqueta().equals("TipoArray"))
        {
            NodoAST tipoBase = nodoTipo.getHijo(0);
            return traducirPalabra("series") + " " + traducirPalabra(tipoBase.getValor());
        }
        else
        {
            return traducirPalabra(nodoTipo.getValor());
        }
    }

    public String traducir(NodoAST nodo)
    {
        if (nodo == null) return "";
        StringBuilder sb = new StringBuilder();
        switch (nodo.getEtiqueta())
        {
            // SECCIONES Y PROGRAMA
            case "Programa":
                for (NodoAST hijo : nodo.getHijos())
                {
                    sb.append(traducir(hijo));
                }
                sb.append(traducirPalabra("FINIS")).append(";\n");
                break;
            case "DeclaracionesGlobales":
                sb.append(traducirPalabra("VARIABILES")).append(">\n");
                for (NodoAST hijo : nodo.getHijos())
                {
                    sb.append(traducir(hijo)).append("\n");
                }
                break;
            case "Funciones":
                sb.append(traducirPalabra("MUNERA")).append(">\n");
                for (NodoAST hijo : nodo.getHijos())
                {
                    sb.append(traducir(hijo)).append("\n");
                }
                break;
            case "Main":
                sb.append(traducirPalabra("MAIOR")).append(">\n");
                for (NodoAST hijo : nodo.getHijos())
                {
                    sb.append(traducir(hijo)).append("\n");
                }
                break;
            // ESTRUCTURAS
            case "Estructura":
                sb.append(traducirPalabra("structura")).append(" ").append(traducirPalabra(nodo.getValor())).append(" {\n");
                for (NodoAST hijo : nodo.getHijos())
                {
                    sb.append(traducir(hijo)).append("\n");
                }
                sb.append("} ").append(traducirPalabra("finis")).append(";\n");
                break;
            case "Atributo":
                // Puede tener hijo EsArreglo para series
                boolean esArreglo = false;
                for (NodoAST hijo : nodo.getHijos())
                {
                    if (hijo.getEtiqueta().equals("EsArreglo"))
                    {
                        esArreglo = true;
                        break;
                    }
                }
                sb.append(traducirPalabra(esArreglo ? "series" : "esto")).append(" ").append(traducirPalabra(nodo.getValor())).append(" : ");
                // El hijo Tipo contiene el tipo
                for (NodoAST hijo : nodo.getHijos())
                {
                    if (hijo.getEtiqueta().equals("Tipo"))
                    {
                        sb.append(traducirPalabra(hijo.getValor()));
                        break;
                    }
                }
                sb.append(";");
                break;
            // FUNCIONES
            case "Funcion":
                String tipoFuncion = nodo.getHijo(0).getValor(); // actio o ratio
                if (tipoFuncion.equals("actio"))
                {
                    sb.append(traducirPalabra("actio")).append(" ");
                }
                else
                {
                    sb.append(traducirPalabra("ratio")).append(" ");
                    sb.append(traducirTipo(nodo.getHijo(1))).append(" ");
                }
                sb.append(traducirPalabra(nodo.getValor())).append("(");
                if (nodo.getHijos().size() > 2 && nodo.getHijo(2).getEtiqueta().equals("Parametros"))
                {
                    sb.append(traducir(nodo.getHijo(2)));
                }
                sb.append("){\n");
                // Variables locales o instrucciones
                for (int i = 2; i < nodo.getHijos().size(); i++)
                {
                    NodoAST hijo = nodo.getHijo(i);
                    if (hijo.getEtiqueta().equals("Parametros")) continue; // ya procesado
                    sb.append(traducir(hijo)).append("\n");
                }
                sb.append("} ").append(traducirPalabra("finis")).append(";\n");
                break;
            case "Parametros":
                for (int i = 0; i < nodo.getHijos().size(); i++)
                {
                    if (i > 0) sb.append(", ");
                    sb.append(traducir(nodo.getHijo(i)));
                }
                break;
            case "Parametro":
                sb.append(traducirPalabra("esto")).append(" ")
                   .append(traducirPalabra(nodo.getValor())).append(" : ")
                   .append(traducirTipo(nodo.getHijo(0)));
                break;
            case "VariablesLocales":
                sb.append(traducirPalabra("VARIABILES")).append("[\n");
                for (NodoAST hijo : nodo.getHijos())
                {
                    sb.append(traducir(hijo)).append("\n");
                }
                sb.append("]");
                break;

            // DECLARACIONES
            case "DeclaracionVariable":
                sb.append(traducirPalabra("esto")).append(" ")
                   .append(traducirPalabra(nodo.getHijo(0).getValor())).append(" : ")
                   .append(traducirTipo(nodo.getHijo(1)));
                if (nodo.getHijos().size() > 2)
                {
                    sb.append(" ").append(traducir(nodo.getHijo(2)));
                }
                sb.append(";");
                break;
            case "DeclaracionBooleana":
                sb.append(traducirPalabra("esto")).append(" ")
                   .append(traducirPalabra(nodo.getHijo(0).getValor())).append(" : ")
                   .append(traducirPalabra(nodo.getHijo(1).getValor())).append(";");
                break;
            case "DeclaracionEstructura":
                sb.append(traducirPalabra("esto")).append(" ")
                   .append(traducirPalabra(nodo.getHijo(0).getValor())).append(" : ")
                   .append(traducirPalabra(nodo.getHijo(1).getValor())).append(" ")
                   .append(traducir(nodo.getHijo(2)));
                break;
            case "DeclaracionArray":
                sb.append(traducirPalabra("series")).append(" ")
                   .append(traducirPalabra(nodo.getHijo(0).getValor())).append("[")
                   .append(traducir(nodo.getHijo(1))).append("] : ")
                   .append(traducirTipo(nodo.getHijo(2)));
                if (nodo.getHijos().size() > 3)
                {
                    sb.append(" ").append(traducir(nodo.getHijo(3)));
                }
                sb.append(";");
                break;
            case "DeclaracionArrayBooleano":
                sb.append(traducirPalabra("series")).append(" ")
                   .append(traducirPalabra(nodo.getHijo(0).getValor())).append("[")
                   .append(traducir(nodo.getHijo(1))).append("] : ")
                   .append(traducir(nodo.getHijo(2))).append(";");
                break;
            case "InicializacionEstructura":
                sb.append("{ ");
                if (nodo.getHijos().size() > 0)
                {
                    sb.append(traducir(nodo.getHijo(0)));
                }
                sb.append(" }");
                break;
            case "ValoresArray":
                sb.append("{ ");
                for (int i = 0; i < nodo.getHijos().size(); i++)
                {
                    if (i > 0) sb.append(", ");
                    sb.append(traducir(nodo.getHijo(i)));
                }
                sb.append(" }");
                break;
            case "Atributos":
                for (int i = 0; i < nodo.getHijos().size(); i++)
                {
                    if (i > 0) sb.append(", ");
                    sb.append(traducir(nodo.getHijo(i)));
                }
                break;
            case "AtributoValor":
                sb.append(traducirPalabra(nodo.getValor())).append(" : ")
                   .append(traducir(nodo.getHijo(0)));
                break;
            case "TamanoArray":
                sb.append(nodo.getValor());
                break;

            // INSTRUCCIONES
            case "Asignacion":
                sb.append(traducirPalabra(nodo.getHijo(0).getValor())).append(" = ")
                   .append(traducir(nodo.getHijo(1))).append(";");
                break;
            case "AsignacionArray":
                sb.append(traducirPalabra(nodo.getHijo(0).getValor())).append("[")
                   .append(traducir(nodo.getHijo(1))).append("] = ")
                   .append(traducir(nodo.getHijo(2))).append(";");
                break;
            case "AsignacionAtributo":
                sb.append(traducirPalabra(nodo.getHijo(0).getValor())).append(".")
                   .append(traducirPalabra(nodo.getHijo(1).getValor())).append(" = ")
                   .append(traducir(nodo.getHijo(2))).append(";");
                break;
            case "AsignacionAtributoArray":
                sb.append(traducirPalabra(nodo.getHijo(0).getValor())).append(".")
                   .append(traducirPalabra(nodo.getHijo(1).getValor())).append("[")
                   .append(traducir(nodo.getHijo(2))).append("] = ")
                   .append(traducir(nodo.getHijo(3))).append(";");
                break;
            case "AsignacionArrayEstructura":
                sb.append(traducirPalabra(nodo.getHijo(0).getValor())).append("[")
                   .append(traducir(nodo.getHijo(1))).append("] = ")
                   .append(traducir(nodo.getHijo(2))).append(";");
                break;
            case "Incremento":
                sb.append(traducirPalabra(nodo.getValor())).append("++;");
                break;
            case "Decremento":
                sb.append(traducirPalabra(nodo.getValor())).append("--;");
                break;
            case "IncrementoArray":
                sb.append(traducirPalabra(nodo.getHijo(0).getValor())).append("[")
                   .append(traducir(nodo.getHijo(1))).append("]++;");
                break;
            case "DecrementoArray":
                sb.append(traducirPalabra(nodo.getHijo(0).getValor())).append("[")
                   .append(traducir(nodo.getHijo(1))).append("]--;");
                break;
            case "IncrementoAtributo":
                sb.append(traducirPalabra(nodo.getHijo(0).getValor())).append(".")
                   .append(traducirPalabra(nodo.getHijo(1).getValor())).append("++;");
                break;
            case "DecrementoAtributo":
                sb.append(traducirPalabra(nodo.getHijo(0).getValor())).append(".")
                   .append(traducirPalabra(nodo.getHijo(1).getValor())).append("--;");
                break;
            case "Retorno":
                sb.append(traducirPalabra("reddere"));
                if (nodo.getHijos().size() > 0)
                {
                    sb.append(" ").append(traducir(nodo.getHijo(0)));
                }
                sb.append(";");
                break;
            case "Impresion":
                sb.append("%OINK ");
                for (int i = 0; i < nodo.getHijos().size(); i++)
                {
                    if (i > 0) sb.append(" %OINK ");
                    sb.append(traducir(nodo.getHijo(i)));
                }
                sb.append(";");
                break;
            case "Lectura":
                if (nodo.getHijos().size() > 0)
                {
                    sb.append(traducirPalabra(nodo.getHijo(0).getValor())).append(" ");
                }
                sb.append("%OINK_OINK");
                break;
            case "Interrupcion":
                sb.append(traducirPalabra(nodo.getValor())).append(";");
                break;

            // CONDICIONALES Y BUCLES
            case "Condicional":
                for (int i = 0; i < nodo.getHijos().size(); i++)
                {
                    NodoAST rama = nodo.getHijo(i);
                    switch (rama.getEtiqueta())
                    {
                        case "Si":
                            sb.append(traducirPalabra("si")).append(" (")
                               .append(traducir(rama.getHijo(0))).append("){\n")
                               .append(traducir(rama.getHijo(1))).append("\n}");
                            break;
                        case "AliterSi":
                            sb.append(" ").append(traducirPalabra("aliter")).append(" (")
                               .append(traducir(rama.getHijo(0))).append("){\n")
                               .append(traducir(rama.getHijo(1))).append("\n}");
                            break;
                        case "Aliter":
                            sb.append(" ").append(traducirPalabra("aliter")).append("{\n")
                               .append(traducir(rama.getHijo(0))).append("\n}");
                            break;
                    }
                }
                sb.append(" ").append(traducirPalabra("finis")).append(";\n");
                break;
            case "Bloque":
                for (NodoAST instr : nodo.getHijos())
                {
                    sb.append(traducir(instr)).append("\n");
                }
                break;
            case "BucleDum":
                sb.append(traducirPalabra("dum")).append(" (")
                   .append(traducir(nodo.getHijo(0))).append("){\n")
                   .append(traducir(nodo.getHijo(1))).append("\n} ")
                   .append(traducirPalabra("finis")).append(";\n");
                break;
            case "BucleFacere":
                sb.append(traducirPalabra("facere")).append("{\n")
                   .append(traducir(nodo.getHijo(0))).append("\n} ")
                   .append(traducirPalabra("dum")).append(" (")
                   .append(traducir(nodo.getHijo(1))).append(");\n");
                break;
            case "BuclePer":
                sb.append(traducirPalabra("per")).append(" (");
                if (nodo.getHijos().size() >= 3)
                {
                    sb.append(traducir(nodo.getHijo(0))).append(" ")
                       .append(traducir(nodo.getHijo(1))).append("; ");
                    sb.append(traducir(nodo.getHijo(2)));
                }
                sb.append("){\n").append(traducir(nodo.getHijo(nodo.getHijos().size()-1))).append("\n}");
                break;

            // EXPRESIONES
            case "+":
            case "-":
            case "*":
            case "/":
            case "==":
            case "!=":
            case "<":
            case "<=":
            case ">":
            case ">=":
            case "&&":
            case "||":
                sb.append(traducir(nodo.getHijo(0)))
                   .append(" ").append(nodo.getEtiqueta()).append(" ")
                   .append(traducir(nodo.getHijo(1)));
                break;
            case "Parentesis":
                sb.append("(").append(traducir(nodo.getHijo(0))).append(")");
                break;
            case "Negacion":
                sb.append(traducirPalabra("non")).append(" ")
                   .append(traducir(nodo.getHijo(0)));
                break;

            // LITERALES
            case "Numero":
            case "Decimal":
            case "Texto":
            case "Caracter":
                sb.append(nodo.getValor());
                break;
            case "Booleano":
                sb.append(traducirPalabra(nodo.getValor()));
                break;
            case "Variable":
                sb.append(traducirPalabra(nodo.getValor()));
                break;
            case "AccesoArray":
                sb.append(traducirPalabra(nodo.getHijo(0).getValor())).append("[")
                   .append(traducir(nodo.getHijo(1))).append("]");
                break;
            case "LlamadaFuncion":
                sb.append(traducirPalabra(nodo.getValor())).append("(");
                if (nodo.getHijos().size() > 0)
                {
                    sb.append(traducir(nodo.getHijo(0)));
                }
                sb.append(")");
                break;
            case "AccesoAtributo":
                sb.append(traducir(nodo.getHijo(0))).append(".")
                   .append(traducirPalabra(nodo.getHijo(1).getValor()));
                break;
            case "AccesoAtributoArray":
                sb.append(traducir(nodo.getHijo(0))).append(".")
                   .append(traducirPalabra(nodo.getHijo(1).getValor())).append("[")
                   .append(traducir(nodo.getHijo(2))).append("]");
                break;
            case "Argumentos":
                for (int i = 0; i < nodo.getHijos().size(); i++)
                {
                    if (i > 0) sb.append(", ");
                    sb.append(traducir(nodo.getHijo(i)));
                }
                break;
            default:
                for (NodoAST hijo : nodo.getHijos())
                {
                    sb.append(traducir(hijo));
                }
                break;
        }
        return sb.toString();
    }
}