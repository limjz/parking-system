package fine;

public class ProgressiveFine implements FineStrategy {
    @Override
    public double calculateFine(int hours) {
        if (hours <= 0) return 0;
        if (hours <= 24) return 50;
        if (hours <= 48) return 150;
        return 300;
    }

    @Override
    public String getName() {
        return "PROGRESSIVE";
    }
}
