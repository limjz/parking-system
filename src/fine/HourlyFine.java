package fine;

public class HourlyFine implements FineStrategy {
    @Override
    public double calculateFine(double overstayHours) {
        return overstayHours * 20.0; // each hour will fine RM20
    }

    @Override
    public String getName() {
        return "HOURLY";
    }
}
