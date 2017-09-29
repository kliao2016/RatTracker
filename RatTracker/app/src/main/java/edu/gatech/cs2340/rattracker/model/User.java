package edu.gatech.cs2340.rattracker.model;

/**
 * @author Charles Hall
 * @version 1.0
 */

public class User {

    private String username, password, email;
    private Boolean locked;

    /**
     *
     * @param username
     * @param password
     * @param email
     * @param locked if the user has been locked out by an admin
     */
    public User(String username, String password, String email, boolean locked) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.locked = locked;
    }

    /**
     * Constructor with a default value of false for locked
     *
     * @param username
     * @param password
     * @param email
     */
    public User(String username, String password, String email) {
        this(username,password,email, false);
    }

    /**
     *
     * @return username of user
     */
    public String getUsername() {
        return this.username;
    }

    /**
     *
     * @return user password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     *
      * @return user email
     */
    public String getEmail() {
        return this.email;
    }

    /**
     *
     * @return if the user has been locked
     */
    public boolean getLocked() {
        return this.locked;
    }

    /**
     *
     * @param lockedValue the value desired for locked
     */
    public void setLocked(boolean lockedValue) { this.locked = lockedValue; }

    /**
     *
     * @param newUsername the new username desired
     */
    public void setUsername(String newUsername) {
        this.username = newUsername;
    }

    /**
     *
     * @param newPassword the new password desired
     */
    public void setPassword(String newPassword) {
        this.password = newPassword;
    }

    /**
     *
     * @param newEmail the new email desired
     */
    public void setEmail(String newEmail) {
        this.email = newEmail;
    }
}