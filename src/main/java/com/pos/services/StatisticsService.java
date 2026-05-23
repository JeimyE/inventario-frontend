package com.pos.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pos.models.DashboardStats;
import com.pos.utils.HttpUtil;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class StatisticsService {

    private static final Gson GSON = new Gson();

    public DashboardStats getResumen() throws Exception {
        String json = HttpUtil.get("/api/estadisticas/resumen");
        return GSON.fromJson(json, DashboardStats.class);
    }

    public List<Map<String, Object>> getVentasPorDia() throws Exception {
        String json = HttpUtil.get("/api/estadisticas/ventas-por-dia");
        Type type = new TypeToken<List<Map<String, Object>>>() {}.getType();
        return GSON.fromJson(json, type);
    }

    public List<Map<String, Object>> getTopProductos() throws Exception {
        String json = HttpUtil.get("/api/estadisticas/top-productos");
        Type type = new TypeToken<List<Map<String, Object>>>() {}.getType();
        return GSON.fromJson(json, type);
    }
}
