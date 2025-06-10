import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FormularioRegistrar extends JFrame {

    public FormularioRegistrar() {
        JFrame frame = new JFrame("Registro de Usuario");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420, 520);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridBagLayout());

        // Paleta de colores nude
        Color ivory = Color.decode("#EDE9E3");
        Color nude = Color.decode("#E7D7C9");
        Color rose = Color.decode("#D4B2A7");
        Color stone = Color.decode("#CDC6C3");
        Color latte = Color.decode("#A38F85");

        frame.getContentPane().setBackground(ivory);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Registro de Usuario", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 22));
        titleLabel.setForeground(rose);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Añadir título en la fila 0, ocupando 2 columnas
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 20, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        frame.add(titleLabel, gbc);

        Font labelFont = new Font("Georgia", Font.PLAIN, 14);
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(12, 12, 12, 12);

        // Fila 1 - Nombre
        gbc.gridy = 1;
        gbc.gridx = 0;
        frame.add(crearLabel("Nombre:", labelFont, latte), gbc);
        gbc.gridx = 1;
        JTextField nameField = crearCampoTexto(stone);
        frame.add(nameField, gbc);

        // Fila 2 - Apellido
        gbc.gridy = 2;
        gbc.gridx = 0;
        frame.add(crearLabel("Apellido:", labelFont, latte), gbc);
        gbc.gridx = 1;
        JTextField apellidoField = crearCampoTexto(stone);
        frame.add(apellidoField, gbc);

        // Fila 3 - Teléfono
        gbc.gridy = 3;
        gbc.gridx = 0;
        frame.add(crearLabel("Teléfono:", labelFont, latte), gbc);
        gbc.gridx = 1;
        JTextField telefonoField = crearCampoTexto(stone);
        frame.add(telefonoField, gbc);

        // Fila 4 - Usuario
        gbc.gridy = 4;
        gbc.gridx = 0;
        frame.add(crearLabel("Usuario:", labelFont, latte), gbc);
        gbc.gridx = 1;
        JTextField usuarioField = crearCampoTexto(stone);
        frame.add(usuarioField, gbc);

        // Fila 5 - Contraseña
        gbc.gridy = 5;
        gbc.gridx = 0;
        frame.add(crearLabel("Contraseña:", labelFont, latte), gbc);
        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(labelFont);
        passwordField.setBackground(Color.WHITE);
        passwordField.setBorder(BorderFactory.createLineBorder(stone));
        frame.add(passwordField, gbc);

        // Fila 6 - Botón Registrar
        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JButton registerButton = crearBoton("Registrar", latte, Color.WHITE);
        frame.add(registerButton, gbc);

        // Fila 7 - Botón Volver
        gbc.gridy = 7;
        JButton volver = crearBoton("Volver", stone, Color.DARK_GRAY);
        frame.add(volver, gbc);

        // Acción para cambiar foco al pulsar Enter en cada campo
        nameField.addActionListener(e -> apellidoField.requestFocusInWindow());
        apellidoField.addActionListener(e -> telefonoField.requestFocusInWindow());
        telefonoField.addActionListener(e -> usuarioField.requestFocusInWindow());
        usuarioField.addActionListener(e -> passwordField.requestFocusInWindow());

        volver.addActionListener(e -> {
            frame.dispose();
            new SignInUI();
        });

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
                    frame.dispose();
                    new SignInUI();
                } else {
                    JOptionPane.showMessageDialog(frame, "Error al registrar usuario.");
                }
            }
        });

        frame.setVisible(true);
    }

    private JLabel crearLabel(String texto, Font fuente, Color color) {
        JLabel label = new JLabel(texto);
        label.setFont(fuente);
        label.setForeground(color);
        return label;
    }

    private JTextField crearCampoTexto(Color bordeColor) {
        JTextField campo = new JTextField();
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setBackground(Color.WHITE);
        campo.setBorder(BorderFactory.createLineBorder(bordeColor));
        return campo;
    }

    private JButton crearBoton(String texto, Color fondo, Color textoColor) {
        JButton boton = new JButton(texto);
        boton.setFocusPainted(false);
        boton.setFont(new Font("Georgia", Font.BOLD, 14));
        boton.setBackground(fondo);
        boton.setForeground(textoColor);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return boton;
    }

    private static boolean registrarUsuario(String nombre, String apellido, String telefono, String usuario, String password) {
        String sql = "INSERT INTO tb_usuarios (nombre, apellido, telefono, usuario, password, estado) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection cn = Conexion.conectar(); PreparedStatement pst = cn.prepareStatement(sql)) {
            pst.setString(1, nombre);
            pst.setString(2, apellido);
            pst.setString(3, telefono);
            pst.setString(4, usuario);
            pst.setString(5, password);
            pst.setInt(6, 1);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, " Error al registrar usuario: " + e.getMessage());
            return false;
        }
    }
}
