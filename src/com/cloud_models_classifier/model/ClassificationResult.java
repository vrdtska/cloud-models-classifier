package com.cloud_models_classifier.model;

public class ClassificationResult {
    private final UserInput userInput;
    private final CloudModel detectedModel;
    private final int scoreIaaS;
    private final int scorePaaS;
    private final int scoreSaaS;
    private final int scoreFaaS;

    public ClassificationResult(UserInput userInput, CloudModel detectedModel,
                                int scoreIaaS, int scorePaaS, int scoreSaaS, int scoreFaaS) {
        this.userInput = userInput;
        this.detectedModel = detectedModel;
        this.scoreIaaS = scoreIaaS;
        this.scorePaaS = scorePaaS;
        this.scoreSaaS = scoreSaaS;
        this.scoreFaaS = scoreFaaS;
    }

    public UserInput getUserInput() { return userInput; }
    public CloudModel getDetectedModel() { return detectedModel; }
    public int getScoreIaaS() { return scoreIaaS; }
    public int getScorePaaS() { return scorePaaS; }
    public int getScoreSaaS() { return scoreSaaS; }
    public int getScoreFaaS() { return scoreFaaS; }
}