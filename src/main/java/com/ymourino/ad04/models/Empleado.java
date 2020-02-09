package com.ymourino.ad04.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "empleados")
public class Empleado implements Serializable {
    private static final long serialVersionUID = -2940543995034725653L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String nombre;

    @NotNull
    private String apellidos;

    @OneToMany(mappedBy = "empleado")
    private List<EmpleadosTiendas> tiendas = new ArrayList<>();

    public Empleado() {}

    public Empleado(String nombre, String apellidos) {
        this.nombre = nombre;
        this.apellidos = apellidos;
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

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNombreCompleto() {
        return nombre + " " + apellidos;
    }

    public List<EmpleadosTiendas> getTiendas() {
        return tiendas;
    }

    public void setTiendas(List<EmpleadosTiendas> tiendas) {
        this.tiendas = tiendas;
    }

    /**
     * Dada una tienda y una jornada, la añade a la lista de relaciones de
     * este empleado con las diferentes tiendas.
     *
     * @param tienda Tienda que debe figurar en la relación que vamos a añadir.
     * @param horas Jornada semanal del empleado.
     */
    public void addTienda(Tienda tienda, Float horas) {
        EmpleadosTiendas relEmpleadoTienda = new EmpleadosTiendas(this, tienda, horas);
        tiendas.add(relEmpleadoTienda);
        tienda.getEmpleados().add(relEmpleadoTienda);
    }

    /**
     * Dada una tienda, elimina la relación que tiene este empleado con ella.
     *
     * @param tienda Tienda que figura en la relación que vamos a eliminar.
     */
    public void delTienda(Tienda tienda) {
        EmpleadosTiendas relEmpleadoTienda = new EmpleadosTiendas(this, tienda);
        tiendas.remove(relEmpleadoTienda);
        tienda.getEmpleados().remove(relEmpleadoTienda);
    }

    public String toString() {
        return id + ". " + nombre + " " + apellidos;
    }
}
