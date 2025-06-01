package com.mycompany.gui;

import com.mycompany.rmi.Citas;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;

public class RegistroDialog extends JDialog {
    private Citas citasStub;

    private JTextField userField;
    private JPasswordField passField;
    private JPasswordField confirmPassField;
    private JTextField nombreCompletoField;
    private JComboBox<String> roleComboBox;
    private JTextField especialidadField;
    private JLabel especialidadLabel;

    public RegistroDialog(Frame owner, Citas citasStub) {
        super(owner, "Registro de Nuevo Usuario", true);
        this.citasStub = citasStub;
        initComponents();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; userField = new JTextField(15); add(userField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; passField = new JPasswordField(15); add(passField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; add(new JLabel("Confirmar Contraseña:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; confirmPassField = new JPasswordField(15); add(confirmPassField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; add(new JLabel("Nombre Completo:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; nombreCompletoField = new JTextField(15); add(nombreCompletoField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; add(new JLabel("Rol:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        roleComboBox = new JComboBox<>(new String[]{"Paciente", "Medico"});
        add(roleComboBox, gbc);

        especialidadLabel = new JLabel("Especialidad:");
        especialidadField = new JTextField(15);
        gbc.gridx = 0; gbc.gridy = 5; add(especialidadLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 5; add(especialidadField, gbc);

        especialidadLabel.setVisible(false);
        especialidadField.setVisible(false);

        roleComboBox.addActionListener(e -> {
            boolean medicoSelected = "Medico".equals(roleComboBox.getSelectedItem());
            especialidadLabel.setVisible(medicoSelected);
            especialidadField.setVisible(medicoSelected);
            pack();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnRegistrar = new JButton("Registrar");
        JButton btnCancelar = new JButton("Cancelar");
        buttonPanel.add(btnRegistrar);
        buttonPanel.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        btnRegistrar.addActionListener(e -> registrarUsuario());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void registrarUsuario() {
        String user = userField.getText().trim();
        String pass = new String(passField.getPassword());
        String confirmPass = new String(confirmPassField.getPassword());
        String nombreCompleto = nombreCompletoField.getText().trim();
        String role = (String) roleComboBox.getSelectedItem();
        String especialidad = especialidadField.getText().trim();

        if (user.isEmpty() || pass.isEmpty() || nombreCompleto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Usuario, contraseña y nombre completo son requeridos.", "Error de Registro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!pass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden.", "Error de Registro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if ("Medico".equals(role) && especialidad.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La especialidad es requerida para médicos.", "Error de Registro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            boolean success = false;
            String mensajeExito = "";

            if ("Paciente".equals(role)) {
                success = citasStub.registrarPaciente(user, pass, nombreCompleto);
                mensajeExito = "Paciente registrado exitosamente: " + user;
            } else if ("Medico".equals(role)) {
                success = citasStub.registrarMedico(user, pass, nombreCompleto, especialidad);
                mensajeExito = "Médico registrado exitosamente: " + user + " (" + especialidad + ")";
            }

            if (success) {
                JOptionPane.showMessageDialog(this, mensajeExito, "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo registrar el usuario. Puede que el usuario ya exista o haya ocurrido un error en el servidor.", "Error de Registro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(this, "Error de comunicación con el servidor: " + ex.getMessage(), "Error RMI", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}