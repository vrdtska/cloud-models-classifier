package com.cloud_models_classifier.service;

import com.cloud_models_classifier.exception.ValidationException;
import com.cloud_models_classifier.model.ClassificationResult;
import com.cloud_models_classifier.model.CloudModel;
import com.cloud_models_classifier.model.UserInput;

import java.util.*;
import java.util.regex.Pattern;

public class CloudClassifierService {

    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$");
    private final TextPreprocessor preprocessor;

    // Diccionarios léxicos con raíces (stems) y sus respectivos pesos (Scoring ponderado)
    private final Map<String, Integer> iaasWeights = new HashMap<>();
    private final Map<String, Integer> paasWeights = new HashMap<>();
    private final Map<String, Integer> saasWeights = new HashMap<>();
    private final Map<String, Integer> faasWeights = new HashMap<>();

    public CloudClassifierService() {
        this.preprocessor = new TextPreprocessor();
        initVocabularies();
    }

    private void initVocabularies() {
        // --- IaaS: Raíces léxicas y conceptos compuestos ---
        iaasWeights.put("iaas", 10);
        iaasWeights.put("maquin virtual", 8);
        iaasWeights.put("virtual machin", 8);
        iaasWeights.put("vm", 6);
        iaasWeights.put("servidor dedic", 7);
        iaasWeights.put("almacen", 4);
        iaasWeights.put("almacen bloque", 7);
        iaasWeights.put("red configur", 6);
        iaasWeights.put("red virtual", 7);
        iaasWeights.put("sistem operativ", 6);
        iaasWeights.put("instal propi", 5);
        iaasWeights.put("infraestructur", 6);
        iaasWeights.put("hardware", 5);
        iaasWeights.put("hipervisor", 6);
        iaasWeights.put("firewall", 5);
        iaasWeights.put("ec2", 8);
        iaasWeights.put("ebs", 8);
        iaasWeights.put("vpc", 8);

        // --- PaaS: Enfoque en desarrollo, despliegue y abstracción de SO/servidores ---
        paasWeights.put("paas", 10);
        paasWeights.put("despleg aplic", 8);
        paasWeights.put("despliegu aplic", 8);
        paasWeights.put("despliegu codig", 8);
        paasWeights.put("administr servidor", 6);
        paasWeights.put("entorn ejecut", 7);
        paasWeights.put("bas dat administr", 7);
        paasWeights.put("managed db", 7);
        paasWeights.put("heroku", 8);
        paasWeights.put("render", 7);
        paasWeights.put("elastic beanstalk", 8);
        paasWeights.put("app engin", 8);
        paasWeights.put("runtime", 5);
        paasWeights.put("sdk", 5);
        paasWeights.put("framework", 4);

        // --- SaaS: Software para usuario final, suscripciones y navegadores ---
        saasWeights.put("saas", 10);
        saasWeights.put("aplic corre", 8);
        saasWeights.put("corre electron", 8);
        saasWeights.put("navegad", 6);
        saasWeights.put("suscripc mensual", 7);
        saasWeights.put("suscripc anual", 7);
        saasWeights.put("suscripc", 5);
        saasWeights.put("usuari final", 7);
        saasWeights.put("softwar list", 7);
        saasWeights.put("salesforc", 8);
        saasWeights.put("workspac", 8);
        saasWeights.put("offic 365", 8);
        saasWeights.put("slack", 8);
        saasWeights.put("dropbox", 8);
        saasWeights.put("crm", 6);
        saasWeights.put("erp", 6);

        // --- FaaS: Eventos, funciones puntuales, triggers y serverless ---
        faasWeights.put("faas", 10);
        faasWeights.put("serverless", 9);
        faasWeights.put("ejecut funcion", 8);
        faasWeights.put("funcion automat", 8);
        faasWeights.put("invocac", 6);
        faasWeights.put("trigger", 7);
        faasWeights.put("microfuncion", 8);
        faasWeights.put("event driven", 8);
        faasWeights.put("pag ejecut", 7);
        faasWeights.put("aws lambda", 9);
        faasWeights.put("cloud function", 9);
        faasWeights.put("azur function", 9);
        faasWeights.put("sub imag", 6);
    }

    public ClassificationResult classify(String firstName, String lastName, String description) throws ValidationException {
        validateInputs(firstName, lastName, description);

        UserInput input = new UserInput(firstName.trim(), lastName.trim(), description.trim());

        // Pipeline NLP
        List<String> unigrams = preprocessor.preprocess(input.getDescription());
        List<String> bigrams = preprocessor.generateNGrams(unigrams, 2);
        List<String> trigrams = preprocessor.generateNGrams(unigrams, 3);

        // Bolsa unificada de n-gramas extraídos
        List<String> allFeatures = new ArrayList<>(unigrams);
        allFeatures.addAll(bigrams);
        allFeatures.addAll(trigrams);

        // Puntuaciones por categoría usando los pesos semánticos
        int scoreIaaS = evaluateCategory(allFeatures, iaasWeights);
        int scorePaaS = evaluateCategory(allFeatures, paasWeights);
        int scoreSaaS = evaluateCategory(allFeatures, saasWeights);
        int scoreFaaS = evaluateCategory(allFeatures, faasWeights);

        CloudModel detectedModel = determineModel(scoreIaaS, scorePaaS, scoreSaaS, scoreFaaS);

        return new ClassificationResult(input, detectedModel, scoreIaaS, scorePaaS, scoreSaaS, scoreFaaS);
    }

    public int evaluateIaaS(String text) {
        List<String> tokens = preprocessor.preprocess(text);
        List<String> all = new ArrayList<>(tokens);
        all.addAll(preprocessor.generateNGrams(tokens, 2));
        return evaluateCategory(all, iaasWeights);
    }

    public int evaluatePaaS(String text) {
        List<String> tokens = preprocessor.preprocess(text);
        List<String> all = new ArrayList<>(tokens);
        all.addAll(preprocessor.generateNGrams(tokens, 2));
        return evaluateCategory(all, paasWeights);
    }

    public int evaluateSaaS(String text) {
        List<String> tokens = preprocessor.preprocess(text);
        List<String> all = new ArrayList<>(tokens);
        all.addAll(preprocessor.generateNGrams(tokens, 2));
        return evaluateCategory(all, saasWeights);
    }

    public int evaluateFaaS(String text) {
        List<String> tokens = preprocessor.preprocess(text);
        List<String> all = new ArrayList<>(tokens);
        all.addAll(preprocessor.generateNGrams(tokens, 2));
        return evaluateCategory(all, faasWeights);
    }

    private int evaluateCategory(List<String> features, Map<String, Integer> vocabulary) {
        int totalScore = 0;
        for (String feature : features) {
            if (vocabulary.containsKey(feature)) {
                totalScore += vocabulary.get(feature);
            }
        }
        return totalScore;
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
            throw new ValidationException("La descripción debe tener al menos 5 caracteres.");
        }
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