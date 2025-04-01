import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class gestionarUsuario extends JFrame {
    private JTable tablaUsuarios;
    private DefaultTableModel modelo;

    public gestionarUsuario(){
        setTitle("Gestión de Usuarios"); //titulo ventana
        setSize(600,400); // tamaño panel
        setLocationRelativeTo(null); //centrado
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //quiere decir que cierra solo la ventana mas no el programa
        setLayout(new BorderLayout()); //divide el espacio en 5 areas

        JPanel panelUsuarios = crearPanelUsuarios();
        add(panelUsuarios, BorderLayout.NORTH);

        modelo = new DefaultTableModel(); //el defaultTableModelpermite manejar los datos de la tabla y facilita agregar, editar o eliminar filas
        modelo.addColumn("ID");
        modelo.addColumn("Nombre");
        modelo.addColumn("Apellido");
        modelo.addColumn("Teléfono");
        modelo.addColumn("Usuario");
        modelo.addColumn("Estado");

        cargarUsuarios();

        tablaUsuarios = new JTable(modelo); // creacion de un objeto Jtable usando como parametro el modelo definido con anterioridad
        JScrollPane scrollPane = new JScrollPane(tablaUsuarios); //el JScrollPane lo que hace es añadir barras de desplazamiento si los datos exceden  el tamaño visible
        add(scrollPane, BorderLayout.CENTER);// se agrega al panel y pasa a verse como lo importante


        JPanel buttonPanel = new JPanel(); //este panel es para organizar los botones
        JButton btnEditar = new JButton("Editar Usuario");
        JButton btnEliminar = new JButton("Eliminar Usuario");
        JButton btnRegresar = new JButton("Regresar al Menú");
        buttonPanel.add(btnEditar);
        buttonPanel.add(btnEliminar);
        buttonPanel.add(btnRegresar);
        add(buttonPanel, BorderLayout.SOUTH);//se crean los botones, se añaden a dicho panel y luego de añadirlos simplemente los ubicamos

        btnEditar.addActionListener(e -> editarUsuario());
        btnEliminar.addActionListener(e -> eliminarUsuario());
        btnRegresar.addActionListener(e -> {
            dispose();
            new Menu(); // Asegúrate de que esta línea crea y muestra la ventana del menú principal
        });


        setVisible(true);
    }

    private JPanel crearPanelUsuarios(){
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel titulo = new JLabel("Gestión de Usuarios", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial",Font.BOLD, 20));
        panel.add(titulo, BorderLayout.NORTH);

        return panel;
    }

    private void cargarUsuarios() {
        try (Connection cn = Conexion.conectar(); //llamar a un metodo conectar de la clase conexion que posiblemente devuelte un objeto conection
             Statement stmt = cn.createStatement(); //para sentencias SQL
             ResultSet rs = stmt.executeQuery("SELECT id, nombre, apellido, telefono, usuario, estado FROM tb_usuarios")){
            //arriba de este coment se ejecuta una consulta para seleccionar los campos de la tabla en mysql
            while (rs.next()) {
                Object[] fila = {
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("telefono"),
                        rs.getString("usuario"),
                        rs.getBoolean("estado") ? "Activo" : "Inactivo"
                };
                modelo.addRow(fila);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar usuarios: " + e.getMessage());
        }
    }

    private void editarUsuario() {
        int filaSeleccionada = tablaUsuarios.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un usuario para editar.");
            return;
        }

        int id = (int) modelo.getValueAt(filaSeleccionada, 0);
        String nuevoNombre = JOptionPane.showInputDialog(this, "Nuevo nombre:", modelo.getValueAt(filaSeleccionada, 1));
        String nuevoApellido = JOptionPane.showInputDialog(this, "Nuevo apellido:", modelo.getValueAt(filaSeleccionada, 2));
        String nuevoTelefono = JOptionPane.showInputDialog(this, "Nuevo teléfono:", modelo.getValueAt(filaSeleccionada, 3));
        String nuevoUsuario = JOptionPane.showInputDialog(this, "Nuevo usuario:", modelo.getValueAt(filaSeleccionada, 4));
        String[] estados = {"Activo", "Inactivo"};
        String nuevoEstado = (String) JOptionPane.showInputDialog(this, "Nuevo estado:", "Estado", JOptionPane.QUESTION_MESSAGE, null, estados, modelo.getValueAt(filaSeleccionada, 5));

        if (nuevoNombre != null && nuevoApellido != null && nuevoTelefono != null && nuevoUsuario != null && nuevoEstado != null) {
            try (Connection cn = Conexion.conectar();
                 PreparedStatement stmt = cn.prepareStatement("UPDATE tb_usuarios SET nombre = ?, apellido = ?, telefono = ?, usuario = ?, estado = ? WHERE id = ?")) {
                stmt.setString(1, nuevoNombre);
                stmt.setString(2, nuevoApellido);
                stmt.setString(3, nuevoTelefono);
                stmt.setString(4, nuevoUsuario);
                stmt.setBoolean(5, nuevoEstado.equals("Activo"));
                stmt.setInt(6, id);
                stmt.executeUpdate();

                modelo.setValueAt(nuevoNombre, filaSeleccionada, 1);
                modelo.setValueAt(nuevoApellido, filaSeleccionada, 2);
                modelo.setValueAt(nuevoTelefono, filaSeleccionada, 3);
                modelo.setValueAt(nuevoUsuario, filaSeleccionada, 4);
                modelo.setValueAt(nuevoEstado, filaSeleccionada, 5);
                JOptionPane.showMessageDialog(this, "Usuario actualizado.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al actualizar usuario: " + e.getMessage());
            }
        }
    }

    private void eliminarUsuario() {
        int filaSeleccionada = tablaUsuarios.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un usuario para eliminar.");
            return;
        }

        int id = (int) modelo.getValueAt(filaSeleccionada, 0);
        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Estás seguro de eliminar este usuario?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            try (Connection cn = Conexion.conectar();
                 PreparedStatement stmt = cn.prepareStatement("DELETE FROM tb_usuarios WHERE id = ?")) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
                modelo.removeRow(filaSeleccionada);
                JOptionPane.showMessageDialog(this, "Usuario eliminado.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar usuario: " + e.getMessage());
            }
        }
    }
}
