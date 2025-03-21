import javax.swing.*;
import java.awt.*;

public class Menu extends JFrame {

    public Menu() {
        setTitle("Dashboard");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(173, 216, 230)); // Color celeste de fondo

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Crear botones
        JButton usuarioBtn = createButton("USUARIO");
        JButton proveedoresBtn = createButton("PROVEEDORES");
        JButton productoBtn = createButton("PRODUCTO");
        JButton facturasBtn = createButton("FACTURAS");
        JButton cerrarSesionBtn = createButton("CERRAR SESIÓN ▶");

        // Posicionar botones en el grid
        gbc.gridx = 0; gbc.gridy = 0; add(usuarioBtn, gbc);
        gbc.gridx = 1; gbc.gridy = 0; add(proveedoresBtn, gbc);
        gbc.gridx = 0; gbc.gridy = 1; add(productoBtn, gbc);
        gbc.gridx = 1; gbc.gridy = 1; add(facturasBtn, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; add(cerrarSesionBtn, gbc);

        // con este abro la de gestionar
        usuarioBtn.addActionListener(e -> {
                new gestionarUsuario(); //abre la ventana de gestion de usuarios
                dispose();//cierra el menu actual
        });


        setVisible(true);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(25, 55, 75)); // Azul oscuro
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(new Color(255, 165, 0), 2)); // Borde naranja
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(180, 50));

        return button;
    }


}