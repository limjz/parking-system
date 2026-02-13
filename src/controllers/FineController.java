package controllers;

import fine.*;
import java.util.List;
import utils.Config;
import utils.FileHandler;

public class FineController {

    // Get the current Enum from the text file
    public FineSchemeType getCurrentScheme() {
        List<String> lines = FileHandler.readAllLines(Config.FINE_SCHEME_FILE);
        if (lines.isEmpty()) return FineSchemeType.FIXED;
        return FineSchemeType.fromString(lines.get(0));
    }

    // Convert Enum to the actual Math Object
    public FineStrategy createStrategy(FineSchemeType type) {
        return switch (type) {
            case FIXED -> new FixedFine();
            case HOURLY -> new HourlyFine();
            case PROGRESSIVE -> new ProgressiveFine();
        };
    }

    // Save the new scheme (Used by Admin)
    public void saveScheme(FineSchemeType scheme) {
        FileHandler.updateData(Config.FINE_SCHEME_FILE, scheme.name());
    }
}