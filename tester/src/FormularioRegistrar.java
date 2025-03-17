import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FormularioRegistrar extends JFrame {

    public FormularioRegistrar() {

        JFrame frame = new JFrame("Registro de Usuario");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 500);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel titleLabel = new JLabel("Registro de Usuario", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        frame.add(titleLabel, gbc);

        // Campo Nombre
        JLabel nameLabel = new JLabel("Nombre:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        frame.add(nameLabel, gbc);


        JTextField nameField = new JTextField();
        gbc.gridx = 1;
        frame.add(nameField, gbc);



        // Campo Apellido
        JLabel apellidoLabel = new JLabel("Apellido:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        frame.add(apellidoLabel, gbc);

        JTextField apellidoField = new JTextField();
        gbc.gridx = 1;
        frame.add(apellidoField, gbc);

        nameField.addActionListener(e -> apellidoField.requestFocusInWindow());

        // Campo Teléfono
        JLabel telefonoLabel = new JLabel("Teléfono:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        frame.add(telefonoLabel, gbc);

        JTextField telefonoField = new JTextField();
        gbc.gridx = 1;
        frame.add(telefonoField, gbc);
        apellidoField.addActionListener(e -> telefonoField.requestFocusInWindow());

        // Campo Usuario
        JLabel usuarioLabel = new JLabel("Usuario:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        frame.add(usuarioLabel, gbc);

        JTextField usuarioField = new JTextField();
        gbc.gridx = 1;
        frame.add(usuarioField, gbc);
        telefonoField.addActionListener(e -> usuarioField.requestFocusInWindow());

        // Campo Contraseña
        JLabel passwordLabel = new JLabel("Contraseña:");
        gbc.gridx = 0;
        gbc.gridy = 5;
        frame.add(passwordLabel, gbc);

        JPasswordField passwordField = new JPasswordField();
        gbc.gridx = 1;
        frame.add(passwordField, gbc);
        usuarioField.addActionListener(e -> passwordField.requestFocusInWindow());

        // Botón de registro
        JButton registerButton = new JButton("Registrar");
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        frame.add(registerButton, gbc);

// Acción del botón
        registerButton.addActionListener(e -> {
            String nombre = nameField.getText().trim();
            String apellido = apellidoField.getText().trim();
            String telefono = telefonoField.getText().trim();
            String usuario = usuarioField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty() || usuario.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Por favor, completa todos los campos.");
            } else {
                if (registrarUsuario(nombre, apellido, telefono, usuario, password)) {
                    JOptionPane.showMessageDialog(frame, "Registro exitoso");
                    frame.dispose(); // Cierra la ventana después de registrar
                } else {
                    JOptionPane.showMessageDialog(frame, "Error al registrar usuario.");
                }
            }
        });

        frame.setVisible(true);
    }

    // Registro base de datos
    private static boolean registrarUsuario(String nombre, String apellido, String telefono, String usuario, String password) {
        String sql = "INSERT INTO tb_usuarios (nombre, apellido, telefono, usuario, password, estado) VALUES (?, ?, ?, ?, ?,?)";

        try (Connection cn = Conexion.conectar(); PreparedStatement pst = cn.prepareStatement(sql)) {
            pst.setString(1, nombre);
            pst.setString(2, apellido);
            pst.setString(3, telefono);
            pst.setString(4, usuario);
            pst.setString(5, password);
            pst.setInt(6, 1);  // Estado fijo en 1

            int filasAfectadas = pst.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "❌ Error al registrar usuario: " + e.getMessage());
            return false;
        }
    }
}