package fine;

public class FixedFine implements FineStrategy {
    @Override
    public double calculateFine(double overstayHours) {
        return overstayHours > 0 ? 50.0 : 0.0; 
    }

    @Override
    public String getName() {
        return "FIXED";
    }
}

