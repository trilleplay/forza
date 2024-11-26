package se.tristanfarkas.forza.api.forsete.html;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

public class IdentifyAllSignals {

    private final Map<String, String> allSignals = new HashMap<>();

    public IdentifyAllSignals(Document doc) {
        Elements buttons = doc.select("button[type=submit]");

        for (var button : buttons) {
            String signal = button.attr("name");
            String name = button.text();
            allSignals.put(name, signal);
        }
    }

    public Map<String, String> getAllSignals() {
        return allSignals;
    }
}
