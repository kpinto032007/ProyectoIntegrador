import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class gestionarProveedor extends JFrame {

    private JTable tablaProveedores;
    private DefaultTableModel modelo;

    public gestionarProveedor() {
        setTitle("Gestión de Proveedor");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.decode("#B7A7A9")); // Granite

        JPanel panelTitulo = crearPanelTitulo();
        add(panelTitulo, BorderLayout.NORTH);

        modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Nombre");
        modelo.addColumn("Apellido");
        modelo.addColumn("Cédula");
        modelo.addColumn("Teléfono");
        modelo.addColumn("Empresa Proveedora");
        modelo.addColumn("Estado");

        tablaProveedores = new JTable(modelo);
        tablaProveedores.setBackground(Color.decode("#F6ECE3")); // Beige
        tablaProveedores.setForeground(Color.BLACK);
        tablaProveedores.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaProveedores.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(tablaProveedores);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.decode("#B7A7A9"));

        JButton btnEditar = createStyledButton("Editar");
        JButton btnEliminar = createStyledButton("Eliminar");
        JButton btnRegresar = createStyledButton("Volver");

        btnEditar.addActionListener(e -> editarProveedor());
        btnEliminar.addActionListener(e -> eliminarProveedor());
        btnRegresar.addActionListener(e -> volver());

        buttonPanel.add(btnEditar);
        buttonPanel.add(btnEliminar);
        buttonPanel.add(btnRegresar);

        add(buttonPanel, BorderLayout.SOUTH);

        cargarProveedor();
        setVisible(true);
    }

    private JPanel crearPanelTitulo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.decode("#91766E")); // Brown

        JLabel titulo = new JLabel("Gestión de Proveedores", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        panel.add(titulo, BorderLayout.CENTER);

        return panel;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(Color.decode("#91766E")); // Brown
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return button;
    }

    private void cargarProveedor() {
        try (Connection cn = Conexion.conectar();
             Statement stmt = cn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT idProveedor, nombre, apellido, cedula, telefono, empresa_proveedora, estado FROM tb_proveedores")) {
            while (rs.next()) {
                Object[] fila = {
                        rs.getInt("idProveedor"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("cedula"),
                        rs.getString("telefono"),
                        rs.getString("empresa_proveedora"),
                        rs.getBoolean("estado") ? "Activo" : "Inactivo"
                };
                modelo.addRow(fila);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar proveedores: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarProveedor() {
        int filaSeleccionada = tablaProveedores.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un proveedor para editar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombre = (String) modelo.getValueAt(filaSeleccionada, 1);
        String apellido = (String) modelo.getValueAt(filaSeleccionada, 2);
        String cedula = (String) modelo.getValueAt(filaSeleccionada, 3);
        String telefono = (String) modelo.getValueAt(filaSeleccionada, 4);
        String empresa_proveedora = (String) modelo.getValueAt(filaSeleccionada, 5);
        String estadoStr = (String) modelo.getValueAt(filaSeleccionada, 6);
        int estado = estadoStr.equals("Activo") ? 1 : 0;

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBackground(Color.decode("#F6ECE3"));

        JTextField tfNombre = createStyledTextField(nombre);
        JTextField tfApellido = createStyledTextField(apellido);
        JTextField tfCedula = createStyledTextField(cedula);
        JTextField tfTelefono = createStyledTextField(telefono);
        JTextField tfEmpresa = createStyledTextField(empresa_proveedora);
        JComboBox<String> cbEstado = new JComboBox<>(new String[]{"Activo", "Inactivo"});
        cbEstado.setSelectedItem(estadoStr);
        cbEstado.setBackground(Color.WHITE);
        cbEstado.setForeground(Color.BLACK);

        panel.add(new JLabel("Nombre:"));
        panel.add(tfNombre);
        panel.add(new JLabel("Apellido:"));
        panel.add(tfApellido);
        panel.add(new JLabel("Cédula:"));
        panel.add(tfCedula);
        panel.add(new JLabel("Teléfono:"));
        panel.add(tfTelefono);
        panel.add(new JLabel("Empresa Proveedora:"));
        panel.add(tfEmpresa);
        panel.add(new JLabel("Estado:"));
        panel.add(cbEstado);

        int option = JOptionPane.showConfirmDialog(this, panel, "Editar Proveedor", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection conn = Conexion.conectar()) {
                String sql = "UPDATE tb_proveedores SET nombre = ?, apellido = ?, cedula = ?, telefono = ?, empresa_proveedora = ?, estado = ? WHERE idProveedor = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, tfNombre.getText());
                stmt.setString(2, tfApellido.getText());
                stmt.setString(3, tfCedula.getText());
                stmt.setString(4, tfTelefono.getText());
                stmt.setString(5, tfEmpresa.getText());
                stmt.setInt(6, cbEstado.getSelectedItem().equals("Activo") ? 1 : 0);

                Object idObj = modelo.getValueAt(filaSeleccionada, 0);
                int idProveedor;
                if (idObj instanceof Integer) {
                    idProveedor = (Integer) idObj;
                } else {
                    idProveedor = Integer.parseInt(idObj.toString());
                }

                stmt.setInt(7, idProveedor);

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    modelo.setValueAt(tfNombre.getText(), filaSeleccionada, 1);
                    modelo.setValueAt(tfApellido.getText(), filaSeleccionada, 2);
                    modelo.setValueAt(tfCedula.getText(), filaSeleccionada, 3);
                    modelo.setValueAt(tfTelefono.getText(), filaSeleccionada, 4);
                    modelo.setValueAt(tfEmpresa.getText(), filaSeleccionada, 5);
                    modelo.setValueAt(cbEstado.getSelectedItem(), filaSeleccionada, 6);
                    JOptionPane.showMessageDialog(this, "Proveedor actualizado correctamente.");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al actualizar el proveedor.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al editar proveedor: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void eliminarProveedor() {
        int filaSeleccionada = tablaProveedores.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un proveedor para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object idObj = modelo.getValueAt(filaSeleccionada, 0);
        int idProveedor;
        if (idObj instanceof Integer) {
            idProveedor = (Integer) idObj;
        } else {
            idProveedor = Integer.parseInt(idObj.toString());
        }

        String nombreProveedor = (String) modelo.getValueAt(filaSeleccionada, 1);

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBackground(Color.decode("#F6ECE3"));
        panel.add(new JLabel("ID Proveedor:"));
        panel.add(new JLabel(String.valueOf(idProveedor)));
        panel.add(new JLabel("Nombre:"));
        panel.add(new JLabel(nombreProveedor));

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                panel,
                "¿Está seguro de eliminar este proveedor?",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            try (Connection cn = Conexion.conectar();
                 PreparedStatement stmt = cn.prepareStatement("DELETE FROM tb_proveedores WHERE idProveedor = ?")) {
                stmt.setInt(1, idProveedor);
                int rowsDeleted = stmt.executeUpdate();
                if (rowsDeleted > 0) {
                    modelo.removeRow(filaSeleccionada);
                    JOptionPane.showMessageDialog(this, "Proveedor eliminado correctamente.");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al eliminar el proveedor.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar proveedor: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void volver() {
        dispose();
        String[] opciones = {"Crear Proveedor", "Gestionar Proveedor", "Cancelar"};
        int opcion = JOptionPane.showOptionDialog(
                null,
                "Seleccione una opción:",
                "Gestión de Proveedores",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );
        switch (opcion) {
            case 0 -> new CrearProveedor().setVisible(true);
            case 1 -> new gestionarProveedor().setVisible(true);
            default -> {}
        }
    }

    private JTextField createStyledTextField(String text) {
        JTextField field = new JTextField(text);
        field.setBackground(Color.WHITE);
        field.setForeground(Color.BLACK);
        field.setBorder(BorderFactory.createLineBorder(Color.decode("#91766E")));
        return field;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new gestionarProveedor().setVisible(true));
    }
}