import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CrearProveedor {
    private JFrame frame;

    public CrearProveedor() {
        mostrarMenuSeleccion();
    }

    private void mostrarMenuSeleccion() {
        String[] opciones = {"Ingresar Proveedor", "Consultar Proveedor"};
        int seleccion = JOptionPane.showOptionDialog(
                null,
                "Seleccione una opción:",
                "Gestión de Proveedores",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );
        if (seleccion ==0){
            mostrarFormulario();

        }else if(seleccion ==1){
            new ConsultarProveedor();
        }
    }

    private void mostrarFormulario() {
        frame = new JFrame("Ingresar Proveedor");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridBagLayout());
        frame.getContentPane().setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        JTextField txtNombre = new JTextField(10);
        frame.add(txtNombre, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        frame.add(new JLabel("Apellido:"), gbc);
        gbc.gridx = 1;
        JTextField txtApellido = new JTextField(10);
        frame.add(txtApellido, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        frame.add(new JLabel("Cédula:"), gbc);
        gbc.gridx = 1;
        JTextField txtCedula = new JTextField(10);
        frame.add(txtCedula, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        frame.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1;
        JTextField txtTelefono = new JTextField(10);
        frame.add(txtTelefono, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        frame.add(new JLabel("Empresa Proveedora:"), gbc);
        gbc.gridx = 1;
        JTextField txtEmpresa = new JTextField(10);
        frame.add(txtEmpresa, gbc);

        JButton btnGuardar = new JButton("Guardar");
        estiloBoton(btnGuardar);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        frame.add(btnGuardar, gbc);

        JButton btnVolver = new JButton("Volver");
        estiloBoton(btnVolver);
        gbc.gridy = 6;
        frame.add(btnVolver, gbc);

        btnVolver.addActionListener(e -> frame.dispose());

        btnGuardar.addActionListener(e -> {
            try {
                Connection conn = Conexion.conectar();
                if (conn == null) {
                    JOptionPane.showMessageDialog(frame, "Error: No se pudo conectar a la base de datos.", "Error de conexión", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String sql = "INSERT INTO tb_proveedores (nombre, apellido, cedula, telefono, empresa_proveedora, estado) VALUES (?, ?, ?, ?, ?, 1)";

                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, txtNombre.getText());
                stmt.setString(2, txtApellido.getText());
                stmt.setString(3, txtCedula.getText());
                stmt.setString(4, txtTelefono.getText());
                stmt.setString(5, txtEmpresa.getText());
                stmt.executeUpdate();
                conn.close();
                JOptionPane.showMessageDialog(frame, "Proveedor guardado correctamente.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Error al guardar en la base de datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setVisible(true);
    }

    public static void estiloBoton(JButton boton) {
        boton.setBackground(new Color(173, 216, 230)); // Azul claro
        boton.setForeground(Color.BLACK);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createLineBorder(new Color(100, 149, 237), 2)); // Azul más oscuro
        boton.setFont(new Font("Arial", Font.BOLD, 14));
    }
}
