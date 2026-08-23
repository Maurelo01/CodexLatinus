package codexlatinus.frontend;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class NumeroLineaVisual extends JComponent
{
    private final JTextPane editor;
    private final JScrollPane scrollPane;
    private final int digitosMin = 2;

    public NumeroLineaVisual(JTextPane editor, JScrollPane scrollPane)
    {
        this.editor = editor;
        this.scrollPane = scrollPane;
        setFont(editor.getFont());
        setPreferredSize(new Dimension(40, 0));
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if (editor == null) return;
        g.setFont(editor.getFont());
        g.setColor(new Color(150, 150, 150));
        int lineaAltura = editor.getFontMetrics(editor.getFont()).getHeight();
        int primeraLinea = getPrimeraLineaVisible();
        int lineasVisibles = getHeight() / lineaAltura + 1;
        for (int i = 0; i < lineasVisibles; i++)
        {
            int numeroLinea = primeraLinea + i + 1;
            int y = (i + 1) * lineaAltura - 3;
            g.drawString(Integer.toString(numeroLinea), 5, y);
        }
    }

    private int getPrimeraLineaVisible()
    {
        if (scrollPane == null) return 0;
        JViewport viewport = scrollPane.getViewport();
        Point viewPosition = viewport.getViewPosition();
        int posicionTexto = editor.viewToModel2D(viewPosition);
        if (posicionTexto < 0) return 0;
        Element root = editor.getDocument().getDefaultRootElement();
        return root.getElementIndex(posicionTexto);
    }

    public void actualizar()
    {
        repaint();
    }
}