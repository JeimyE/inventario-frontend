package com.pos.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pos.models.Product;
import com.pos.utils.HttpUtil;

import java.lang.reflect.Type;
import java.util.List;

public class ProductService {

    private static final Gson GSON = new Gson();

    public List<Product> getAll() throws Exception {
        String json = HttpUtil.get("/api/productos");
        Type type = new TypeToken<List<Product>>() {}.getType();
        return GSON.fromJson(json, type);
    }

    public Product create(Product product) throws Exception {
        String response = HttpUtil.post("/api/productos", GSON.toJson(product));
        return GSON.fromJson(response, Product.class);
    }

    public Product update(Long id, Product product) throws Exception {
        String response = HttpUtil.put("/api/productos/" + id, GSON.toJson(product));
        return GSON.fromJson(response, Product.class);
    }

    public void delete(Long id) throws Exception {
        HttpUtil.delete("/api/productos/" + id);
    }
}
