package codexlatinus;

import codexlatinus.compiler.semantic.MiVisitor;
import codexlatinus.compiler.translation.PigLatinTraductor;
import java.nio.file.Files;
import java.nio.file.Path;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class CodexLatinus
{
    public static void main(String[] args) throws Exception
    {
        CharStream input;
        if (args.length > 0)
        {
            input = CharStreams.fromFileName(args[0]);
        }
        else
        {
            input = CharStreams.fromStream(System.in);
        }
        CodexLatinusLexer lexer = new CodexLatinusLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CodexLatinusParser parser = new CodexLatinusParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new BaseErrorListener()
        {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e)
            {
                System.err.printf("Error sintáctico en línea %d, columna %d: %s%n", line, charPositionInLine + 1, msg);
            }
        });
        ParseTree tree = parser.programa();
        if (parser.getNumberOfSyntaxErrors() > 0)
        {
            System.err.println("Se encontraron errores de sintaxis.");
            System.exit(1);
        }
        MiVisitor visitor = new MiVisitor();
        visitor.visit(tree);
        if (visitor.getErroresSemanticos() > 0)
        {
            System.err.println("Se encontraron " + visitor.getErroresSemanticos() + " errores semánticos.");
            System.exit(1);
        }
        else
        {
            PigLatinTraductor traductor = new PigLatinTraductor();
            String pigCode = traductor.visit(tree);
            String nombreArchivo = args.length > 0 ? args[0].replaceAll("\\.lat$", "") : "salida";
            Path path = Path.of(nombreArchivo + ".pig");
            Files.writeString(path, pigCode);
            System.out.println("Traducción generada en: " + path.toAbsolutePath());
        }
        System.out.println("Programa válido: análisis sintáctico y semántico completado sin errores.");
    }
}