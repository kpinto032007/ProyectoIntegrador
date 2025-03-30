import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class gestionarUsuario extends JFrame {
    private JTable tablaUsuarios;
    private DefaultTableModel modelo;

    public gestionarUsuario(){
        setTitle("Gestión de Usuarios");
        setSize(600,400);
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

        cargarUsuarios();

        tablaUsuarios = new JTable(modelo);
        JScrollPane scrollPane = new JScrollPane(tablaUsuarios);
        add(scrollPane, BorderLayout.CENTER);


        JPanel buttonPanel = new JPanel();
        JButton btnEditar = new JButton("Editar Usuario");
        JButton btnEliminar = new JButton("Eliminar Usuario");
        JButton btnRegresar = new JButton("Regresar al Menú");

        buttonPanel.add(btnRegresar);
        add(buttonPanel, BorderLayout.SOUTH);


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

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar usuarios: " + e.getMessage());
        }
    }


}
