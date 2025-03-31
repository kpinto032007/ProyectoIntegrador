import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class gestionarProducto extends JFrame {
    private JTable tablaProductos;
    private DefaultTableModel modelo;

    public gestionarProducto() {
        setTitle("Gestión de Productos");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panelProductos = crearPanelProductos();
        add(panelProductos, BorderLayout.NORTH);

        modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Nombre");
        modelo.addColumn("Cantidad");
        modelo.addColumn("Precio");
        modelo.addColumn("Descripción");
        modelo.addColumn("Estado");

        cargarProductos();

        tablaProductos = new JTable(modelo);
        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton btnEditar = new JButton("Editar Producto");
        JButton btnEliminar = new JButton("Eliminar Producto");
        JButton btnRegresar = new JButton("Regresar al Menú");
        buttonPanel.add(btnEditar);
        buttonPanel.add(btnEliminar);
        buttonPanel.add(btnRegresar);
        add(buttonPanel, BorderLayout.SOUTH);

        btnEditar.addActionListener(e -> editarProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());
        btnRegresar.addActionListener(e -> {
            dispose(); // Cierra la ventana actual de gestión de productos
            Menu menu = new Menu(); // Crea una nueva instancia del menú
            menu.setVisible(true); // Asegura que la nueva ventana del menú sea visible
        });

        setVisible(true);
    }

    private JPanel crearPanelProductos() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel titulo = new JLabel("Gestión de Productos", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titulo, BorderLayout.NORTH);

        return panel;
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
                        rs.getBoolean("estado") ? "Activo" : "Inactivo"
                };
                modelo.addRow(fila);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar productos: " + e.getMessage());
        }
    }

    private void editarProducto() {
        int filaSeleccionada = tablaProductos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto para editar.");
            return;
        }

        String id = (String) modelo.getValueAt(filaSeleccionada, 0);
        String nuevoNombre = JOptionPane.showInputDialog(this, "Nuevo nombre:", modelo.getValueAt(filaSeleccionada, 1));
        String nuevaCantidad = JOptionPane.showInputDialog(this, "Nueva cantidad:", modelo.getValueAt(filaSeleccionada, 2));
        String nuevoPrecio = JOptionPane.showInputDialog(this, "Nuevo precio:", modelo.getValueAt(filaSeleccionada, 3));
        String nuevaDescripcion = JOptionPane.showInputDialog(this, "Nueva descripción:", modelo.getValueAt(filaSeleccionada, 4));
        String[] estados = {"Activo", "Inactivo"};
        String nuevoEstado = (String) JOptionPane.showInputDialog(this, "Nuevo estado:", "Estado", JOptionPane.QUESTION_MESSAGE, null, estados, modelo.getValueAt(filaSeleccionada, 5));

        if (nuevoNombre != null && nuevaCantidad != null && nuevoPrecio != null && nuevaDescripcion != null && nuevoEstado != null) {
            try (Connection cn = Conexion.conectar();
                 PreparedStatement stmt = cn.prepareStatement("UPDATE tb_productos SET nombre = ?, cantidad = ?, precio = ?, descripcion = ?, estado = ? WHERE idProducto = ?")) {
                stmt.setString(1, nuevoNombre);
                stmt.setInt(2, Integer.parseInt(nuevaCantidad));
                stmt.setDouble(3, Double.parseDouble(nuevoPrecio));
                stmt.setString(4, nuevaDescripcion);
                stmt.setBoolean(5, nuevoEstado.equals("Activo"));
                stmt.setString(6, id);
                stmt.executeUpdate();

                modelo.setValueAt(nuevoNombre, filaSeleccionada, 1);
                modelo.setValueAt(Integer.parseInt(nuevaCantidad), filaSeleccionada, 2);
                modelo.setValueAt(Double.parseDouble(nuevoPrecio), filaSeleccionada, 3);
                modelo.setValueAt(nuevaDescripcion, filaSeleccionada, 4);
                modelo.setValueAt(nuevoEstado, filaSeleccionada, 5);
                JOptionPane.showMessageDialog(this, "Producto actualizado.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al actualizar producto: " + e.getMessage());
            }
        }
    }

    private void eliminarProducto() {
        int filaSeleccionada = tablaProductos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto para eliminar.");
            return;
        }

        int id = (int) modelo.getValueAt(filaSeleccionada, 0);
        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Estás seguro de eliminar este producto?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            try (Connection cn = Conexion.conectar();
                 PreparedStatement stmt = cn.prepareStatement("DELETE FROM tb_productos WHERE idProducto = ?")) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
                modelo.removeRow(filaSeleccionada);
                JOptionPane.showMessageDialog(this, "Producto eliminado.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar producto: " + e.getMessage());
            }
        }
    }
}
