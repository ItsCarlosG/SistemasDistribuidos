package com.mycompany.gui;

import com.mycompany.model.Cita;
import com.mycompany.rmi.Citas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MedicoFrame extends JFrame {
    private Citas citasStub;
    private String medicoUser;
    private JTable citasTable;
    private DefaultTableModel tableModel;
    private LoginFrame loginFrame;

    public MedicoFrame(Citas citasStub, String medicoUser, LoginFrame loginFrame) {
        this.citasStub = citasStub;
        this.medicoUser = medicoUser;
        this.loginFrame = loginFrame;

        setTitle("Portal del Médico - " + medicoUser);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                logout();
            }
        });
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Título del panel
        JLabel titleLabel = new JLabel("Citas Asignadas a " + medicoUser, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"ID Cita", "Paciente", "Fecha", "Hora", "Motivo", "Especialidad", "Estado"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        citasTable = new JTable(tableModel);
        citasTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(citasTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton refrescarCitasButton = new JButton("Ver/Refrescar Citas");
        JButton modificarEstadoButton = new JButton("Modificar Estado de Cita");
        JButton logoutButton = new JButton("Cerrar Sesión");

        buttonPanel.add(refrescarCitasButton);
        buttonPanel.add(modificarEstadoButton);
        buttonPanel.add(logoutButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        refrescarCitasButton.addActionListener(e -> cargarCitas());
        modificarEstadoButton.addActionListener(e -> modificarEstadoCitaSeleccionada());
        logoutButton.addActionListener(e -> logout());

        add(mainPanel);
        cargarCitas(); // Cargar citas al abrir
    }

    private void cargarCitas() {
        try {
            List<Cita> misCitas = citasStub.getCitasMedico(medicoUser);
            tableModel.setRowCount(0); // Limpiar tabla
            for (Cita cita : misCitas) {
                tableModel.addRow(new Object[]{
                        cita.getId(),
                        cita.getPacienteId(),
                        cita.getFecha(),
                        cita.getHora(),
                        cita.getMotivo(),
                        cita.getEspecialidadRequerida(),
                        cita.getEstado()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar citas: " + ex.getMessage(),
                    "Error RMI", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void modificarEstadoCitaSeleccionada() {
        int selectedRow = citasTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una cita de la tabla para modificar.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int citaId = (int) tableModel.getValueAt(selectedRow, 0);
        String estadoActual = (String) tableModel.getValueAt(selectedRow, 6); // Columna de Estado

        String[] estadosPosibles = {"Pendiente", "Atendida", "Cancelada"};
        String nuevoEstado = (String) JOptionPane.showInputDialog(this,
                "Seleccione el nuevo estado para la cita ID: " + citaId + "\nPaciente: " + tableModel.getValueAt(selectedRow, 1) +
                        "\n(Actual: " + estadoActual + ")",
                "Modificar Estado de Cita",
                JOptionPane.PLAIN_MESSAGE,
                null,
                estadosPosibles,
                estadoActual);

        if (nuevoEstado != null && !nuevoEstado.equals(estadoActual)) {
            try {
                citasStub.modificarEstadoCita(citaId, nuevoEstado);
                JOptionPane.showMessageDialog(this, "Estado de la cita ID " + citaId + " actualizado a '" + nuevoEstado + "'.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarCitas(); // Refrescar tabla
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al modificar estado de la cita: " + ex.getMessage(),
                        "Error RMI", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void logout() {
        this.dispose();
        loginFrame.setVisible(true);
    }
}