package fine;

public class ProgressiveFine implements FineStrategy {
    @Override
    public double calculateFine(double overstayHours) {
        if (overstayHours <= 0) return 0;
        if (overstayHours <= 24) return 50; //1st day 
        if (overstayHours <= 48) return 150; //2nd day 
        return 300; //maximum 
    }

    @Override
    public String getName() {
        return "PROGRESSIVE";
    }
}
