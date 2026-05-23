package com.pos.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pos.models.Client;
import com.pos.utils.HttpUtil;

import java.lang.reflect.Type;
import java.util.List;

public class ClientService {

    private static final Gson GSON = new Gson();

    public List<Client> getAll() throws Exception {
        String json = HttpUtil.get("/api/clientes");
        Type type = new TypeToken<List<Client>>() {}.getType();
        return GSON.fromJson(json, type);
    }

    public Client create(Client client) throws Exception {
        String response = HttpUtil.post("/api/clientes", GSON.toJson(client));
        return GSON.fromJson(response, Client.class);
    }

    public Client update(Long id, Client client) throws Exception {
        String response = HttpUtil.put("/api/clientes/" + id, GSON.toJson(client));
        return GSON.fromJson(response, Client.class);
    }

    public void delete(Long id) throws Exception {
        HttpUtil.delete("/api/clientes/" + id);
    }
}
