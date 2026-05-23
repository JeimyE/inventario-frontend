package com.pos.models;

public class InvoiceLine {
    private Long id;
    private Product producto;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;

    public InvoiceLine() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProducto() { return producto; }
    public void setProducto(Product producto) { this.producto = producto; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }

    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }
}
