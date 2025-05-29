import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class CrearProveedor extends JFrame{

    public CrearProveedor() {
       JFrame frame = new JFrame("Crear proveedor");
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setSize(400,500);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;//los componentes ocupran todoo el ancho disponible
        // Título
        JLabel titleLabel = new JLabel("Crear Proveedor", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;//ocupa dos columnas
        frame.add(titleLabel, gbc);

        //Id producto
        JLabel IdProductoLabel = new JLabel("Id Proveedor:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        frame.add(IdProductoLabel, gbc);

        JTextField IdField = new JTextField(15); //ingresar el id
        gbc.gridx = 1;
        frame.add(IdField, gbc); // al campo


        //Nombre
        JLabel nombreLabel = new JLabel("Nombre: ");
        gbc.gridx =0;
        gbc.gridy =2;
        gbc.gridwidth = 1;
        frame.add(nombreLabel,gbc);


        JTextField nombreField = new JTextField();
        gbc.gridx=1;
        frame.add(nombreField,gbc);
        IdField.addActionListener(e -> nombreField.requestFocusInWindow());


        //Apellido
        JLabel apellidoLabel= new JLabel("Apellido: ");
        gbc.gridx= 0;
        gbc.gridy= 3;
        gbc.gridwidth =1;
        frame.add(apellidoLabel,gbc);

        JTextField apellidoField = new JTextField();
        gbc.gridx= 1;
        frame.add(apellidoField,gbc);
        nombreField.addActionListener(e ->apellidoField.requestFocusInWindow());

        //Cedula
        JLabel cedulaLabel= new JLabel("Cédula: ");
        gbc.gridx =0;
        gbc.gridy =4;
        gbc.gridwidth = 1;
        frame.add(cedulaLabel,gbc);


        JTextField cedulaField = new JTextField();
        gbc.gridx=1;
        frame.add(cedulaField,gbc);
        apellidoField.addActionListener(e -> cedulaField.requestFocusInWindow());


        JLabel telefonoLabel = new JLabel("Telefono: ");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        frame.add(telefonoLabel, gbc);

        JTextField telefonoField = new JTextField();
        gbc.gridx=1;
        frame.add(telefonoField,gbc);
        cedulaField.addActionListener(e -> telefonoField.requestFocusInWindow());

        //empresa proveedora
        JLabel empresaLabel = new JLabel("Empresa Proveedora: ");
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        frame.add(empresaLabel, gbc);

        JTextField empresaField = new JTextField();
        gbc.gridx=1;
        frame.add(empresaField,gbc);
        telefonoField.addActionListener(e -> empresaField.requestFocusInWindow());


        frame.setVisible(true);

        // Botón de registro
        JButton registerButton = new JButton("Registrar");
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        frame.add(registerButton, gbc);

        // Botón "Volver"
        JButton volver = new JButton("Volver");
        gbc.gridx = 0;
        gbc.gridy = 8; // Debe estar en la siguiente fila
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 0, 0, 0); // Margen superior
        gbc.anchor = GridBagConstraints.CENTER; // Centrar el botón
        frame.add(volver,gbc);

        volver.addActionListener(e -> {
            frame.dispose(); // Cierra la ventana actual
            // Abre la ventana de inicio de sesión
            String[] opciones= {" Crear proveedor", "Gestionar Proveedor", "Volver"};
            int opcion = JOptionPane.showOptionDialog(
                    null,
                    "Seleccione una opción:",
                    "Gestión de Proveedor",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
            );
            switch (opcion) {
                case 0 -> new CrearProveedor().setVisible(true);
                case 1 -> new gestionarProveedor().setVisible(true);
                default -> { }
            }

        });
        registerButton.addActionListener(e -> {
            String IdProveedor= IdField.getText().trim();
            String nombre = nombreField.getText().trim();
            String apellido = apellidoField.getText().trim();
            String cedula = cedulaField.getText().trim();
            String telefono = telefonoField.getText().trim();
            String empresa_proveedora = empresaField.getText().trim();


            if (IdProveedor.isEmpty() || nombre.isEmpty() || apellido.isEmpty() || cedula.isEmpty() || telefono.isEmpty() || empresa_proveedora.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Por favor, completa todos los campos.");
                return;
            }
            try {
                long Cedula = Long.parseLong(cedula);  // Convierte a long
                long Telefono = Long.parseLong(telefono);


                //aqui llama a la funcion registrar usuarioven la base de datos
                if (CrearProveedor(IdProveedor,nombre,apellido,Cedula,Telefono, empresa_proveedora)) {
                    JOptionPane.showMessageDialog(frame, "Registro exitoso");
                   // Cierra la ventana después de registrar


                } else {
                    JOptionPane.showMessageDialog(frame, "Error al registrar usuario.");
                }

            } catch (NumberFormatException ex) {
                // Si el usuario ingresa un valor no numérico, muestra un mensaje de error
                JOptionPane.showMessageDialog(null, "Error: Ingresa solo números en Cedula y Telefono.", "Error de validación", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    public static boolean  CrearProveedor(String IdProveedor, String nombre, String apellido, long cedula, long telefono, String empresa_proveedora){
        String sql = "INSERT INTO tb_proveedores (idProveedor, nombre, apellido, cedula, telefono, empresa_proveedora, estado) VALUES (?, ?, ?, ?, ?,?,?)";
        //proteger contra inyecciones sql y poner los valores despues

        try (Connection cn = Conexion.conectar(); PreparedStatement pst = cn.prepareStatement(sql)) {//preparamos la sentencia sql para q reciba los valores
            //llenamos los ? con los datos ingresados por el usuario
            pst.setString(1, IdProveedor);
            pst.setString(2, nombre);
            pst.setString(3, apellido);
            pst.setLong(4, cedula);
            pst.setLong(5, telefono);
            pst.setString(6, empresa_proveedora);
            pst.setInt(7, 1);  // Estado fijo en 1

            //ejecutamos la inserccion y verificamos si se efectó alguna fila
            int filasAfectadas = pst.executeUpdate();
            //si se incertó correctamente, retorna true
            return filasAfectadas > 0;
        } catch (SQLException e) {
            //si hay error, muestra un mensaje y retorna false
            JOptionPane.showMessageDialog(null, " Error al crear proveedor: " + e.getMessage());
            return false;
        }
    }
    }

