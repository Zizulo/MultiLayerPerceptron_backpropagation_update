import java.awt.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.List;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Test extends JFrame {
    private final MojKomponent komponent = new MojKomponent();
    private Siec siec = new Siec(144, 2, new int[]{25, 3});
    private String selectedLetter;
    private final JLabel letterDisplay = new JLabel("None", SwingConstants.CENTER);
    private final JLabel trainingResultDisplay = new JLabel("Trening: Brak", SwingConstants.CENTER);
    private final ButtonGroup letterGroup = new ButtonGroup();
    private List<UczacaWartosc> uczaceWartosci = new ArrayList<>();
    private List<TestowaWartosc> testoweWartosci = new ArrayList<>();

    public Test(String title) {
        super(title);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Dodajemy margines
        mainPanel.add(createControlPanel(), BorderLayout.WEST); // Kontrolki po lewej
        mainPanel.add(komponent, BorderLayout.CENTER); // Siatka w centrum
        mainPanel.add(createTrainingResultPanel(), BorderLayout.EAST);

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Wybierz literę", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Wyśrodkowanie napisu
        controlPanel.add(titleLabel);

        controlPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Odstęp

        String[] letters = {"O", "D", "M"};
        for (String letter : letters) {
            JRadioButton button = new JRadioButton(letter);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.addActionListener(e -> selectedLetter = letter);
            letterGroup.add(button);
            controlPanel.add(button);
        }

        controlPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Odstęp

        addButton(controlPanel, "Clear", e -> komponent.clearGrid());
        addButton(controlPanel, "Trening", e -> train());
        addButton(controlPanel, "Testuj", e -> test());
        addButton(controlPanel, "Rozpoznaj", e -> recognize());
        addButton(controlPanel, "Dodaj ciąg uczący do listy", e -> appendDataToList(komponent.getGridAsInput(), selectedLetter, "ciąg uczący"));
        addButton(controlPanel, "Dodaj ciąg testowy do listy", e -> appendDataToList(komponent.getGridAsInput(), selectedLetter, "ciąg testowy"));
        addButton(controlPanel, "Zapisz ciąg uczący do pliku", e -> saveDataToFile(uczaceWartosci, "ciagi uczące"));
        addButton(controlPanel, "Zapisz ciąg testowy do pliku", e -> saveDataToFile(testoweWartosci, "ciągi testowe"));
        addButton(controlPanel, "Załaduj dane treningowe", e -> loadTrainingData());
        addButton(controlPanel, "Załaduj dane testowe", e -> loadTestData());

        letterDisplay.setOpaque(true);
        letterDisplay.setPreferredSize(new Dimension(100, 50));
        letterDisplay.setFont(new Font("SansSerif", Font.BOLD, 14));
        letterDisplay.setAlignmentX(Component.CENTER_ALIGNMENT);
        letterDisplay.setBackground(Color.LIGHT_GRAY);
        letterDisplay.setForeground(Color.BLACK);
        letterDisplay.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        controlPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Odstęp
        controlPanel.add(letterDisplay);

        return controlPanel;
    }

    private JPanel createTrainingResultPanel() {
        JPanel resultJPanel = new JPanel();
        resultJPanel.setLayout(new BoxLayout(resultJPanel, BoxLayout.Y_AXIS));
        resultJPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        trainingResultDisplay.setOpaque(true);
        trainingResultDisplay.setPreferredSize(new Dimension(300, 100));
        trainingResultDisplay.setFont(new Font("SansSerif", Font.BOLD, 16));
        trainingResultDisplay.setBackground(Color.LIGHT_GRAY);
        trainingResultDisplay.setForeground(Color.BLACK);
        trainingResultDisplay.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        resultJPanel.add(trainingResultDisplay);

        return resultJPanel;
    }

    private void addButton(JPanel panel, String title, ActionListener listener) {
        JButton button = new JButton(title);
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setMaximumSize(new Dimension(200, 40)); // Ustal maksymalny rozmiar przycisku
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(listener);
        panel.add(button);
        panel.add(Box.createRigidArea(new Dimension(0, 10))); // Odstęp między przyciskami
    }

    private void train() {
        double wymaganaSkutecznosc = 88;
        double aktualnyBladSieci = 100;
        double minimalnyBladSieci = aktualnyBladSieci;
        double aktualnaSkutecznosc = 0;

        if (uczaceWartosci.isEmpty()) {
            showMessage("Brak danych uczących");
            return;
        }

        Collections.shuffle(uczaceWartosci);

        while (aktualnaSkutecznosc < wymaganaSkutecznosc) {
            for (int i = 0; i < 500; i++) {
                aktualnyBladSieci = 0;
                
                for (UczacaWartosc data : uczaceWartosci) {
                    double[] wynik = siec.trenuj(data.getInputExamples(), data.getDestination());
                    aktualnyBladSieci += wynik[0];
                    aktualnyBladSieci += wynik[1];
                    aktualnyBladSieci += wynik[2];
                }
                minimalnyBladSieci = Math.min(aktualnyBladSieci, minimalnyBladSieci);
                aktualnaSkutecznosc = (100 - (minimalnyBladSieci / uczaceWartosci.size()) * 100);
                if (aktualnaSkutecznosc >= wymaganaSkutecznosc) break;
            }

            trainingResultDisplay.setText("Skutecznosc: " + String.format("%.2f", aktualnaSkutecznosc) + "%");

            if (aktualnaSkutecznosc >= wymaganaSkutecznosc) {
                showMessage("Trening zakończony");
                break;
            }
            siec = new Siec(144, 2, new int[]{25, 3});
        }
    }

    private void test() {
        if (testoweWartosci.isEmpty()) {
            showMessage("Brak danych testowych");
            return;
        }

        StringBuilder resultMessage = new StringBuilder("<html>Wyniki testowania:<br>");

        for (TestowaWartosc data : testoweWartosci) {
            double[] wynik = siec.oblicz_wyjscie(data.getInputExamples());
            resultMessage.append(displayResult(wynik, data.getDestination())).append("<br>");
        }

        resultMessage.append("</html>");
        trainingResultDisplay.setText(resultMessage.toString());
    }

    private void recognize() {
        double[] wejscia = komponent.getGridAsInput();
        double[] wynik = siec.oblicz_wyjscie(wejscia);
        letterDisplay.setText(displayResult(wynik, -1));
        letterDisplay.setBackground(Color.GREEN);
    }

    private String displayResult(double[] wynik, int pozadane) {
        String[] letters = {"O", "D", "M"};
        double odp = wynik[0];
        String message = letters[0];

        for (int i = 1; i < wynik.length; i++) {
            if (wynik[i] > odp) {
                odp = wynik[i];
                message = letters[i];
            } else if (odp < 0.8) {
                message = "Żadna z liter";
            }
        }     

        if (pozadane >= 0) {
            return "Rozpoznano: " + message + " | Oczekiwane: " + letters[pozadane-1];
        } else {
            return message;
        }
    }

    private void loadTrainingData() {
        loadData(uczaceWartosci, "Trening");
    }

    private void loadTestData() {
        loadData(testoweWartosci, "Test");
    }

    private void loadData(List<?> dataList, String dataType) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text files", "txt"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (BufferedReader br = new BufferedReader(new FileReader(fileChooser.getSelectedFile()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(":");
                    double[] input = convertToDouble(parts[0].split(","));
                    int destination = Integer.parseInt(parts[1]);
                    if (dataType.equals("Trening")) {
                        uczaceWartosci.add(new UczacaWartosc(input, destination));
                    } else {
                        testoweWartosci.add(new TestowaWartosc(input, destination));
                    }
                }
                showMessage("Dane " + dataType.toLowerCase() + " załadowane.");
            } catch (IOException e) {
                e.printStackTrace();
                showMessage("Błąd przy wczytywaniu danych.");
            }
        }
    }

    private void saveDataToFile(List<?> dataList, String dataType) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text files", "txt"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().endsWith(".txt")) {
                file = new File(file.getAbsolutePath() + ".txt");
            }
    
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                for (Object data : dataList) {
                    if (data instanceof UczacaWartosc) {
                        UczacaWartosc uw = (UczacaWartosc) data;
                        bw.write(convertArrayToString(uw.getInputExamples()) + ":" + uw.getDestination());
                    } else if (data instanceof TestowaWartosc) {
                        TestowaWartosc tw = (TestowaWartosc) data;
                        bw.write(convertArrayToString(tw.getInputExamples()) + ":" + tw.getDestination());
                    }
                    bw.newLine();
                }
                showMessage(dataType + " zapisane do pliku.");
            } catch (IOException e) {
                e.printStackTrace();
                showMessage("Błąd przy zapisywaniu danych.");
            }
        }
    }
    
    private void appendDataToList(double[] inputExamples, String destination, String dataType) {
        int destinated = 0;

        if(destination == "O") destinated = 1;
        else if(destination == "D") destinated = 2;
        else if(destination == "M") destinated = 3;

        if (dataType == "ciąg uczący") {
            uczaceWartosci.add(new UczacaWartosc(inputExamples, destinated));
            showMessage("Dodano ciąg uczący do listy.");
        } else if (dataType == "ciąg testowy") {
            testoweWartosci.add(new TestowaWartosc(inputExamples, destinated));
            showMessage("Dodano ciąg testowy do listy.");
        }
    }
    
    private String convertArrayToString(double[] array) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i < array.length - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }    

    private double[] convertToDouble(String[] input) {
        double[] inputs = new double[input.length];
        for (int i = 0; i < input.length; i++) {
            inputs[i] = Double.parseDouble(input[i]);
        }
        return inputs;
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new Test("Neural Network Interface"));
    }
}
