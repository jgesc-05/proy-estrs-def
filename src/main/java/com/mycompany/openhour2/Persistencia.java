
package com.mycompany.openhour2;

import java.io.*;
import java.util.List;

public class Persistencia {
    public static void guardarUsuarios(List<Usuario> usuarios, String archivo) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivo))) {
            oos.writeObject(usuarios);
        }
    }

    public static List<Usuario> cargarUsuarios(String archivo) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            return (List<Usuario>) ois.readObject();
        }
    }
}