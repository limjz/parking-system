package fine;

public enum FineSchemeType {
    FIXED,
    HOURLY,
    PROGRESSIVE;

    public static FineSchemeType fromString(String s) {
        if (s == null) return FIXED;
        try {
            return FineSchemeType.valueOf(s.trim().toUpperCase());
        } catch (Exception e) {
            return FIXED;
        }
    }
}
