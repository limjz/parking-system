package fine;

public class FineContext {

    private FineStrategy strategy;

    public FineContext(FineStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(FineStrategy strategy) {
        this.strategy = strategy;
    }

    public double calculateFine(int hoursOverstayed) {
        return strategy.calculateFine(hoursOverstayed);
    }
}
