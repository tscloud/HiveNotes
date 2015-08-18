package net.tscloud.hivenotes.db;

import java.io.Serializable;

/**
 * Created by tscloud on 8/15/15.
 */
public class Profile {

    public static final String TAG = "Profile";

    private long id;
    private String name;
    private String email;

    public Profile(){}

    public Profile(String name, String email) {
        this.email = email;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
