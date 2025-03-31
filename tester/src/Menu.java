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
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(usuarioBtn, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(proveedoresBtn, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(productoBtn, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(facturasBtn, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(cerrarSesionBtn, gbc);
        // con este abro la de gestionar

        
        usuarioBtn.addActionListener(e -> {
            dispose();//cierra el menu actual
            new gestionarUsuario(); //abre la ventana de gestion de usuarios
        });


        productoBtn.addActionListener(e -> {
            String[] opciones = {"Crear Producto", "Gestionar Producto", "Cancelar"};
            int seleccion = JOptionPane.showOptionDialog(
                    this,
                    "Seleccione una opción:",
                    "Gestión de Productos",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
            );

            if (seleccion == 0) {
                new CrearProducto(); // Abre la ventana para crear productos
                dispose();
            } else if (seleccion == 1) {
                new gestionarProducto(); // Abre la ventana para gestionar productos
                dispose();
            }
        });


        // Acción para el botón PROVEEDORES
        proveedoresBtn.addActionListener(e -> {
            new CrearProveedor();
            // Se abre la interfaz de ingreso de proveedores
        });

        // Acción para el botón FACTURAS
        facturasBtn.addActionListener(e -> {
            String[] opciones = {"Crear Factura", "Gestionar Facturas", "Cancelar"};
            int seleccion = JOptionPane.showOptionDialog(
                    this,
                    "Seleccione una opción:",
                    "Gestión de Facturas",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
            );

            if (seleccion == 0) {
                new CrearFactura().setVisible(true); // Asegúrate de que esta clase exista
            } else if (seleccion == 1) {
                JOptionPane.showMessageDialog(this, "La función de gestión de facturas aún está en desarrollo.");
            }
        });

        // Cerrar sesión
        cerrarSesionBtn.addActionListener(e -> {
            new SignInUI(); // Se llama al constructor, que ya muestra la ventana
            dispose(); // Cierra el menú actual
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