package edu.gatech.cs2340.rattracker.model;

/**
 * @author Charles Hall
 * @version 1.0
 */
public class Admin extends User {
    /**
     * Constructor that sets up Admin with default locked status as false
     *
     * @param username
     * @param password
     * @param email
     */
    public Admin(String username, String password, String email) {
        super(username, password, email);
    }

    /**
     *
     * @param user the user to be added
     * @return if the user was successfully added
     */
    public boolean addUser(User user) {
        //TODO implement
        return false;
    }

    /**
     *
     * @param user
     * @return boolean if the user was successfully removed
     */
    public boolean removeUser(User user) {
        //TODO implement
        return false;
    }

    /**
     *
     * @param user the user to be unlocked
     */
    public void unlock(User user) { user.setLocked(false); }

    /**
     *
     * @param user user to lock
     */
    public void lock(User user) { user.setLocked(true); }
}