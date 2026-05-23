package com.pos.models;

import java.util.List;

public class Invoice {
    private Long id;
    private Client cliente;
    private Cashier cajero;
    private String fecha;
    private Double total;
    private List<InvoiceLine> lineas;

    public Invoice() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Client getCliente() { return cliente; }
    public void setCliente(Client cliente) { this.cliente = cliente; }

    public Cashier getCajero() { return cajero; }
    public void setCajero(Cashier cajero) { this.cajero = cajero; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public List<InvoiceLine> getLineas() { return lineas; }
    public void setLineas(List<InvoiceLine> lineas) { this.lineas = lineas; }
}
