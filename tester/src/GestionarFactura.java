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
        modelo.addColumn("ID Factura");
        modelo.addColumn("Fecha");
        modelo.addColumn("Productos");
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
            String sql = """
            SELECT 
                f.idFactura,
                f.fecha,
                GROUP_CONCAT(d.nombre SEPARATOR ', ') AS productos,
                SUM(d.total_a_pagar) AS total_factura
            FROM 
                tb_facturas f
            JOIN 
                tb_detalle_factura d ON f.idFactura = d.idFactura
            GROUP BY 
                f.idFactura, f.fecha
        """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                modelo.addRow(new Object[]{
                        rs.getString("idFactura"),
                        rs.getString("fecha"),
                        rs.getString("productos"),
                        rs.getDouble("total_factura")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar las facturas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void editarFactura() {
        int filaSeleccionada = tablaFacturas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una factura para editar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtener idFactura del modelo (columna 0)
        int idFactura = Integer.parseInt(modelo.getValueAt(filaSeleccionada, 0).toString());

        try (Connection conn = Conexion.conectar()) {
            // Obtener todos los detalles de la factura
            String sqlDetalles = "SELECT * FROM tb_detalle_factura WHERE idFactura = ?";
            PreparedStatement stmtDetalles = conn.prepareStatement(sqlDetalles);
            stmtDetalles.setInt(1, idFactura);
            ResultSet rsDetalles = stmtDetalles.executeQuery();

            DefaultTableModel modeloDetalles = new DefaultTableModel();
            modeloDetalles.addColumn("ID Detalle");
            modeloDetalles.addColumn("Nombre");
            modeloDetalles.addColumn("Precio");
            modeloDetalles.addColumn("Cantidad");
            modeloDetalles.addColumn("Total");

            while (rsDetalles.next()) {
                modeloDetalles.addRow(new Object[]{
                        rsDetalles.getInt("idDetalle"),
                        rsDetalles.getString("nombre"),
                        rsDetalles.getDouble("precio"),
                        rsDetalles.getInt("cantidad"),
                        rsDetalles.getDouble("total_a_pagar")
                });
            }

            JTable tablaDetalles = new JTable(modeloDetalles);
            JScrollPane scrollPane = new JScrollPane(tablaDetalles);
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(new JLabel("Seleccione un producto para editar"), BorderLayout.NORTH);
            panel.add(scrollPane, BorderLayout.CENTER);

            int option = JOptionPane.showConfirmDialog(this, panel, "Editar Factura - Detalles", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                int filaDetalle = tablaDetalles.getSelectedRow();
                if (filaDetalle == -1) {
                    JOptionPane.showMessageDialog(this, "Seleccione un producto para editar.");
                    return;
                }

                int idDetalle = (int) modeloDetalles.getValueAt(filaDetalle, 0);
                String nombre = (String) modeloDetalles.getValueAt(filaDetalle, 1);
                double precio = (double) modeloDetalles.getValueAt(filaDetalle, 2);
                int cantidad = (int) modeloDetalles.getValueAt(filaDetalle, 3);
                double total = (double) modeloDetalles.getValueAt(filaDetalle, 4);

                // Formulario de edición
                JPanel formPanel = new JPanel(new GridLayout(0, 2));
                JTextField tfNombre = new JTextField(nombre);
                JTextField tfPrecio = new JTextField(String.valueOf(precio));
                JTextField tfCantidad = new JTextField(String.valueOf(cantidad));

                formPanel.add(new JLabel("Nombre:")); formPanel.add(tfNombre);
                formPanel.add(new JLabel("Precio:")); formPanel.add(tfPrecio);
                formPanel.add(new JLabel("Cantidad:")); formPanel.add(tfCantidad);

                int editOption = JOptionPane.showConfirmDialog(this, formPanel, "Editar Producto", JOptionPane.OK_CANCEL_OPTION);
                if (editOption == JOptionPane.OK_OPTION) {
                    try {
                        double nuevoPrecio = Double.parseDouble(tfPrecio.getText());
                        int nuevaCantidad = Integer.parseInt(tfCantidad.getText());
                        double nuevoTotal = nuevoPrecio * nuevaCantidad;

                        String sqlActualizar = """
                        UPDATE tb_detalle_factura
                        SET nombre = ?, precio = ?, cantidad = ?, total_a_pagar = ?
                        WHERE idDetalle = ?
                    """;
                        PreparedStatement stmtActualizar = conn.prepareStatement(sqlActualizar);
                        stmtActualizar.setString(1, tfNombre.getText());
                        stmtActualizar.setDouble(2, nuevoPrecio);
                        stmtActualizar.setInt(3, nuevaCantidad);
                        stmtActualizar.setDouble(4, nuevoTotal);
                        stmtActualizar.setInt(5, idDetalle);
                        stmtActualizar.executeUpdate();

                        JOptionPane.showMessageDialog(this, "Producto actualizado correctamente");

                        // Opcional: Recargar facturas para reflejar cambios
                        modelo.setRowCount(0);
                        cargarFacturas();

                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Datos inválidos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al editar la factura: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    private void eliminarFactura() {
        int filaSeleccionada = tablaFacturas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una factura para eliminar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar esta factura?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            try (Connection conn = Conexion.conectar()) {
                // Obtener idFactura desde la base de datos usando fecha y nombreProducto (si no lo guardaste)
                String fecha = (String) modelo.getValueAt(filaSeleccionada, 0);
                String nombreProducto = (String) modelo.getValueAt(filaSeleccionada, 1);

                // Buscar idFactura (por ejemplo, usando la fecha)
                String sqlIdFactura = "SELECT idFactura FROM tb_facturas WHERE fecha = ?";
                PreparedStatement stmtId = conn.prepareStatement(sqlIdFactura);
                stmtId.setString(1, fecha);
                ResultSet rs = stmtId.executeQuery();

                if (rs.next()) {
                    int idFactura = rs.getInt("idFactura");

                    // Eliminar detalle primero
                    String sqlEliminarDetalle = "DELETE FROM tb_detalle_factura WHERE idFactura = ?";
                    PreparedStatement stmtDetalle = conn.prepareStatement(sqlEliminarDetalle);
                    stmtDetalle.setInt(1, idFactura);
                    stmtDetalle.executeUpdate();

                    // Eliminar factura principal
                    String sqlEliminarFactura = "DELETE FROM tb_facturas WHERE idFactura = ?";
                    PreparedStatement stmtFactura = conn.prepareStatement(sqlEliminarFactura);
                    stmtFactura.setInt(1, idFactura);
                    stmtFactura.executeUpdate();

                    // Eliminar fila de la tabla
                    modelo.removeRow(filaSeleccionada);

                    JOptionPane.showMessageDialog(this, "Factura eliminada correctamente");
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró la factura.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar la factura: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
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
