package com.cloud_models_classifier.model;

public class UserInput {
    private final String firstName;
    private final String lastName;
    private final String description;

    public UserInput(String firstName, String lastName, String description) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.description = description;
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getDescription() { return description; }
    public String getFullName() { return firstName + " " + lastName; }
}