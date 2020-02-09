package com.ymourino.ad04.models;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "productos_tiendas")
public class ProductosTiendas implements Serializable {
    private static final long serialVersionUID = -2763578137201954867L;

    @EmbeddedId
    private ProductoTiendaID id;

    @ManyToOne
    @MapsId("producto_id")
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @ManyToOne
    @MapsId("tienda_id")
    @JoinColumn(name = "tienda_id")
    private Tienda tienda;

    private Long stock;

    public ProductosTiendas() {}

    public ProductosTiendas(Producto producto, Tienda tienda, Long stock) {
        this.id = new ProductoTiendaID(producto, tienda);
        this.producto = producto;
        this.tienda = tienda;
        this.stock = stock;
    }

    public ProductosTiendas(Producto producto, Tienda tienda) {
        this(producto, tienda, 0L);
    }

    public ProductoTiendaID getId() {
        return id;
    }

    public void setId(ProductoTiendaID id) {
        this.id = id;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Tienda getTienda() {
        return tienda;
    }

    public void setTienda(Tienda tienda) {
        this.tienda = tienda;
    }

    public Long getStock() {
        return stock;
    }

    public void setStock(Long stock) {
        this.stock = stock;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null) {
            return false;
        }

        if (getClass() != object.getClass()) {
            return false;
        }

        ProductosTiendas other = (ProductosTiendas) object;

        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }

        return true;
    }
}
