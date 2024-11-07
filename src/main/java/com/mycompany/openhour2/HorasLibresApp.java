
package com.mycompany.openhour2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;


public class HorasLibresApp {
    private Sistema sistema;
    private Usuario usuarioActual;
    private HorasLibres horasLibres;
    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private boolean isDarkMode = false;

    public HorasLibresApp() {
        try {
            sistema = Sistema.cargarSistema("sistema.dat");
        } catch (IOException | ClassNotFoundException e) {
            sistema = new Sistema();
        }
        horasLibres = new HorasLibres();
        crearInterfaz();
    }

    private void crearInterfaz() {
        frame = new JFrame("Gestión de Horas Libres");
        frame.setSize(650, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(crearPanelLogin(), "login");
        mainPanel.add(crearPanelPrincipal(), "principal");

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private JPanel crearPanelLogin() {
        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel idLabel = new JLabel("ID:");
        idLabel.setBounds(10, 20, 80, 25);
        panel.add(idLabel);

        JTextField idText = new JTextField(20);
        idText.setBounds(100, 20, 165, 25);
        panel.add(idText);

        JLabel nombreLabel = new JLabel("Usuario:");
        nombreLabel.setBounds(10, 60, 80, 25);
        panel.add(nombreLabel);

        JTextField nombreText = new JTextField(20);
        nombreText.setBounds(100, 60, 165, 25);
        panel.add(nombreText);

        JLabel contrasenaLabel = new JLabel("Contraseña:");
        contrasenaLabel.setBounds(10, 100, 80, 25);
        panel.add(contrasenaLabel);

        JPasswordField contrasenaText = new JPasswordField(20);
        contrasenaText.setBounds(100, 100, 165, 25);
        panel.add(contrasenaText);

        JLabel semestreLabel = new JLabel("Semestre:");
        semestreLabel.setBounds(10, 140, 80, 25);
        panel.add(semestreLabel);

        JTextField semestreText = new JTextField(20);
        semestreText.setBounds(100, 140, 165, 25);
        panel.add(semestreText);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(10, 180, 80, 25);
        panel.add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombre = nombreText.getText();
                String contrasena = new String(contrasenaText.getPassword());
                if (nombre.isEmpty() || contrasena.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor, complete todos los campos");
                    return;
                }
                usuarioActual = sistema.login(nombre, contrasena);
                if (usuarioActual != null) {
                    horasLibres = new HorasLibres(); // Reiniciar horas libres
                    JOptionPane.showMessageDialog(null, "Login exitoso");
                    cardLayout.show(mainPanel, "principal");
                } else {
                    JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos");
                }
            }
        });

        JButton registrarButton = new JButton("Registrar");
        registrarButton.setBounds(100, 180, 100, 25);
        panel.add(registrarButton);

        registrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idText.getText();
                String nombre = nombreText.getText();
                String contrasena = new String(contrasenaText.getPassword());
                int semestre;
                try {
                    semestre = Integer.parseInt(semestreText.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Por favor, ingrese un semestre válido");
                    return;
                }
                if (id.isEmpty() || nombre.isEmpty() || contrasena.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor, complete todos los campos");
                    return;
                }
                if (sistema.login(nombre, contrasena) != null) {
                    JOptionPane.showMessageDialog(null, "El usuario ya existe");
                    return;
                }
                sistema.registrarUsuario(id, nombre, contrasena, semestre);
                try {
                    sistema.guardarSistema("sistema.dat");
                    JOptionPane.showMessageDialog(null, "Usuario registrado");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        return panel;
    }

    private JPanel crearPanelPrincipal() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        
        JButton toggleThemeButton = new JButton("Cambiar Tema");
        toggleThemeButton.setBounds(380,8,110, 18);
        panel.add(toggleThemeButton);

        toggleThemeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleTheme();
            }
        });
        
        JButton logoutButton = new JButton("Cerrar Sesión");
        logoutButton.setBounds(10, 340, 150, 25);
        panel.add(logoutButton);

        logoutButton.addActionListener(new ActionListener() {
            @Override
         public void actionPerformed(ActionEvent e) {
            usuarioActual = null; // Limpiar el usuario actual
            cardLayout.show(mainPanel, "login");
        }
    });

        JLabel horasLabel = new JLabel("Horas Libres:");
        horasLabel.setBounds(10, 20, 80, 25);
        panel.add(horasLabel);

        JTextField horasText = new JTextField(20);
        horasText.setBounds(100, 20, 165, 25);
        panel.add(horasText);

        JButton agregarHorasButton = new JButton("Agregar Horas");
        agregarHorasButton.setBounds(10, 60, 150, 25);
        panel.add(agregarHorasButton);

        agregarHorasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int horas = Integer.parseInt(horasText.getText());
                    if (horas < 0) {
                        JOptionPane.showMessageDialog(null, "Por favor, ingrese un número válido de horas");
                        return;
                    }
                    horasLibres.agregarHoras(horas);
                    usuarioActual.setHorasLibresTotales(usuarioActual.getHorasLibresTotales() + horas);
                    guardarUsuarioActual();
                    JOptionPane.showMessageDialog(null, "Horas agregadas");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Por favor, ingrese un número válido de horas");
                }
            }

            private void guardarUsuarioActual() {
                try {
                    sistema.guardarSistema("sistema.dat");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error al guardar los datos del usuario");
                }
            }
        });
        
        JButton eliminarHorasButton = new JButton("Eliminar Horas");
        eliminarHorasButton.setBounds(320, 60, 150, 25);
        panel.add(eliminarHorasButton);

        eliminarHorasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int horas = Integer.parseInt(horasText.getText());
                    if (horas < 0 || horas > usuarioActual.getHorasLibresTotales()) {
                        JOptionPane.showMessageDialog(null, "Por favor, ingrese un número válido de horas");
                        return;
                    }
                    horasLibres.eliminarHoras(horas);
                    usuarioActual.setHorasLibresTotales(usuarioActual.getHorasLibresTotales() - horas);
                    guardarUsuarioActual();
                    JOptionPane.showMessageDialog(null, "Horas eliminadas");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Por favor, ingrese un número válido de horas");
                }
            }
             private void guardarUsuarioActual() {
                try {
                    sistema.guardarSistema("sistema.dat");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error al guardar los datos del usuario");
                }
            }
            
        });

        JButton actualizarHorasButton = new JButton("Actualizar Horas");
actualizarHorasButton.setBounds(320, 100, 150, 25);
panel.add(actualizarHorasButton);

actualizarHorasButton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            int horas = Integer.parseInt(horasText.getText());
            if (horas < 0) {
                JOptionPane.showMessageDialog(null, "Por favor, ingrese un número válido de horas");
                return;
            }
            usuarioActual.setHorasLibresTotales(horas);
            guardarUsuarioActual();
            JOptionPane.showMessageDialog(null, "Horas actualizadas");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Por favor, ingrese un número válido de horas");
        }
    }

    private void guardarUsuarioActual() {
        try {
            sistema.guardarSistema("sistema.dat");
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al guardar los datos del usuario");
        }
    }
});

        JButton promedioSemanaButton = new JButton("Promedio por Semana");
        promedioSemanaButton.setBounds(10, 100, 200, 25);
        panel.add(promedioSemanaButton);

        promedioSemanaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double promedio = horasLibres.promedioPorSemana();
                JOptionPane.showMessageDialog(null, "Promedio por Semana: " + promedio);
            }
        });

        JButton promedioMesButton = new JButton("Promedio por Mes");
        promedioMesButton.setBounds(10, 140, 200, 25);
        panel.add(promedioMesButton);

        promedioMesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double promedio = horasLibres.promedioPorMes();
                JOptionPane.showMessageDialog(null, "Promedio por Mes: " + promedio);
            }
        });

        JButton promedioAnoButton = new JButton("Promedio por Año");
        promedioAnoButton.setBounds(10, 180, 200, 25);
        panel.add(promedioAnoButton);

        promedioAnoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double promedio = horasLibres.promedioPorAno();
                JOptionPane.showMessageDialog(null, "Promedio por Año: " + promedio);
            }
        });

        JButton promedioSemestreButton = new JButton("Promedio por Semestre");
        promedioSemestreButton.setBounds(10, 220, 200, 25);
        panel.add(promedioSemestreButton);

        promedioSemestreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double promedio = horasLibres.promedioPorSemestre();
                JOptionPane.showMessageDialog(null, "Promedio por Semestre: " + promedio);
            }
        });

        JButton horasFaltantesButton = new JButton("Horas Faltantes para Graduarse");
        horasFaltantesButton.setBounds(10, 260, 250, 25);
        panel.add(horasFaltantesButton);

        horasFaltantesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Horas Faltantes para Graduarse: " + (usuarioActual.getHorasLibresParaGraduarse() - usuarioActual.getHorasLibresTotales()));
            }
        });

        JButton verPerfilButton = new JButton("Ver Perfil del Usuario");
        verPerfilButton.setBounds(10, 300, 200, 25);
        panel.add(verPerfilButton);

        verPerfilButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String perfil = "Nombre: " + usuarioActual.getNombre() + "\nHoras Libres Totales: " + usuarioActual.getHorasLibresTotales() + "\nId:" + usuarioActual.getId() + "\nSemestre:" + usuarioActual.getSemestre()  + "\nNivel:" + usuarioActual.getNivel();
                JOptionPane.showMessageDialog(null, perfil);
            }
        });

        return panel;
    }
    
     private void toggleTheme() {
        if (isDarkMode) {
            FlatLightLaf.setup();
        } else {
            FlatDarkLaf.setup();
        }
        isDarkMode = !isDarkMode;

        // Actualiza el aspecto de la interfaz
        SwingUtilities.updateComponentTreeUI(frame);
    }


    public static void main(String[] args) {
        FlatLightLaf.setup();
        javax.swing.SwingUtilities.invokeLater(() -> {
            new HorasLibresApp();
        });
    }
}
