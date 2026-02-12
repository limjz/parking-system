package fine;

public class FineContext {

    private FineStrategy strategy;

    public FineContext(FineStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(FineStrategy strategy) {
        this.strategy = strategy;
    }

    public double calculateFine(int hours) {
        return strategy.calculateFine(hours);
    }

    public String getCurrentScheme() {
        return strategy.getName();
    }
}
