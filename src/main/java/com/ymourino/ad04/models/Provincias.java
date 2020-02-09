package com.ymourino.ad04.models;

import java.io.Serializable;
import java.util.List;

public class Provincias implements Serializable {
    private static final long serialVersionUID = 5019764435301990684L;

    private List<Provincia> provincias;

    public List<Provincia> getProvincias() {
        return provincias;
    }
}
