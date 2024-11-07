package com.mycompany.openhour2;
import java.util.*;
        
public class HorasLibres {
      private List<Integer> horasPorSemana;

    public HorasLibres() {
        this.horasPorSemana = new ArrayList<>();
    }

    public void agregarHoras(int horas) {
        horasPorSemana.add(horas);
    }
    
    public void eliminarHoras(int horas) {
        if (!horasPorSemana.isEmpty()) {
            horasPorSemana.remove(horasPorSemana.size() - 1); // Elimina las últimas horas agregadas
        }
    }

    public double promedioPorSemana() {
        return horasPorSemana.stream().mapToInt(Integer::intValue).average().orElse(0);
    }

    public double promedioPorMes() {
        return promedioPorSemana() * 4;
    }

    public double promedioPorAno() {
        return promedioPorSemana() * 52;
    }

    public double promedioPorSemestre() {
        return promedioPorAno() / 2;
    }

    public int horasFaltantesParaGraduarse(int horasRequeridas) {
        int totalHoras = horasPorSemana.stream().mapToInt(Integer::intValue).sum();
        return horasRequeridas - totalHoras;
    }
}
