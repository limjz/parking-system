package fine;

public interface FineStrategy {
    double calculateFine(int overstayHours);
    String getName();
}
