import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;

public class CrearFactura extends JFrame {
    private JComboBox<String> cmbProductos;
    private DefaultListModel<String> modeloLista;
    private JList<String> lstProductosSeleccionados;
    private JButton btnAgregarProducto, btnGuardar, btnVolver;

    private JTextField txtFecha, txtNombre, txtPrecio, txtCantidad;
    private JTextArea txtDescripcion;

    public CrearFactura() {
        setTitle("Crear Factura");
        setSize(550, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ======= Selección de producto =======
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Seleccionar Producto:"), gbc);

        cmbProductos = new JComboBox<>();
        gbc.gridx = 1; gbc.gridy = 0;
        add(cmbProductos, gbc);

        btnAgregarProducto = new JButton("Agregar Producto");
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        add(btnAgregarProducto, gbc);

        modeloLista = new DefaultListModel<>();
        lstProductosSeleccionados = new JList<>(modeloLista);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 0.3;
        add(new JScrollPane(lstProductosSeleccionados), gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;

        // ======= Detalles del producto =======
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Fecha de emisión:"), gbc);
        txtFecha = new JTextField(LocalDate.now().toString());
        txtFecha.setEditable(false);
        gbc.gridx = 1;
        add(txtFecha, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        add(new JLabel("Nombre del producto:"), gbc);
        txtNombre = new JTextField();
        gbc.gridx = 1;
        add(txtNombre, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        add(new JLabel("Descripción:"), gbc);
        txtDescripcion = new JTextArea(3, 10);
        JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);
        gbc.gridx = 1;
        add(scrollDescripcion, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        add(new JLabel("Precio:"), gbc);
        txtPrecio = new JTextField();
        gbc.gridx = 1;
        add(txtPrecio, gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        add(new JLabel("Cantidad:"), gbc);
        txtCantidad = new JTextField("1");
        gbc.gridx = 1;
        add(txtCantidad, gbc);

        // ======= Botones =======
        btnGuardar = new JButton("Guardar Factura");
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        add(btnGuardar, gbc);

        btnVolver = new JButton("Volver");
        gbc.gridy = 9;
        add(btnVolver, gbc);

        // ======= Funcionalidad =======
        cargarProductosDesdeBD();

        btnAgregarProducto.addActionListener(e -> {
            String seleccion = (String) cmbProductos.getSelectedItem();
            if (seleccion != null && !modeloLista.contains(seleccion)) {
                modeloLista.addElement(seleccion);
                String idProducto = seleccion.split(" - ")[0];

                try (Connection conn = Conexion.conectar();
                     PreparedStatement ps = conn.prepareStatement("SELECT nombre, descripcion, precio FROM tb_productos WHERE idProducto = ?")) {

                    ps.setString(1, idProducto);
                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                        txtNombre.setText(rs.getString("nombre"));
                        txtDescripcion.setText(rs.getString("descripcion"));
                        txtPrecio.setText(String.valueOf(rs.getDouble("precio")));
                    }

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error al obtener detalles del producto.");
                }
            }
        });

        btnGuardar.addActionListener(e -> guardarFactura());

        btnVolver.addActionListener(e -> {
            dispose();
            String[] opciones = {"Crear Factura", "Gestionar Facturas", "Cancelar"};
            int opcion = JOptionPane.showOptionDialog(
                    null,
                    "Seleccione una opción:",
                    "Gestión de Facturas",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
            );

            switch (opcion) {
                case 0 -> new CrearFactura().setVisible(true);
                case 1 -> new GestionarFactura().setVisible(true);
                default -> {}
            }
        });

        txtFecha.addActionListener(e -> cmbProductos.requestFocusInWindow());
        cmbProductos.addActionListener(e -> btnAgregarProducto.requestFocusInWindow());
        txtCantidad.addActionListener(e -> btnGuardar.requestFocusInWindow());

        getRootPane().setDefaultButton(btnGuardar);
    }

    private void cargarProductosDesdeBD() {
        try (Connection conn = Conexion.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT idProducto, nombre FROM tb_productos")) {

            while (rs.next()) {
                String id = rs.getString("idProducto");
                String nombre = rs.getString("nombre");
                cmbProductos.addItem(id + " - " + nombre);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar productos: " + e.getMessage());
        }
    }
    private void guardarFactura() {
        if (modeloLista.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar al menos un producto", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String fecha = txtFecha.getText();
        int estado = 1;

        try (Connection conn = Conexion.conectar()) {
            conn.setAutoCommit(false);

            // 1. Insertar en tb_facturas (cabecera) y obtener ID generado
            int idFacturaGenerado = -1;
            try (PreparedStatement psFactura = conn.prepareStatement(
                    "INSERT INTO tb_facturas (fecha, estado) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                psFactura.setString(1, fecha);
                psFactura.setInt(2, estado);
                psFactura.executeUpdate();

                ResultSet generatedKeys = psFactura.getGeneratedKeys();
                if (generatedKeys.next()) {
                    idFacturaGenerado = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("No se pudo obtener el ID de la factura.");
                }
            }

            // 2. Insertar los productos en tb_detalle_factura
            for (int i = 0; i < modeloLista.getSize(); i++) {
                String item = modeloLista.getElementAt(i);
                String idProducto = item.split(" - ")[0];

                try (PreparedStatement psSelect = conn.prepareStatement(
                        "SELECT nombre, descripcion, precio FROM tb_productos WHERE idProducto = ?");
                     PreparedStatement psInsertDetalle = conn.prepareStatement(
                             "INSERT INTO tb_detalle_factura (idFactura, nombre, descripcion, precio, cantidad, total_a_pagar) VALUES (?, ?, ?, ?, ?, ?)")) {

                    psSelect.setString(1, idProducto);
                    ResultSet rs = psSelect.executeQuery();

                    if (rs.next()) {
                        String nombre = rs.getString("nombre");
                        String descripcion = rs.getString("descripcion");
                        double precio = rs.getDouble("precio");
                        int cantidad = Integer.parseInt(txtCantidad.getText().trim());
                        double total = precio * cantidad;

                        psInsertDetalle.setInt(1, idFacturaGenerado);
                        psInsertDetalle.setString(2, nombre);
                        psInsertDetalle.setString(3, descripcion);
                        psInsertDetalle.setDouble(4, precio);
                        psInsertDetalle.setInt(5, cantidad);
                        psInsertDetalle.setDouble(6, total);

                        psInsertDetalle.executeUpdate();
                    }
                }
            }

            conn.commit();
            JOptionPane.showMessageDialog(this, "Factura guardada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error: Cantidad debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar la factura en la base de datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
