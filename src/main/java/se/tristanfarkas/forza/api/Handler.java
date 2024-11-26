package se.tristanfarkas.forza.api;

/**
 * Generic descriptor of a service that can take in problem solutions.
 */
public interface Handler {
    /**
     * Sign in to the handler.
     * @param credential The credentials to authenticate with the handler
     * @return Whether authentication succeeded or not.
     */
    boolean signIn(Credential credential);

    /**
     * Upload a submission to the handler, `signIn` MUST be called before this method.
     * @param submission The solution you wish to submit.
     * @return If we succeeded to submit.
     */
    boolean upload(Submission submission);
}
