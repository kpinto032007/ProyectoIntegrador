import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class gestionarUsuario extends JFrame {
    private JTable tablaUsuarios;
    private DefaultTableModel modelo;

    public gestionarUsuario() {
        setTitle("Gestión de Usuarios");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panelUsuarios = crearPanelUsuarios();
        add(panelUsuarios, BorderLayout.NORTH);

        modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Nombre");
        modelo.addColumn("Apellido");
        modelo.addColumn("Teléfono");
        modelo.addColumn("Usuario");
        modelo.addColumn("Estado");

        tablaUsuarios = new JTable(modelo);
        JScrollPane scrollPane = new JScrollPane(tablaUsuarios);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnRegresar = new JButton("Volver");
        buttonPanel.add(btnEditar);
        buttonPanel.add(btnEliminar);
        buttonPanel.add(btnRegresar);
        add(buttonPanel, BorderLayout.SOUTH);

        cargarUsuarios();

        btnEditar.addActionListener(e -> {
            System.out.println("Botón de edición presionado");
            editarUsuario();
        });

        btnEliminar.addActionListener(e -> {
            System.out.println("Botón de eliminación presionado");
            eliminarUsuario();
        });

        btnRegresar.addActionListener(e -> {
            dispose();
            new Menu(); // Asegúrate de que esta línea crea y muestra la ventana del menú principal
        });

        setVisible(true);
    }

    private JPanel crearPanelUsuarios() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel titulo = new JLabel("Gestión de Usuarios", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titulo, BorderLayout.NORTH);

        return panel;
    }

    private void cargarUsuarios() {
        try (Connection cn = Conexion.conectar();
             Statement stmt = cn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT idUsuario, nombre, apellido, telefono, usuario, estado FROM tb_usuarios")) {

            while (rs.next()) {
                Object[] fila = {
                        rs.getInt("idUsuario"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("telefono"),
                        rs.getString("usuario"),
                        rs.getBoolean("estado") ? "Activo" : "Inactivo"
                };
                modelo.addRow(fila);
            }

            System.out.println("Usuarios cargados correctamente. Total: " + modelo.getRowCount());

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar usuarios: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void editarUsuario() {
        System.out.println("Iniciando edición de usuario");
        int filaSeleccionada = tablaUsuarios.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario para editar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        System.out.println("Fila seleccionada: " + filaSeleccionada);

        String nombre = (String) modelo.getValueAt(filaSeleccionada, 1);
        String apellido = (String) modelo.getValueAt(filaSeleccionada, 2);
        String telefono = (String) modelo.getValueAt(filaSeleccionada, 3);
        String usuario = (String) modelo.getValueAt(filaSeleccionada, 4);
        String estadoStr = (String) modelo.getValueAt(filaSeleccionada, 5);

        // Convertir estado a entero (1 para Activo, 0 para Inactivo)
        int estado = estadoStr.equals("Activo") ? 1 : 0;

        JPanel panel = new JPanel(new GridLayout(0, 2));
        JTextField tfNombre = new JTextField(nombre);
        JTextField tfApellido = new JTextField(apellido);
        JTextField tfTelefono = new JTextField(telefono);
        JTextField tfUsuario = new JTextField(usuario);
        JComboBox<String> cbEstado = new JComboBox<>(new String[]{"Activo", "Inactivo"});
        cbEstado.setSelectedItem(estadoStr);

        panel.add(new JLabel("Nombre:"));
        panel.add(tfNombre);
        panel.add(new JLabel("Apellido:"));
        panel.add(tfApellido);
        panel.add(new JLabel("Teléfono:"));
        panel.add(tfTelefono);
        panel.add(new JLabel("Usuario:"));
        panel.add(tfUsuario);
        panel.add(new JLabel("Estado:"));
        panel.add(cbEstado);

        int option = JOptionPane.showConfirmDialog(this, panel, "Editar Usuario", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection conn = Conexion.conectar()) {
                String sql = "UPDATE tb_usuarios SET nombre = ?, apellido = ?, telefono = ?, usuario = ?, estado = ? WHERE idUsuario = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, tfNombre.getText());
                stmt.setString(2, tfApellido.getText());
                stmt.setString(3, tfTelefono.getText());
                stmt.setString(4, tfUsuario.getText());
                stmt.setInt(5, cbEstado.getSelectedItem().equals("Activo") ? 1 : 0);
                stmt.setInt(6, (int) modelo.getValueAt(filaSeleccionada, 0));

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    modelo.setValueAt(tfNombre.getText(), filaSeleccionada, 1);
                    modelo.setValueAt(tfApellido.getText(), filaSeleccionada, 2);
                    modelo.setValueAt(tfTelefono.getText(), filaSeleccionada, 3);
                    modelo.setValueAt(tfUsuario.getText(), filaSeleccionada, 4);
                    modelo.setValueAt(cbEstado.getSelectedItem(), filaSeleccionada, 5);
                    JOptionPane.showMessageDialog(this, "Usuario actualizado correctamente");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al actualizar el usuario", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al editar el usuario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void eliminarUsuario() {
        System.out.println("Botón de eliminar usuario presionado");

        int filaSeleccionada = tablaUsuarios.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario para eliminar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        System.out.println("Fila seleccionada para eliminar: " + filaSeleccionada);

        int idUsuario = (int) modelo.getValueAt(filaSeleccionada, 0);
        String nombreUsuario = (String) modelo.getValueAt(filaSeleccionada, 1);

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("ID Usuario:"));
        panel.add(new JLabel(String.valueOf(idUsuario)));
        panel.add(new JLabel("Nombre:"));
        panel.add(new JLabel(nombreUsuario));

        int confirmacion = JOptionPane.showConfirmDialog(this, panel, "¿Está seguro de eliminar este usuario?", JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            try (Connection cn = Conexion.conectar();
                 PreparedStatement stmt = cn.prepareStatement("DELETE FROM tb_usuarios WHERE idUsuario = ?")) {

                stmt.setInt(1, idUsuario);

                int rowsDeleted = stmt.executeUpdate();
                if (rowsDeleted > 0) {
                    modelo.removeRow(filaSeleccionada);
                    JOptionPane.showMessageDialog(this, "Usuario eliminado correctamente");
                    System.out.println("Usuario eliminado: ID " + idUsuario);
                } else {
                    JOptionPane.showMessageDialog(this, "Error al eliminar el usuario", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar usuario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}