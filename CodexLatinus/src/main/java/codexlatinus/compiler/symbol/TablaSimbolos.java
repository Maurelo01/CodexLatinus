package codexlatinus.compiler.symbol;

import java.util.*;

public class TablaSimbolos
{
    private final Ambito ambitoGlobal;
    private Ambito ambitoActual;
    private final List<Ambito> todosLosAmbitos = new ArrayList<>();
    public TablaSimbolos()
    {
        ambitoGlobal = new Ambito("global", null);
        ambitoActual = ambitoGlobal;
        todosLosAmbitos.add(ambitoGlobal);
    }

    public void ingresarOtroAmbito(String nombre)
    {
        Ambito nuevo = new Ambito(nombre, ambitoActual);
        todosLosAmbitos.add(nuevo);
        ambitoActual = nuevo;
    }

    public void salirAmbito()
    {
        if (ambitoActual.getPadre() != null)
        {
            ambitoActual = ambitoActual.getPadre();
        }
    }

    public boolean agregar(Simbolo simbolo)
    {
        if (ambitoActual.contiene(simbolo.getId()))
        {
            return false;
        }
        ambitoActual.agregar(simbolo);
        return true;
    }

    public Simbolo buscar(String id)
    {
        Ambito ambito = ambitoActual;
        while (ambito != null)
        {
            Simbolo simbolo = ambito.buscarLocal(id);
            if (simbolo != null) return simbolo;
            ambito = ambito.getPadre();
        }
        return null;
    }

    public Simbolo buscarEnAmbitoActual(String id)
    {
        return ambitoActual.buscarLocal(id);
    }

    public Ambito getAmbitoActual()
    {
        return ambitoActual;
    }
    public Ambito getAmbitoGlobal()
    {
        return ambitoGlobal;
    }
    public List<Ambito> getTodosLosAmbitos()
    {
        return todosLosAmbitos;
    }
}