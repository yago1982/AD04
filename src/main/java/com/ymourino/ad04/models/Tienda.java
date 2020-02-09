package com.ymourino.ad04.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "tiendas")
public class Tienda implements Serializable {
    private static final long serialVersionUID = 6149885969685510536L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotNull
    private String nombre;

    @NotNull
    private String ciudad;

    @ManyToOne
    @JoinColumn(name = "provincia_id")
    private Provincia provincia;

    @OneToMany(mappedBy = "tienda")
    private List<ProductosTiendas> productos = new ArrayList<>();

    @OneToMany(mappedBy = "tienda")
    private List<EmpleadosTiendas> empleados = new ArrayList<>();

    public Tienda() {}

    public Tienda(String nombre, String ciudad, Provincia provincia) {
        this.nombre = nombre;
        this.ciudad = ciudad;
        this.provincia = provincia;
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

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public Provincia getProvincia() {
        return provincia;
    }

    public void setProvincia(Provincia provincia) {
        this.provincia = provincia;
    }

    public List<ProductosTiendas> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductosTiendas> productos) {
        this.productos = productos;
    }

    public List<EmpleadosTiendas> getEmpleados() {
        return empleados;
    }

    public void setEmpleados(List<EmpleadosTiendas> empleados) {
        this.empleados = empleados;
    }

    /**
     * Dado un producto y un stock, lo añade a la lista de relaciones de
     * esta tienda con los diferentes productos.
     *
     * @param producto Producto que debe figurar en la relación que vamos a añadir.
     * @param stock Stock del producto.
     */
    public void addProducto(Producto producto, Long stock) {
        ProductosTiendas relProductoTienda = new ProductosTiendas(producto, this, stock);
        productos.add(relProductoTienda);
        producto.getTiendas().add(relProductoTienda);
    }

    /**
     * Dado un empleado y una jornada, lo añade a la lista de relaciones de
     * esta tienda con los diferentes empleados.
     *
     * @param empleado Empleado que debe figurar en la relación que vamos a añadir.
     * @param horas Jornada del empleado.
     */
    public void addEmpleado(Empleado empleado, Float horas) {
        EmpleadosTiendas relEmpleadoTienda = new EmpleadosTiendas(empleado, this, horas);
        empleados.add(relEmpleadoTienda);
        empleado.getTiendas().add(relEmpleadoTienda);
    }

    /**
     * Dado un producto, elimina la relación que esta tienda tiene con él.
     *
     * @param producto El producto a eliminar de la tienda.
     */
    public void delProducto(Producto producto) {
        ProductosTiendas relProductoTienda = new ProductosTiendas(producto, this);
        productos.remove(relProductoTienda);
        producto.getTiendas().remove(relProductoTienda);
    }

    /**
     * Dado un empleado, elimina la relación que esta tienda tiene con él.
     *
     * @param empleado El empleado a eliminar de la tienda.
     */
    public void delEmpleado(Empleado empleado) {
        EmpleadosTiendas relEmpleadoTienda = new EmpleadosTiendas(empleado, this);
        empleados.remove(relEmpleadoTienda);
        empleado.getTiendas().remove(relEmpleadoTienda);
    }

    public String toString() {
        return id + ". " + nombre + " - " + ciudad + " (" + provincia.getNome() + ")";
    }
}
