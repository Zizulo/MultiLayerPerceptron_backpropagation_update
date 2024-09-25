public class Trening {
    Siec siec;
    double [] wejscie;
    double [] wynik;
    double [] blad_sieci = new double [3];

    public Trening() {
        siec = null;
        wejscie = null;
        wynik = null;
        blad_sieci = null;
    }

    public Trening(double[] dane_wejsciowe) {
        wejscie = new double[dane_wejsciowe.length];

        for(int i = 0; i < dane_wejsciowe.length; i++) {
            wejscie[i] = dane_wejsciowe[i];
        }
    }

    public double [] trenuj(int dane_pozadane) {
        siec = new Siec();
        wynik = siec.oblicz_wyjscie(wejscie);

        double blad_sieci_O = ( (dane_pozadane == 1 ? 1 : 0) - wynik[0] );
        double blad_sieci_D = ( (dane_pozadane == 2 ? 1 : 0) - wynik[1] );
        double blad_sieci_M = ( (dane_pozadane == 3 ? 1 : 0) - wynik[2] );

        for(int i = siec.warstwy.length - 1; i >= 0; i-- ) {

            if(i == siec.warstwy.length - 1) {
                siec.warstwy[i].neurony[0].blad = blad_sieci_O;
                siec.warstwy[i].neurony[1].blad = blad_sieci_D;
                siec.warstwy[i].neurony[2].blad = blad_sieci_M;
            }else {
                Neuron neuron_wyjsciowy_O = siec.warstwy[siec.liczba_warstw-1].neurony[0];
                Neuron neuron_wyjsciowy_D = siec.warstwy[siec.liczba_warstw-1].neurony[1];
                Neuron neuron_wyjsciowy_M = siec.warstwy[siec.liczba_warstw-1].neurony[2];

                for(int j = 0; j < siec.warstwy[i].liczba_neuronow; j++) {
                    Neuron neuron = siec.warstwy[i].neurony[j];
                    neuron.blad = (blad_sieci_O * neuron_wyjsciowy_O.wagi[j]) + (blad_sieci_D * neuron_wyjsciowy_D.wagi[j]) + (blad_sieci_M * neuron_wyjsciowy_M.wagi[j]);
                }
            }
        }

        for(int i = 0; i < siec.liczba_warstw; i++) {

            for(int j = 0; j < siec.warstwy[i].liczba_neuronow; j++) {

                for(int k = 0; k <= siec.warstwy[i].neurony[j].liczba_wejsc; k++) {
                    double blad_neuronu = siec.warstwy[i].neurony[j].blad;
                    double wynik_neuronu = siec.warstwy[i].neurony[j].wynik;
                    double wartosc_zadana = 0;

                    if(i == 0) {
                        if(k < wejscie.length) {
                            wartosc_zadana = wejscie[k];
                        }else {
                            if(k < siec.warstwy[i-1].neurony.length) {
                                wartosc_zadana = siec.warstwy[i-1].neurony[k].wynik;
                            }
                        }

                        siec.warstwy[i].neurony[j].wagi[k] += wynik_neuronu * (1 - wynik_neuronu) * blad_neuronu * wartosc_zadana;
                    }
                }
            }
        }

        blad_sieci[0] = Math.abs(blad_sieci_O);
        blad_sieci[1] = Math.abs(blad_sieci_D);
        blad_sieci[2] = Math.abs(blad_sieci_M);

        return blad_sieci;
    }
}
