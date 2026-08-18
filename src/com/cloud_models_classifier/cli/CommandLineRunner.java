package com.cloud_models_classifier.cli;


import com.cloud_models_classifier.exception.ValidationException;
import com.cloud_models_classifier.model.ClassificationResult;
import com.cloud_models_classifier.service.CloudClassifierService;

public class CommandLineRunner {

    private final CloudClassifierService classifierService;

    public CommandLineRunner(CloudClassifierService classifierService) {
        this.classifierService = classifierService;
    }

    public void run(String[] args) {
        // Une todos los argumentos pasados por consola en una sola cadena
        String inputDescription = String.join(" ", args).trim();

        try {
            // Reutiliza la misma lógica del servicio pasando valores predeterminados para la CLI
            ClassificationResult result = classifierService.classify("CLI", "User", inputDescription);
            
            System.out.println("Modelo identificado: " + result.getDetectedModel().getAcronym());
            
        } catch (ValidationException ve) {
            System.err.println("Error de validación: " + ve.getMessage());
            System.exit(1);
        } catch (Exception ex) {
            System.err.println("Error inesperado: " + ex.getMessage());
            System.exit(1);
        }
    }
}