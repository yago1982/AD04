package com.ymourino.ad04.models;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "empleados_tiendas")
public class EmpleadosTiendas implements Serializable {
    private static final long serialVersionUID = -9057921101288530289L;

    @EmbeddedId
    private EmpleadoTiendaID id;

    @ManyToOne
    @MapsId("empleado_id")
    @JoinColumn(name = "empleado_id")
    private Empleado empleado;

    @ManyToOne
    @MapsId("tienda_id")
    @JoinColumn(name = "tienda_id")
    private Tienda tienda;

    private Float jornada;

    public EmpleadosTiendas() {}

    public EmpleadosTiendas(Empleado empleado, Tienda tienda, Float jornada) {
        this.id = new EmpleadoTiendaID(empleado, tienda);
        this.empleado = empleado;
        this.tienda = tienda;
        this.jornada = jornada;
    }

    public EmpleadosTiendas(Empleado empleado, Tienda tienda) {
        this(empleado, tienda, 0f);
    }

    public EmpleadoTiendaID getId() {
        return id;
    }

    public void setId(EmpleadoTiendaID id) {
        this.id = id;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public Tienda getTienda() {
        return tienda;
    }

    public void setTienda(Tienda tienda) {
        this.tienda = tienda;
    }

    public Float getJornada() {
        return jornada;
    }

    public void setJornada(Float horas) {
        this.jornada = horas;
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

        EmpleadosTiendas other = (EmpleadosTiendas) object;

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
