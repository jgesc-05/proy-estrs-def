

package com.mycompany.openhour2;

import java.io.*;

        
 public class Usuario implements Serializable {
    private String id;
    private String nombre;
    private String contrasena;
    private int semestre;
    private int nivel;
    private String correo;
    private int horasLibresTotales;
    private int horasLibresParaGraduarse = 96;

    public Usuario(String id, String nombre, String contrasena, int semestre) {
        this.id = id;
        this.nombre = nombre;
        this.contrasena = contrasena;
        this.semestre = semestre;
        this.nivel = calcularNivel(semestre);
    }

    private int calcularNivel(int semestre) {
        if (semestre == 1) {
            return 0;
        } else if (semestre >= 2 && semestre <= 6) {
            return 1;
        } else {
            return 2;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public int getSemestre() {
        return semestre;
    }

    public void setSemestre(int semestre) {
        this.semestre = semestre;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }
    


    public int getHorasLibresTotales() {
        return horasLibresTotales;
    }

    public void setHorasLibresTotales(int horasLibresTotales) {
        this.horasLibresTotales = horasLibresTotales;
    }

    public int getHorasLibresParaGraduarse() {
        return horasLibresParaGraduarse;
    }

    public void setHorasLibresParaGraduarse(int horasLibresParaGraduarse) {
        this.horasLibresParaGraduarse = horasLibresParaGraduarse;
    }

    
}

