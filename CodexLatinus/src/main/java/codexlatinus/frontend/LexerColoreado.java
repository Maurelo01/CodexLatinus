package codexlatinus.frontend;

import codexlatinus.CodexLatinusLexer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class LexerColoreado
{

    private final SimpleAttributeSet attrPalabraReservada = new SimpleAttributeSet();
    private final SimpleAttributeSet attrTipoDato = new SimpleAttributeSet();
    private final SimpleAttributeSet attrCadena = new SimpleAttributeSet();
    private final SimpleAttributeSet attrCaracter = new SimpleAttributeSet();
    private final SimpleAttributeSet attrComentario = new SimpleAttributeSet();
    private final SimpleAttributeSet attrNumero = new SimpleAttributeSet();
    private final SimpleAttributeSet attrOperador = new SimpleAttributeSet();
    private final SimpleAttributeSet attrIdentificador = new SimpleAttributeSet();

    public LexerColoreado()
    {
    StyleConstants.setForeground(attrPalabraReservada, Color.BLUE);
    StyleConstants.setForeground(attrTipoDato, Color.GREEN.darker());
    StyleConstants.setForeground(attrCadena, Color.RED);
    StyleConstants.setForeground(attrCaracter, Color.CYAN);
    StyleConstants.setForeground(attrComentario, Color.LIGHT_GRAY);
    StyleConstants.setForeground(attrNumero, Color.MAGENTA);
    StyleConstants.setForeground(attrOperador, Color.ORANGE.darker());
    StyleConstants.setForeground(attrIdentificador, Color.YELLOW.darker());
    }

    public void aplicarResaltado(JTextPane editor)
    {
        StyledDocument doc = editor.getStyledDocument();
        String texto = editor.getText();
        doc.setCharacterAttributes(0, texto.length(), editor.getStyle(StyleContext.DEFAULT_STYLE), true);
        CodexLatinusLexer lexer = new CodexLatinusLexer(CharStreams.fromString(texto));
        lexer.removeErrorListeners();
        for (Token token : lexer.getAllTokens())
        {
            int tipo = token.getType();
            AttributeSet estilo = obtenerEstiloParaToken(tipo);
            if (estilo != null)
            {
                doc.setCharacterAttributes(token.getStartIndex(), token.getText().length(), estilo, true);
            }
        }
    }

    private AttributeSet obtenerEstiloParaToken(int tipoToken)
    {
        return switch (tipoToken)
        {
            case CodexLatinusLexer.ESTO,
                 CodexLatinusLexer.SERIES,
                 CodexLatinusLexer.SI,
                 CodexLatinusLexer.ALITER,
                 CodexLatinusLexer.DUM,
                 CodexLatinusLexer.FACERE,
                 CodexLatinusLexer.PER,
                 CodexLatinusLexer.PERGE,
                 CodexLatinusLexer.INTERRUMPE,
                 CodexLatinusLexer.STRUCTURA,
                 CodexLatinusLexer.FINIS,
                 CodexLatinusLexer.FINIS_MAYUS,
                 CodexLatinusLexer.ACTIO,
                 CodexLatinusLexer.RATIO,
                 CodexLatinusLexer.REDDERE,
                 CodexLatinusLexer.NON,
                 CodexLatinusLexer.VARIABILES_GLOBAL,
                 CodexLatinusLexer.VARIABILES_LOCAL,
                 CodexLatinusLexer.MUNERA,
                 CodexLatinusLexer.MAIOR -> attrPalabraReservada;

            case CodexLatinusLexer.NUMERUS,
                 CodexLatinusLexer.TEXTUM,
                 CodexLatinusLexer.DECIMALIS,
                 CodexLatinusLexer.LITTERA,
                 CodexLatinusLexer.BOOL,
                 CodexLatinusLexer.VERUM,
                 CodexLatinusLexer.FALSUS -> attrTipoDato;

            case CodexLatinusLexer.TEXTO -> attrCadena;
            case CodexLatinusLexer.CARACTER -> attrCaracter;
            case CodexLatinusLexer.NUMERO,
                 CodexLatinusLexer.DECIMALES -> attrNumero;
                
            case CodexLatinusLexer.COMENTARIO_LINEA,
                 CodexLatinusLexer.COMENTARIO_BLOQUE -> attrComentario;

            case CodexLatinusLexer.MAS,
                 CodexLatinusLexer.MENOS,
                 CodexLatinusLexer.POR,
                 CodexLatinusLexer.DIVISION,
                 CodexLatinusLexer.MAS_ABREVIADO,
                 CodexLatinusLexer.MENOS_ABREVIADO,
                 CodexLatinusLexer.IGUALIGUAL,
                 CodexLatinusLexer.DIFERENTEDE,
                 CodexLatinusLexer.MENOR,
                 CodexLatinusLexer.MENOR_IGUAL,
                 CodexLatinusLexer.MAYOR,
                 CodexLatinusLexer.MAYOR_IGUAL,
                 CodexLatinusLexer.AND,
                 CodexLatinusLexer.OR,
                 CodexLatinusLexer.IMPRIMIR,
                 CodexLatinusLexer.LECTURA,
                 CodexLatinusLexer.DOSPUNTOS,
                 CodexLatinusLexer.IGUAL,
                 CodexLatinusLexer.COMA,
                 CodexLatinusLexer.PUNTOYCOMA,
                 CodexLatinusLexer.PUNTO,
                 CodexLatinusLexer.LLAVE_IZQ,
                 CodexLatinusLexer.LLAVE_DER,
                 CodexLatinusLexer.CORCH_IZQ,
                 CodexLatinusLexer.CORCH_DER,
                 CodexLatinusLexer.PAREN_IZQ,
                 CodexLatinusLexer.PAREN_DER -> attrOperador;

            case CodexLatinusLexer.ID -> attrIdentificador;

            default -> null;
        };
    }
}