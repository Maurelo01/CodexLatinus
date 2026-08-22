package codexlatinus.compiler;

import java.util.*;

public class Ambito
{
    private final String nombre;
    private final Map<String, Simbolo> simbolos = new LinkedHashMap<>();
    private final Ambito padre;
    private final List<Ambito> hijos = new ArrayList<>();

    public Ambito(String nombre, Ambito padre)
    {
        this.nombre = nombre;
        this.padre = padre;
        if (padre != null)
        {
            padre.hijos.add(this);
        }
    }

    public String getNombre()
    {
        return nombre;
    }
    public Ambito getPadre()
    {
        return padre;
    }
    public List<Ambito> getHijos()
    {
        return hijos;
    }
    public Map<String, Simbolo> getSimbolos()
    {
        return simbolos;
    }

    public boolean contiene(String id)
    {
        return simbolos.containsKey(id);
    }

    public void agregar(Simbolo s)
    {
        simbolos.put(s.getId(), s);
    }

    public Simbolo buscarLocal(String id)
    {
        return simbolos.get(id);
    }

    @Override
    public String toString()
    {
        return "Ambito: " + nombre + " (" + simbolos.size() + " simbolos)";
    }
}