public class Siec {
    Warstwa [] warstwy;
    int liczba_warstw;

    public Siec() {
        warstwy = null;
        this.liczba_warstw = 0;
    }

    public Siec(int liczba_wejsc, int liczba_warstw, int [] lnww) {
        this.liczba_warstw = liczba_warstw;
        warstwy = new Warstwa[liczba_warstw];

        for(int i = 0; i < liczba_warstw; i++) {
            warstwy[i] = new Warstwa((i == 0) ? liczba_wejsc : lnww[i-1], lnww[i]);
        }
    }

    double [] oblicz_wyjscie(double [] wejscia) {
        double [] wyjscie = null;

        for(int i = 0; i < liczba_warstw; i++) {
            wejscia = wyjscie = warstwy[i].oblicz_wyjscie(wejscia);
        }

        return wyjscie;
    }

    public double [] trenuj(double[] dane_wejsciowe, int dane_pozadane) {
        double [] wynik;
        double [] blad_sieci = new double [3];

        // Oblicz wyjście sieci
        wynik = oblicz_wyjscie(dane_wejsciowe);

        // Oblicz błędy dla wyjść
        double blad_sieci_O = ((dane_pozadane == 1 ? 1 : 0) - wynik[0]);
        double blad_sieci_D = ((dane_pozadane == 2 ? 1 : 0) - wynik[1]);
        double blad_sieci_M = ((dane_pozadane == 3 ? 1 : 0) - wynik[2]);

        // Propagacja wsteczna błędów
        for(int i = warstwy.length - 1; i >= 0; i-- ) {
            if(i == warstwy.length - 1) {
                // Warstwa wyjściowa
                warstwy[i].neurony[0].blad = blad_sieci_O;
                warstwy[i].neurony[1].blad = blad_sieci_D;
                warstwy[i].neurony[2].blad = blad_sieci_M;
            } else {
                // Ukryte warstwy
                for(int j = 0; j < warstwy[i].liczba_neuronow; j++) {
                    Neuron neuron = warstwy[i].neurony[j];
                    neuron.blad = (blad_sieci_O * warstwy[liczba_warstw-1].neurony[0].wagi[j])
                                + (blad_sieci_D * warstwy[liczba_warstw-1].neurony[1].wagi[j])
                                + (blad_sieci_M * warstwy[liczba_warstw-1].neurony[2].wagi[j]);
                }
            }
        }

        // Aktualizacja wag
        for(int i = 0; i < liczba_warstw; i++) {
            for(int j = 0; j < warstwy[i].liczba_neuronow; j++) {
                for(int k = 0; k < warstwy[i].neurony[j].liczba_wejsc; k++) {
                    double blad_neuronu = warstwy[i].neurony[j].blad;
                    double wynik_neuronu = warstwy[i].neurony[j].wynik;
                    double wartosc_zadana = (i == 0) ? dane_wejsciowe[k] : warstwy[i-1].neurony[k].wynik;

                    warstwy[i].neurony[j].wagi[k] += wynik_neuronu * (1 - wynik_neuronu) * blad_neuronu * wartosc_zadana;
                }
            }
        }

        // Zwróć wartości błędu
        blad_sieci[0] = Math.abs(blad_sieci_O);
        blad_sieci[1] = Math.abs(blad_sieci_D);
        blad_sieci[2] = Math.abs(blad_sieci_M);

        return blad_sieci;
    }
}
