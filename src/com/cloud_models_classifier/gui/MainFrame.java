package com.cloud_models_classifier.gui;

import com.cloud_models_classifier.exception.ValidationException;
import com.cloud_models_classifier.model.ClassificationResult;
import com.cloud_models_classifier.model.CloudModel;
import com.cloud_models_classifier.service.CloudClassifierService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {

    private final CloudClassifierService classifierService;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextArea txtDescripcion;
    private JLabel lblResultado;
    private JLabel lblDetalles;

    public MainFrame(CloudClassifierService classifierService) {
        this.classifierService = classifierService;
        initUI();
    }

    private void initUI() {
        setTitle("Clasificador de Servicios Cloud NLP (IaaS, PaaS, SaaS, FaaS)");
        setSize(650, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel pnlUsuario = new JPanel(new GridLayout(2, 2, 10, 8));
        pnlUsuario.setBorder(BorderFactory.createTitledBorder("Información del Usuario"));
        pnlUsuario.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        pnlUsuario.add(txtNombre);
        pnlUsuario.add(new JLabel("Apellido:"));
        txtApellido = new JTextField();
        pnlUsuario.add(txtApellido);
        mainPanel.add(pnlUsuario);

        mainPanel.add(Box.createVerticalStrut(10));

        JPanel pnlTexto = new JPanel(new BorderLayout(5, 5));
        pnlTexto.setBorder(BorderFactory.createTitledBorder("Descripción o palabras clave del servicio Cloud"));
        txtDescripcion = new JTextArea(6, 40);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        pnlTexto.add(new JScrollPane(txtDescripcion), BorderLayout.CENTER);
        mainPanel.add(pnlTexto);

        mainPanel.add(Box.createVerticalStrut(10));

        JButton btnAnalizar = new JButton("Analizar y Clasificar Modelo");
        btnAnalizar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAnalizar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnAnalizar.addActionListener(e -> onAnalyzeClicked());
        mainPanel.add(btnAnalizar);

        mainPanel.add(Box.createVerticalStrut(10));

        JPanel pnlResultado = new JPanel(new GridLayout(2, 1, 5, 5));
        pnlResultado.setBorder(BorderFactory.createTitledBorder("Resultado del Análisis"));

        lblResultado = new JLabel("Ingresa los datos y presiona 'Analizar'", SwingConstants.CENTER);
        lblResultado.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblResultado.setForeground(new Color(30, 80, 160));

        lblDetalles = new JLabel("", SwingConstants.CENTER);
        lblDetalles.setFont(new Font("SansSerif", Font.PLAIN, 12));

        pnlResultado.add(lblResultado);
        pnlResultado.add(lblDetalles);
        mainPanel.add(pnlResultado);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void onAnalyzeClicked() {
        try {
            ClassificationResult result = classifierService.classify(
                    txtNombre.getText(),
                    txtApellido.getText(),
                    txtDescripcion.getText()
            );
            renderResult(result);
        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(this, ve.getMessage(), "Error de Validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error inesperado: " + ex.getMessage(), "Error Interno", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void renderResult(ClassificationResult result) {
        if (result.getDetectedModel() == CloudModel.UNDETERMINED) {
            lblResultado.setText("Resultado: " + result.getDetectedModel().getDisplayName());
            lblResultado.setForeground(Color.DARK_GRAY);
            lblDetalles.setText("Usuario: " + result.getUserInput().getFullName() + " | No se detectaron patrones concluyentes.");
        } else {
            lblResultado.setText("Modelo Detectado: " + result.getDetectedModel().getDisplayName());
            lblResultado.setForeground(new Color(0, 128, 0));
            lblDetalles.setText(String.format("Usuario: %s | Puntos -> IaaS: %d, PaaS: %d, SaaS: %d, FaaS: %d",
                    result.getUserInput().getFullName(),
                    result.getScoreIaaS(),
                    result.getScorePaaS(),
                    result.getScoreSaaS(),
                    result.getScoreFaaS()));
        }
    }
}