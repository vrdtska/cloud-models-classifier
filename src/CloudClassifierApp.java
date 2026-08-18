import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CloudClassifierApp extends JFrame {

    // Componentes visuales
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextArea txtDescripcion;
    private JLabel lblResultado;
    private JLabel lblDetalles;

    public CloudClassifierApp() {
        // Configuración básica de la ventana
        setTitle("Clasificador de Servicios Cloud (IaaS, PaaS, SaaS, FaaS)");
        setSize(650, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Panel principal con margen interno
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // --- SECCIÓN 1: Datos del usuario ---
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

        // --- SECCIÓN 2: Entrada del texto a clasificar ---
        JPanel pnlTexto = new JPanel(new BorderLayout(5, 5));
        pnlTexto.setBorder(BorderFactory.createTitledBorder("Descripción o palabras clave del servicio Cloud"));
        txtDescripcion = new JTextArea(6, 40);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(txtDescripcion);
        pnlTexto.add(scroll, BorderLayout.CENTER);
        mainPanel.add(pnlTexto);

        mainPanel.add(Box.createVerticalStrut(10));

        // --- SECCIÓN 3: Botón de análisis ---
        JButton btnAnalizar = new JButton("Analizar y Clasificar Modelo");
        btnAnalizar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAnalizar.setFont(new Font("SansSerif", Font.BOLD, 13));
        mainPanel.add(btnAnalizar);

        mainPanel.add(Box.createVerticalStrut(10));

        // --- SECCIÓN 4: Visualización del resultado ---
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

        // Evento del botón
        btnAnalizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                procesarClasificacion();
            }
        });
    }

    /**
     * Valida entradas, ejecuta las reglas de conteo por modelo y determina el resultado.
     */
    private void procesarClasificacion() {
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String texto = txtDescripcion.getText().trim();

        if (nombre.isEmpty() || apellido.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingresa tu nombre y apellido.", "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, escribe una descripción o palabras clave para clasificar.", "Texto vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Evaluar puntuaciones mediante métodos específicos para cada categoría
        int scoreIaaS = evaluarIaaS(texto);
        int scorePaaS = evaluarPaaS(texto);
        int scoreSaaS = evaluarSaaS(texto);
        int scoreFaaS = evaluarFaaS(texto);

        // Determinar el modelo con mayor coincidencia
        int maxScore = Math.max(scoreIaaS, Math.max(scorePaaS, Math.max(scoreSaaS, scoreFaaS)));

        if (maxScore == 0) {
            lblResultado.setText("Resultado: Indeterminado / No clasificado");
            lblResultado.setForeground(Color.DARK_GRAY);
            lblDetalles.setText("Usuario: " + nombre + " " + apellido + " | No se detectaron suficientes patrones clave.");
        } else {
            String categoria = "";
            if (maxScore == scoreIaaS) categoria = "IaaS (Infrastructure as a Service)";
            else if (maxScore == scorePaaS) categoria = "PaaS (Platform as a Service)";
            else if (maxScore == scoreSaaS) categoria = "SaaS (Software as a Service)";
            else if (maxScore == scoreFaaS) categoria = "FaaS (Function as a Service / Serverless)";

            lblResultado.setText("Modelo Detectado: " + categoria);
            lblResultado.setForeground(new Color(0, 128, 0));
            lblDetalles.setText(String.format("Usuario: %s %s | Puntos -> IaaS: %d, PaaS: %d, SaaS: %d, FaaS: %d",
                    nombre, apellido, scoreIaaS, scorePaaS, scoreSaaS, scoreFaaS));
        }
    }

    /**
     * Evalúa términos relacionados con Infraestructura como Servicio (IaaS)
     */
    private int evaluarIaaS(String texto) {
        String regex = "\\b(iaas|m[aá]quina\\s+virtual|virtual\\s+machine|vm|servidor\\s+dedicado|almacenamiento\\s+en\\s+bloque|ebs|s3|ec2|vpc|red\\s+virtual|firewall|compute\\s+engine|azure\\s+vm|hardware|hipervisor|storage)\\b";
        return contarCoincidenciasRegex(texto, regex);
    }

    /**
     * Evalúa términos relacionados con Plataforma como Servicio (PaaS)
     */
    private int evaluarPaaS(String texto) {
        String regex = "\\b(paas|despliegue\\s+de\\s+c[oó]digo|entorno\\s+de\\s+ejecuci[oó]n|heroku|app\\s+engine|elastic\\s+beanstalk|base\\s+de\\s+datos\\s+administrada|managed\\s+db|runtime|middleware|sdk|framework|render|fly\\.io)\\b";
        return contarCoincidenciasRegex(texto, regex);
    }

    /**
     * Evalúa términos relacionados con Software como Servicio (SaaS)
     */
    private int evaluarSaaS(String texto) {
        String regex = "\\b(saas|aplicaci[oó]n\\s+final|usuario\\s+final|google\\s+workspace|office\\s*365|salesforce|dropbox|gmail|zoom|slack|software\\s+listo|correo\\s+electr[oó]nico|crm|erp)\\b";
        return contarCoincidenciasRegex(texto, regex);
    }

    /**
     * Evalúa términos relacionados con Funciones como Servicio (FaaS / Serverless)
     */
    private int evaluarFaaS(String texto) {
        String regex = "\\b(faas|serverless|sin\\s+servidor|aws\\s+lambda|cloud\\s+functions|azure\\s+functions|ejecuci[oó]n\\s+por\\s+eventos|event-driven|microfunci[oó]n|pago\\s+por\\s+ejecuci[oó]n|invocaci[oó]n|trigger)\\b";
        return contarCoincidenciasRegex(texto, regex);
    }

    /**
     * Cuenta cuántas veces se repiten los patrones regex dentro del texto (insensible a mayúsculas/minúsculas).
     */
    private int contarCoincidenciasRegex(String texto, String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Matcher matcher = pattern.matcher(texto);
        int coincidencias = 0;
        while (matcher.find()) {
            coincidencias++;
        }
        return coincidencias;
    }

    public static void main(String[] args) {
        // Ajuste de Look and Feel del sistema para una apariencia nativa en Linux
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            new CloudClassifierApp().setVisible(true);
        });
    }
}