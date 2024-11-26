package se.tristanfarkas.forza.api.forsete.html;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class IdentifyAssignments {

    private final List<String> allSignals = new ArrayList<>();
    private final List<String> fileUploadSignals = new ArrayList<>();

    public IdentifyAssignments(Document doc) {
        Elements buttons = doc.select("button[type=submit]");
        Elements fileUploads = doc.select("input[type=file]");

        for (var button : buttons) {
            String signal = button.attr("name");
            String name = button.text();
            if (name.equals("submit")) {
                allSignals.add(signal);
            }
        }

        for (var upload : fileUploads) {
            fileUploadSignals.add(upload.id());
        }
    }

    public List<String> getAllSignals() {
        return allSignals;
    }

    public List<String> getFileUploadSignals() {
        return fileUploadSignals;
    }
}
