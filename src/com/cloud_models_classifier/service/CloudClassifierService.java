package com.cloud_models_classifier.service;

import com.cloud_models_classifier.exception.ValidationException;
import com.cloud_models_classifier.model.ClassificationResult;
import com.cloud_models_classifier.model.CloudModel;
import com.cloud_models_classifier.model.UserInput;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CloudClassifierService {

    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$");

    public ClassificationResult classify(String firstName, String lastName, String description) throws ValidationException {
        validateInputs(firstName, lastName, description);

        UserInput input = new UserInput(firstName.trim(), lastName.trim(), description.trim());
        String text = input.getDescription();

        int scoreIaaS = evaluateIaaS(text);
        int scorePaaS = evaluatePaaS(text);
        int scoreSaaS = evaluateSaaS(text);
        int scoreFaaS = evaluateFaaS(text);

        CloudModel detectedModel = determineModel(scoreIaaS, scorePaaS, scoreSaaS, scoreFaaS);

        return new ClassificationResult(input, detectedModel, scoreIaaS, scorePaaS, scoreSaaS, scoreFaaS);
    }

    private void validateInputs(String firstName, String lastName, String description) throws ValidationException {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new ValidationException("El campo 'Nombre' no puede estar vacío.");
        }
        if (!NAME_PATTERN.matcher(firstName.trim()).matches()) {
            throw new ValidationException("El campo 'Nombre' solo debe contener letras y espacios.");
        }

        if (lastName == null || lastName.trim().isEmpty()) {
            throw new ValidationException("El campo 'Apellido' no puede estar vacío.");
        }
        if (!NAME_PATTERN.matcher(lastName.trim()).matches()) {
            throw new ValidationException("El campo 'Apellido' solo debe contener letras y espacios.");
        }

        if (description == null || description.trim().isEmpty()) {
            throw new ValidationException("El campo de descripción no puede estar vacío.");
        }
        if (description.trim().length() < 5) {
            throw new ValidationException("La descripción debe tener al menos 5 caracteres para poder ser analizada.");
        }
    }

    /**
     * IaaS: Detecta VMs (singular/plural), redes, almacenamiento básico, hardware y control de SO.
     */
    public int evaluateIaaS(String text) {
        String regex = "\\b(" +
                "iaas|" +
                "m[aá]quinas?\\s+virtual(es)?|" +
                "virtual\\s+machines?|" +
                "vms?|" +
                "servidores?\\s+dedicados?|" +
                "almacenamiento|" +
                "redes?(\\s+configurables?)?|" +
                "instalar\\s+(mi\\s+propio\\s+)?sistema\\s+operativo|" +
                "infraestructura|" +
                "ebs|s3|ec2|vpc|firewall|compute\\s+engine|azure\\s+vm|hardware|hipervisor" +
                ")\\b";
        return countMatches(text, regex);
    }

    /**
     * PaaS: Detecta despliegue de aplicaciones y abstracción de servidores/SO.
     */
    public int evaluatePaaS(String text) {
        String regex = "\\b(" +
                "paas|" +
                "desplegar(\\s+mi)?\\s+aplicaci[oó]n(\\s+web)?|" +
                "despliegue(\\s+de\\s+c[oó]digo)?|" +
                "sin\\s+administrar(\\s+directamente)?\\s+(servidores|sistemas\\s+operativos)|" +
                "entorno\\s+de\\s+ejecuci[oó]n|" +
                "heroku|app\\s+engine|elastic\\s+beanstalk|render|fly\\.io|" +
                "base\\s+de\\s+datos\\s+administrada|managed\\s+db|runtime|middleware|sdk|framework" +
                ")\\b";
        return countMatches(text, regex);
    }

    /**
     * SaaS: Detecta aplicaciones web listas para el usuario final, suscripciones y navegadores.
     */
    public int evaluateSaaS(String text) {
        String regex = "\\b(" +
                "saas|" +
                "aplicaci[oó]n\\s+(de\\s+)?correo(\\s+electr[oó]nico)?|" +
                "desde\\s+el\\s+navegador|" +
                "suscripci[oó]n(\\s+mensual|\\s+anual)?|" +
                "usuario\\s+final|" +
                "software\\s+listo|" +
                "google\\s+workspace|office\\s*365|salesforce|dropbox|gmail|zoom|slack|crm|erp" +
                ")\\b";
        return countMatches(text, regex);
    }

    /**
     * FaaS: Detecta ejecución de funciones disparadas por eventos o acciones puntuales.
     */
    public int evaluateFaaS(String text) {
        String regex = "\\b(" +
                "faas|" +
                "serverless|" +
                "sin\\s+servidor|" +
                "ejecutar\\s+una\\s+funci[oó]n|" +
                "funci[oó]n\\s+autom[aá]ticamente|" +
                "cada\\s+vez\\s+que\\s+.*(suba|cargue|dispare)|" +
                "ejecuci[oó]n\\s+por\\s+eventos|" +
                "event-driven|" +
                "microfunci[oó]n|" +
                "pago\\s+por\\s+ejecuci[oó]n|" +
                "invocaci[oó]n|" +
                "trigger|" +
                "aws\\s+lambda|cloud\\s+functions|azure\\s+functions" +
                ")\\b";
        return countMatches(text, regex);
    }

    private int countMatches(String text, String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Matcher matcher = pattern.matcher(text);
        int matches = 0;
        while (matcher.find()) {
            matches++;
        }
        return matches;
    }

    private CloudModel determineModel(int iaas, int paas, int saas, int faas) {
        int max = Math.max(iaas, Math.max(paas, Math.max(saas, faas)));
        if (max == 0) {
            return CloudModel.UNDETERMINED;
        }
        if (max == iaas) return CloudModel.IAAS;
        if (max == paas) return CloudModel.PAAS;
        if (max == saas) return CloudModel.SAAS;
        return CloudModel.FAAS;
    }
}