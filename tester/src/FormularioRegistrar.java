import javax.swing.*;  //crear la ventana
import java.awt.*;
import java.sql.Connection;  //conexion y manejar la base de datos
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FormularioRegistrar extends JFrame { //representa una ventana gráfica para registrar nuevos usuarios

    public FormularioRegistrar() {

        JFrame frame = new JFrame("Registro de Usuario"); //creamos la ventana principal y su titulo
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //si se cierra la ventana, tambien la aplicación
        frame.setSize(400, 500); //tamaño ancho y alto
        frame.setLocationRelativeTo(null); //la ventana aparecerá centrada en la pantalla
        frame.setLayout(new GridBagLayout()); //usamos un diseño flexible en forma de cuadricula (GridBagLayout)

        GridBagConstraints gbc = new GridBagConstraints(); //aqui configuramos los componentes de la cuadricula
        gbc.insets = new Insets(10, 10, 10, 10); //espacio entre componentes
        gbc.fill = GridBagConstraints.HORIZONTAL;  //los componentes ocuparan todoo el ancho disponible

        // ======= TÍTULO DEL FORMULARIO =======
        JLabel titleLabel = new JLabel("Registro de Usuario", SwingConstants.CENTER); //texto centrado
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18)); //fuente arial, negrita, tamaño 18
        //posición y tamaño del título en la cuadricula
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; //ocupa dos columnas
        frame.add(titleLabel, gbc); //la etiqueta a la ventana

        // ======= CAMPO NOMBRE =======
        JLabel nameLabel = new JLabel("Nombre:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        frame.add(nameLabel, gbc);

        JTextField nameField = new JTextField(); //ingresar el nombre
        gbc.gridx = 1;
        frame.add(nameField, gbc); //al campo

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
        //tomamos los valores ingresados y eliminamos espacios
        registerButton.addActionListener(e -> {
            String nombre = nameField.getText().trim();
            String apellido = apellidoField.getText().trim();
            String telefono = telefonoField.getText().trim();
            String usuario = usuarioField.getText().trim();
            String password = new String(passwordField.getPassword()).trim(); //aquí la contraseña está como texto

            //validación: si algun campo está vacio, muestra un mensaje
            if (nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty() || usuario.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Por favor, completa todos los campos.");
            } else {
                //aqui llama a la funcion registrar usuario en la base de datos
                if (registrarUsuario(nombre, apellido, telefono, usuario, password)) {
                    JOptionPane.showMessageDialog(frame, "Registro exitoso");
                    frame.dispose(); //cierra la ventana después de registrar

                    //añadi esta "new SignInUI que es con la que abro la de sesión pero para eso necesito un contructor que me inicialice
                    new SignInUI(); //con esta abro la de inicio de sesión

                } else { //
                    JOptionPane.showMessageDialog(frame, "Error al registrar usuario.");
                }
            }
        });
        //mostramos la ventana
        frame.setVisible(true);
    }

    //funcion para guardar en la base de datos
    private static boolean registrarUsuario(String nombre, String apellido, String telefono, String usuario, String password) {
        String sql = "INSERT INTO tb_usuarios (nombre, apellido, telefono, usuario, password, estado) VALUES (?, ?, ?, ?, ?,?)"; //? proteger contra inyecciones SQL y poner los valores después.

        try (Connection cn = Conexion.conectar(); PreparedStatement pst = cn.prepareStatement(sql)) { //conectamos a la base de datos usando la clase conexion
            //llenamos los ? con los datos ingresados por el usuario                                    //preparamos la sentencia SQL para q pueda recibir los valores
            pst.setString(1, nombre);
            pst.setString(2, apellido);
            pst.setString(3, telefono);
            pst.setString(4, usuario);
            pst.setString(5, password);
            pst.setInt(6, 1);  // Estado fijo en 1

            //ejecutamos la inserción y verificamos si se afectó alguna fila
            int filasAfectadas = pst.executeUpdate();
            //si se insertó correctamente, retorna true
            return filasAfectadas > 0;
        } catch (SQLException e) {
            //si hay error, muestra un mensaje y retorna false
            JOptionPane.showMessageDialog(null, "❌ Error al registrar usuario: " + e.getMessage());
            return false;
        }
    }
}