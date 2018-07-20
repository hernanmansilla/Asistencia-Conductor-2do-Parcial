package com.example.herna.asistenciaconductor;

public class DatosFireBase {
    private String Nombre;
    private String DNI;
    private int Velocidad;

    public DatosFireBase() {
        //Es obligatorio incluir constructor por defecto
    }

    public DatosFireBase(String nombre, String dni, int velocidad)
    {
        this.Nombre = nombre;
        this.DNI = dni;
        this.Velocidad = velocidad;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        this.Nombre = nombre;
    }

    public String getDNI() {
        return DNI;
    }

    public void setTemperatura(long temperatura) {
        this.temperatura = temperatura;
    }

    public double getHumedad() {
        return humedad;
    }

    public void setHumedad(double humedad) {
        this.humedad = humedad;
    }

    @Override
    public String toString() {
        return "Prediccion{" +
                "cielo='" + cielo + '\'' +
                ", temperatura=" + temperatura +
                ", humedad=" + humedad +
                '}';
    }
}
