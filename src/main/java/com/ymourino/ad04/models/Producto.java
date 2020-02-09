package com.ymourino.ad04.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "productos")
public class Producto implements Serializable {
    private static final long serialVersionUID = -6321646765907912960L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String nombre;

    @NotNull
    private String descripcion;

    @NotNull
    private float precio;

    @OneToMany(mappedBy = "producto")
    private List<ProductosTiendas> tiendas = new ArrayList<>();

    public Producto() {}

    public Producto(String nombre, String descripcion, float precio) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public List<ProductosTiendas> getTiendas() {
        return tiendas;
    }

    public void setTiendas(List<ProductosTiendas> tiendas) {
        this.tiendas = tiendas;
    }

    /**
     * Dada una tienda y un stock, la añade a la lista de relaciones de
     * este producto con las diferentes tiendas.
     *
     * @param tienda Tienda que debe figurar en la relación que vamos a añadir.
     * @param stock Stock del producto.
     */
    public void addTienda(Tienda tienda, Long stock) {
        ProductosTiendas relProductoTienda = new ProductosTiendas(this, tienda, stock);
        tiendas.add(relProductoTienda);
        tienda.getProductos().add(relProductoTienda);
    }

    /**
     * Dada una tienda, elimina la relación que tiene este producto con ella.
     *
     * @param tienda Tienda que figura en la relación que vamos a eliminar.
     */
    public void delTienda(Tienda tienda) {
        ProductosTiendas relProductoTienda = new ProductosTiendas(this, tienda);
        tiendas.remove(relProductoTienda);
        tienda.getProductos().remove(relProductoTienda);
    }

    public String toString() {
        return id + ". " + nombre + " - " + descripcion + " - " + precio + "€";
    }
}
