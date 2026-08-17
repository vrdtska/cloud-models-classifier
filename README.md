# Cloud Models Classifier

A small Java application that classifies text into cloud service categories such as IaaS, PaaS, SaaS, and FaaS.

## Requirements

- Java JDK 17 or newer
- A terminal with `javac` and `java` available on your PATH

## Install

```bash
git clone https://github.com/vrdtska/cloud-models-classifier.git
cd cloud-models-classifier
```

## Build

```bash
javac -d bin $(find src -name '*.java')
```

## Run

### GUI version

```bash
java -cp bin CloudClassifierApp
```

This launches the Swing desktop app, where the user enters a name, surname, and a description of the cloud service to classify.

### CLI version

```bash
java -cp bin com.cloud_models_classifier.cli.CommandLineRunner "your cloud service description here"
```

This runs the same classifier without the graphical interface, using command-line input and printing the detected model.

## How the project is organized

The code is split into a few simple layers:

- GUI layer: the Swing window class that collects input and displays results.
- CLI layer: a lightweight runner that accepts text as arguments and prints the classification.
- Service layer: the classifier logic that evaluates keywords and scores each cloud model.
- Model layer: data objects such as the detected model and classification result.
- Validation/exception layer: handles invalid or empty inputs cleanly.

The core idea is that both the desktop app and the console app reuse the same classification service, so the business logic is not duplicated. The GUI and CLI are just different entry points to the same classifier.
