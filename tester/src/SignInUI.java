import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class SignInUI {

    public SignInUI() {
        createAndShowGUI();
    }

    public void createAndShowGUI() {
        JFrame frame = new JFrame("Sign In");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);

        // Fondo general claro con color #EDE9E3
        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setBackground(Color.decode("#EDE9E3"));
        backgroundPanel.setLayout(new GridBagLayout());

        // Panel formulario translúcido con color #EDE9E3 y sombra marrón grisáceo
        JPanel formPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fondo translúcido beige claro (#EDE9E3)
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
                g2.setColor(Color.decode("#EDE9E3"));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                // Sombra suave marrón grisáceo (#A38F85)
                g2.setColor(new Color(163, 143, 133, 80)); // con alpha para sombra
                g2.fillRoundRect(5, 5, getWidth(), getHeight(), 30, 30);
                g2.dispose();

                super.paintComponent(g);
            }
        };
        formPanel.setOpaque(false);
        formPanel.setLayout(new GridBagLayout());
        formPanel.setPreferredSize(new Dimension(370, 340));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Ícono usuario
        JLabel userIcon = new JLabel(new ImageIcon(
                new ImageIcon("src/imagenes/img.png").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)
        ));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        formPanel.add(userIcon, gbc);

        // Campo usuario
        JTextField userField = new JTextField();
        addPlaceholder(userField, "Usuario");
        styleTextField(userField);
        gbc.gridx = 1;
        formPanel.add(userField, gbc);

        // Ícono contraseña
        JLabel passIcon = new JLabel(new ImageIcon(
                new ImageIcon("src/imagenes/img_1.png").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)
        ));
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(passIcon, gbc);

        // Campo contraseña
        JPasswordField passwordField = new JPasswordField();
        addPlaceholder(passwordField, "Clave");
        styleTextField(passwordField);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        // Botón login
        JButton loginButton = new JButton("→");
        styleButton(loginButton, Color.decode("#E7D7C9"), Color.decode("#000000"));
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 24));
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(loginButton, gbc);

        // Botón registro
        JButton signupButton = new JButton("SIGN UP");
        styleButton(signupButton, Color.decode("#D4B2A7"), Color.decode("#000000"));
        signupButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        signupButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signupButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                signupButton.setBackground(Color.decode("#E7D7C9"));
            }
            public void mouseExited(MouseEvent e) {
                signupButton.setBackground(Color.decode("#D4B2A7"));
            }
        });
        gbc.gridy = 3;
        formPanel.add(signupButton, gbc);

        // Listeners
        loginButton.addActionListener(e -> iniciarSesion(userField, passwordField, frame));
        signupButton.addActionListener(e -> {
            frame.dispose();
            new FormularioRegistrar();
        });

        userField.addActionListener(e -> passwordField.requestFocusInWindow());
        passwordField.addActionListener(e -> iniciarSesion(userField, passwordField, frame));

        backgroundPanel.add(formPanel);
        frame.setContentPane(backgroundPanel);
        frame.setVisible(true);
    }

    // Estilo campos de texto
    private void styleTextField(JTextComponent field) {
        field.setPreferredSize(new Dimension(180, 35));
        field.setBackground(Color.decode("#EDE9E3"));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#A38F85"), 2, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setForeground(Color.decode("#A38F85"));
    }

    // Estilo botones
    private void styleButton(JButton button, Color bg, Color fg) {
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.decode("#A38F85"), 2, true));
        button.setBackground(bg);
        button.setForeground(fg);
        button.setPreferredSize(new Dimension(150, 40));
    }


    private void iniciarSesion(JTextField userField, JPasswordField passwordField, JFrame frame) {
        String usuario = userField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        try (Connection cn = Conexion.conectar();
             PreparedStatement stmt = cn.prepareStatement("SELECT * FROM tb_usuarios WHERE usuario = ? AND password = ?")) {

            stmt.setString(1, usuario);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                frame.dispose();
                new Menu();
            } else {
                JOptionPane.showMessageDialog(frame, "Usuario o contraseña incorrectos");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage());
        }
    }

    // Método para agregar placeholder
    private void addPlaceholder(JTextComponent field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(Color.decode("#CDC6C3"));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.decode("#000000"));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.decode("#CDC6C3"));
                }
            }
        });
    }
}
