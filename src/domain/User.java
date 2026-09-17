package domain;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String password;
    private int score;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.score = 0;
    }


    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }


}
