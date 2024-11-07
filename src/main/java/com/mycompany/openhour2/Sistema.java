
package com.mycompany.openhour2;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Sistema implements Serializable {
    private Map<String, Usuario> usuarios;

    public Sistema() {
        usuarios = new HashMap<>();
    }

    public void registrarUsuario(String id, String nombre, String contrasena, int semestre) {
        Usuario usuario = new Usuario(id,nombre,contrasena,semestre);
        usuarios.put(id, usuario);
    }

    public Usuario login(String nombre, String contrasena) {
        Usuario usuario = usuarios.get(nombre);
        if (usuario != null && usuario.getContrasena().equals(contrasena)) {
            return usuario;
        }
        return null;
    }

    public void guardarSistema(String archivo) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivo))) {
            oos.writeObject(this);
        }
    }

    public static Sistema cargarSistema(String archivo) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            return (Sistema) ois.readObject();
        }
    }
}

