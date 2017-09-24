public class User {

    private String username, password, email;
    private Boolean locked;

    public User(String username, String password, String email, boolean locked) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.locked = locked;
    }

    public User(String username, String password, String email) {
        this(username,password,email, false);
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getEmail() {
        return this.email;
    }

    public boolean getLocked() {
        return this.locked;
    }

    public boolean setLocked(boolean lockedValue) { this.locked = lockedValue; }

    public void setUsername(String newUsername) {
        this.username = newUsername;
    }

    public void setPassword(String newPassword) {
        this.password = newPassword;
    }

    public void setEmail(String newEmail) {
        this.email = newEmail;
    }
}