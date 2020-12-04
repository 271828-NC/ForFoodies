package com.example.app;

import android.app.Application;
//class that extends application in order to hold the logged user for ease of use along the application
public class logged extends Application {
    private User logged;

    public User getLogged() {
        return logged;
    }

    public void setLogged(User logged) {
        this.logged = logged;
    }
}
