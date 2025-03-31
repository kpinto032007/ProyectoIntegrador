import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Conexion {
    // Metodo de conexion con base de datos
    public static Connection conectar() {
        Connection cn = null; // Variable para la conexión

        try {
            // Establecer la conexión a MySQL
            cn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bd_sistema_inventario", "root", "2102");
            System.out.println("Conexión exitosa a la base de datos.");
        } catch (SQLException e) {
            System.out.println("Error en la conexión local: " + e.getMessage());
        }

        return cn; // Retornar la conexión
    }
    public static boolean registrarUsuario(String nombre, String apellido, String telefono, String usuario, String password) {
        String sql = "INSERT INTO tb_usuarios (nombre, apellido, telefono, usuario, password) VALUES (?, ?, ?, ?, ?)";

        try (Connection cn = conectar(); PreparedStatement pst = cn.prepareStatement(sql)) {
            pst.setString(1, nombre);
            pst.setString(2, apellido);
            pst.setString(3, telefono);
            pst.setString(4, usuario);
            pst.setString(5, password);

            int filasAfectadas = pst.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.out.println(" Error al registrar usuario: " + e.getMessage());
            return false;
        }
    }
    public static boolean validarUsuario(String usuario, String password) {
        String sql = "SELECT * FROM tb_usuarios WHERE usuario = ? AND password = ?";

        try (Connection cn = conectar(); PreparedStatement pst = cn.prepareStatement(sql)) {
            pst.setString(1, usuario);
            pst.setString(2, password);

            return pst.executeQuery().next(); // Si hay un resultado, el usuario es válido
        } catch (SQLException e) {
            System.out.println(" Error al validar usuario: " + e.getMessage());
            return false;
        }
    }


}