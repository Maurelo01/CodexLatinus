package codexlatinus.compiler.visualizacion;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class GraphvizRenderizado
{
    public static ImageIcon render(String codigoDot) throws IOException, InterruptedException
    {
        Path tempDir = Files.createTempDirectory("graphviz");
        Path archivoDot = tempDir.resolve("grafico.dot");
        Path archivoPng = tempDir.resolve("grafico.png");
        Files.writeString(archivoDot, codigoDot);
        ProcessBuilder pb = new ProcessBuilder("dot", "-Tpng", archivoDot.toString(), "-o", archivoPng.toString());
        pb.redirectErrorStream(true);
        Process process = pb.start();
        int exitCode = process.waitFor();
        if (exitCode != 0)
        {
            throw new IOException("Error al ejecutar Graphviz. Asegurate de tenerlo instalado");
        }
        BufferedImage imagen = ImageIO.read(archivoPng.toFile());
        if (imagen == null)
        {
            throw new IOException("No se pudo leer la imagen generada.");
        }
        return new ImageIcon(imagen);
    }
    
    public static String renderSVG(String codigoDot) throws IOException, InterruptedException
    {
        Path tempDir = Files.createTempDirectory("graphviz");
        Path archivoDot = tempDir.resolve("grafico.dot");
        Path archivoSvg = tempDir.resolve("grafico.svg");
        Files.writeString(archivoDot, codigoDot);
        ProcessBuilder pb = new ProcessBuilder("dot", "-Tsvg", archivoDot.toString(), "-o", archivoSvg.toString());
        pb.redirectErrorStream(true);
        Process process = pb.start();
        int exitCode = process.waitFor();
        if (exitCode != 0)
        {
            throw new IOException("Error al ejecutar Graphviz. Asegúrate de tenerlo instalado");
        }
        return Files.readString(archivoSvg);
    }
}
