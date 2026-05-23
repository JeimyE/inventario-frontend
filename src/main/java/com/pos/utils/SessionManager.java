package com.pos.utils;

public class SessionManager {

    private static SessionManager instance;
    private String token;
    private String username;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public boolean isLoggedIn() { return token != null && !token.isEmpty(); }

    public void clearSession() {
        this.token = null;
        this.username = null;
    }
}
