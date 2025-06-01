package com.mycompany.gui;

import com.mycompany.rmi.Citas;

import javax.swing.*;
import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class LoginFrame extends JFrame {
    private JTextField userField;
    private JPasswordField passField;
    private JComboBox<String> roleComboBox;
    private JButton loginButton;
    private JButton registerButton; // Nuevo botón
    private Citas citasStub;

    public LoginFrame() {
        setTitle("Login Sistema de Citas Médicas");
        setSize(450, 300); // Aumentado tamaño para el nuevo botón
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // Mayor espaciado
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Inicializar RMI Stub
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            citasStub = (Citas) registry.lookup("CitasService");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error conectando al servidor RMI: " + e.getMessage(),
                    "Error de Conexión", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(1);
        }

        // Componentes de la GUI
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Usuario:"), gbc);

        gbc.gridx = 1;
        userField = new JTextField(20); // Campo más ancho
        add(userField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Contraseña:"), gbc);

        gbc.gridx = 1;
        passField = new JPasswordField(20);
        add(passField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Rol:"), gbc);

        gbc.gridx = 1;
        roleComboBox = new JComboBox<>(new String[]{"Paciente", "Medico"});
        add(roleComboBox, gbc);

        // Panel para botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        loginButton = new JButton("Login");
        registerButton = new JButton("Registrarse"); // Nuevo
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> handleRegister());

        // Permitir login con Enter
        getRootPane().setDefaultButton(loginButton);
    }

    private void handleLogin() {
        String user = userField.getText().trim();
        String pass = new String(passField.getPassword());
        String role = (String) roleComboBox.getSelectedItem();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Usuario y contraseña no pueden estar vacíos.",
                    "Error de Login", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            boolean loginSuccess = false;
            if ("Paciente".equals(role)) {
                loginSuccess = citasStub.loginPaciente(user, pass);
                if (loginSuccess) {
                    PacienteFrame pacienteFrame = new PacienteFrame(citasStub, user, this);
                    pacienteFrame.setVisible(true);
                    this.setVisible(false); // Ocultar en lugar de dispose para poder volver
                }
            } else if ("Medico".equals(role)) {
                loginSuccess = citasStub.loginMedico(user, pass);
                if (loginSuccess) {
                    MedicoFrame medicoFrame = new MedicoFrame(citasStub, user, this);
                    medicoFrame.setVisible(true);
                    this.setVisible(false); // Ocultar
                }
            }

            if (!loginSuccess) {
                JOptionPane.showMessageDialog(this, "Login fallido. Usuario, contraseña o rol incorrecto.",
                        "Error de Login", JOptionPane.ERROR_MESSAGE);
            } else {
                // Limpiar campos si el login es exitoso y se abre otra ventana
                userField.setText("");
                passField.setText("");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error durante el login: " + ex.getMessage(),
                    "Error RMI", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void handleRegister() {
        RegistroDialog registroDialog = new RegistroDialog(this, citasStub);
        registroDialog.setVisible(true);
        // No se necesita hacer nada más aquí, el diálogo maneja el registro.
    }

    public static void main(String[] args) { // Para pruebas directas del LoginFrame
        SwingUtilities.invokeLater(() -> {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception e) {
                // Si Nimbus no está disponible, se usará el L&F por defecto
            }
            new LoginFrame().setVisible(true);
        });
    }
}