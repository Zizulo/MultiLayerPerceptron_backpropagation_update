public class UczacaWartosc {
    private final double[] inputExamples;
    private final int destination;

    public UczacaWartosc(double[] inputExamples, int destination) {
        this.inputExamples = inputExamples;
        this.destination = destination;
    }

    public double[] getInputExamples() {
        return inputExamples;
    }

    public int getDestination() {
        return destination;
    }
}
