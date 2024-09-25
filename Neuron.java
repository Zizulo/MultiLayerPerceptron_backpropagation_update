import java.util.Random;

public class Neuron {
    double [] wagi;
    int liczba_wejsc;
    double wynik = 0;
    double blad = 0;

    public Neuron() {
        liczba_wejsc = 0;
        wagi = null;
    }

    public Neuron(int liczba_wejsc) {
        this.liczba_wejsc = liczba_wejsc;
        wagi = new double[liczba_wejsc + 1];
        generuj();
    }

    private void generuj() {
        Random r = new Random();

        for(int i = 0; i < liczba_wejsc; i++){
            wagi[i] = (r.nextDouble() - 0.5) * 2.0 * 0.01;
        }
    }

    public double oblicz_wyjscie(double [] wejscia){
        double bias = wagi[0];

        for(int i = 1; i < liczba_wejsc; i++){
            bias += wagi[i] * wejscia[i-1];
        }

        wynik = 1.0 / (1.0 + Math.exp(-bias));
        return wynik;
    }
}