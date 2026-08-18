package com.cloud_models_classifier.service;

import java.text.Normalizer;
import java.util.*;

public class TextPreprocessor {

    // Conjunto de palabras vacías (stopwords) en español
    private static final Set<String> STOPWORDS = new HashSet<>(Arrays.asList(
            "a", "al", "algo", "algunas", "algunos", "ante", "antes", "como", "con", "contra",
            "cual", "cuando", "de", "del", "desde", "donde", "durante", "e", "el", "ella",
            "ellas", "ellos", "en", "entre", "era", "erais", "eran", "eras", "eres", "es",
            "esa", "esas", "ese", "eso", "esos", "esta", "estaba", "estas", "este", "esto",
            "estos", "estoy", "fue", "ha", "habia", "han", "has", "hasta", "hay", "la",
            "las", "le", "les", "lo", "los", "me", "mi", "mis", "mucho", "muchos", "muy",
            "nos", "nosotros", "o", "otra", "otras", "otro", "otros", "para", "pero", "poco",
            "por", "porque", "que", "quien", "quienes", "se", "sea", "sean", "segun", "ser",
            "si", "sido", "sobre", "son", "su", "sus", "suya", "suyas", "suyo", "suyos",
            "tambien", "tanto", "te", "tenemos", "tener", "tengo", "ti", "tiene", "tienen",
            "toda", "todas", "todo", "todos", "tu", "tus", "un", "una", "unas", "uno",
            "unos", "vosostros", "y", "ya"
    ));

    /**
     * Pipeline completo: Normalización -> Limpieza -> Tokenización -> Filtrado -> Stemming
     */
    public List<String> preprocess(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }

        // 1. Minúsculas y eliminación de acentos/diacríticos (NFD normalization)
        String normalized = Normalizer.normalize(text.toLowerCase(), Normalizer.Form.NFD);
        String withoutAccents = normalized.replaceAll("\\p{M}", "");

        // 2. Eliminación de signos de puntuación y caracteres innecesarios
        String cleaned = withoutAccents.replaceAll("[^a-z0-9\\s]", " ");

        // 3. Tokenización por espacios en blanco
        String[] rawTokens = cleaned.split("\\s+");

        // 4. Filtrado de stopwords y tokens de longitud < 2
        List<String> filteredTokens = new ArrayList<>();
        for (String token : rawTokens) {
            String trimmed = token.trim();
            if (trimmed.length() >= 2 && !STOPWORDS.contains(trimmed)) {
                // 5. Aplicar Stemming morfológico en español
                filteredTokens.add(stem(trimmed));
            }
        }

        return filteredTokens;
    }

    /**
     * Stemmer ligero basado en sufijos comunes del español para colapsar
     * plurales, formas verbales y derivaciones a una raíz léxica común.
     */
    public String stem(String word) {
        if (word.length() <= 3) return word;

        // Plurales y desinencias comunes
        if (word.endsWith("es") && word.length() > 4) {
            return word.substring(0, word.length() - 2);
        } else if (word.endsWith("s") && !word.endsWith("is") && !word.endsWith("us")) {
            return word.substring(0, word.length() - 1);
        }

        // Sufijos verbales comunes (infinitivos, gerundios, participios)
        String[] suffixes = {"ando", "iendo", "acion", "mente", "idad", "ar", "er", "ir"};
        for (String suffix : suffixes) {
            if (word.endsWith(suffix) && (word.length() - suffix.length()) >= 3) {
                return word.substring(0, word.length() - suffix.length());
            }
        }

        return word;
    }

    /**
     * Genera n-gramas contiguos (para capturar conceptos multipalabra como "maquin virtual")
     */
    public List<String> generateNGrams(List<String> tokens, int n) {
        List<String> nGrams = new ArrayList<>();
        for (int i = 0; i <= tokens.size() - n; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < n; j++) {
                if (j > 0) sb.append(" ");
                sb.append(tokens.get(i + j));
            }
            nGrams.add(sb.toString());
        }
        return nGrams;
    }
}