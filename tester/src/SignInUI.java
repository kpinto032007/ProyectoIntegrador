import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SignInUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(SignInUI::createAndShowGUI);
    }

    private static void createAndShowGUI() {
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
        JTextField userField = new JTextField("USER. . .");
        userField.setPreferredSize(new Dimension(200, 30));
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        rightPanel.add(userField, gbc);

        // Campo de contraseña
        JPasswordField passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(200, 30));
        gbc.gridy = 2;
        rightPanel.add(passwordField, gbc);

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

        // Acción del botón LOGIN
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText();
                String password = new String(passwordField.getPassword());
                if (username.equals("admin") && password.equals("1234")) {
                    JOptionPane.showMessageDialog(frame, "Login exitoso", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "Usuario o contraseña incorrectos", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Acción del botón SIGN UP
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "Funcionalidad de registro no implementada", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        frame.setVisible(true);
    }
}
