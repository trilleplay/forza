package se.tristanfarkas.forza.api;

public class Credential {

    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    private Credential(Builder b) {
        this.username = b.username;
        this.password = b.password;
    }


    public static class Builder {
        private String username;
        private String password;

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Credential build() {
            return new Credential(this);
        }
    }
}
