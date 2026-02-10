package controllers;

import models.TicketRecord;
import utils.SimpleFile;

import java.util.ArrayList;
import java.util.List;

public class TicketFileService {

    private final String ticketFilePath;

    public TicketFileService(String ticketFilePath) {
        this.ticketFilePath = ticketFilePath;
    }

    public List<TicketRecord> loadAll() {
        List<String> lines = SimpleFile.readAllLines(ticketFilePath);
        List<TicketRecord> records = new ArrayList<>();

        for (String raw : lines) {
            if (raw == null) continue;
            String line = raw.trim();
            if (line.isEmpty()) continue;

            String[] p = line.contains("|") ? line.split("\\|") : line.split(",");
            for (int i = 0; i < p.length; i++) p[i] = p[i].trim();

            // [0]=id [1]=plate [2]=entry [3]=exit [4]=payment [5]=status [6]=fine [7]=spot(optional)
            String id = safe(p, 0);
            String plate = safe(p, 1);
            String entry = safe(p, 2);
            String exit = safe(p, 3);
            double payment = toDouble(safe(p, 4));
            String status = safe(p, 5);
            double fine = toDouble(safe(p, 6));
            String spot = safe(p, 7);

            records.add(new TicketRecord(id, plate, entry, exit, payment, status, fine, spot));
        }

        return records;
    }

    private String safe(String[] arr, int idx) {
        if (arr == null) return "";
        if (idx < 0 || idx >= arr.length) return "";
        return arr[idx] == null ? "" : arr[idx].trim();
    }

    private double toDouble(String s) {
        try {
            if (s == null || s.isBlank()) return 0;
            return Double.parseDouble(s);
        } catch (Exception e) {
            return 0;
        }
    }
}
