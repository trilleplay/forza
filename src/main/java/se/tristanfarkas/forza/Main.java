package se.tristanfarkas.forza;

import se.tristanfarkas.forza.api.Credential;
import se.tristanfarkas.forza.api.Handler;
import se.tristanfarkas.forza.api.Submission;
import se.tristanfarkas.forza.api.forsete.ForseteFile;
import se.tristanfarkas.forza.api.forsete.ForseteHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    Handler handler = new ForseteHandler();


    void run(String courseName, int assignmentIndex, String filePath) throws IOException {
        var username = System.getenv("FORZA_USERNAME");
        if (username == null) {
            System.out.println("Invalid username provided, exiting.");
            System.exit(1);
        }
        var password = System.getenv("FORZA_PASSWORD");
        if (password == null) {
            System.out.println("Invalid password provided, exiting.");
            System.exit(1);
        }
        var c = new Credential.Builder().setUsername(username).setPassword(password).build();
        if (handler.signIn(c)) {
            System.out.println("Signed in as: " + username);
            var didUpload = handler.upload(new Submission(courseName, assignmentIndex, new ForseteFile("submission.c", Files.readAllBytes(Path.of(filePath)))));
            if (didUpload) {
                System.out.println("All done!");
            } else {
                System.out.println("Failed to upload.");
            }
        } else {
            System.out.println("Failed to sign in!");
            System.exit(1);
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("Arguments provided MUST be exactly 3. " + args.length + " was provided.");
            System.out.println("Arguments \"Course name\" assignment-index, \"File path\"");
            System.exit(1);
        }
        new Main().run(args[0], Integer.parseInt(args[1]), args[2]);
    }
}
