package codexlatinus;

import codexlatinus.frontend.VentanaPrincipal;
import javax.swing.SwingUtilities;

public class CodexLatinus
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() ->
        {
            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
        });
    }
}
