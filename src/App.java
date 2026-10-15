import ui.LoginJFrame;

import javax.swing.SwingUtilities;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class App {
    public static void main(String[] args) {
        Path pictures = Path.of(args.length == 0 ? "pictures" : args[0])
                .toAbsolutePath().normalize();
        if (!Files.isDirectory(pictures)) {
            System.err.println("Picture directory not found: " + pictures);
            System.err.println("Run from the repository root or pass the picture directory as an argument.");
            return;
        }
        String assetPath = pictures + File.separator;
        SwingUtilities.invokeLater(() -> new LoginJFrame(assetPath));
    }
}
