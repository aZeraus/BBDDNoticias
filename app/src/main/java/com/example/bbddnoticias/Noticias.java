package com.example.bbddnoticias;

/**
 * Clase para almacenar las noticias obtenidas de la consulta a la BBDD.
 */
public class Noticias {
    private int id;
    private String Titulo;
    private String Enlace;
    private String Fecha;
    private boolean Leido;
    private boolean Favorita;
    private boolean Fuente;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return Titulo;
    }

    public void setTitulo(String titulo) {
        Titulo = titulo;
    }

    public String getEnlace() {
        return Enlace;
    }

    public void setEnlace(String enlace) {
        Enlace = enlace;
    }

    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String fecha) {
        Fecha = fecha;
    }

    public boolean isLeido() {
        return Leido;
    }

    public void setLeido(boolean leido) {
        Leido = leido;
    }

    public boolean isFavorita() {
        return Favorita;
    }

    public void setFavorita(boolean favorita) {
        Favorita = favorita;
    }

    public boolean isFuente() {
        return Fuente;
    }

    public void setFuente(boolean fuente) {
        Fuente = fuente;
    }

    @Override
    public String toString() {
        return "Número: " + id +
                ",\nTitulo: " + Titulo;

/*
         return "Número: " + id +
                ",\nTitulo: " + Titulo +
                ",\nEnlace: " + Enlace +
                ",\nFecha: " + Fecha +
                ",\nLeido: " + Leido +
                ",\nFavorita: " + Favorita +
                ",\nFuente: " + Fuente;
*/
    }
}
