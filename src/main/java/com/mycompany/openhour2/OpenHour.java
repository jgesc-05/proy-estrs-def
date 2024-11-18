
package com.mycompany.openhour2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import java.net.URI;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat; 
import javax.swing.table.DefaultTableModel;


public class OpenHour {
    private Sistema sistema;
    private Usuario usuarioActual;
    private HorasLibres horasLibres;
    private EmailSender emailSender;
    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private ArrayList <Evento> eventos = new ArrayList();
    private String fichero = "eventos.dat";
    private boolean isDarkMode = false;

    public OpenHour() {
        try {
            sistema = Sistema.cargarSistema("sistema.dat");
        } catch (IOException | ClassNotFoundException e) {
            sistema = new Sistema();
        }
        horasLibres = new HorasLibres();
        emailSender = new EmailSender();
        eventos = cargarProductos();
        crearInterfaz();
    }

    private void crearInterfaz() {
        frame = new JFrame("OpenHour");
        frame.setSize(650, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(crearPanelLogin(), "login");
        mainPanel.add(crearPanelPrincipal(), "principal");
        mainPanel.add(crearPanelCrudHoras(), "crudHoras");
        mainPanel.add(crearPanelHorasConProgreso(), "progresoHoras");
        mainPanel.add(crearConsultarPromedio(), "consultarPromedio"); 
        mainPanel.add(crearPanelProductos(), "productos");
        mainPanel.add(crearPanelRecomendacion(), "recomendacion");
        mainPanel.add(crearPanelFeedback(), "correo");


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
                    agregarMenuNavegacion();
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
    

    
    private void agregarMenuNavegacion() {
        JMenuBar menuBar = new JMenuBar();

        JMenu inicioMenu = new JMenu("Usuario");
        JMenuItem verPerfilItem = new JMenuItem("Ver Información de Usuario");
        verPerfilItem.addActionListener(e -> mostrarInformacionUsuario());
        inicioMenu.add(verPerfilItem);

        JMenu configuracionMenu = new JMenu("Ajustes");
        JMenuItem cambiarTemaItem = new JMenuItem("Cambiar Tema");
        cambiarTemaItem.addActionListener(e -> toggleTheme());
        configuracionMenu.add(cambiarTemaItem);
        
        JMenu horasLibresMenu = new JMenu("Horas Libres");
        JMenuItem crudHorasItem = new JMenuItem("Modificar horas");
        JMenuItem consultarProgreso = new JMenuItem("Consultar progreso");
        JMenuItem verPromedio = new JMenuItem("Ver promedio (únicamente después de añadir horas)");
        crudHorasItem.addActionListener(e -> cardLayout.show(mainPanel, "crudHoras"));
        consultarProgreso.addActionListener(e -> cardLayout.show(mainPanel, "progresoHoras")); 
        verPromedio.addActionListener(e -> cardLayout.show(mainPanel, "consultarPromedio")); 
        horasLibresMenu.add(crudHorasItem);
        horasLibresMenu.add(consultarProgreso);
        horasLibresMenu.add(verPromedio);
        
        JMenu eventosMenu = new JMenu("Eventos");
        JMenuItem verEventosItem = new JMenuItem("Eventos");
        verEventosItem.addActionListener(e -> cardLayout.show(mainPanel, "productos"));
        eventosMenu.add(verEventosItem);
        
        JMenu correosMenu = new JMenu("Correos/recom");
        JMenuItem correosSugerenciasItem = new JMenuItem("Enviar correo");
        correosSugerenciasItem.addActionListener(e -> cardLayout.show(mainPanel, "correo"));
        JMenuItem recomendacionesHorasItem = new JMenuItem("Recomendaciones / Redes");
        recomendacionesHorasItem.addActionListener(e -> cardLayout.show(mainPanel, "recomendacion"));
        correosMenu.add(correosSugerenciasItem);
        correosMenu.add(recomendacionesHorasItem);
 

        menuBar.add(inicioMenu);
        menuBar.add(horasLibresMenu);
        menuBar.add(eventosMenu);
        menuBar.add(correosMenu);
        menuBar.add(configuracionMenu);


        frame.setJMenuBar(menuBar);
    }
    
        private void mostrarInformacionUsuario() {
        String perfil = "Nombre: " + usuarioActual.getNombre() + "\nID: " + usuarioActual.getId() +
                        "\nSemestre: " + usuarioActual.getSemestre() + "\nNivel: " + usuarioActual.getNivel() +
                        "\nHoras Libres Totales: " + usuarioActual.getHorasLibresTotales();
        JOptionPane.showMessageDialog(null, perfil, "Perfil de Usuario", JOptionPane.INFORMATION_MESSAGE);
    }


private JPanel crearPanelPrincipal() {
    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.CENTER;
    
    JLabel texto1 = new JLabel("<html>¡Bienvenido a la app OpenHour!<br>Siéntete libre de navegar por las opciones del menú superior<br><br>Cordialmente, el equipo de OpenHour</html>");
    panel.add(texto1, gbc);

    return panel;
}
    
private JPanel crearPanelCrudHoras() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));  // Usamos BoxLayout en columna

    // Título
    JLabel label = new JLabel("Modificar horas libres");
    label.setAlignmentX(Component.CENTER_ALIGNMENT);  // Centrar el título
    panel.add(label);

    // Espaciado entre componentes
    panel.add(Box.createVerticalStrut(10));

    // Etiqueta de horas
    JLabel horasLabel = new JLabel("Horas:");
    horasLabel.setAlignmentX(Component.CENTER_ALIGNMENT);  // Centrar la etiqueta
    panel.add(horasLabel);

    // Cuadro de texto para ingresar las horas
    JTextField horasText = new JTextField(20);
    horasText.setMaximumSize(new Dimension(200, 25));  // Limitar el tamaño del cuadro de texto
    horasText.setPreferredSize(new Dimension(200, 25));  // Tamaño preferido
    horasText.setAlignmentX(Component.CENTER_ALIGNMENT);  // Centrar el cuadro de texto
    panel.add(horasText);

    // Espaciado entre componentes
    panel.add(Box.createVerticalStrut(10));

    // Botones de acciones
    JButton agregarButton = new JButton("Agregar Horas");
    agregarButton.setAlignmentX(Component.CENTER_ALIGNMENT);  // Centrar el botón
    panel.add(agregarButton);
    agregarButton.addActionListener(new ActionListener() {
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
                String asunto = "Registro de horas exitoso";
                String destinatario = JOptionPane.showInputDialog(null, "Ingresa tu correo");
                String cuerpo = "Hola " + usuarioActual.getNombre() + ",\n\nTu registro de horas libres ha sido exitoso.\n\nSaludos,\nEquipo de OpenHour";
                emailSender.enviarCorreo(destinatario, asunto, cuerpo);
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

    // Espaciado entre botones
    panel.add(Box.createVerticalStrut(10));

    JButton eliminarButton = new JButton("Eliminar Horas");
    eliminarButton.setAlignmentX(Component.CENTER_ALIGNMENT);  // Centrar el botón
    panel.add(eliminarButton);
    eliminarButton.addActionListener(new ActionListener() {
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

    JButton actualizarButton = new JButton("Actualizar Horas");
    actualizarButton.setAlignmentX(Component.CENTER_ALIGNMENT);  // Centrar el botón
    panel.add(actualizarButton);
    actualizarButton.addActionListener(new ActionListener() {
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

    JButton consultarButton = new JButton("Consultar Horas");
    consultarButton.setAlignmentX(Component.CENTER_ALIGNMENT);  // Centrar el botón
    panel.add(consultarButton);
    consultarButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
                    String horas = "Horas que llevas: " + usuarioActual.getHorasLibresTotales();
        JOptionPane.showMessageDialog(null, horas, "Horas que llevas", JOptionPane.INFORMATION_MESSAGE);
        }
    });

    return panel;
}

private JPanel crearPanelFeedback() {
    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5); // Espaciado entre componentes

    // Etiqueta y campo 1
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.EAST;
    panel.add(new JLabel("Para (correo OpenHour):"), gbc);

    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    JTextField campoPara = new JTextField(20);
    panel.add(campoPara, gbc);

    // Etiqueta y campo 2
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.EAST;
    panel.add(new JLabel("Asunto:"), gbc);

    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    JTextField campoAsunto = new JTextField(20);
    panel.add(campoAsunto, gbc);

    // Etiqueta y campo 3
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.anchor = GridBagConstraints.EAST;
    panel.add(new JLabel("Cuerpo:"), gbc);

    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.anchor = GridBagConstraints.WEST;
    JTextField campoCuerpo = new JTextField(30); // Hacer más grande el campo 3
    panel.add(campoCuerpo, gbc);

    // Botón de enviar
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.CENTER;
    JButton botonEnviar = new JButton("Enviar");
    panel.add(botonEnviar, gbc);

    // Acción del botón enviar
    botonEnviar.addActionListener(e -> {
    String receptor = campoPara.getText();
        String asunto = campoAsunto.getText();
        String cuerpo = campoCuerpo.getText();
        emailSender.enviarCorreo(receptor, asunto, cuerpo);
        JOptionPane.showMessageDialog(null,"Correo enviado a nuestro equipo");
        campoPara.setText("");
        campoAsunto.setText("");
        campoCuerpo.setText("");
    });

    return panel;
}

    private JPanel crearPanelProductos() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    eventos = cargarProductos();

    JButton agregarButton = new JButton("Agregar eventos");
    agregarButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    agregarButton.addActionListener(e -> crearProducto());
    

    JButton eliminarButton = new JButton("Eliminar eventos");
    eliminarButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    eliminarButton.addActionListener(e -> eliminarProducto());

    JButton modificarButton = new JButton("Modificar eventos");
    modificarButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    modificarButton.addActionListener(e -> modificarProducto());

    JButton listarButton = new JButton("Listar eventos");
    listarButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    listarButton.addActionListener(e -> JOptionPane.showMessageDialog(null, listarInfo()));

    panel.add(agregarButton);
    panel.add(Box.createVerticalStrut(10));
    panel.add(eliminarButton);
    panel.add(Box.createVerticalStrut(10));
    panel.add(modificarButton);
    panel.add(Box.createVerticalStrut(10));
    panel.add(listarButton);
    
   
    

    return panel;
}

private JPanel crearPanelHorasConProgreso() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Usamos BoxLayout en columna

    // Título
    JLabel label = new JLabel("Progreso de Horas Libres");
    label.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrar el título
    panel.add(label);

    // Espaciado entre componentes
    panel.add(Box.createVerticalStrut(10));

    // Etiqueta de horas
    JLabel horasLabel = new JLabel("Horas totales:");
    horasLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrar la etiqueta
    panel.add(horasLabel);

    // Barra de progreso
    JProgressBar progressBar = new JProgressBar(0,96); 
    progressBar.setValue(0); 
    progressBar.setStringPainted(true); // Mostrar el porcentaje
    progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.add(progressBar);

    // Espaciado entre componentes
    panel.add(Box.createVerticalStrut(10));

    // Botón para consultar horas y actualizar la barra
    JButton consultarButton = new JButton("Consultar Horas");
    consultarButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrar el botón
    panel.add(consultarButton);
    consultarButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (usuarioActual != null) {
                int horasTotales = usuarioActual.getHorasLibresTotales();
                int maxHoras = 96; // Cambia esto al valor máximo esperado de horas
                progressBar.setMaximum(maxHoras); // Establece el máximo dinámicamente
                progressBar.setValue(horasTotales); // Actualiza la barra de progreso
                String mensaje = "Horas que llevas: " + horasTotales + " / " + maxHoras;
                JOptionPane.showMessageDialog(null, mensaje, "Progreso de Horas", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "El usuario no está inicializado.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    });

  

    return panel;
}

private JPanel crearConsultarPromedio() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Usamos BoxLayout en columna

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
    return panel;
}

private JPanel crearPanelRecomendacion() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Usamos BoxLayout en columna

    String recomendacion;

    int semestre;
    try {
        semestre = Integer.parseInt(JOptionPane.showInputDialog("Ingresa el semestre para confirmar:")); 

        if (semestre >= 1 && semestre <= 3) {
            recomendacion = "Aún estás comenzando la carrera, así que empieza con actividades como: "
                            + "<a href='https://unab.edu.co'>Ulibro</a> o "
                            + "<a href='https://miportalu.unab.edu.co'>Días de películas en la biblioteca</a>.";
        } else if (semestre >= 4 && semestre <= 6) {
            recomendacion = "Comienza a asistir a más actividades de horas libres como las "
                            + "<a href='https://unab.edu.co'>charlas de la universidad</a>.";
        } else if (semestre >= 7 && semestre <= 12) {
            recomendacion = "Debido al poco tiempo que tienes, te recomiendo unirte al equipo de organizadores de "
                            + "<a href='https://unab.edu.co'>Ulibro</a> para completar las horas que te faltan.";
        } else {
            recomendacion = "El semestre ingresado no parece válido. Por favor, verifica tu información.";
        }
    } catch (NumberFormatException ex) {
        recomendacion = "No se ingresó un semestre válido. Por favor, intenta nuevamente.";
    }

    // Crear un JEditorPane para mostrar recomendaciones con hipervínculos
    JEditorPane editorPane = new JEditorPane("text/html", "<html>" + recomendacion + "</html>");
    editorPane.setEditable(false); // Hacer que el editor no sea editable
    editorPane.setOpaque(false); // Transparente para integrarlo en el panel
    editorPane.addHyperlinkListener(e -> {
        if (e.getEventType() == javax.swing.event.HyperlinkEvent.EventType.ACTIVATED) {
            try {
                Desktop.getDesktop().browse(e.getURL().toURI()); // Abrir el enlace en el navegador
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    });

    panel.add(editorPane);
    return panel;
}


// Método para abrir los enlaces del texto
private void abrirHipervinculos(String texto) {
    // Extraer y abrir enlaces del texto HTML
    try {
        if (texto.contains("https://unab.edu.co")) {
            Desktop.getDesktop().browse(new URI("https://unab.edu.co"));
        }
        if (texto.contains("https://miportalu.unab.edu.co")) {
            Desktop.getDesktop().browse(new URI("https://miportalu.unab.edu.co"));
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}






public void eliminarProducto() {
        int index = Integer.parseInt(JOptionPane.showInputDialog("Indique el número del evento a eliminar: " + listarInfo()));
        eventos.remove(index - 1);
        guardarProductos(); 
        JOptionPane.showMessageDialog(null, "Se ha dado de baja correctamente");
    }

    public void crearProducto() {
    String nombre, descripcion;
    String ubicacion;
    String fecha;
    String hora;
    Float duracion;
    int horasOtorgadas;
    
    nombre = JOptionPane.showInputDialog("Nombre:");
    descripcion = JOptionPane.showInputDialog("Descripción:");
    ubicacion = JOptionPane.showInputDialog("Ubicación del evento:");
    hora = JOptionPane.showInputDialog("Hora del evento (hh:mm):");
    
    try {
        duracion = Float.parseFloat(JOptionPane.showInputDialog("Duración del evento (en horas):"));
        horasOtorgadas = Integer.parseInt(JOptionPane.showInputDialog("Horas libres que otorga el evento:"));
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Error: Duración y horas otorgadas deben ser números válidos.", "Error de entrada", JOptionPane.ERROR_MESSAGE);
        return; // Salir del método si hay error en los números
    }

    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy"); // Ajustar el formato esperado
    try {
        fecha = JOptionPane.showInputDialog("Fecha del evento (formato dd/MM/yyyy):");
        Date fechaConvertida = format.parse(fecha); // Parsear la fecha

        // Crear el objeto Evento y añadirlo a la lista
        Evento evento = new Evento(nombre, descripcion, ubicacion, fechaConvertida, hora, duracion, horasOtorgadas);
        eventos.add(evento);
        guardarProductos(); 
        JOptionPane.showMessageDialog(null, "Evento creado exitosamente.");
    } catch (ParseException e) {
        JOptionPane.showMessageDialog(null, "Error: Formato de fecha inválido. Usa el formato dd/MM/yyyy.", "Error de entrada", JOptionPane.ERROR_MESSAGE);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error inesperado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    public String listarInfo() {
        StringBuilder text = new StringBuilder();
        if (eventos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay eventos para mostrar");
        } else {
            for (Evento evento : eventos) {
                text.append("\n").append(" [ ").append(eventos.indexOf(evento) + 1).append(" ] ").append(evento);
            }
        }
        return text.toString();
    }

    public void modificarProducto() {
        int index = Integer.parseInt(JOptionPane.showInputDialog("Indique el número del evento a actualizar: " + listarInfo()));
        Evento evento = eventos.get(index - 1);
        evento.setUbicacion(JOptionPane.showInputDialog("Escribe el campus y lugar:"));
        evento.setDuracion(Float.parseFloat(JOptionPane.showInputDialog("Escribe la duración del evento:")));
         SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try{
        String fecha =JOptionPane.showInputDialog("Fecha con formato dd/mm/aaaa");
        //Formato de conversión
        Date fechaConvertida = format.parse(fecha); //Se parsea la fecha
        evento.setFecha(fechaConvertida);
        } catch (Exception e) {
            System.err.println("No se pudo convertir");
        }
        evento.setDescripcion(JOptionPane.showInputDialog("Escribe la descripción:"));
        evento.setHora(JOptionPane.showInputDialog("Escribe la hora del evento:"));
        evento.setHorasOtorgadas(Integer.parseInt(JOptionPane.showInputDialog("Escribe las horas libres que da el evento:")));
    }



     public  ArrayList<Evento> cargarProductos() {
       ArrayList<Evento> eventos = new ArrayList<Evento>();
       try {
          ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fichero));
          Object aux = ois.readObject();
          while (aux != null) {
             if (aux instanceof Evento)
                eventos.add((Evento) aux);
             aux = ois.readObject();

          }
          ois.close();
       } catch (FileNotFoundException e) {
          try {
             File archivo = new File(fichero);
             archivo.createNewFile();
          } catch (IOException e1) {
             // TODO Auto-generated catch block
             e1.printStackTrace();
          }
       } catch (Exception e) {

       }
       return eventos;
    }

    /**
     * Recibe un listado de productos y guarda en un archivo .dat
     * 
     * @param productos
     */
    public  void guardarProductos(){
       try {
          ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fichero));
          for (Evento evento : eventos) {
             oos.writeObject(evento);
          }
          oos.close();
       } catch (Exception e) {
          e.printStackTrace();
       }
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
            new OpenHour();
        });
    }
}
