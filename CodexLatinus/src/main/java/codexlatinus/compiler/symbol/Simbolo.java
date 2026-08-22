package codexlatinus.compiler.symbol;

import java.util.HashMap;
import java.util.Map;

public class Simbolo
{
    public enum Tipo
    {
        NUMERUS,
        TEXTUM,
        DECIMALIS,
        LITTERA,
        BOOL,
        ESTRUCTURA,
        ARREGLO,
        FUNCION
    }
    private final String id;
    private final Tipo tipo;
    private Object valor;
    private final boolean esGlobal;
    private Integer tamaño;
    private Map<String, Integer> tamañosAtributos;

    public Simbolo(String id, Tipo tipo, Object valor, boolean esGlobal)
    {
        this.id = id;
        this.tipo = tipo;
        this.valor = valor;
        this.esGlobal = esGlobal;
    }

    public String getId()
    {
        return id;
    }
    public Tipo getTipo()
    {
        return tipo;
    }
    public Object getValor()
    {
        return valor;
    }
    public void setValor(Object valor)
    {
        this.valor = valor;
    }
    public boolean esGlobal()
    {
        return esGlobal;
    }
    public Integer getTamaño()
    {
        return tamaño;
    }
    public void setTamaño(Integer tamaño)
    {
        this.tamaño = tamaño;
    }

    public Map<String, Integer> getTamañosAtributos()
    {
        if (tamañosAtributos == null)
        {
            tamañosAtributos = new HashMap<>();
        }
        return tamañosAtributos;
    }

    public void setTamañoAtributo(String nombreAtributo, Integer tamaño)
    {
        getTamañosAtributos().put(nombreAtributo, tamaño);
    }

    public Integer getTamañoAtributo(String nombreAtributo)
    {
        return getTamañosAtributos().get(nombreAtributo);
    }

    @Override
    public String toString()
    {
        return String.format("Simbolo{id='%s', tipo=%s, valor=%s, esGlobal=%s, tamaño=%s, tamañosAtributos=%s}", id, tipo, valor, esGlobal, tamaño, tamañosAtributos);
    }
}