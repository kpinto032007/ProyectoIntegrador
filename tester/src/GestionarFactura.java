import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.io.FileOutputStream;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import javax.swing.JFileChooser;
import com.itextpdf.text.pdf.draw.LineSeparator;

public class GestionarFactura extends JFrame {
    private JTable tablaFacturas;
    private DefaultTableModel modelo;
    private JButton btnEditar, btnEliminar, btnExportarPDF, btnVolver;

    public GestionarFactura() {
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
        tablaFacturas.setBackground(Color.decode("#F6ECE3")); //color beige
        tablaFacturas.setForeground(Color.BLACK);
        tablaFacturas.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
        tablaFacturas.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(tablaFacturas);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.decode("#B7A7A9"));

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
        btnExportarPDF.addActionListener(e -> {
            int filaSeleccionada = tablaFacturas.getSelectedRow();
            if (filaSeleccionada == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione una factura para exportar", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Suponiendo que el ID de la factura está en la columna 0
            int idFactura = Integer.parseInt(tablaFacturas.getValueAt(filaSeleccionada, 0).toString());
            exportarPDF(idFactura);
        });

        btnVolver.addActionListener(e -> volver());



    }
    private JPanel crearPanelTitulo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.decode("#91766E")); // Brown

        JLabel titulo = new JLabel("Gestión de Proveedores", SwingConstants.CENTER);
        titulo.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        panel.add(titulo, BorderLayout.CENTER);

        return panel;
    }
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(Color.decode("#91766E")); // Brown
        button.setForeground(Color.WHITE);
        button.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return button;
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
                        rsDetalles.getDouble("Precio"),
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

        // Obtener idFactura del modelo (columna 0)
        int idFactura = Integer.parseInt(modelo.getValueAt(filaSeleccionada, 0).toString());

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de eliminar esta factura?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            try (Connection conn = Conexion.conectar()) {

                // Eliminar primero los detalles de la factura
                String sqlEliminarDetalle = "DELETE FROM tb_detalle_factura WHERE idFactura = ?";
                PreparedStatement stmtDetalle = conn.prepareStatement(sqlEliminarDetalle);
                stmtDetalle.setInt(1, idFactura);
                stmtDetalle.executeUpdate();

                // Luego eliminar la factura principal
                String sqlEliminarFactura = "DELETE FROM tb_facturas WHERE idFactura = ?";
                PreparedStatement stmtFactura = conn.prepareStatement(sqlEliminarFactura);
                stmtFactura.setInt(1, idFactura);
                stmtFactura.executeUpdate();

                // Eliminar fila del modelo de la tabla
                modelo.removeRow(filaSeleccionada);

                JOptionPane.showMessageDialog(this, "Factura eliminada correctamente");

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar la factura: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    private void exportarPDF(int idFactura) {
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar factura como PDF");
            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".pdf")) {
                    filePath += ".pdf";
                }

                PdfWriter.getInstance(document, new FileOutputStream(filePath));
                document.open();

                Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, BaseColor.BLACK);
                Font fontSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.DARK_GRAY);
                Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);

                // ======= ENCABEZADO =======
                Paragraph titulo = new Paragraph("FACTURA", fontTitulo);
                titulo.setAlignment(Element.ALIGN_CENTER);
                document.add(titulo);

                document.add(new Paragraph(" "));

                // Datos generales de la factura
                try (Connection conn = Conexion.conectar();
                     PreparedStatement ps = conn.prepareStatement("SELECT fecha FROM tb_facturas WHERE idFactura = ?")) {
                    ps.setInt(1, idFactura);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        String fecha = rs.getString("fecha");

                        PdfPTable tablaCabecera = new PdfPTable(2);
                        tablaCabecera.setWidthPercentage(100);
                        tablaCabecera.setSpacingBefore(10f);
                        tablaCabecera.setWidths(new float[]{1, 3});

                        tablaCabecera.addCell(crearCeldaEtiqueta("Fecha:", fontSubtitulo));
                        tablaCabecera.addCell(crearCeldaValor(fecha, fontNormal));

                        tablaCabecera.addCell(crearCeldaEtiqueta("Factura N°:", fontSubtitulo));
                        tablaCabecera.addCell(crearCeldaValor(String.valueOf(idFactura), fontNormal));

                        document.add(tablaCabecera);
                    }
                }

                document.add(new Paragraph(" "));

                // ======= TABLA DE PRODUCTOS =======
                PdfPTable tablaProductos = new PdfPTable(5);
                tablaProductos.setWidthPercentage(100);
                tablaProductos.setWidths(new float[]{3, 4, 2, 2, 2});

                tablaProductos.addCell(crearCeldaEtiqueta("Producto", fontSubtitulo));
                tablaProductos.addCell(crearCeldaEtiqueta("Descripción", fontSubtitulo));
                tablaProductos.addCell(crearCeldaEtiqueta("Precio", fontSubtitulo));
                tablaProductos.addCell(crearCeldaEtiqueta("Cantidad", fontSubtitulo));
                tablaProductos.addCell(crearCeldaEtiqueta("Subtotal", fontSubtitulo));

                double totalGeneral = 0.0;

                try (Connection conn = Conexion.conectar();
                     PreparedStatement ps = conn.prepareStatement("SELECT nombre, descripcion, precio, cantidad, total_a_pagar FROM tb_detalle_factura WHERE idFactura = ?")) {
                    ps.setInt(1, idFactura);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        String nombre = rs.getString("nombre");
                        String descripcion = rs.getString("descripcion");
                        double precio = rs.getDouble("precio");
                        int cantidad = rs.getInt("cantidad");
                        double subtotal = rs.getDouble("total_a_pagar");
                        totalGeneral += subtotal;

                        tablaProductos.addCell(crearCeldaValor(nombre, fontNormal));
                        tablaProductos.addCell(crearCeldaValor(descripcion, fontNormal));
                        tablaProductos.addCell(crearCeldaValor(String.format("$%.2f", precio), fontNormal));
                        tablaProductos.addCell(crearCeldaValor(String.valueOf(cantidad), fontNormal));
                        tablaProductos.addCell(crearCeldaValor(String.format("$%.2f", subtotal), fontNormal));
                    }
                }

                document.add(tablaProductos);

                document.add(new Paragraph(" "));

                // TOTAL GENERAL
                Paragraph total = new Paragraph("Total a pagar: $" + String.format("%.2f", totalGeneral), fontSubtitulo);
                total.setAlignment(Element.ALIGN_RIGHT);
                document.add(total);

                document.close();
                JOptionPane.showMessageDialog(this, "Factura exportada exitosamente.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al exportar el PDF", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    private PdfPCell crearCeldaEtiqueta(String texto, Font font) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, font));
        celda.setBackgroundColor(BaseColor.LIGHT_GRAY);
        celda.setHorizontalAlignment(Element.ALIGN_LEFT);
        celda.setPadding(8);
        return celda;
    }

    private PdfPCell crearCeldaValor(String texto, Font font) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, font));
        celda.setHorizontalAlignment(Element.ALIGN_LEFT);
        celda.setPadding(8);
        return celda;
    }



    private void volver() {
        dispose();
    }
}