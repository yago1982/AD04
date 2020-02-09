package com.ymourino.ad04;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.ymourino.ad04.models.*;

import com.ymourino.ad04.utils.Config;
import org.hibernate.Session;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import javax.persistence.EntityManagerFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Properties;


public class Empresa {
    private final EntityManagerFactory entityManagerFactory;
    private final Session session;

    public Empresa() throws Exception {
        Gson gson = new Gson();
        Configuration configuration = new Configuration();
        Properties properties = new Properties();

        try {
            // El fichero config.json tendrá que proporcionarlo el usuario y
            // ubicarlo en el mismo directorio donde resida el jar de la aplicación.
            // El contenido del fichero de configuración debería ser algo similar
            // a esto (modificando los valores que correspondan):
            //
            // {
            //     "dbConnection": {
            //     "address": "localhost",
            //             "port": "3306",
            //             "name": "ad04",
            //             "user": "ad04",
            //             "password": "ad04"
            //     },
            //
            //     "hibernate":{
            //     "driver": "org.mariadb.jdbc.Driver",
            //             "dialect": "org.hibernate.dialect.MariaDB103Dialect",
            //             "HBM2DDL_AUTO": "create",
            //             "SHOW_SQL": true
            //     }
            // }
            //

            JsonReader jr = new JsonReader(new FileReader("config.json"));
            Config config = gson.fromJson(jr, Config.class);

            Config.DbConnection dbConnection = config.getDbConnection();
            Config.Hibernate hibernate = config.getHibernate();

            properties.put(Environment.URL, "jdbc:mariadb://"
                    + dbConnection.getAddress()
                    + ":" + dbConnection.getPort()
                    + "/" + dbConnection.getName());
            properties.put(Environment.USER, dbConnection.getUser());
            properties.put(Environment.PASS, dbConnection.getPassword());

            properties.put(Environment.DRIVER, hibernate.getDriver());
            properties.put(Environment.DIALECT, hibernate.getDialect());
            properties.put(Environment.HBM2DDL_AUTO, hibernate.getHBM2DDL_AUTO());
            properties.put(Environment.SHOW_SQL, hibernate.getSHOW_SQL());

            configuration.setProperties(properties);

            configuration.addAnnotatedClass(Cliente.class);
            configuration.addAnnotatedClass(Provincia.class);
            configuration.addAnnotatedClass(Tienda.class);
            configuration.addAnnotatedClass(Producto.class);
            configuration.addAnnotatedClass(ProductoTiendaID.class);
            configuration.addAnnotatedClass(ProductosTiendas.class);
            configuration.addAnnotatedClass(Empleado.class);
            configuration.addAnnotatedClass(EmpleadoTiendaID.class);
            configuration.addAnnotatedClass(EmpleadosTiendas.class);
        } catch (FileNotFoundException | JsonSyntaxException e) {
            e.printStackTrace();
            throw new Exception(e);
        }


        final StandardServiceRegistry registry =
                new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties())
                        .build();

        try {
            entityManagerFactory = configuration
                    .buildSessionFactory(registry);
        } catch (Exception e) {
            e.printStackTrace();
            StandardServiceRegistryBuilder.destroy(registry);
            throw new Exception(e);
        }


        /*final StandardServiceRegistry registry =
                new StandardServiceRegistryBuilder()
                        .configure(new File("config.xml"))
                        .build();

        try {
            entityManagerFactory = new Configuration()
                    .buildSessionFactory(registry);
        } catch (Exception e) {
            e.printStackTrace();
            StandardServiceRegistryBuilder.destroy(registry);
            throw new Exception(e);
        }*/


        session = entityManagerFactory.createEntityManager().unwrap(Session.class);
    }

    public void close() {
        if (session != null && session.isOpen()) {
            session.close();
        }

        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }


    /* ALTAS */

    /**
     * Dado un objeto, lo añade a la base de datos.
     *
     * @param element Objeto a añadir.
     * @return True si no se producen excepciones al intentar añadir el objeto.
     */
    private boolean addElement(Object element) {
        if (element != null) {
            try {
                session.beginTransaction();
                session.save(element);
                session.getTransaction().commit();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                // IMPORTANTE HACER ROLLBACK
                session.getTransaction().rollback();
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Dados los datos de una tienda, crea un objeto de la clase Tienda y lo
     * añade a la base de datos.
     *
     * @param nombre Nombre de la tienda.
     * @param ciudad Ciudad de la tienda.
     * @param provincia Provincia de la tienda.
     * @return True si se ha añadido la tienda a la base de datos.
     */
    public boolean addTienda(String nombre, String ciudad, Provincia provincia) {
        Tienda tienda = new Tienda(nombre, ciudad, provincia);

        if (addElement(tienda)) {
            return tienda.getId() > 0;
        } else {
            return false;
        }
    }

    /**
     * Dados los datos de un producto, crea un objeto de la clase Producto y lo
     * añade a la base de datos.
     *
     * @param nombre Nombre del producto.
     * @param descripcion Descripción del producto.
     * @param precio Precio del producto.
     * @return True si se ha añadido el producto a la base de datos.
     */
    public boolean addProducto(String nombre, String descripcion, float precio) {
        Producto producto = new Producto(nombre, descripcion, precio);

        if (addElement(producto)) {
            return producto.getId() > 0;
        } else {
            return false;
        }
    }

    /**
     * Dados los datos de un cliente, crea un objeto de la clase Cliente y lo
     * añade a la base de datos.
     *
     * @param nombre Nombre del cliente.
     * @param apellidos Apellidos del cliente.
     * @param mail Correo electrónico del cliente.
     * @return True si se ha añadido el cliente a la base de datos.
     */
    public boolean addCliente(String nombre, String apellidos, String mail) {
        Cliente cliente = new Cliente(nombre, apellidos, mail);

        if (addElement(cliente)) {
            return cliente.getId() > 0;
        } else {
            return false;
        }
    }

    /**
     * Dados los datos de un empleado, crea un objeto de la clase Empleado y lo
     * añade a la base de datos.
     *
     * @param nombre Nombre del empleado.
     * @param apellidos Apellidos del empleado.
     * @return True si se ha añadido el empleado a la base de datos.
     */
    public boolean addEmpleado(String nombre, String apellidos) {
        Empleado empleado = new Empleado(nombre, apellidos);

        if (addElement(empleado)) {
            return empleado.getId() > 0;
        } else {
            return false;
        }
    }

    /**
     * Dado un objeto de la clase Provincia, lo añade a la base de datos.
     *
     * @param provincia Un objeto de la clase Provincia.
     */
    public boolean addProvincia(Provincia provincia) {
        // Las provincias se deserializan de un fichero JSON, de modo que
        // solo hay que almacenar en la base de datos los objetos que obtenemos
        // con la deserialización.
        //
        // Además, al contrario que con el resto de métodos add###, no hay que
        // comprobar que se haya generado un identificador correcto porque
        // ya nos viene dado.
        return addElement(provincia);
    }

    /**
     * Dados los datos adecuados, añade un producto a una tienda con un stock
     * inicial.
     *
     * @param producto Producto a añadir.
     * @param tienda Tienda donde añadirlo.
     * @param stock Stock inicial.
     * @return True si se ha añadido el producto a la tienda.
     */
    public boolean addProductoToTienda(Producto producto, Tienda tienda, Long stock) {
        ProductosTiendas relProductoTienda = new ProductosTiendas(producto, tienda, stock);

        if (addElement(relProductoTienda)) {
            tienda.addProducto(producto, stock);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Dados los datos adecuados, añade un empleado a una tienda con una jornada
     * semana determinada.
     *
     * @param empleado Empleado a añadir.
     * @param tienda Tienda donde añadirlo.
     * @param horas Jornada semanal.
     * @return True si se ha añadido el empleado a la tienda.
     */
    public boolean addEmpleadoToTienda(Empleado empleado, Tienda tienda, Float horas) {
        EmpleadosTiendas relEmpleadoTienda = new EmpleadosTiendas(empleado, tienda, horas);

        if (addElement(relEmpleadoTienda)) {
            tienda.addEmpleado(empleado, horas);
            return true;
        } else {
            return false;
        }
    }


    /* BAJAS */

    /**
     * Dado un objeto, lo elimina de la base de datos.
     *
     * @param element Elemento a eliminar.
     */
    private boolean delElement(Object element) {
        if (element != null) {
            try {
                session.beginTransaction();
                session.delete(element);
                session.getTransaction().commit();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                // IMPORTANTE HACER ROLLBACK
                session.getTransaction().rollback();
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Dado un objeto de la clase Tienda, lo elimina de la base de datos.
     *
     * @param tienda Un objeto de la clase Tienda.
     * @return True si se ha eliminado el objeto de la base de datos.
     */
    public boolean delTienda(Tienda tienda) {
        if (tienda.getId() != null && getTienda(tienda.getId()) != null) {
            if (delElement(tienda)) {
                return getTienda(tienda.getId()) == null;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Dado un objeto de la clase Producto, lo elimina de la base de datos.
     *
     * @param producto Un objeto de la clase Producto.
     * @return True si se ha eliminado el objeto de la base de datos.
     */
    public boolean delProducto(Producto producto) {
        if (producto.getId() != null && getProducto(producto.getId()) != null) {
            if (delElement(producto)) {
                return getProducto(producto.getId()) == null;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Dado un objeto de la clase Cliente, lo elimina de la base de datos.
     *
     * @param cliente Un objeto de la clase Cliente.
     * @return True si se ha eliminado el objeto de la base de datos.
     */
    public boolean delCliente(Cliente cliente) {
        if (cliente.getId() != null && getCliente(cliente.getId()) != null) {
            if (delElement(cliente)) {
                return getCliente(cliente.getId()) == null;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Dado un objeto de la clase Empleado, lo elimina de la base de datos.
     *
     * @param empleado Un objeto de la clase Empleado.
     * @return True si se ha eliminado el objeto de la base de datos.
     */
    public boolean delEmpleado(Empleado empleado) {
        if (empleado.getId() != null && getEmpleado(empleado.getId()) != null) {
            if (delElement(empleado)) {
                return getEmpleado(empleado.getId()) == null;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Dada una relación entre un producto y una tienda, la elimina de la base
     * de datos.
     *
     * @param relProductoTienda Relación entre producto y tienda.
     * @return True si se ha eliminado la relación de la base de datos.
     */
    public boolean delProductoFromTienda(ProductosTiendas relProductoTienda) {
        if (getRelProductoTienda(relProductoTienda.getId()) != null) {
            if (delElement(relProductoTienda)) {
                // Tras eliminar la relación entre producto y tienda de la base
                // de datos, ya no debería encontrarse al buscarla.
                return getRelProductoTienda(relProductoTienda.getId()) == null;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Dada una relación entre un empleado y una tienda, la elimina de la base
     * de datos.
     *
     * @param relEmpleadoTienda Relación entre empleado y tienda.
     * @return True si se ha eliminado la relación de la base de datos.
     */
    public boolean delEmpleadoFromTienda(EmpleadosTiendas relEmpleadoTienda) {
        if (getRelEmpleadoTienda(relEmpleadoTienda.getId()) != null) {
            if (delElement(relEmpleadoTienda)) {
                // Tras eliminar la relación entre producto y tienda de la base
                // de datos, ya no debería encontrarse al buscarla.
                return getRelEmpleadoTienda(relEmpleadoTienda.getId()) == null;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }



    /* MODIFICACIONES */

    /**
     * Dado un elemento ya existente en la base de datos, lo actualiza.
     *
     * @param element Objeto con los datos actualizados.
     * @return True si se ha actualizado correctamente.
     */
    private boolean updateElement(Object element) {
        if (element != null) {
            try {
                session.beginTransaction();
                session.update(element);
                session.getTransaction().commit();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                // IMPORTANTE HACER ROLLBACK
                session.getTransaction().rollback();
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Dada una relación entre un producto y una tienda, y una cantidad de stock,
     * actualiza el stock del producto en la tienda.
     *
     * @param relProductoTienda Objeto que relaciona el producto y la tienda.
     * @param newStock Nuevo stock del producto en la tienda.
     * @return True si se ha hecho la actualización correctamente.
     */
    public boolean updateStock(ProductosTiendas relProductoTienda, Long newStock) {
        relProductoTienda.setStock(newStock);
        return updateElement(relProductoTienda);
    }

    /**
     * Dada una relación entre un empleado y una tienda, y una jornada semanal,
     * actualiza la jornada del empleado en la tienda.
     *
     * @param relEmpleadoTienda Objeto que relaciona el empleado y la tienda.
     * @param newJornada Nueva jornada del empleado en la tienda.
     * @return True si se ha hecho la actualización correctamente.
     */
    public boolean updateJornada(EmpleadosTiendas relEmpleadoTienda, Float newJornada) {
        relEmpleadoTienda.setJornada(newJornada);
        return updateElement(relEmpleadoTienda);
    }


    /* CONSULTAS */

    /**
     * Dado un identificador y la clase del objeto que queremos recuperar,
     * devuelve el objeto que tenga la clave adecuada desde la base de datos,
     * o null si no está almacenado.
     *
     * @param id Identificador.
     * @param type Clase del objeto a recuperar.
     * @param <T> Tipo (genérico) de los elementos a recuperar.
     * @param <U> Tipo (genérico) del identificador.
     * @return El objeto almacenado en la base de datos (o null si no existe).
     */
    private <T, U> T getElement(U id, Class<T> type) {
        if (id != null) {
            return session.find(type, id);
        } else {
            return null;
        }
    }

    /**
     * Obtiene una lista de objetos de la base de datos, dada una consulta
     * determinada y la clase de los objetos.
     *
     * @param query Consulta para obtener la lista.
     * @param type Clase de los objetos.
     * @param <T> Tipo (genérico) de los elementos.
     * @return Lista de objetos.
     */
    private <T> List<T> getElements(String query, Class<T> type) {
        return session.createQuery(query, type).getResultList();
    }

    /**
     * Dado el identificador de una tienda, obtiene dicha tienda de la base de
     * datos.
     *
     * @param id Identificador de la tienda.
     * @return La tienda que corresponde al identificador (o null si no existe).
     */
    public Tienda getTienda(Long id) {
        return getElement(id, Tienda.class);
    }

    /**
     * Dado el identificador de un producto, obtiene dicho producto de la base
     * de datos.
     *
     * @param id Identificador del producto.
     * @return El producto que corresponde al identificador (o null si no existe).
     */
    public Producto getProducto(Long id) {
        return getElement(id, Producto.class);
    }

    /**
     * Dado el identificador de un cliente, obtiene dicho cliente de la base de
     * datos.
     *
     * @param id Identificador del cliente.
     * @return El cliente que corresponde al identificador (o null si no existe).
     */
    public Cliente getCliente(Long id) {
        return getElement(id, Cliente.class);
    }

    /**
     * Dado el identificador de un empleado, obtiene dicho empleado de la base de
     * datos.
     *
     * @param id Identificador del empleado.
     * @return El empleado que corresponde al identificador (o null si no existe).
     */
    public Empleado getEmpleado(Long id) {
        return getElement(id, Empleado.class);
    }

    /**
     * Dado el identificador de una provincia, obtiene dicha provincia de la
     * base de datos.
     *
     * @param id Identificador de la provincia.
     * @return La provincia que corresponde al identificador (o null si no existe).
     */
    public Provincia getProvincia(Long id) {
        return getElement(id, Provincia.class);
    }

    /**
     * Dado el identificador de una relación entre un producto y una tienda,
     * obtiene dicha relación de la base de datos.
     *
     * @param id Identificador de la relación entre producto y tienda.
     * @return La relación entre el producto y la tienda (o null si no existe).
     */
    public ProductosTiendas getRelProductoTienda(ProductoTiendaID id) {
        return getElement(id, ProductosTiendas.class);
    }

    /**
     * Dado el identificador de una relación entre un empleado y una tienda,
     * obtiene dicha relación de la base de datos.
     *
     * @param id Identificador de la relación entre empleado y tienda.
     * @return La relación entre el empleado y la tienda (o null si no existe).
     */
    public EmpleadosTiendas getRelEmpleadoTienda(EmpleadoTiendaID id) {
        return getElement(id, EmpleadosTiendas.class);
    }

    /**
     * Obtiene una lista con todas las tiendas de la base de datos.
     *
     * @return Lista de tiendas.
     */
    public List<Tienda> getTiendas() {
        return getElements(
                "from Tienda order by nombre, provincia.nome, ciudad",
                Tienda.class);
    }

    /**
     * Obtiene una lista con todos los productos de la base de datos.
     *
     * @return Lista de productos.
     */
    public List<Producto> getProductos() {
        return getElements(
                "from Producto order by nombre",
                Producto.class);
    }

    /**
     * Obtiene una lista con todos los clientes de la base de datos.
     *
     * @return Lista de clientes.
     */
    public List<Cliente> getClientes() {
        return getElements(
                "from Cliente order by nombre, apellidos",
                Cliente.class);
    }

    /**
     * Obtiene una lista con todos los empleados de la base de datos.
     *
     * @return Lista de empleados.
     */
    public List<Empleado> getEmpleados() {
        return getElements(
                "from Empleado order by nombre, apellidos",
                Empleado.class);
    }

    /**
     * Obtiene una lista con todas las provincias de la base de datos.
     *
     * @return Lista de provincias.
     */
    public List<Provincia> getProvincias() {
        return getElements(
                "from Provincia order by nome",
                Provincia.class);
    }
}
