import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class gestionarProveedor extends JFrame {
    private JTable tablaProveedores;
    private DefaultTableModel modelo;

    public gestionarProveedor() {
        setTitle("Gestión de Proveedor");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panelUsuarios = crearPanelProveedor();
        add(panelUsuarios, BorderLayout.NORTH);

        modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Nombre");
        modelo.addColumn("Apellido");
        modelo.addColumn("Cedula");
        modelo.addColumn("Teléfono");
        modelo.addColumn("Empresa Proveedora");
        modelo.addColumn("Estado");

        tablaProveedores = new JTable(modelo);
        JScrollPane scrollPane = new JScrollPane(tablaProveedores);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnRegresar = new JButton("Volver");
        buttonPanel.add(btnEditar);
        buttonPanel.add(btnEliminar);
        buttonPanel.add(btnRegresar);
        add(buttonPanel, BorderLayout.SOUTH);

        cargarProveedor();

        btnEditar.addActionListener(e -> {
            System.out.println("Botón de edición presionado");
            editarProveedor();
        });

        btnEliminar.addActionListener(e -> {
            System.out.println("Botón de eliminación presionado");
            eliminarProveedor();
        });

        btnRegresar.addActionListener(e -> {volver();// Asegúrate de que esta línea crea y muestra la ventana del menú principal
        });

        setVisible(true);
    }

    private JPanel crearPanelProveedor() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel titulo = new JLabel("Gestión de Proveedores", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titulo, BorderLayout.NORTH);

        return panel;
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

            System.out.println("Proveedores cargados correctamente. Total: " + modelo.getRowCount());

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar Proveedores: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void editarProveedor() {
        System.out.println("Iniciando edición de proveedor");
        int filaSeleccionada = tablaProveedores.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un proveedor para editar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        System.out.println("Fila seleccionada: " + filaSeleccionada);

        String nombre = (String) modelo.getValueAt(filaSeleccionada, 1);
        String apellido = (String) modelo.getValueAt(filaSeleccionada, 2);
        String cedula = (String) modelo.getValueAt(filaSeleccionada, 3);
        String telefono = (String) modelo.getValueAt(filaSeleccionada, 4);
        String empresa_proveedora = (String) modelo.getValueAt(filaSeleccionada, 5);
        String estadoStr = (String) modelo.getValueAt(filaSeleccionada, 6);

        // Convertir estado a entero (1 para Activo, 0 para Inactivo)
        int estado = estadoStr.equals("Activo") ? 1 : 0;

        JPanel panel = new JPanel(new GridLayout(0, 2));
        JTextField tfNombre = new JTextField(nombre);
        JTextField tfApellido = new JTextField(apellido);
        JTextField tfCedula = new JTextField(cedula);
        JTextField tfTelefono = new JTextField(telefono);
        JTextField tfEmpresa_Proveedora = new JTextField(empresa_proveedora);
        JComboBox<String> cbEstado = new JComboBox<>(new String[]{"Activo", "Inactivo"});
        cbEstado.setSelectedItem(estadoStr);

        panel.add(new JLabel("Nombre:"));
        panel.add(tfNombre);
        panel.add(new JLabel("Apellido:"));
        panel.add(tfApellido);
        panel.add(new JLabel("Cédula:"));
        panel.add(tfCedula);
        panel.add(new JLabel("Teléfono:"));
        panel.add(tfTelefono);
        panel.add(new JLabel("Empresa Proveedora:"));
        panel.add(tfEmpresa_Proveedora);
        panel.add(new JLabel("Estado:"));
        panel.add(cbEstado);

        int option = JOptionPane.showConfirmDialog(this, panel, "Editar Proveedor", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection conn = Conexion.conectar()) {
                String sql = "UPDATE tb_proveedores SET nombre = ?, apellido = ?, cedula = ?, telefono =?,  empresa_proveedora = ?, estado = ? WHERE idProveedor = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, tfNombre.getText());
                stmt.setString(2, tfApellido.getText());
                stmt.setString(3, tfCedula.getText());
                stmt.setString(4, tfTelefono.getText());
                stmt.setString(5, tfEmpresa_Proveedora.getText());
                stmt.setInt(6, cbEstado.getSelectedItem().equals("Activo") ? 1 : 0);
                stmt.setInt(7, (int) modelo.getValueAt(filaSeleccionada, 0));

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    modelo.setValueAt(tfNombre.getText(), filaSeleccionada, 1);
                    modelo.setValueAt(tfApellido.getText(), filaSeleccionada, 2);
                    modelo.setValueAt(tfCedula.getText(), filaSeleccionada, 3);
                    modelo.setValueAt(tfTelefono.getText(), filaSeleccionada, 4);
                    modelo.setValueAt(tfEmpresa_Proveedora.getText(), filaSeleccionada, 5);
                    modelo.setValueAt(cbEstado.getSelectedItem(), filaSeleccionada, 6);
                    JOptionPane.showMessageDialog(this, "Proveedor actualizado correctamente");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al actualizar el Proveedor", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al editar el Proveedor: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void eliminarProveedor() {
        System.out.println("Botón de eliminar proveedor presionado");

        int filaSeleccionada = tablaProveedores.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un proveedor para eliminar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        System.out.println("Fila seleccionada para eliminar: " + filaSeleccionada);

        int idProveedor = (int) modelo.getValueAt(filaSeleccionada, 0);
        String nombreProveedor = (String) modelo.getValueAt(filaSeleccionada, 1);

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("ID Proveedor:"));
        panel.add(new JLabel(String.valueOf(idProveedor)));
        panel.add(new JLabel("Nombre:"));
        panel.add(new JLabel(nombreProveedor));

        int confirmacion = JOptionPane.showConfirmDialog(this, panel, "¿Está seguro de eliminar este proveedor?", JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            try (Connection cn = Conexion.conectar();
                 PreparedStatement stmt = cn.prepareStatement("DELETE FROM tb_proveedores WHERE idProveedor = ?")) {

                stmt.setInt(1, idProveedor);

                int rowsDeleted = stmt.executeUpdate();
                if (rowsDeleted > 0) {
                    modelo.removeRow(filaSeleccionada);
                    JOptionPane.showMessageDialog(this, "Proveedor eliminado correctamente");
                    System.out.println("Usuario eliminado: ID " + idProveedor);
                } else {
                    JOptionPane.showMessageDialog(this, "Error al eliminar el Proveedor", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar proveedor: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
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
            case 0 -> new CrearProveedor().setVisible(true); /*.setVisible(true);*/
            case 1 -> new gestionarProveedor().setVisible(true);
            default -> {
            }
        }// Cierra solo esta ventana
    }
}

