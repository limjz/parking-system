package models;

public enum FineSchemeType {
    FIXED,
    HOURLY,
    PROGRESSIVE;
    // NEW;

    // encapsulation, this fromString function only can be use by the fineSchemeType not other class
    public static FineSchemeType fromString(String s) {
        if (s == null) return FIXED;
        try {
            return FineSchemeType.valueOf(s.trim().toUpperCase());
        } catch (Exception e) {
            return FIXED;
        }
    }
}
