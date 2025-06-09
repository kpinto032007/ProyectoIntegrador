import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CrearProducto extends JFrame {

    public CrearProducto() {
        super("Crear Producto"); // Título del JFrame
        setSize(400, 500);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel titleLabel = new JLabel("Crear Producto", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        // Campo ID
        JLabel idProductoLabel = new JLabel("Id Producto:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(idProductoLabel, gbc);

        JTextField idField = new JTextField(15);
        gbc.gridx = 1;
        add(idField, gbc);

        // Campo Nombre
        JLabel nombreLabel = new JLabel("Nombre:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(nombreLabel, gbc);

        JTextField nombreField = new JTextField();
        gbc.gridx = 1;
        add(nombreField, gbc);
        idField.addActionListener(e -> nombreField.requestFocusInWindow());

        // Campo Cantidad
        JLabel cantidadLabel = new JLabel("Cantidad:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(cantidadLabel, gbc);

        JTextField cantidadField = new JTextField();
        gbc.gridx = 1;
        add(cantidadField, gbc);
        nombreField.addActionListener(e -> cantidadField.requestFocusInWindow());

        // Campo Precio
        JLabel precioLabel = new JLabel("Precio:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(precioLabel, gbc);

        JTextField precioField = new JTextField();
        gbc.gridx = 1;
        add(precioField, gbc);
        cantidadField.addActionListener(e -> precioField.requestFocusInWindow());

        // Campo Descripción
        JLabel descripcionLabel = new JLabel("Descripción:");
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(descripcionLabel, gbc);

        JTextArea descripcionField = new JTextArea(3, 20);
        descripcionField.setLineWrap(true);
        descripcionField.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(descripcionField);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        add(scroll, gbc);
        precioField.addActionListener(e -> descripcionField.requestFocusInWindow());

        // Botón Registrar
        JButton registerButton = new JButton("Registrar");
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        add(registerButton, gbc);

        // Botón Volver
        JButton volver = new JButton("Volver");
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(volver, gbc);

        // Acción Registrar
        registerButton.addActionListener((ActionEvent e) -> {
            String id = idField.getText().trim();
            String nombre = nombreField.getText().trim();
            String cantidad = cantidadField.getText().trim();
            String precio = precioField.getText().trim();
            String descripcion = descripcionField.getText().trim();

            if (id.isEmpty() || nombre.isEmpty() || cantidad.isEmpty() || precio.isEmpty() || descripcion.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, completa todos los campos.");
                return;
            }

            try {
                long cantidadParseada = Long.parseLong(cantidad);
                double precioParseado = Double.parseDouble(precio);

                if (CrearProducto(id, nombre, cantidadParseada, precioParseado, descripcion)) {
                    JOptionPane.showMessageDialog(this, "Registro exitoso");

                } else {
                    JOptionPane.showMessageDialog(this, "Error al registrar el producto.");
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error: Ingresa solo números en Cantidad y Precio.", "Error de validación", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Acción Volver
        volver.addActionListener((ActionEvent e) -> {
            dispose(); // Cierra esta ventana

            String[] opciones = {"Crear producto", "Gestionar Producto", "Volver"};
            int opcion = JOptionPane.showOptionDialog(
                    null,
                    "Seleccione una opción:",
                    "Gestión de Productos",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
            );

            switch (opcion) {
                case 0 -> new CrearProducto().setVisible(true); // Solo crea UNA nueva
                case 1 -> new gestionarProducto().setVisible(true);
                default -> {} // No hacer nada
            }
        });

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public static boolean CrearProducto(String id, String nombre, long cantidad, double precio, String descripcion) {
        String sql = "INSERT INTO tb_productos (idProducto, nombre, cantidad, precio, descripcion, estado) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection cn = Conexion.conectar(); PreparedStatement pst = cn.prepareStatement(sql)) {
            pst.setString(1, id);
            pst.setString(2, nombre);
            pst.setLong(3, cantidad);
            pst.setDouble(4, precio);
            pst.setString(5, descripcion);
            pst.setInt(6, 1); // Estado activo

            int filasAfectadas = pst.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al crear producto: " + e.getMessage());
            return false;
        }
    }
}