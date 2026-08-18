package com.cloud_models_classifier;

import com.cloud_models_classifier.cli.CommandLineRunner;
import com.cloud_models_classifier.gui.MainFrame;
import com.cloud_models_classifier.service.CloudClassifierService;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class CloudClassifier {

    public static void main(String[] args) {
        // Instancia única del servicio clasificador compartida por ambas interfaces
        CloudClassifierService service = new CloudClassifierService();

        if (args.length > 0) {
            // Modo CLI: Procesa argumentos pasados por línea de comandos
            CommandLineRunner cli = new CommandLineRunner(service);
            cli.run(args);
        } else {
            // Modo GUI: Sin argumentos, inicia la interfaz visual
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            SwingUtilities.invokeLater(() -> {
                new MainFrame(service).setVisible(true);
            });
        }
    }
}