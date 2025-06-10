import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableCellRenderer;


public class gestionarUsuario extends JFrame {
    private JTable tablaUsuarios;
    private DefaultTableModel modelo;

    // definicion de los colores de la paleta
    private final Color COLOR_BLACK = Color.decode("#000000");
    private final Color COLOR_GRANITE = Color.decode("#B7A7A9");
    private final Color COLOR_BROWN = Color.decode("#91766E");
    private final Color COLOR_BEIGE = Color.decode("#F6ECE3");
    private final Color COLOR_WHITE = Color.decode("#FFFFFF");

    public gestionarUsuario() {
        setTitle("Gestión de Usuarios");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        getContentPane().setBackground(COLOR_BEIGE); // Fondo general

        JPanel panelUsuarios = crearPanelUsuarios();
        add(panelUsuarios, BorderLayout.NORTH);

        modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Nombre");
        modelo.addColumn("Apellido");
        modelo.addColumn("Teléfono");
        modelo.addColumn("Usuario");
        modelo.addColumn("Estado");
    //nuevo
        tablaUsuarios = new JTable(modelo);
        tablaUsuarios.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaUsuarios.setRowHeight(25);
        tablaUsuarios.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaUsuarios.getTableHeader().setBackground(COLOR_BROWN);
        tablaUsuarios.getTableHeader().setForeground(COLOR_WHITE);
        tablaUsuarios.setBackground(COLOR_WHITE);
        tablaUsuarios.setForeground(COLOR_BLACK);

        // Centrado de celdas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tablaUsuarios.getColumnCount(); i++) {
            tablaUsuarios.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(tablaUsuarios);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(COLOR_BEIGE);
        JButton btnEditar = crearBoton("Editar Usuario");
        JButton btnEliminar = crearBoton("Eliminar Usuario");
        JButton btnRegresar = crearBoton("Volver");

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
//nuevo
    private JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setBackground(COLOR_GRANITE);//color
        boton.setForeground(COLOR_BLACK);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createLineBorder(COLOR_BROWN, 1));
        return boton;
    }

    private JPanel crearPanelUsuarios() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(COLOR_BEIGE);

        JLabel titulo = new JLabel("Gestión de Usuarios", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(COLOR_BROWN);
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
        System.out.println("Fila seleccionada: " + filaSeleccionada);//.....
        // Datos iniciales
        String nombre = (String) modelo.getValueAt(filaSeleccionada, 1);
        String apellido = (String) modelo.getValueAt(filaSeleccionada, 2);
        String telefono = (String) modelo.getValueAt(filaSeleccionada, 3);
        String usuario = (String) modelo.getValueAt(filaSeleccionada, 4);
        String estadoStr = (String) modelo.getValueAt(filaSeleccionada, 5);

        Color beige = Color.decode("#F6ECE3");
        Color brown = Color.decode("#91766E");
        Color white = Color.decode("#FFFFFF");

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBackground(beige);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        Font labelFont = new Font("Arial", Font.BOLD, 14);

        // Convertir estado a entero (1 para Activo, 0 para Inactivo)
        int estado = estadoStr.equals("Activo") ? 1 : 0;

       // JPanel panel = new JPanel(new GridLayout(0, 2));
        JTextField tfNombre = new JTextField(nombre);
        JTextField tfApellido = new JTextField(apellido);
        JTextField tfTelefono = new JTextField(telefono);
        JTextField tfUsuario = new JTextField(usuario);
        JComboBox<String> cbEstado = new JComboBox<>(new String[]{"Activo", "Inactivo"});
        cbEstado.setSelectedItem(estadoStr);

        JTextField[] campos = {tfNombre, tfApellido, tfTelefono, tfUsuario};
        for (JTextField campo : campos) {
            campo.setBackground(white);
            campo.setForeground(Color.BLACK);
            campo.setBorder(BorderFactory.createLineBorder(brown));
            campo.setFont(new Font("Arial", Font.PLAIN, 13));
        }
        cbEstado.setBackground(white);
        cbEstado.setForeground(Color.BLACK);
        cbEstado.setFont(new Font("Arial", Font.PLAIN, 13));
        cbEstado.setBorder(BorderFactory.createLineBorder(brown));
        //de aqui para allá hice un cambio de JOptionPane por el JDialog para poder cambiar el diseño de el panel de edición en los botones
        String[] etiquetas = {"Nombre:", "Apellido:", "Teléfono:", "Usuario:", "Estado:"};
        Component[] componentes = {tfNombre, tfApellido, tfTelefono, tfUsuario, cbEstado};
        for (int i = 0; i < etiquetas.length; i++) {
            JLabel lbl = new JLabel(etiquetas[i]);
            lbl.setFont(labelFont);
            lbl.setForeground(brown);
            panel.add(lbl);
            panel.add(componentes[i]);
        }

        // Crear diálogo personalizado
        JDialog dialog = new JDialog((Frame) null, "Editar Usuario", true);
        dialog.getContentPane().setBackground(beige);
        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);

        // Panel botones
        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(beige);

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setBackground(COLOR_GRANITE);
        btnGuardar.setForeground(COLOR_BLACK);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setFont(new Font("Arial", Font.BOLD, 13));
        btnGuardar.setBorder(BorderFactory.createLineBorder(brown));
        btnGuardar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(COLOR_GRANITE);
        btnCancelar.setForeground(COLOR_BLACK);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 13));
        btnCancelar.setBorder(BorderFactory.createLineBorder(brown));
        btnCancelar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        dialog.add(panelBotones, BorderLayout.SOUTH);

        // Acción botón Guardar
        btnGuardar.addActionListener(e -> {
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
                    JOptionPane.showMessageDialog(dialog, "Usuario actualizado correctamente");
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Error al actualizar el usuario", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error al editar el usuario: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        // Acción botón Cancelar
        btnCancelar.addActionListener(e -> dialog.dispose());

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
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