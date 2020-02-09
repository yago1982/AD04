package com.ymourino.ad04.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "provincias")
public class Provincia implements Serializable {
    private static final long serialVersionUID = 8330729771273082960L;

    @Id
    private Long id;

    @Column(unique = true)
    @NotNull
    private String nome;

    public Provincia() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String toString() {
        return id + ". " + nome;
    }
}
