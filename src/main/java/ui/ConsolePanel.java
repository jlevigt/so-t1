package ui;

import util.Logger;
import javax.swing.*;
import java.awt.*;

public class ConsolePanel extends JPanel {
    private final JTextArea logArea;
    private static final int MAX_LINES = 100;

    public ConsolePanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Console de Eventos"));

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setLineWrap(false);
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);

        // Timer para atualizar logs
        new Timer(100, e -> updateLogs()).start();
    }

    private void updateLogs() {
        String msg;
        boolean added = false;
        while ((msg = Logger.poll()) != null) {
            logArea.append(msg + "\n");
            added = true;
        }

        if (added) {
            limitLines();
            logArea.setCaretPosition(logArea.getDocument().getLength());
        }
    }

    private void limitLines() {
        int lineCount = logArea.getLineCount();
        if (lineCount > MAX_LINES) {
            try {
                int end = logArea.getLineEndOffset(lineCount - MAX_LINES - 1);
                logArea.replaceRange("", 0, end);
            } catch (Exception e) {
                // Ignore
            }
        }
    }
}
