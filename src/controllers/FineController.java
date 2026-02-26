package controllers;

import fine.*;
import java.util.List;
import models.FineSchemeType;
import utils.Config;
import utils.FileHandler;

public class FineController {

    // Get the current Enum from the text file
    public FineSchemeType getCurrentScheme() {
        List<String> lines = FileHandler.readAllLines(Config.FINE_SCHEME_FILE);
        if (lines.isEmpty()) return FineSchemeType.FIXED; //default fine: FIXED  
        return FineSchemeType.fromString(lines.get(0));
    }

    // Convert Enum to the actual Math Object, using the logic in the fine folder
    public FineStrategy createStrategy(FineSchemeType type) {
        return switch (type) {
            case FIXED -> new FixedFine(); 
            case HOURLY -> new HourlyFine();
            case PROGRESSIVE -> new ProgressiveFine();
            //case NEW -> new NewFine(); // just a extra one to experiment 
        };
    }

    // write the current use scheme into fine_scheme.txt
    public void saveScheme(FineSchemeType scheme) {
        FileHandler.updateData(Config.FINE_SCHEME_FILE, scheme.name());
    }
}