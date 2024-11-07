
package com.mycompany.openhour2;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Persistencia {
    public static void guardarUsuario(Usuario usuario, String archivo) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivo))) {
            oos.writeObject(usuario);
        }
    }
        
     public static Usuario cargarUsuario(String archivo) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            return (Usuario) ois.readObject();
        }
}
}