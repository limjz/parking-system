package fine;

public class FixedFine implements FineStrategy {
    @Override
    public double calculateFine(int hours) {
        return hours > 0 ? 50.0 : 0.0;
    }

    @Override
    public String getName() {
        return "FIXED";
    }
}

