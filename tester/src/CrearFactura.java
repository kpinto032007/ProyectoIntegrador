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
        setSize(600, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());

        Color beige = Color.decode("#F6ECE3");
        Color granite = Color.decode("#B7A7A9");
        Color brown = Color.decode("#91766E");
        Color white = Color.decode("#FFFFFF");
        Color black = Color.decode("#000000");

        getContentPane().setBackground(beige);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("SansSerif", Font.BOLD, 14);
        Font fieldFont = new Font("SansSerif", Font.PLAIN, 13);

        // ======= Selección de producto =======
        JLabel lblSeleccionar = new JLabel("Seleccionar Producto:");
        lblSeleccionar.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = 0;
        add(lblSeleccionar, gbc);

        cmbProductos = new JComboBox<>();
        cmbProductos.setFont(fieldFont);
        gbc.gridx = 1;
        add(cmbProductos, gbc);

        btnAgregarProducto = new JButton("+ Agregar Producto");
        estilizarBoton(btnAgregarProducto, brown, white);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        add(btnAgregarProducto, gbc);

        modeloLista = new DefaultListModel<>();
        lstProductosSeleccionados = new JList<>(modeloLista);
        lstProductosSeleccionados.setFont(fieldFont);
        lstProductosSeleccionados.setBorder(BorderFactory.createLineBorder(granite));
        JScrollPane scrollLista = new JScrollPane(lstProductosSeleccionados);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 0.3;
        add(scrollLista, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0;

        gbc.gridwidth = 1;

        agregarCampo("Fecha de emisión:", txtFecha = new JTextField(LocalDate.now().toString()), labelFont, fieldFont, false, gbc, 3);
        agregarCampo("Nombre del producto:", txtNombre = new JTextField(), labelFont, fieldFont, true, gbc, 4);

        JLabel lblDescripcion = new JLabel("Descripción:");
        lblDescripcion.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = 5;
        add(lblDescripcion, gbc);
        txtDescripcion = new JTextArea(3, 10);
        txtDescripcion.setFont(fieldFont);
        JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);
        gbc.gridx = 1;
        add(scrollDescripcion, gbc);

        agregarCampo("Precio:", txtPrecio = new JTextField(), labelFont, fieldFont, true, gbc, 6);
        agregarCampo("Cantidad:", txtCantidad = new JTextField("1"), labelFont, fieldFont, true, gbc, 7);

        // ======= Botones =======
        btnGuardar = new JButton("Guardar Factura");
        estilizarBoton(btnGuardar, granite, black);
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        add(btnGuardar, gbc);

        btnVolver = new JButton("Volver");
        estilizarBoton(btnVolver, Color.GRAY, white);
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
            }
        });

        txtFecha.addActionListener(e -> cmbProductos.requestFocusInWindow());
        cmbProductos.addActionListener(e -> btnAgregarProducto.requestFocusInWindow());
        txtCantidad.addActionListener(e -> btnGuardar.requestFocusInWindow());

        getRootPane().setDefaultButton(btnGuardar);
    }

    private void agregarCampo(String etiqueta, JTextField campo, Font labelFont, Font fieldFont, boolean editable, GridBagConstraints gbc, int y) {
        JLabel label = new JLabel(etiqueta);
        label.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = y;
        add(label, gbc);

        campo.setFont(fieldFont);
        campo.setEditable(editable);
        gbc.gridx = 1;
        add(campo, gbc);
    }

    private void estilizarBoton(JButton boton, Color fondo, Color texto) {
        boton.setFocusPainted(false);
        boton.setBackground(fondo);
        boton.setForeground(texto);
        boton.setFont(new Font("SansSerif", Font.BOLD, 13));
        boton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
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