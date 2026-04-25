package ui;

import javax.swing.*;
import java.awt.*;

public class Launcher {
    public static void main(String[] args) {
        if (GraphicsEnvironment.isHeadless()) {
            System.err.println("ERRO: Ambiente gráfico não detectado.");
            System.err.println("Este programa requer uma interface gráfica (X11) para funcionar.");
            System.err.println("Se estiver usando Linux/WSL, verifique a variável $DISPLAY.");
            System.exit(1);
        }

        SwingUtilities.invokeLater(MainFrame::new);
    }
}

