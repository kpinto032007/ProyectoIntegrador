
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

    public class CrearFactura extends JFrame {

        private JTextField txtFecha, txtNombre, txtPrecio, txtCantidad;
        private JTextArea txtDescripcion;
        private JButton btnGuardar, btnVolver;

        public CrearFactura() {
            setTitle("Crear Factura");
            setSize(400, 400);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // Fecha de emisión
            JLabel lblFecha = new JLabel("Fecha de emisión:");
            gbc.gridx = 0; gbc.gridy = 0;
            add(lblFecha, gbc);

            txtFecha = new JTextField(LocalDate.now().toString());
            txtFecha.setEditable(false);
            gbc.gridx = 1;
            add(txtFecha, gbc);

            // Nombre del producto
            JLabel lblNombre = new JLabel("Nombre del producto:");
            gbc.gridx = 0; gbc.gridy = 1;
            add(lblNombre, gbc);

            txtNombre = new JTextField();
            gbc.gridx = 1;
            add(txtNombre, gbc);

            // Descripción del producto
            JLabel lblDescripcion = new JLabel("Descripción:");
            gbc.gridx = 0; gbc.gridy = 2;
            add(lblDescripcion, gbc);

            txtDescripcion = new JTextArea(3, 20);
            gbc.gridx = 1;
            add(new JScrollPane(txtDescripcion), gbc);

            // Precio
            JLabel lblPrecio = new JLabel("Precio:");
            gbc.gridx = 0; gbc.gridy = 3;
            add(lblPrecio, gbc);

            txtPrecio = new JTextField();
            gbc.gridx = 1;
            add(txtPrecio, gbc);


            // Cantidad
            JLabel lblCantidad = new JLabel("Cantidad:");
            gbc.gridx = 0; gbc.gridy = 4;
            add(lblCantidad, gbc);

            txtCantidad = new JTextField();
            gbc.gridx = 1;
            add(txtCantidad, gbc);


            // Botón Guardar
            btnGuardar = new JButton("Guardar Factura");
            gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
            add(btnGuardar, gbc);



            // Acción del botón para guardar
            btnGuardar.addActionListener(e -> guardarFactura());

            // Permitir que Enter en "Guardar Factura" ejecute el guardado
            getRootPane().setDefaultButton(btnGuardar);

            // Eventos para cambiar de campo con ENTER
            txtFecha.addActionListener(e -> txtNombre.requestFocusInWindow());
            txtNombre.addActionListener(e -> txtDescripcion.requestFocusInWindow());
            txtPrecio.addActionListener(e -> txtCantidad.requestFocusInWindow());
            txtCantidad.addActionListener(e -> btnGuardar.requestFocusInWindow());

            // Manejar ENTER en txtDescripcion para que no agregue nueva línea
            txtDescripcion.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        e.consume(); // Evita salto de línea
                        txtPrecio.requestFocusInWindow();
                    }
                }
            });

        }

        private void guardarFactura() {
            try {
                // Validar que los campos no estén vacíos
                if (txtNombre.getText().trim().isEmpty() ||
                        txtDescripcion.getText().trim().isEmpty() ||
                        txtPrecio.getText().trim().isEmpty() ||
                        txtCantidad.getText().trim().isEmpty()) {

                    JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
                    return; // Detiene la ejecución del método
                }

                // Obtener valores
                String fecha = txtFecha.getText();
                String nombre = txtNombre.getText();
                String descripcion = txtDescripcion.getText();
                double precio = Double.parseDouble(txtPrecio.getText().trim());
                int cantidad = Integer.parseInt(txtCantidad.getText().trim());
                double total = precio * cantidad;
                int estado = 1; // Estado por defecto

                // Conectar y guardar en la base de datos
                try (Connection conn = Conexion.conectar()) {
                    String sql = "INSERT INTO tb_facturas (fecha, nombre, descripcion, precio, cantidad, total_a_pagar, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, fecha);
                    stmt.setString(2, nombre);
                    stmt.setString(3, descripcion);
                    stmt.setDouble(4, precio);
                    stmt.setInt(5, cantidad);
                    stmt.setDouble(6, total);
                    stmt.setInt(7, estado);

                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Factura guardada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    dispose(); // Cierra la ventana después de guardar
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error: Precio y Cantidad deben ser números válidos.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar la factura en la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

