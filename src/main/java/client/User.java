package client;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {
    private String login;
    private String message;
    private byte[] file;

    public User(String login, String message) {
        this.login =
        this.message = message;
        this.file = null;
    }

    public User(String login, String filename, byte[] content) {
        this.login = login;
        this.message = filename;
        this.file = content;
    }

    public boolean isMessage() {
        return this.file == null;
    }
}
