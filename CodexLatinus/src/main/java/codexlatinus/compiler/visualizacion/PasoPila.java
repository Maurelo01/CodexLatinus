package codexlatinus.compiler.visualizacion;

import java.util.*;

public class PasoPila
{
    private final List<String> pila;
    private final List<String> log;
    public PasoPila(List<String> pila, List<String> log) 
    {
        this.pila = new ArrayList<>(pila);
        this.log = new ArrayList<>(log);
    }

    public List<String> getPila()
    {
        return pila;
    }
    public List<String> getLog()
    {
        return log;
    }
}