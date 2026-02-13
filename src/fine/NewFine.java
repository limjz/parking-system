package fine;

public class NewFine implements FineStrategy{
    @Override
    public double calculateFine(double overstayHours) {
        // calculation logic 
        return 0.0;
    }

    @Override
    public String getName() {
        return "NEW";
    }
}
