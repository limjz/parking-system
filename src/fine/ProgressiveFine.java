package fine;

public class ProgressiveFine implements FineStrategy {

    @Override
    public double calculateFine(int hours) {
        if (hours <= 1) {
            return 20;
        } else if (hours <= 3) {
            return 50;
        } else {
            return 50 + (hours - 3) * 30;
        }
    }

    @Override
    public String getName() {
        return "PROGRESSIVE";
    }
}
