
package com.mycompany.openhour2;

import java.io.Serializable;
import java.util.Date;

public class Evento implements Serializable {
    private String nombre;
    private String descripcion;
    private String ubicacion;
    private Date fecha;
    private String hora;
    private Float duracion;
    private int horasOtorgadas;

    public Evento(String nombre, String descripcion, String ubicacion, Date fecha, String hora, Float duracion, Integer horasOtorgadas) {
       this.nombre = nombre;
       this.descripcion = descripcion;
       this.ubicacion = ubicacion;
       this.fecha= fecha;
       this.hora = hora;
       this.duracion = duracion;
       this.horasOtorgadas = horasOtorgadas;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public Float getDuracion() {
        return duracion;
    }

    public void setDuracion(Float duracion) {
        this.duracion = duracion;
    }

    public int getHorasOtorgadas() {
        return horasOtorgadas;
    }

    public void setHorasOtorgadas(int horasOtorgadas) {
        this.horasOtorgadas = horasOtorgadas;
    }

    @Override
    public String toString() {
       return "Evento [" + nombre + ", " + descripcion + ", Ubicación=" + ubicacion + ", Fecha="
             + fecha + ", Hora=" + hora + ", Duración=" + duracion + ", Horas otorgadas=" + horasOtorgadas + "]";
    }

}

