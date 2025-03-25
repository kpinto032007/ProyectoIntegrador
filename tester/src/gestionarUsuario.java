import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class gestionarUsuario extends JFrame {
    private JTable tablaUsuarios;
    private DefaultTableModel modelo;
    private JButton btnModificar, btnEliminar;
    private Connection conn;

    public gestionarUsuario() {
        setTitle("Gestión de Usuarios"); //titulo de la ventana
        setSize(600, 400); //con esta defino el tamaño de la ventana en pixeles
        setLocationRelativeTo(null);//con esta centro la ventana de la pantalla
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // y con este cierro solo esta ventana mas no detengo la ejecucion del programa

        //con este creo el panel superior del titulo
        JPanel panelUsuarios = crearPanelUsuarios();
        add(panelUsuarios, BorderLayout.NORTH);

        //aqui creo el modelo de la tabla
        modelo = new DefaultTableModel();
        modelo.addColumn("IDUsuario");
        modelo.addColumn("Nombre");
        modelo.addColumn("Apellido");
        modelo.addColumn("Teléfono");
        modelo.addColumn("Usuario");
        modelo.addColumn("Estado");

        //aqui creo la tabla y a asocio con el modelo
        tablaUsuarios = new JTable(modelo);
        JScrollPane scrollPane = new JScrollPane(tablaUsuarios);
        add(scrollPane, BorderLayout.CENTER);

        //con este cargo los daatos de la tabla
        cargarUsuarios();

        //hago visible la ventana
        setVisible(true);


    }
    private JPanel crearPanelUsuarios(){
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel titulo = new JLabel("Gestion de Usuarios", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial",Font.BOLD, 20));
        panel.add(titulo, BorderLayout.NORTH);

        return panel;
    }

    private void cargarUsuarios() {
        try (Connection cn = Conexion.conectar();
             Statement stmt = cn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, nombre, apellido, telefono, usuario, estado FROM tb_usuarios")) {

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
    

}
