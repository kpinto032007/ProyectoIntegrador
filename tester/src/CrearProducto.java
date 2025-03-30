import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CrearProducto extends JFrame {

    public CrearProducto() {
        JFrame frame = new JFrame("Crear producto");
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setSize(400,500);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;//los componentes ocupran todoo el ancho disponible


        // Título
        JLabel titleLabel = new JLabel("Crear Producto", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;//ocupa dos columnas
        frame.add(titleLabel, gbc);

        //Id producto
        JLabel IdProductoLabel = new JLabel("Id Producto:");
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


        //Cantidad
        JLabel cantidadLabel= new JLabel("Cantidad: ");
        gbc.gridx= 0;
        gbc.gridy= 3;
        gbc.gridwidth =1;
        frame.add(cantidadLabel,gbc);

        JTextField cantidadField = new JTextField();
        gbc.gridx= 1;
        frame.add(cantidadField,gbc);
        nombreField.addActionListener(e ->cantidadField.requestFocusInWindow());

        //Precio
        JLabel precioLabel= new JLabel("Precio: ");
        gbc.gridx =0;
        gbc.gridy =4;
        gbc.gridwidth = 1;
        frame.add(precioLabel,gbc);


        JTextField precioField = new JTextField();
        gbc.gridx=1;
        frame.add(precioField,gbc);
        cantidadField.addActionListener(e -> precioField.requestFocusInWindow());


        JLabel descripcionLabel = new JLabel("Descripción: ");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        frame.add(descripcionLabel, gbc);

        // Área de texto para Descripción
        JTextArea descripcionField = new JTextArea(3, 20); // 3 filas, 20 columnas
        descripcionField.setLineWrap(true);
        descripcionField.setWrapStyleWord(true);


        // Scroll para el área de texto
        JScrollPane scroll = new JScrollPane(descripcionField);
        gbc.gridx = 1;
        gbc.gridwidth = 2; // Para que ocupe más espacio en la interfaz
        frame.add(scroll, gbc);
        precioField.addActionListener(e -> descripcionField.requestFocusInWindow());


        frame.setVisible(true);

        // Botón de registro
        JButton registerButton = new JButton("Registrar");
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        frame.add(registerButton, gbc);

        // Botón "Volver"
        JButton volver = new JButton("Volver");
        gbc.gridx = 0;
        gbc.gridy = 7; // Debe estar en la siguiente fila
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 0, 0, 0); // Margen superior
        gbc.anchor = GridBagConstraints.CENTER; // Centrar el botón
        frame.add(volver,gbc);

        volver.addActionListener(e -> {
            frame.dispose(); // Cierra la ventana actual
            new Menu();  // Abre la ventana de inicio de sesión
        });

        registerButton.addActionListener(e -> {
            String id= IdField.getText().trim();
            String nombre = nombreField.getText().trim();
            String cantidad = cantidadField.getText().trim();
            String precio = precioField.getText().trim();
            String descripcion= descripcionField.getText().trim();

            if (id.isEmpty() || nombre.isEmpty() || cantidad.isEmpty() || precio.isEmpty() || descripcion.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Por favor, completa todos los campos.");
                return;
            }

            try {
                long Cantidad = Long.parseLong(cantidad);  // Convierte a long
                double Precio = Double.parseDouble(precio);


                //aqui llama a la funcion registrar usuarioven la base de datos
                if (CrearProducto(id,nombre,Cantidad,Precio, descripcion)) {
                    JOptionPane.showMessageDialog(frame, "Registro exitoso");
                    frame.dispose(); // Cierra la ventana después de registrar

                    new SignInUI(); //con esta abro la de inicio de sesión

                } else {
                    JOptionPane.showMessageDialog(frame, "Error al registrar usuario.");
                }

            } catch (NumberFormatException ex) {
                // Si el usuario ingresa un valor no numérico, muestra un mensaje de error
                JOptionPane.showMessageDialog(null, "Error: Ingresa solo números en Cantidad y Precio.", "Error de validación", JOptionPane.ERROR_MESSAGE);
            }

        });
    }
    public static boolean  CrearProducto(String id, String nombre, long cantidad, double precio, String descripcion){
        String sql = "INSERT INTO tb_productos (idProducto, nombre, cantidad, precio, descripcion, estado) VALUES (?, ?, ?, ?, ?,?)";
        //proteger contra inyecciones sql y poner los valores despues

        try (Connection cn = Conexion.conectar(); PreparedStatement pst = cn.prepareStatement(sql)) {//preparamos la sentencia sql para q reciba los valores
            //llenamos los ? con los datos ingresados por el usuario
            pst.setString(1, id);
            pst.setString(2, nombre);
            pst.setLong(3, cantidad);
            pst.setDouble(4, precio);
            pst.setString(5, descripcion);
            pst.setInt(6, 1);  // Estado fijo en 1

            //ejecutamos la inserccion y verificamos si se efectó alguna fila
            int filasAfectadas = pst.executeUpdate();
            //si se incertó correctamente, retorna true
            return filasAfectadas > 0;
        } catch (SQLException e) {
            //si hay error, muestra un mensaje y retorna false
            JOptionPane.showMessageDialog(null, " Error al crear producto: " + e.getMessage());
            return false;
        }
    }


    }

