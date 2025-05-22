import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

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
        JButton cerrarSesionBtn = createButton("CERRAR SESIÓN ");
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
                new GestionarFactura().setVisible(true);
                dispose();
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
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Color de fondo con hover
                Color background = getModel().isRollover() ? new Color(30, 70, 100) : new Color(25, 55, 75);
                g2.setColor(background);

                // Fondo redondeado
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                super.paintComponent(g);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(255, 165, 0)); // Borde naranja
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
            }

            @Override
            public boolean contains(int x, int y) {
                // Área clickeable redonda
                Shape shape = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 30, 30);
                return shape.contains(x, y);
            }
        };

        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(180, 50));

        return button;
    }



}