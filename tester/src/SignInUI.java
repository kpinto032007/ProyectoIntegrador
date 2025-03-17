import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SignInUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(SignInUI::createAndShowGUI);
    }

    static void createAndShowGUI() {
        JFrame frame = new JFrame("Sign In");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // Panel izquierdo con color celeste
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(173, 216, 230));
        leftPanel.setPreferredSize(new Dimension(200, frame.getHeight()));
        leftPanel.setLayout(new GridBagLayout());
        frame.add(leftPanel, BorderLayout.WEST);

        // Logo de la aplicación
        JLabel logoLabel = new JLabel("TECHSTOCK", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Arial", Font.BOLD, 18));
        leftPanel.add(logoLabel);

        // Panel derecho con color azul oscuro
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(new Color(25, 55, 75));
        rightPanel.setLayout(new GridBagLayout());
        frame.add(rightPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título SIGN IN
        JLabel signInLabel = new JLabel("SIGN IN", SwingConstants.CENTER);
        signInLabel.setFont(new Font("Serif", Font.BOLD, 24));
        signInLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        rightPanel.add(signInLabel, gbc);

        // Campo de usuario
        JTextField userField = new JTextField(" ");
        userField.setPreferredSize(new Dimension(200, 30));
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        rightPanel.add(userField, gbc);

        // Campo de contraseña
        JPasswordField passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(200, 30));
        gbc.gridy = 2;
        rightPanel.add(passwordField, gbc);

        // 🔹 Mover el foco con Enter en usuario
        userField.addActionListener(e -> passwordField.requestFocusInWindow());
        // 🔹 Iniciar sesión con Enter en la contraseña
        passwordField.addActionListener(e -> iniciarSesion(userField, passwordField, frame));

        // Enlace de recuperación de contraseña
        JLabel forgotPasswordLabel = new JLabel("¿Forgot your password?");
        forgotPasswordLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        forgotPasswordLabel.setForeground(Color.WHITE);
        gbc.gridy = 3;
        rightPanel.add(forgotPasswordLabel, gbc);

        // Panel de botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);



        // Botón LOGIN
        JButton loginButton = new JButton("LOGIN");
        JButton signupButton = new JButton("SIGN UP");
        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);


        gbc.gridy = 4;
        gbc.gridwidth = 2;
        rightPanel.add(buttonPanel, gbc);

        loginButton.addActionListener(e -> iniciarSesion(userField, passwordField, frame));
        signupButton.addActionListener(e -> {
            frame.dispose(); // Cierra la ventana de inicio de sesión
            new FormularioRegistrar(); // Abre la ventana de registro
        });



        frame.setVisible(true);
    }

    private static void iniciarSesion(JTextField userField, JPasswordField passwordField, JFrame frame) {
        String usuario = userField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        try (Connection cn = Conexion.conectar();
             PreparedStatement stmt = cn.prepareStatement("SELECT * FROM tb_usuarios WHERE usuario = ? AND password = ?")) {

            stmt.setString(1, usuario);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(frame, "Inicio de sesión exitoso");
                frame.dispose(); // Cerrar la ventana de inicio
                new Menu(); // Aquí debes abrir la ventana principal
            } else {
                JOptionPane.showMessageDialog(frame, "Usuario o contraseña incorrectos");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage());
        }

    }





}
