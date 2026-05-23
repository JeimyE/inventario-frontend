package com.pos.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pos.utils.HttpUtil;
import com.pos.utils.SessionManager;

public class AuthService {

    private static final Gson GSON = new Gson();

    public boolean login(String username, String password) throws Exception {
        JsonObject body = new JsonObject();
        body.addProperty("username", username);
        body.addProperty("password", password);

        String response = HttpUtil.post("/api/auth/login", GSON.toJson(body));
        JsonObject json = JsonParser.parseString(response).getAsJsonObject();

        if (json.has("token")) {
            SessionManager.getInstance().setToken(json.get("token").getAsString());
            SessionManager.getInstance().setUsername(username);
            return true;
        }
        return false;
    }
}
