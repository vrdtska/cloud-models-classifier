# Cloud Service Model Classifier (IaaS, PaaS, SaaS, FaaS)

A decoupled Java application designed to classify descriptive text and keywords into one of the four main Cloud Computing service models: **IaaS**, **PaaS**, **SaaS**, or **FaaS**. The system includes input validation, basic Natural Language Processing (NLP) with weighted scoring, and dual interface support (**GUI** and **CLI**).

---

## 🏛️ Architecture Overview

The system strictly decouples interface presentation from business and classification logic:

              ┌───────────────┐
              │   GUI (Swing) │
              │  (MainFrame)  │──┐
              └───────────────┘  │
                                 ▼

┌──────────────┐             ┌───────────────┐             ┌─────────────────────┐
│     CLI      │────────────▶│  Classifier   │────────────▶│       Result        │
│ (CmdRunner)  │             │   Service     │             │ (IaaS|PaaS|SaaS|FaaS│
└──────────────┘             │(NLP + Scoring)│             │   + Score Breakdown)│
└───────────────┘             └─────────────────────┘


* **Model Layer (`com.cloudclassifier.model`)**: Contains core entities (`UserInput`, `ClassificationResult`, and `CloudModel` enum).
* **Service Layer (`com.cloudclassifier.service`)**:
  * `TextPreprocessor`: Handles diacritic removal, punctuation stripping, tokenization, stopword removal, and Spanish morphological stemming.
  * `CloudClassifierService`: Orchestrates validation, feature extraction (unigrams, bigrams, trigrams), and domain-specific weighted scoring.
* **CLI Controller (`com.cloudclassifier.cli`)**: Runs command-line input without launching graphical interfaces.
* **GUI Layer (`com.cloudclassifier.gui`)**: Swing-based desktop user interface.
* **Entry Point (`com.cloudclassifier.CloudClassifier`)**: Automatically routes execution to CLI (if arguments are passed) or GUI (if no arguments are present).

---

## 🧠 NLP Pipeline & Classification Engine

Instead of rigid regular expressions, the classifier uses a lightweight, standard Java NLP pipeline:

1. **Unicode Normalization & Lowercasing**: Strips accents/diacritics (`máquina` → `maquina`).
2. **Sanitization**: Removes punctuation and non-alphanumeric noise.
3. **Tokenization & Stopword Removal**: Filters out common Spanish grammatical words (*de*, *la*, *para*, *un*, etc.).
4. **Morphological Stemming**: Reduces inflected/derived words to their root stem (`servidores` → `servidor`, `virtuales` → `virtual`).
5. **Feature Extraction**: Generates unigrams, bigrams, and trigrams to capture multi-word concepts (e.g., `"maquin virtual"`, `"administr servidor"`).
6. **Weighted Scoring (TF-based)**: Computes a weighted sum against category vocabularies, assigning higher weights to specialized keywords (e.g., `serverless`, `vpc`, `lambda`) over generic terms.

---

## 📁 Repository Structure

```text
cloud-service-classifier/
├── bin/                                      # Compiled bytecode (.class files)
├── src/
│   └── com/
│       └── cloudclassifier/
│           ├── CloudClassifier.java          # Main entry point (GUI / CLI router)
│           ├── cli/
│           │   └── CommandLineRunner.java    # CLI execution handler
│           ├── exception/
│           │   └── ValidationException.java  # Custom input validation errors
│           ├── gui/
│           │   └── MainFrame.java            # Swing GUI implementation
│           ├── model/
│           │   ├── ClassificationResult.java # Result data carrier
│           │   ├── CloudModel.java           # Cloud model definitions (IaaS, PaaS, SaaS, FaaS)
│           │   └── UserInput.java            # Immutable user input entity
│           └── service/
│               ├── CloudClassifierService.java # Business logic & scoring
│               └── TextPreprocessor.java     # NLP pipeline (stemming, tokenization, stopwords)
├── .gitignore
└── README.md

Prerequisites

    OS: Linux Fedora Workstation (or any OS supporting Java 17+)

    JDK: OpenJDK 17 or higher

To install the latest OpenJDK on Fedora:

Bash

sudo dnf install java-latest-openjdk java-latest-openjdk-devel

Build & Compilation

From the root directory of the repository, compile all Java classes into the bin/ directory:
Bash

# 1. Create the bin directory if it does not exist
mkdir -p bin

# 2. Compile all source files
javac -d bin $(find src -name "*.java")

Execution Guide
1. Command-Line Interface (CLI) Mode

Pass the text to classify as a command-line argument:
Bash

java -cp bin com.cloudclassifier.CloudClassifier "máquinas virtuales almacenamiento redes"
# Output: Modelo identificado: IaaS

java -cp bin com.cloudclassifier.CloudClassifier "desplegar mi aplicación web sin administrar servidores"
# Output: Modelo identificado: PaaS

java -cp bin com.cloudclassifier.CloudClassifier "correo electrónico desde el navegador con suscripción mensual"
# Output: Modelo identificado: SaaS

java -cp bin com.cloudclassifier.CloudClassifier "ejecutar una función automáticamente cuando un usuario suba una imagen"
# Output: Modelo identificado: FaaS

2. Graphical User Interface (GUI) Mode

Run the command without parameters to open the desktop GUI:
Bash

java -cp bin com.cloudclassifier.CloudClassifier

nput Validation & Error Handling

    GUI:

        Prevents blank or numeric-only names/surnames.

        Rejects queries shorter than 5 characters.

        Displays user-friendly warning modals (JOptionPane) without crashing the application.

    CLI:

        Rejects empty or invalid strings and outputs structured errors to stderr with non-zero exit codes (exit code 1).