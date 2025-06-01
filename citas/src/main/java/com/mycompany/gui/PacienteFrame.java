package com.mycompany.gui;

import com.mycompany.model.Cita;
import com.mycompany.rmi.Citas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class PacienteFrame extends JFrame {
    private Citas citasStub;
    private String pacienteUser;
    private JTable citasTable;
    private DefaultTableModel tableModel;
    private LoginFrame loginFrame;

    private JComboBox<String> especialidadComboBox;
    private JComboBox<String> medicoComboBox;
    private JSpinner fechaSpinner;
    private JSpinner horaSpinner;
    private JTextField motivoField;

    public PacienteFrame(Citas citasStub, String pacienteUser, LoginFrame loginFrame) {
        this.citasStub = citasStub;
        this.pacienteUser = pacienteUser;
        this.loginFrame = loginFrame;

        setTitle("Portal del Paciente - " + pacienteUser);
        setSize(900, 700);
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

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Bienvenido, " + pacienteUser, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        JButton logoutButton = new JButton("Cerrar Sesión");
        logoutButton.addActionListener(e -> logout());
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.add(logoutButton);
        topPanel.add(logoutPanel, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);


        JPanel citasPanel = new JPanel(new BorderLayout(5,5));
        citasPanel.setBorder(BorderFactory.createTitledBorder("Mis Citas"));

        String[] columnNames = {"ID Cita", "Médico", "Fecha", "Hora", "Motivo", "Especialidad", "Estado"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        citasTable = new JTable(tableModel);
        citasTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(citasTable);
        citasPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel citasButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton refrescarCitasButton = new JButton("Ver/Refrescar Mis Citas");
        JButton cancelarCitaButton = new JButton("Cancelar Cita Seleccionada");
        citasButtonPanel.add(refrescarCitasButton);
        citasButtonPanel.add(cancelarCitaButton);
        citasPanel.add(citasButtonPanel, BorderLayout.SOUTH);

        mainPanel.add(citasPanel, BorderLayout.CENTER);

        JPanel agendarPanel = new JPanel(new GridBagLayout());
        agendarPanel.setBorder(BorderFactory.createTitledBorder("Agendar Nueva Cita"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; agendarPanel.add(new JLabel("Especialidad:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        especialidadComboBox = new JComboBox<>();
        agendarPanel.add(especialidadComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1; agendarPanel.add(new JLabel("Médico:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        medicoComboBox = new JComboBox<>();
        agendarPanel.add(medicoComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 2; agendarPanel.add(new JLabel("Fecha (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        fechaSpinner = new JSpinner(dateModel);
        fechaSpinner.setEditor(new JSpinner.DateEditor(fechaSpinner, "yyyy-MM-dd"));
        agendarPanel.add(fechaSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 3; agendarPanel.add(new JLabel("Hora (HH:mm):"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        SpinnerDateModel timeModel = new SpinnerDateModel();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 9); // Default 9 AM
        cal.set(Calendar.MINUTE, 0);
        timeModel.setValue(cal.getTime());
        horaSpinner = new JSpinner(timeModel);
        horaSpinner.setEditor(new JSpinner.DateEditor(horaSpinner, "HH:mm"));
        agendarPanel.add(horaSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 4; agendarPanel.add(new JLabel("Motivo:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        motivoField = new JTextField(20);
        agendarPanel.add(motivoField, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        JButton agendarButton = new JButton("Agendar Cita");
        agendarPanel.add(agendarButton, gbc);

        mainPanel.add(agendarPanel, BorderLayout.SOUTH);


        refrescarCitasButton.addActionListener(e -> cargarCitas());
        cancelarCitaButton.addActionListener(e -> cancelarCitaSeleccionada());
        agendarButton.addActionListener(e -> agendarNuevaCita());

        especialidadComboBox.addActionListener(e -> cargarMedicosPorEspecialidad());

        add(mainPanel);
        cargarCitas();
        cargarEspecialidades();
    }

    private void cargarCitas() {
        try {
            List<Cita> misCitas = citasStub.getCitasPaciente(pacienteUser);
            tableModel.setRowCount(0);
            for (Cita cita : misCitas) {
                tableModel.addRow(new Object[]{
                        cita.getId(),
                        cita.getMedicoId(),
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

    private void cargarEspecialidades() {
        try {
            List<String> especialidades = citasStub.getEspecialidadesDisponibles();
            especialidadComboBox.removeAllItems();
            if (especialidades.isEmpty()) {
                especialidadComboBox.addItem("No hay especialidades");
                medicoComboBox.removeAllItems();
                medicoComboBox.addItem("Seleccione especialidad primero");
                return;
            }
            for (String esp : especialidades) {
                especialidadComboBox.addItem(esp);
            }

            if (especialidadComboBox.getItemCount() > 0) {
                especialidadComboBox.setSelectedIndex(0);
                cargarMedicosPorEspecialidad();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar especialidades: " + ex.getMessage(),
                    "Error RMI", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            especialidadComboBox.addItem("Error al cargar");
        }
    }

    private void cargarMedicosPorEspecialidad() {
        String especialidadSeleccionada = (String) especialidadComboBox.getSelectedItem();
        if (especialidadSeleccionada == null || especialidadSeleccionada.equals("No hay especialidades") || especialidadSeleccionada.equals("Error al cargar")) {
            medicoComboBox.removeAllItems();
            medicoComboBox.addItem("N/A");
            return;
        }
        try {

            List<String> medicos = citasStub.getMedicosDisponiblesPorEspecialidad(especialidadSeleccionada);
            medicoComboBox.removeAllItems();
            if (medicos.isEmpty()) {
                medicoComboBox.addItem("No hay médicos para esta esp.");
            } else {
                for (String medicoId : medicos) {
                    medicoComboBox.addItem(medicoId);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar médicos: " + ex.getMessage(),
                    "Error RMI", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            medicoComboBox.removeAllItems();
            medicoComboBox.addItem("Error al cargar médicos");
        }
    }

    private void agendarNuevaCita() {
        String especialidad = (String) especialidadComboBox.getSelectedItem();
        String medicoId = (String) medicoComboBox.getSelectedItem(); // Este es el username del médico

        if (especialidad == null || especialidad.startsWith("No hay") || especialidad.startsWith("Error")) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una especialidad válida.", "Error de Agendamiento", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (medicoId == null || medicoId.startsWith("No hay") || medicoId.startsWith("Seleccione") || medicoId.startsWith("Error")) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un médico válido.", "Error de Agendamiento", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Date fechaSeleccionada = (Date) fechaSpinner.getValue();
        Date horaSeleccionada = (Date) horaSpinner.getValue();
        String motivo = motivoField.getText().trim();

        if (motivo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El motivo de la consulta es requerido.", "Error de Agendamiento", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SimpleDateFormat sdfFecha = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");

        String fechaStr = sdfFecha.format(fechaSeleccionada);
        String horaStr = sdfHora.format(horaSeleccionada);

        try {
            if (!citasStub.verificarDisponibilidadMedico(medicoId, fechaStr, horaStr)) {
                JOptionPane.showMessageDialog(this, "El médico " + medicoId +
                                " no está disponible en la fecha y hora seleccionadas.\n" +
                                "Por favor, elija otro horario o médico.",
                        "Conflicto de Horario", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Cita nuevaCita = new Cita(0, pacienteUser, medicoId, fechaStr, horaStr, motivo, especialidad);
            citasStub.agendarCita(nuevaCita);

            JOptionPane.showMessageDialog(this, "Cita agendada exitosamente con el Dr./Dra. " + medicoId +
                            " para el " + fechaStr + " a las " + horaStr + ".",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarCitas(); // Refrescar tabla
            motivoField.setText(""); // Limpiar campo motivo
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al agendar cita: " + ex.getMessage(),
                    "Error RMI", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void cancelarCitaSeleccionada() {
        int selectedRow = citasTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una cita de la tabla para cancelar.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int citaId = (int) tableModel.getValueAt(selectedRow, 0);
        String medico = (String) tableModel.getValueAt(selectedRow, 1);
        String fecha = (String) tableModel.getValueAt(selectedRow, 2);
        String hora = (String) tableModel.getValueAt(selectedRow, 3);

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea cancelar la cita ID: " + citaId +
                        "\nCon: " + medico + " el " + fecha + " a las " + hora + "?",
                "Confirmar Cancelación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                citasStub.cancelarCita(citaId, pacienteUser);
                JOptionPane.showMessageDialog(this, "Cita ID " + citaId + " cancelada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarCitas();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al cancelar cita: " + ex.getMessage(),
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