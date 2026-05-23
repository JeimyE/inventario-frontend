package com.pos.models;

public class DashboardStats {
    private Long totalProductos;
    private Long totalClientes;
    private Long totalFacturas;
    private Double ingresos;

    public DashboardStats() {}

    public Long getTotalProductos() { return totalProductos; }
    public void setTotalProductos(Long totalProductos) { this.totalProductos = totalProductos; }

    public Long getTotalClientes() { return totalClientes; }
    public void setTotalClientes(Long totalClientes) { this.totalClientes = totalClientes; }

    public Long getTotalFacturas() { return totalFacturas; }
    public void setTotalFacturas(Long totalFacturas) { this.totalFacturas = totalFacturas; }

    public Double getIngresos() { return ingresos; }
    public void setIngresos(Double ingresos) { this.ingresos = ingresos; }
}
