public class Admin extends User {
    public Admin(String username, String password, String email) {
        super(username, password, email, false);
    }
    public boolean addUser(User user) {
        //TODO implement
        return false;
    }
    public boolean removeUser(User user) {
        //TODO implement
        return false;
    }
    public void unlock(User user) { user.setLocked(false); }
    public void lock(User user) { user.setLocked(true); }
}