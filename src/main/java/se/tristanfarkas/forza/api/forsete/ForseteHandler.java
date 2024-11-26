package se.tristanfarkas.forza.api.forsete;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import se.tristanfarkas.forza.api.Credential;
import se.tristanfarkas.forza.api.Handler;
import se.tristanfarkas.forza.api.Submission;
import se.tristanfarkas.forza.api.forsete.html.ExtractSignInTargets;
import se.tristanfarkas.forza.api.forsete.html.IdentifyAllSignals;
import se.tristanfarkas.forza.api.forsete.html.IdentifyAssignments;
import se.tristanfarkas.forza.api.forsete.html.IdentifyWtdSessionIdentifier;
import se.tristanfarkas.forza.api.forsete.ssh.ForseteOutputChecker;

import java.io.IOException;

import static se.tristanfarkas.forza.api.forsete.ForseteTrust.certificates;


/**
 * Manages everything we need to interact with <a href="https://forsete.cs.lth.se/">Forsete</a>.
 */
public class ForseteHandler implements Handler {

    // Base URL for the Forsete service.
    private static final String url = "https://forsete.cs.lth.se/";
    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .addInterceptor(new UserAgentInterceptor())
            .sslSocketFactory(certificates.sslSocketFactory(), certificates.trustManager())
            .build();
    private boolean isAuthorized;
    private String wtdSessionIdentifier;
    private Credential credential;


    @Override
    public boolean signIn(Credential credential) {
        if (isAuthorized) {
            throw new ForseteException("Attempted to authorize, whilst already signed in.");
        }
        this.credential = credential;
        if (wtdSessionIdentifier == null) {
            Document doc = getForseteResponse();
            wtdSessionIdentifier = new IdentifyWtdSessionIdentifier(doc).getSessionIdentifier().orElseThrow();
        }

        Document doc = getForseteResponse();
        var targets = new ExtractSignInTargets(doc);
        var formBody = new ForseteForm.Builder()
                .add("request", "page")
                .add("wtd", wtdSessionIdentifier)
                .add(targets.getUsernameTextFieldTarget(), credential.getUsername())
                .add(targets.getLoginButtonSignal(), "")
                .add(targets.getPasswordTextFieldTarget(), credential.getPassword())
                .build();
        Request request = new Request.Builder()
                .url(getRequestUrl())
                .post(formBody.getMultipartBody())
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.body() == null) {
                throw new ForseteException("Got empty body from Forsete");
            }
            isAuthorized = (response.body().string().contains("Connected to"));
            return isAuthorized;
        } catch (IOException e) {
            throw new ForseteException(e);
        }
    }

    @Override
    public boolean upload(Submission submission) {
        var request = new IdentifyAllSignals(getForseteResponse());
        var signal = request.getAllSignals().get(submission.course());
        if (signal == null) {
            throw new ForseteException("Attempted to find signal for \"" + submission.course() + "\" failed, make sure it is correct.");
        }
        var formBody = new ForseteForm.Builder()
                .add("request", "page")
                .add("wtd", wtdSessionIdentifier)
                .add(signal, "")
                .build();
        Request changePageRequest = new Request.Builder()
                .url(getRequestUrl())
                .post(formBody.getMultipartBody())
                .build();
        try (Response response = httpClient.newCall(changePageRequest).execute()) {
            if (response.body() == null) {
                throw new ForseteException("Got empty body from Forsete");
            }
            var s = response.body().string();
            var identified = new IdentifyAssignments(Jsoup.parse(s));
            var assignmentSignal = identified.getAllSignals().get(submission.assignmentIndex());
            var fileUpload = identified.getFileUploadSignals().get(submission.assignmentIndex());
            var uploadFormBody = new ForseteForm.Builder()
                    .add("request", "page")
                    .add("wtd", wtdSessionIdentifier)
                    .add(assignmentSignal, "")
                    .addFile(fileUpload, submission.file());
            identified.getFileUploadSignals().forEach((f) -> {
                if (!f.equals(fileUpload)) {
                    uploadFormBody.emptyFile(f);
                }
            });
            Request uploadFileRequest = new Request.Builder()
                    .url(getRequestUrl())
                    .post(uploadFormBody.build().getMultipartBody())
                    .build();
            try (Response uploadResponse = httpClient.newCall(uploadFileRequest).execute()) {
                if (uploadResponse.body() == null) {
                    throw new ForseteException("Got empty body from Forsete");
                }
                System.out.println("Uploaded submission!");
                System.out.println("Connecting to machine:");
                var outputChecker = new ForseteOutputChecker("power.cs.lth.se", credential.getUsername(), credential.getPassword());
                outputChecker.consumeAndPrintOutput();
                return true;
            }
        } catch (IOException e) {
            throw new ForseteException(e);
        }

    }

    private Document getForseteResponse() {
        Request request = new Request.Builder()
                .url(getRequestUrl())
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.body() == null) {
                throw new ForseteException("Got empty body from Forsete");
            }
            return Jsoup.parse(response.body().string());
        } catch (IOException e) {
            throw new ForseteException(e);
        }
    }

    private String getRequestUrl() {
        return (wtdSessionIdentifier == null) ? url : url + "?wtd=" + wtdSessionIdentifier + "&js=no";
    }
}
