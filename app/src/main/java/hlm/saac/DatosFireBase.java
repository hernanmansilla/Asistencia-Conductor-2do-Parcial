package hlm.saac;

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

    public void setDNI(String dni) {
        this.DNI = dni;
    }

    public double getVelocidad() {
        return Velocidad;
    }

    public void setVelocidad(int velocidad) {
        this.Velocidad = velocidad;
    }
/*
    @Override
    public String toString() {
        return "Prediccion{" +
                "cielo='" + cielo + '\'' +
                ", temperatura=" + temperatura +
                ", humedad=" + humedad +
                '}';
    }*/
}
