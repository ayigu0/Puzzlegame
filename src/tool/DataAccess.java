package tool;

import domain.User;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class DataAccess {
    private User user;
    private List<User> allUsers;

    public DataAccess() {
        File f = new File("puzzlegame-users.ser");
        if (f.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
                //noinspection unchecked
                this.allUsers = (List<User>) ois.readObject();
            } catch (IOException | ClassNotFoundException | ClassCastException e) {
                System.out.println("Could not load users file, starting with empty user list: " + e.getMessage());
                this.allUsers = new ArrayList<>();
            }
        } else {
            // first runs: no file yet
            this.allUsers = new ArrayList<>();
        }
        this.user = null;

    }

    public void registerUser(String username, String password) {

        User newUser = new User(username, password);
        this.user = newUser;
        this.allUsers.add(newUser);
        this.save();

    }

    public void save() {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("puzzlegame-users.ser");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(this.allUsers);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            System.err.println("Error saving users to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<User> getAllUsers() {
        return allUsers;
    }
}
