package com.pos.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pos.models.Category;
import com.pos.utils.HttpUtil;

import java.lang.reflect.Type;
import java.util.List;

public class CategoryService {

    private static final Gson GSON = new Gson();

    public List<Category> getAll() throws Exception {
        String json = HttpUtil.get("/api/categorias");
        Type type = new TypeToken<List<Category>>() {}.getType();
        return GSON.fromJson(json, type);
    }

    public Category create(Category category) throws Exception {
        String response = HttpUtil.post("/api/categorias", GSON.toJson(category));
        return GSON.fromJson(response, Category.class);
    }

    public void delete(Long id) throws Exception {
        HttpUtil.delete("/api/categorias/" + id);
    }
}
