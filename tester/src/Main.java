import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Verificar conexión con la base de datos
        Conexion.conectar();

        // Iniciar la interfaz gráfica
        SwingUtilities.invokeLater(SignInUI::new);

    }
}
