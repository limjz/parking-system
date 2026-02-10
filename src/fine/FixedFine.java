package fine;

public class FixedFine implements FineStrategy {

    @Override
    public double calculateFine(int hours) {
        return 50;
    }

    @Override
    public String getName() {
        return "FIXED";
    }
}
