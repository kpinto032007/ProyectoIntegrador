import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class gestionarProducto extends JFrame {
    private JTable tablaProductos;
    private DefaultTableModel modelo;
    private JButton btnEditar, btnEliminar, btnVolver;

    public gestionarProducto() {

        setTitle("Gestión de Productos");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panelTitulo = new JPanel(new BorderLayout());
        panelTitulo.setBackground(Color.decode("#91766E"));
        JLabel titulo = new JLabel("Gestión de Productos", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        titulo.setForeground(Color.WHITE);
        panelTitulo.add(titulo, BorderLayout.CENTER);
        add(panelTitulo, BorderLayout.NORTH);

        modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Nombre");
        modelo.addColumn("Cantidad");
        modelo.addColumn("Precio");
        modelo.addColumn("Descripción");
        modelo.addColumn("Estado");

        tablaProductos = new JTable(modelo);
        tablaProductos.setBackground(Color.decode("#F6ECE3"));
        tablaProductos.setFont(new Font("Arial", Font.PLAIN, 14));
        tablaProductos.setRowHeight(25);

        // Encabezados
        tablaProductos.getTableHeader().setBackground(Color.decode("#B7A7A9"));
        tablaProductos.getTableHeader().setForeground(Color.BLACK);
        tablaProductos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        // Alineación centrada
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tablaProductos.getColumnCount(); i++) {
            tablaProductos.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        add(scrollPane, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(Color.decode("#B7A7A9"));

        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");
        btnVolver = new JButton("Volver");

        JButton[] botones = {btnEditar, btnEliminar, btnVolver};
        for (JButton btn : botones) {
            btn.setBackground(Color.decode("#91766E"));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setFont(new Font("Arial", Font.BOLD, 14));
            btn.setPreferredSize(new Dimension(100, 35));
            panelBotones.add(btn);
        }

        add(panelBotones, BorderLayout.SOUTH);

        cargarProductos();

        btnEditar.addActionListener(e -> editarProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());
        btnVolver.addActionListener(e -> volver());
    }

    private void cargarProductos() {

        try (Connection cn = Conexion.conectar();
             Statement stmt = cn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT idProducto, nombre, cantidad, precio, descripcion, estado FROM tb_productos")) {

            while (rs.next()) {
                Object[] fila = {
                        rs.getString("idProducto"),
                        rs.getString("nombre"),
                        rs.getInt("cantidad"),
                        rs.getDouble("precio"),
                        rs.getString("descripcion"),
                        (rs.getInt("estado") == 1) ? "Activo" : "Inactivo"
                };
                modelo.addRow(fila);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar productos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarProducto() {
        int filaSeleccionada = tablaProductos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para editar.");
            return;
        }

        String idProducto = (String) modelo.getValueAt(filaSeleccionada, 0);
        String nombre = (String) modelo.getValueAt(filaSeleccionada, 1);
        int cantidad = (int) modelo.getValueAt(filaSeleccionada, 2);
        double precio = (double) modelo.getValueAt(filaSeleccionada, 3);
        String descripcion = (String) modelo.getValueAt(filaSeleccionada, 4);
        String estadoStr = (String) modelo.getValueAt(filaSeleccionada, 5);

        JPanel panel = new JPanel(new GridLayout(0, 2));
        JTextField tfNombre = new JTextField(nombre);
        JTextField tfCantidad = new JTextField(String.valueOf(cantidad));
        JTextField tfPrecio = new JTextField(String.valueOf(precio));
        JTextField tfDescripcion = new JTextField(descripcion);
        JComboBox<String> cbEstado = new JComboBox<>(new String[]{"Activo", "Inactivo"});
        cbEstado.setSelectedItem(estadoStr);

        panel.add(new JLabel("Nombre:"));
        panel.add(tfNombre);
        panel.add(new JLabel("Cantidad:"));
        panel.add(tfCantidad);
        panel.add(new JLabel("Precio:"));
        panel.add(tfPrecio);
        panel.add(new JLabel("Descripción:"));
        panel.add(tfDescripcion);
        panel.add(new JLabel("Estado:"));
        panel.add(cbEstado);

        int option = JOptionPane.showConfirmDialog(this, panel, "Editar Producto", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection conn = Conexion.conectar()) {
                String sql = "UPDATE tb_productos SET nombre = ?, cantidad = ?, precio = ?, descripcion = ?, estado = ? WHERE idProducto = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, tfNombre.getText());
                stmt.setInt(2, Integer.parseInt(tfCantidad.getText()));
                stmt.setDouble(3, Double.parseDouble(tfPrecio.getText()));
                stmt.setString(4, tfDescripcion.getText());
                stmt.setInt(5, cbEstado.getSelectedItem().equals("Activo") ? 1 : 0);
                stmt.setString(6, idProducto);

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    modelo.setValueAt(tfNombre.getText(), filaSeleccionada, 1);
                    modelo.setValueAt(Integer.parseInt(tfCantidad.getText()), filaSeleccionada, 2);
                    modelo.setValueAt(Double.parseDouble(tfPrecio.getText()), filaSeleccionada, 3);
                    modelo.setValueAt(tfDescripcion.getText(), filaSeleccionada, 4);
                    modelo.setValueAt(cbEstado.getSelectedItem(), filaSeleccionada, 5);
                    JOptionPane.showMessageDialog(this, "Producto actualizado correctamente.");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al actualizar el producto.");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al editar producto: " + e.getMessage());
            }
        }
    }

    private void eliminarProducto() {
        int filaSeleccionada = tablaProductos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para eliminar.");
            return;
        }

        String idProducto = (String) modelo.getValueAt(filaSeleccionada, 0);
        String nombreProducto = (String) modelo.getValueAt(filaSeleccionada, 1);

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("ID Producto:"));
        panel.add(new JLabel(idProducto));
        panel.add(new JLabel("Nombre:"));
        panel.add(new JLabel(nombreProducto));

        int confirmacion = JOptionPane.showConfirmDialog(this, panel, "¿Está seguro de eliminar este producto?", JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            try (Connection cn = Conexion.conectar();
                 PreparedStatement stmt = cn.prepareStatement("DELETE FROM tb_productos WHERE idProducto = ?")) {

                stmt.setString(1, idProducto);

                int rowsDeleted = stmt.executeUpdate();
                if (rowsDeleted > 0) {
                    modelo.removeRow(filaSeleccionada);
                    JOptionPane.showMessageDialog(this, "Producto eliminado correctamente.");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al eliminar el producto.");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar producto: " + e.getMessage());
            }
        }
    }

    private void volver() {
        dispose();
        String[] opciones = {"Crear Producto", "Gestionar Producto", "Cancelar"};
        int opcion = JOptionPane.showOptionDialog(
                null,
                "Seleccione una opción:",
                "Gestión de Productos",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );

        switch (opcion) {
            case 0 -> new CrearProducto().setVisible(true);
            case 1 -> new gestionarProducto().setVisible(true);
            default -> {
            }
        }
    }
}
