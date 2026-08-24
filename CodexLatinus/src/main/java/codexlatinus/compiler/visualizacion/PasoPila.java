package codexlatinus.compiler.visualizacion;

import java.util.*;

public class PasoPila
{
    private final List<String> pila;
    private final String accion;
    public PasoPila(List<String> pila, String accion) 
    {
        this.pila = new ArrayList<>(pila);
        this.accion = accion;
    }

    public List<String> getPila()
    {
        return pila;
    }
    public String getAccion()
    {
        return accion;
    }
}