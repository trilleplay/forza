package se.tristanfarkas.forza.api.forsete;

/**
 * A runtime error stemming from interactions with the Forsete service.
 */
public class ForseteException extends RuntimeException {
    ForseteException(String s) {
        super(s);
    }

    public ForseteException(Exception e) {
        super(e);
    }
}
