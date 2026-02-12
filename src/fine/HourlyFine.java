package fine;

public class HourlyFine implements FineStrategy {
    @Override
    public double calculateFine(int hours) {
        return hours * 20.0;
    }

    @Override
    public String getName() {
        return "HOURLY";
    }
}
