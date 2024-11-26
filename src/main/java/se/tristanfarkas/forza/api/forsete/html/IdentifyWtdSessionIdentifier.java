package se.tristanfarkas.forza.api.forsete.html;

import org.jsoup.nodes.Document;

import java.util.Optional;
import java.util.regex.Pattern;

public class IdentifyWtdSessionIdentifier {

    private final Optional<String> sessionIdentifier;
    private final Pattern regex = Pattern.compile("(?<=\\?wtd=)[^&]+");

    public IdentifyWtdSessionIdentifier(Document doc) {
        sessionIdentifier = Optional.ofNullable(doc.select("noscript a").first())
                .flatMap(
                        (tag) -> Optional.of(tag.attr("href"))
                ).flatMap((href) -> {
                            var matches = regex.matcher(href);
                            if (matches.find()) {
                                return Optional.of(matches.group(0));
                            }
                            return Optional.empty();
                        }
                );
    }

    public Optional<String> getSessionIdentifier() {
        return sessionIdentifier;
    }

}
