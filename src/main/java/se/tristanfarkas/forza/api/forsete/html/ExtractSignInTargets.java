package se.tristanfarkas.forza.api.forsete.html;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ExtractSignInTargets {

    private String usernameTextFieldTarget;
    private String passwordTextFieldTarget;
    private String submitButtonSignal;

    public ExtractSignInTargets(Document doc) {
        Elements inputFields = doc.select("input[type=text], input[type=password]");

         for (Element input : inputFields) {
             if (input.attr("type").equals("password")) {
                 passwordTextFieldTarget = input.attr("name");
             }
             if (input.attr("type").equals("text")) {
                usernameTextFieldTarget = input.attr("name");
             }
         }

        Elements submitButtons = doc.select("button[type=submit]");
        for (Element button : submitButtons) {
            if (button.text().equals("login")) {
                submitButtonSignal = button.attr("name");
            }
        }
    }

    public String getUsernameTextFieldTarget() {
        return usernameTextFieldTarget;
    }

    public String getPasswordTextFieldTarget() {
        return passwordTextFieldTarget;
    }

    public String getLoginButtonSignal() {
        return submitButtonSignal;
    }
}
