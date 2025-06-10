import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CrearProducto extends JFrame {

    public CrearProducto() {
        super("Crear Producto");
        setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Colores
        Color beige = Color.decode("#F6ECE3");
        Color brown = Color.decode("#91766E");
        Color granite = Color.decode("#B7A7A9");
        Color black = Color.decode("#000000");
        Color white = Color.decode("#FFFFFF");

        // Panel principal
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.decode("#ECEBEA"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Panel de formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(beige);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título
        JLabel titleLabel = new JLabel("Crear Producto", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(black);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(titleLabel, gbc);

        // Campos
        String[] labels = {"Id Producto:", "Nombre:", "Cantidad:", "Precio:", "Descripción:"};
        JTextField[] textFields = new JTextField[4];
        JTextArea descripcionField = new JTextArea(3, 20);
        JScrollPane scroll = new JScrollPane(descripcionField);
        descripcionField.setLineWrap(true);
        descripcionField.setWrapStyleWord(true);

        gbc.gridwidth = 1;

        for (int i = 0; i < labels.length; i++) {
            gbc.gridy = i + 1;
            gbc.gridx = 0;
            JLabel label = new JLabel(labels[i]);
            label.setForeground(black);
            formPanel.add(label, gbc);

            gbc.gridx = 1;
            if (i < 4) {
                textFields[i] = new JTextField(15);
                formPanel.add(textFields[i], gbc);
            } else {
                gbc.gridwidth = 2;
                formPanel.add(scroll, gbc);
            }
        }

        // Botón Registrar
        JButton registerButton = new JButton("Registrar");
        registerButton.setBackground(brown);
        registerButton.setForeground(white);
        registerButton.setFocusPainted(false);
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        formPanel.add(registerButton, gbc);

        // Botón Volver
        JButton volverButton = new JButton("Volver");
        volverButton.setBackground(granite);
        volverButton.setForeground(black);
        volverButton.setFocusPainted(false);
        volverButton.setFont(new Font("Arial", Font.PLAIN, 13));
        gbc.gridy = 7;
        formPanel.add(volverButton, gbc);

        mainPanel.add(formPanel);
        add(mainPanel);

        // Lógica de registro
        registerButton.addActionListener((ActionEvent e) -> {
            String id = textFields[0].getText().trim();
            String nombre = textFields[1].getText().trim();
            String cantidad = textFields[2].getText().trim();
            String precio = textFields[3].getText().trim();
            String descripcion = descripcionField.getText().trim();

            if (id.isEmpty() || nombre.isEmpty() || cantidad.isEmpty() || precio.isEmpty() || descripcion.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, completa todos los campos.");
                return;
            }

            try {
                long cantidadVal = Long.parseLong(cantidad);
                double precioVal = Double.parseDouble(precio);
                if (CrearProducto(id, nombre, cantidadVal, precioVal, descripcion)) {
                    JOptionPane.showMessageDialog(this, "Registro exitoso");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al registrar el producto.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Cantidad y Precio deben ser numéricos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Lógica volver
        volverButton.addActionListener(e -> {
            dispose();
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
                case 0 -> new CrearProducto().setVisible(true);
                case 1 -> new gestionarProducto().setVisible(true);
            }
        });

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
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al crear producto: " + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        new CrearProducto();
    }
}
