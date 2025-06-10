import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class CrearProveedor extends JFrame {

    public CrearProveedor() {
        // Configuración básica del JFrame
        JFrame frame = new JFrame("Crear proveedor");
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setSize(450, 600);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridBagLayout());
        frame.setBackground(Color.decode("#B7A7A9")); // Granite como fondo general

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel titleLabel = new JLabel("Crear Proveedor", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.BLACK); // Texto blanco sobre fondo oscuro
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        frame.add(titleLabel, gbc);

        // Panel para campos con fondo beige
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.decode("#F6ECE3")); // Beige
        GridBagConstraints gbcForm = new GridBagConstraints();
        gbcForm.insets = new Insets(8, 8, 8, 8);
        gbcForm.fill = GridBagConstraints.HORIZONTAL;

        // Campos de entrada

        // Id Proveedor
        JLabel idLabel = new JLabel("Id Proveedor:");
        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        idLabel.setForeground(Color.BLACK);
        gbcForm.gridx = 0;
        gbcForm.gridy = 0;
        formPanel.add(idLabel, gbcForm);

        JTextField idField = new JTextField(15);
        idField.setBackground(Color.WHITE);
        idField.setForeground(Color.BLACK);
        idField.setBorder(BorderFactory.createLineBorder(Color.decode("#91766E")));
        gbcForm.gridx = 1;
        formPanel.add(idField, gbcForm);

        // Nombre
        JLabel nombreLabel = new JLabel("Nombre:");
        nombreLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nombreLabel.setForeground(Color.BLACK);
        gbcForm.gridx = 0;
        gbcForm.gridy = 1;
        formPanel.add(nombreLabel, gbcForm);

        JTextField nombreField = new JTextField(15);
        nombreField.setBackground(Color.WHITE);
        nombreField.setForeground(Color.BLACK);
        nombreField.setBorder(BorderFactory.createLineBorder(Color.decode("#91766E")));
        gbcForm.gridx = 1;
        formPanel.add(nombreField, gbcForm);
        idField.addActionListener(e -> nombreField.requestFocusInWindow());

        // Apellido
        JLabel apellidoLabel = new JLabel("Apellido:");
        apellidoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        apellidoLabel.setForeground(Color.BLACK);
        gbcForm.gridx = 0;
        gbcForm.gridy = 2;
        formPanel.add(apellidoLabel, gbcForm);

        JTextField apellidoField = new JTextField(15);
        apellidoField.setBackground(Color.WHITE);
        apellidoField.setForeground(Color.BLACK);
        apellidoField.setBorder(BorderFactory.createLineBorder(Color.decode("#91766E")));
        gbcForm.gridx = 1;
        formPanel.add(apellidoField, gbcForm);
        nombreField.addActionListener(e -> apellidoField.requestFocusInWindow());

        // Cédula
        JLabel cedulaLabel = new JLabel("Cédula:");
        cedulaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cedulaLabel.setForeground(Color.BLACK);
        gbcForm.gridx = 0;
        gbcForm.gridy = 3;
        formPanel.add(cedulaLabel, gbcForm);

        JTextField cedulaField = new JTextField(15);
        cedulaField.setBackground(Color.WHITE);
        cedulaField.setForeground(Color.BLACK);
        cedulaField.setBorder(BorderFactory.createLineBorder(Color.decode("#91766E")));
        gbcForm.gridx = 1;
        formPanel.add(cedulaField, gbcForm);
        apellidoField.addActionListener(e -> cedulaField.requestFocusInWindow());

        // Teléfono
        JLabel telefonoLabel = new JLabel("Teléfono:");
        telefonoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        telefonoLabel.setForeground(Color.BLACK);
        gbcForm.gridx = 0;
        gbcForm.gridy = 4;
        formPanel.add(telefonoLabel, gbcForm);

        JTextField telefonoField = new JTextField(15);
        telefonoField.setBackground(Color.WHITE);
        telefonoField.setForeground(Color.BLACK);
        telefonoField.setBorder(BorderFactory.createLineBorder(Color.decode("#91766E")));
        gbcForm.gridx = 1;
        formPanel.add(telefonoField, gbcForm);
        cedulaField.addActionListener(e -> telefonoField.requestFocusInWindow());

        // Empresa Proveedora
        JLabel empresaLabel = new JLabel("Empresa:");
        empresaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        empresaLabel.setForeground(Color.BLACK);
        gbcForm.gridx = 0;
        gbcForm.gridy = 5;
        formPanel.add(empresaLabel, gbcForm);

        JTextField empresaField = new JTextField(15);
        empresaField.setBackground(Color.WHITE);
        empresaField.setForeground(Color.BLACK);
        empresaField.setBorder(BorderFactory.createLineBorder(Color.decode("#91766E")));
        gbcForm.gridx = 1;
        formPanel.add(empresaField, gbcForm);
        telefonoField.addActionListener(e -> empresaField.requestFocusInWindow());

        // Añadir panel al frame principal
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        frame.add(formPanel, gbc);

        // Botón Registrar
        JButton registerButton = new JButton("Registrar");
        registerButton.setBackground(Color.decode("#91766E")); // Brown
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerButton.setFocusPainted(false);
        registerButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        frame.add(registerButton, gbc);

        // Botón Volver
        JButton volverButton = new JButton("Volver");
        volverButton.setBackground(Color.decode("#B7A7A9")); // Granite
        volverButton.setForeground(Color.BLACK);
        volverButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        volverButton.setFocusPainted(false);
        volverButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        gbc.gridy = 3;
        frame.add(volverButton, gbc);

        // Acción botón Volver
        volverButton.addActionListener(e -> {
            frame.dispose();
            String[] opciones = {"Crear proveedor", "Gestionar Proveedor", "Volver"};
            int opcion = JOptionPane.showOptionDialog(
                    null,
                    "Seleccione una opción:",
                    "Gestión de Proveedor",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
            );
            switch (opcion) {
                case 0 -> new CrearProveedor().setVisible(true);
                case 1 -> new gestionarProveedor().setVisible(true);
                default -> {}
            }
        });

        // Acción botón Registrar
        registerButton.addActionListener(e -> {
            String IdProveedor = idField.getText().trim();
            String nombre = nombreField.getText().trim();
            String apellido = apellidoField.getText().trim();
            String cedula = cedulaField.getText().trim();
            String telefono = telefonoField.getText().trim();
            String empresa_proveedora = empresaField.getText().trim();

            if (IdProveedor.isEmpty() || nombre.isEmpty() || apellido.isEmpty() || cedula.isEmpty() || telefono.isEmpty() || empresa_proveedora.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Por favor, completa todos los campos.");
                return;
            }

            try {
                long Cedula = Long.parseLong(cedula);
                long Telefono = Long.parseLong(telefono);

                if (CrearProveedor(IdProveedor, nombre, apellido, Cedula, Telefono, empresa_proveedora)) {
                    JOptionPane.showMessageDialog(frame, "Registro exitoso");
                    frame.dispose();
                } else {
                    JOptionPane.showMessageDialog(frame, "Error al registrar proveedor.");
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Error: Ingresa solo números en Cédula y Teléfono.", "Error de validación", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setVisible(true);
    }

    public static boolean CrearProveedor(String IdProveedor, String nombre, String apellido, long cedula, long telefono, String empresa_proveedora) {
        String sql = "INSERT INTO tb_proveedores (idProveedor, nombre, apellido, cedula, telefono, empresa_proveedora, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection cn = Conexion.conectar(); PreparedStatement pst = cn.prepareStatement(sql)) {
            pst.setString(1, IdProveedor);
            pst.setString(2, nombre);
            pst.setString(3, apellido);
            pst.setLong(4, cedula);
            pst.setLong(5, telefono);
            pst.setString(6, empresa_proveedora);
            pst.setInt(7, 1);
            int filasAfectadas = pst.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al crear proveedor: " + e.getMessage());
            return false;
        }
    }

    // Main para ejecutar
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CrearProveedor().setVisible(true));
    }
}