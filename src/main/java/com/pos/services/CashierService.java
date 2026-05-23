package com.pos.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pos.models.Cashier;
import com.pos.utils.HttpUtil;

import java.lang.reflect.Type;
import java.util.List;

public class CashierService {

    private static final Gson GSON = new Gson();

    public List<Cashier> getAll() throws Exception {
        String json = HttpUtil.get("/api/cajeros");
        Type type = new TypeToken<List<Cashier>>() {}.getType();
        return GSON.fromJson(json, type);
    }

    public Cashier create(Cashier cashier) throws Exception {
        String response = HttpUtil.post("/api/cajeros", GSON.toJson(cashier));
        return GSON.fromJson(response, Cashier.class);
    }

    public Cashier update(Long id, Cashier cashier) throws Exception {
        String response = HttpUtil.put("/api/cajeros/" + id, GSON.toJson(cashier));
        return GSON.fromJson(response, Cashier.class);
    }

    public void delete(Long id) throws Exception {
        HttpUtil.delete("/api/cajeros/" + id);
    }
}
