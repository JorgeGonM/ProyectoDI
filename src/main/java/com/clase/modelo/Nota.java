package com.clase.modelo;

public class Nota {

    private int idnota;
    private String notas;

    // Constructor vacio disponible para herramientas que requieren crear el bean.
    public Nota() {
    }

    // Construye una cita con los datos persistidos o introducidos en el formulario.
    public Nota(String notas) {

        
        this.notas = notas;
        
    }

    public int getIdnota() {
        return idnota;
    }

    public void setIdnota(int idnota) {
        this.idnota = idnota;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }
}