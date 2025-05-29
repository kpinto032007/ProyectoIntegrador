import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class gestionarFactura extends JFrame {
    private JTable tablaFacturas;
    private DefaultTableModel modelo;
    private JButton btnEditar, btnEliminar, btnExportarPDF, btnVolver;

    public gestionarFactura() {
        setTitle("Gestionar Facturas");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Modelo de la tabla
        modelo = new DefaultTableModel();
        modelo.addColumn("Fecha");
        modelo.addColumn("Nombre Producto");
        modelo.addColumn("Precio");
        modelo.addColumn("Cantidad");
        modelo.addColumn("Total");

        tablaFacturas = new JTable(modelo);
        JScrollPane scrollPane = new JScrollPane(tablaFacturas);
        add(scrollPane, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel();
        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");
        btnExportarPDF = new JButton("Exportar PDF");
        btnVolver = new JButton("Volver");


        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnExportarPDF);
        panelBotones.add(btnVolver);


        add(panelBotones, BorderLayout.SOUTH);

        cargarFacturas();

        // Eventos
        btnEditar.addActionListener(e -> editarFactura());
        btnEliminar.addActionListener(e -> eliminarFactura());
        btnExportarPDF.addActionListener(e -> exportarPDF());
        btnVolver.addActionListener(e -> volver());

    }

    private void cargarFacturas() {
        try (Connection conn = Conexion.conectar()) {
            String sql = "SELECT fecha, nombre, precio, cantidad, total_a_pagar FROM tb_facturas";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                modelo.addRow(new Object[]{
                        rs.getString("fecha"),
                        rs.getString("nombre"),
                        rs.getDouble("precio"),
                        rs.getInt("cantidad"),
                        rs.getDouble("total_a_pagar")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar las facturas", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarFactura() {
        int filaSeleccionada = tablaFacturas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una factura para editar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtener valores de la tabla con el tipo correcto
        String fecha = (String) modelo.getValueAt(filaSeleccionada, 0);
        String nombreProducto = (String) modelo.getValueAt(filaSeleccionada, 1);
        double precio = (Double) modelo.getValueAt(filaSeleccionada, 2);
        int cantidad = (Integer) modelo.getValueAt(filaSeleccionada, 3);
        double total = (Double) modelo.getValueAt(filaSeleccionada, 4);

        // Crear un panel de edición
        JPanel panel = new JPanel(new GridLayout(0, 2));
        JTextField tfFecha = new JTextField(fecha);
        JTextField tfNombreProducto = new JTextField(nombreProducto);
        JTextField tfPrecio = new JTextField(String.valueOf(precio));
        JTextField tfCantidad = new JTextField(String.valueOf(cantidad));
        JTextField tfTotal = new JTextField(String.valueOf(total));

        panel.add(new JLabel("Fecha:"));
        panel.add(tfFecha);
        panel.add(new JLabel("Nombre Producto:"));
        panel.add(tfNombreProducto);
        panel.add(new JLabel("Precio:"));
        panel.add(tfPrecio);
        panel.add(new JLabel("Cantidad:"));
        panel.add(tfCantidad);
        panel.add(new JLabel("Total:"));
        panel.add(tfTotal);

        int option = JOptionPane.showConfirmDialog(this, panel, "Editar Factura", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            // Validar y actualizar la factura
            try (Connection conn = Conexion.conectar()) {
                String sql = "UPDATE tb_facturas SET fecha = ?, nombre = ?, precio = ?, cantidad = ?, total_a_pagar = ? WHERE fecha = ? AND nombre = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, tfFecha.getText());
                stmt.setString(2, tfNombreProducto.getText());
                stmt.setDouble(3, Double.parseDouble(tfPrecio.getText()));
                stmt.setInt(4, Integer.parseInt(tfCantidad.getText()));
                stmt.setDouble(5, Double.parseDouble(tfTotal.getText()));
                stmt.setString(6, fecha); // Fecha original como criterio de actualización
                stmt.setString(7, nombreProducto); // Nombre del producto como criterio de actualización

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    // Actualizar la tabla con los nuevos valores
                    modelo.setValueAt(tfFecha.getText(), filaSeleccionada, 0);
                    modelo.setValueAt(tfNombreProducto.getText(), filaSeleccionada, 1);
                    modelo.setValueAt(Double.parseDouble(tfPrecio.getText()), filaSeleccionada, 2);
                    modelo.setValueAt(Integer.parseInt(tfCantidad.getText()), filaSeleccionada, 3);
                    modelo.setValueAt(Double.parseDouble(tfTotal.getText()), filaSeleccionada, 4);
                    JOptionPane.showMessageDialog(this, "Factura actualizada correctamente");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al actualizar la factura", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al editar la factura", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void eliminarFactura() {
        int filaSeleccionada = tablaFacturas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una factura para eliminar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar la factura?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            String fecha = (String) modelo.getValueAt(filaSeleccionada, 0);
            String nombreProducto = (String) modelo.getValueAt(filaSeleccionada, 1);

            try (Connection conn = Conexion.conectar()) {
                String sql = "DELETE FROM tb_facturas WHERE fecha = ? AND nombre = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, fecha);
                stmt.setString(2, nombreProducto);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    modelo.removeRow(filaSeleccionada);
                    JOptionPane.showMessageDialog(this, "Factura eliminada correctamente");
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró la factura para eliminar", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar la factura", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportarPDF() {
        JOptionPane.showMessageDialog(this, "Funcionalidad de exportación a PDF en desarrollo");
    }

    private void volver() {
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
            case 1 -> new gestionarFactura().setVisible(true);
            default -> { }
        }// Cierra solo esta ventana

    }



}
