package com.pos.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pos.models.Invoice;
import com.pos.utils.HttpUtil;

import java.lang.reflect.Type;
import java.util.List;

public class InvoiceService {

    private static final Gson GSON = new Gson();

    public List<Invoice> getAll() throws Exception {
        String json = HttpUtil.get("/api/facturas");
        Type type = new TypeToken<List<Invoice>>() {}.getType();
        return GSON.fromJson(json, type);
    }

    public Invoice create(Invoice invoice) throws Exception {
        String response = HttpUtil.post("/api/facturas", GSON.toJson(invoice));
        return GSON.fromJson(response, Invoice.class);
    }

    public byte[] downloadPdf(Long id) throws Exception {
        return HttpUtil.downloadBytes("/api/facturas/" + id + "/pdf");
    }
}
