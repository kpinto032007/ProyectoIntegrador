import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ConsultarProveedor {

    private JFrame frame;
    private JTable tablaProveedores;
    private DefaultTableModel modelo;

    public ConsultarProveedor() {
        inicializar();
        cargarDatos();
    }

    private void inicializar() {
        frame = new JFrame("Consultar Proveedores");
        frame.setSize(800, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        modelo = new DefaultTableModel(new String[]{
                "ID", "Nombre", "Apellido", "Cédula", "Teléfono", "Empresa", "Editar", "Eliminar"
        }, 0);

        tablaProveedores = new JTable(modelo) {
            public boolean isCellEditable(int row, int column) {
                return column == 6 || column == 7;
            }
        };

        tablaProveedores.getColumnModel().getColumn(6).setCellRenderer(new BotonRenderer());
        tablaProveedores.getColumnModel().getColumn(6).setCellEditor(new BotonEditor(new JCheckBox(), true));
        tablaProveedores.getColumnModel().getColumn(7).setCellRenderer(new BotonRenderer());
        tablaProveedores.getColumnModel().getColumn(7).setCellEditor(new BotonEditor(new JCheckBox(), false));

        JScrollPane scroll = new JScrollPane(tablaProveedores);
        frame.add(scroll, BorderLayout.CENTER);

        // Panel inferior con los botones de navegación
        JPanel panelBotones = new JPanel();
        JButton btnMenu = new JButton("Volver al Menú Principal");
        JButton btnAtras = new JButton("Volver Atrás");
        JButton btnCancelar = new JButton("Cancelar");

        btnAtras.addActionListener(e -> {
            frame.dispose();
            new CrearProveedor(); // Vuelve al menú de "Ingresar / Consultar"
        });

        btnMenu.addActionListener(e -> {
            frame.dispose();
            new Menu(); // Asegúrate de tener esta clase
        });

        btnCancelar.addActionListener(e -> frame.dispose());

        panelBotones.add(btnCancelar);
        panelBotones.add(btnAtras);
        panelBotones.add(btnMenu);
        frame.add(panelBotones, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void cargarDatos() {
        modelo.setRowCount(0); // limpiar tabla
        try {
            Connection conn = Conexion.conectar();
            String sql = "SELECT * FROM tb_proveedores WHERE estado = 1";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] fila = {
                        rs.getInt("IdProveedor"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("cedula"),
                        rs.getString("telefono"),
                        rs.getString("empresa_proveedora"),
                        "Editar",
                        "Eliminar"
                };
                modelo.addRow(fila);
            }

            conn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error al cargar proveedores: " + ex.getMessage());
        }
    }

    class BotonRenderer extends JButton implements TableCellRenderer {
        public BotonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    class BotonEditor extends DefaultCellEditor {
        private JButton button;
        private boolean esEditar;
        private int fila;

        public BotonEditor(JCheckBox checkBox, boolean esEditar) {
            super(checkBox);
            this.esEditar = esEditar;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> {
                if (esEditar) {
                    editarProveedor(fila);
                } else {
                    eliminarProveedor(fila);
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.fila = row;
            button.setText((value == null) ? "" : value.toString());
            return button;
        }
    }

    private void editarProveedor(int fila) {
        int id = (int) modelo.getValueAt(fila, 0);
        String nombre = (String) modelo.getValueAt(fila, 1);
        String apellido = (String) modelo.getValueAt(fila, 2);
        String cedula = (String) modelo.getValueAt(fila, 3);
        String telefono = (String) modelo.getValueAt(fila, 4);
        String empresa = (String) modelo.getValueAt(fila, 5);

        JTextField txtNombre = new JTextField(nombre);
        JTextField txtApellido = new JTextField(apellido);
        JTextField txtCedula = new JTextField(cedula);
        JTextField txtTelefono = new JTextField(telefono);
        JTextField txtEmpresa = new JTextField(empresa);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Nombre:"));
        panel.add(txtNombre);
        panel.add(new JLabel("Apellido:"));
        panel.add(txtApellido);
        panel.add(new JLabel("Cédula:"));
        panel.add(txtCedula);
        panel.add(new JLabel("Teléfono:"));
        panel.add(txtTelefono);
        panel.add(new JLabel("Empresa:"));
        panel.add(txtEmpresa);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Editar Proveedor", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Connection conn = Conexion.conectar();
                String sql = "UPDATE tb_proveedores SET nombre=?, apellido=?, cedula=?, telefono=?, empresa_proveedora=? WHERE IdProveedor=?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, txtNombre.getText());
                stmt.setString(2, txtApellido.getText());
                stmt.setString(3, txtCedula.getText());
                stmt.setString(4, txtTelefono.getText());
                stmt.setString(5, txtEmpresa.getText());
                stmt.setInt(6, id);
                stmt.executeUpdate();
                conn.close();
                cargarDatos();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Error al actualizar proveedor: " + ex.getMessage());
            }
        }
    }

    private void eliminarProveedor(int fila) {
        int id = (int) modelo.getValueAt(fila, 0);
        int confirm = JOptionPane.showConfirmDialog(frame, "¿Desea eliminar este proveedor?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection conn = Conexion.conectar();
                String sql = "UPDATE tb_proveedores SET estado = 0 WHERE IdProveedor = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, id);
                stmt.executeUpdate();
                conn.close();
                cargarDatos();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Error al eliminar proveedor: " + ex.getMessage());
            }
        }
    }
}
