package codexlatinus.compiler.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NodoAST
{
    private final String etiqueta;
    private String valor;
    private final List<NodoAST> hijos;

    public NodoAST(String etiqueta)
    {
        this(etiqueta, null);
    }
    public NodoAST(String etiqueta, String valor)
    {
        this.etiqueta = Objects.requireNonNull(etiqueta, "La etiqueta no puede ser nula");
        this.valor = valor;
        this.hijos = new ArrayList<>();
    }
    public void agregarHijo(NodoAST hijo)
    {
        if (hijo != null)
        {
            this.hijos.add(hijo);
        }
    }
    public void agregarHijos(List<NodoAST> nuevosHijos)
    {
        if (nuevosHijos != null)
        {
            for (NodoAST hijo : nuevosHijos)
            {
                agregarHijo(hijo);
            }
        }
    }
    public boolean esHoja()
    {
        return hijos.isEmpty();
    }
    
    public NodoAST getHijo(int indice)
    {
        if (indice >= 0 && indice < hijos.size())
        {
            return hijos.get(indice);
        }
        return null;
    }
    public NodoAST getUltimoHijo()
    {
        if (hijos.isEmpty())
        {
            return null;
        }
        return hijos.get(hijos.size() - 1);
    }
    public List<NodoAST> getHijos()
    {
        return hijos;
    }

    public String getEtiqueta()
    {
        return etiqueta;
    }
    
    public String getValor()
    {
        return valor;
    }
    public void setValor(String valor)
    {
        this.valor = valor;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("NodoAST{etiqueta='").append(etiqueta).append('\'');

        if (valor != null) 
        {
            sb.append(", valor='").append(valor).append('\'');
        }

        if (!hijos.isEmpty())
        {
            sb.append(", hijos=").append(hijos.size());
        }

        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (!(obj instanceof NodoAST)) return false;
        NodoAST otro = (NodoAST) obj;
        return etiqueta.equals(otro.etiqueta) && Objects.equals(valor, otro.valor) && hijos.equals(otro.hijos);
    }
}