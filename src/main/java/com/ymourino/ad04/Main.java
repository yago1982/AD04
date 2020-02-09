package com.ymourino.ad04;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.ymourino.ad04.models.*;
import com.ymourino.ad04.utils.Command;
import com.ymourino.ad04.utils.CommandPrompt;
import com.ymourino.ad04.utils.KeyboardReader;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;


public class Main {
    private static Empresa empresa;

    public static void main(String[] args) throws Exception {
        // Se redirige System.err hacia un fichero. De esta forma se eliminan
        // de pantalla todos los mensajes de Hibernate sin perderlos.
        File file = new File("err.log");
        FileOutputStream fos = new FileOutputStream(file, true);
        PrintStream ps = new PrintStream(fos);
        System.setErr(ps);


        try {
            empresa = new Empresa();

            // Si en la base de datos no tenemos ninguna provincia, las obtenemos
            // desde el fichero provincias.json y las almacenamos.
            if (empresa.getProvincias().size() == 0) {
                Gson gson = new Gson();

                try {
                    JsonReader jr = new JsonReader(
                            new InputStreamReader(
                                    Main.class.getResourceAsStream(
                                            "/provincias.json")));
                    Provincias provincias = gson.fromJson(jr, Provincias.class);

                    for (Provincia provincia : provincias.getProvincias()) {
                        empresa.addProvincia(provincia);
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    empresa.close();
                    System.out.println("No se han podido almacenar las provincias en la base de datos");

                    ps.close();
                    fos.close();
                    System.exit(1);
                }
            }

            // Si no hay exactamente 52 provincias (que son las que hay en el
            // fichero provincias.json) el programa termina inmediatamente.
            if (empresa.getProvincias().size() != 52) {
                empresa.close();
                System.out.println("Hay un error con las provincias almacenadas en la base de datos");
                System.exit(1);
            }

            // Se ignora la posibilidad de que exista un número de provincias
            // distinto a 52. A dicha situación se llegaría tras una edición
            // manual de la base de datos, y en ese caso no se debería interferir
            // en el estado de la misma.

            CommandPrompt.withCommands(
                    Command.withName("1") .withDescription("Añadir una tienda")                                         .withMethod(Main::addTienda),
                    Command.withName("2") .withDescription("Eliminar una tienda")                                       .withMethod(Main::delTienda),
                    Command.withName("3") .withDescription("Listar tiendas")                                            .withMethod(Main::listTiendas)
                            .addSeparator(),

                    Command.withName("4") .withDescription("Añadir un producto")                                        .withMethod(Main::addProducto),
                    Command.withName("5") .withDescription("Añadir un producto a una tienda")                           .withMethod(Main::addProductoToTienda),
                    Command.withName("6") .withDescription("Eliminar un producto")                                      .withMethod(Main::delProducto),
                    Command.withName("7") .withDescription("Eliminar un producto de una tienda")                        .withMethod(Main::delProductoFromTienda),
                    Command.withName("8") .withDescription("Actualizar el stock de un producto en una tienda")          .withMethod(Main::updateStock),
                    Command.withName("9") .withDescription("Listar productos")                                          .withMethod(Main::listProductos),
                    Command.withName("10").withDescription("Listar productos de una tienda")                            .withMethod(Main::listProductosFromTienda),
                    Command.withName("11").withDescription("Listar tiendas en las que se vende un producto")            .withMethod(Main::listTiendasWithProducto),
                    Command.withName("12").withDescription("Mostrar el stock de un producto en una tienda")             .withMethod(Main::showStock),
                    Command.withName("13").withDescription("Generar informe de stock por productos")                    .withMethod(Main::reportStockProductos),
                    Command.withName("14").withDescription("Generar informe de stock por tiendas")                      .withMethod(Main::reportStockTiendas),
                    Command.withName("15").withDescription("Generar informe de stock global")                           .withMethod(Main::reportStockGlobal)
                            .addSeparator(),

                    Command.withName("16").withDescription("Añadir un cliente")                                         .withMethod(Main::addCliente),
                    Command.withName("17").withDescription("Eliminar un cliente")                                       .withMethod(Main::delCliente),
                    Command.withName("18").withDescription("Listar clientes")                                           .withMethod(Main::listClientes)
                            .addSeparator(),

                    Command.withName("19").withDescription("Añadir un empleado")                                        .withMethod(Main::addEmpleado),
                    Command.withName("20").withDescription("Añadir un empleado a una tienda")                           .withMethod(Main::addEmpleadoToTienda),
                    Command.withName("21").withDescription("Eliminar un empleado")                                      .withMethod(Main::delEmpleado),
                    Command.withName("22").withDescription("Eliminar un empleado de una tienda")                        .withMethod(Main::delEmpleadoFromTienda),
                    Command.withName("23").withDescription("Actualizar la jornada semanal de un empleado en una tienda").withMethod(Main::updateJornada),
                    Command.withName("24").withDescription("Listar empleados")                                          .withMethod(Main::listEmpleados),
                    Command.withName("25").withDescription("Listar empleados de una tienda")                            .withMethod(Main::listEmpleadosFromTienda),
                    Command.withName("26").withDescription("Listar tiendas en las que trabaja un empleado")             .withMethod(Main::listTiendasWithEmpleado),
                    Command.withName("27").withDescription("Mostrar la jornada semanal de un empleado en una tienda")   .withMethod(Main::showJornada)
                            .addSeparator(),

                    Command.withName("28").withDescription("Leer los titulares de El País")                             .withMethod(Main::titulares),
                    Command.withName("29").withDescription("Salir del programa")                                        .withMethod(() -> {
                        empresa.close();

                        System.out.println();
                        System.exit(0);
                    })
            )
                    .withPrompt("[AD04] >> ")
                    .run();
        } catch (Exception e) {
            if (empresa != null) {
                empresa.close();
            }

            e.printStackTrace();
            System.out.println("EXCEPCIÓN: " + e.getClass().getName());
            System.out.println("Revise el registro de la aplicación");
        } finally {
            ps.close();
            fos.close();
        }
    }


    /* TIENDAS */

    /**
     * Solicita al usuario los datos de una nueva tienda y la añade a la base
     * de datos.
     */
    private static void addTienda() {
        System.out.println();

        String nombre = KeyboardReader.readString(
                "Introduzca el nombre de la tienda: ",
                "Error con el nombre introducido",
                false);
        String ciudad = KeyboardReader.readString(
                "Introduzca la ciudad de la tienda: ",
                "Error con la ciudad introducida",
                false);
        Provincia provincia = selectProvincia();

        if (provincia != null) {
            if (confirmarSiNo("¿Guardar los datos? (S/N): ")) {
                System.out.println();

                if (empresa.addTienda(nombre, ciudad, provincia)) {
                    System.out.println("Tienda añadida correctamente");
                } else {
                    System.out.println("Error al añadir la tienda");
                    System.out.println("Revise el registro de la aplicación");
                }
            }
        }
    }

    /**
     * Solicita al usuario un cliente y lo elimina de la base de datos.
     */
    private static void delTienda() {
        System.out.println();

        Tienda tienda = selectTienda();

        if (tienda != null && confirmarSiNo(
                "¿Realmente quiere eliminar la tienda? (S/N): ")) {
            System.out.println();

            if (empresa.delTienda(tienda)) {
                System.out.println("Tienda eliminada correctamente");
            } else {
                System.out.println("Error al eliminar la tienda");
                System.out.println("Revise el registro de la aplicación");
            }
        }
    }

    /**
     * Muestra un listado con todas las tiendas de la base de datos.
     */
    private static void listTiendas() {
        System.out.println();

        List<Tienda> tiendas = empresa.getTiendas();

        if (tiendas.size() > 0) {
            showList(tiendas);
        } else {
            System.out.println("No hay tiendas en la base de datos");
        }
    }


    /* PRODUCTOS */

    /**
     * Solicita al usuario los datos de un nuevo producto y lo añade a la base
     * de datos.
     */
    private static void addProducto() {
        System.out.println();

        String nombre = KeyboardReader.readString(
                "Introduzca el nombre del producto: ",
                "Error con el nombre introducido",
                false);
        String descripcion = KeyboardReader.readString(
                "Introduzca la descripción del producto: ",
                "Error con la descripción introducida",
                false);
        float precio = KeyboardReader.readFloat(
                "Introduzca el precio del producto: ",
                "Error con el precio introducido");

        if (confirmarSiNo("¿Guardar los datos? (S/N): ")) {
            System.out.println();

            if (empresa.addProducto(nombre, descripcion, precio)) {
                System.out.println("Producto añadido correctamente");
            } else {
                System.out.println("Error al añadir el producto");
                System.out.println("Revise el registro de la aplicación");
            }
        }
    }

    /**
     * Añade un producto al stock de una tienda tras solicitar al usuario los
     * datos adecuados.
     */
    private static void addProductoToTienda() {
        System.out.println();

        Producto producto = selectProducto();

        if (producto != null) {
            Tienda tienda = selectTienda();

            if (tienda != null) {
                Long stock = KeyboardReader.readLong(
                        "Introduzca el stock inicial del producto: ",
                        "Error con la cantidad introducida");

                if (confirmarSiNo("¿Guardar los datos? (S/N): ")) {
                    System.out.println();

                    if (empresa.addProductoToTienda(producto, tienda, stock)) {
                        System.out.println("Producto añadido a la tienda correctamente");
                    } else {
                        System.out.println("Error al añadir el producto a la tienda");
                        System.out.println("Revise el registro de la aplicación");
                    }
                }
            }
        }
    }

    /**
     * Solicita al usuario un producto y lo elimina de la base de datos.
     */
    private static void delProducto() {
        System.out.println();

        Producto producto = selectProducto();

        if (producto != null && confirmarSiNo(
                "¿Realmente quiere eliminar el producto? (S/N): ")) {
            System.out.println();

            if (empresa.delProducto(producto)) {
                System.out.println("Producto eliminado correctamente");
            } else {
                System.out.println("Error al eliminar el producto");
                System.out.println("Revise el registro de la aplicación");
            }
        }
    }

    /**
     * Solicita una tienda al usuario y permite eliminar uno de sus productos.
     */
    private static void delProductoFromTienda() {
        System.out.println();

        Tienda tienda = selectTienda();

        if (tienda != null) {
            ProductosTiendas relProductoTienda = selectProductoFromTienda(tienda, true);

            if (relProductoTienda != null && confirmarSiNo(
                    "¿Realmente quiere eliminar el producto? (S/N): ")) {
                System.out.println();

                if (empresa.delProductoFromTienda(relProductoTienda)) {
                    // Si se ha eliminado la relación de la base de datos, se
                    // tiene que eliminar también el producto de la tienda.
                    tienda.delProducto(relProductoTienda.getProducto());
                    System.out.println("Producto eliminado de la tienda correctamente");
                } else {
                    System.out.println("Error al eliminar el producto");
                    System.out.println("Revise el registro de la aplicación");
                }
            }
        }
    }

    /**
     * Solicita una tienda al usuario y permite actualizar el stock de uno de
     * sus productos.
     */
    private static void updateStock() {
        System.out.println();

        Tienda tienda = selectTienda();

        if (tienda != null) {
            ProductosTiendas relProductoTienda = selectProductoFromTienda(tienda, true);

            if (relProductoTienda != null) {
                Long newStock = KeyboardReader.readLong(
                        "Introduzca el nuevo stock del producto: ",
                        "Error con el stock introducido");

                if (confirmarSiNo("¿Guardar los datos? (S/N): ")) {
                    System.out.println();

                    if (empresa.updateStock(relProductoTienda, newStock)) {
                        System.out.println("Stock actualizado correctamente");
                    } else {
                        System.out.println("Error al actualizar el stock");
                        System.out.println("Revise el registro de la aplicación");
                    }
                }
            }
        }
    }

    /**
     * Muestra un listado con todos los productos de la base de datos.
     */
    private static void listProductos() {
        System.out.println();

        List<Producto> productos = empresa.getProductos();

        if (productos.size() > 0) {
            showList(productos);
        } else {
            System.out.println("No hay productos en la base de datos");
        }
    }

    /**
     * Muestra un listado con todos los productos de una tienda seleccionada.
     */
    private static void listProductosFromTienda() {
        System.out.println();

        Tienda tienda = selectTienda();
        System.out.println();

        if (tienda != null) {
            List<ProductosTiendas> relsTiendaProductos = tienda.getProductos();

            if (relsTiendaProductos.size() > 0) {
                for (ProductosTiendas rel : relsTiendaProductos) {
                    Producto producto = rel.getProducto();

                    System.out.println(producto.getId() + ". "
                            + producto.getNombre()
                            + " - " + producto.getDescripcion()
                            + " - " + producto.getPrecio() + "€"
                            + " (stock: " + rel.getStock() + ")"
                    );
                }
            } else {
                System.out.println("No hay productos en la tienda seleccionada");
            }
        }
    }

    /**
     * Muestra un listado con todas las tiendas en las que se vende un producto
     * seleccionado.
     */
    private static void listTiendasWithProducto() {
        System.out.println();

        Producto producto = selectProducto();
        System.out.println();

        if (producto != null) {
            List<ProductosTiendas> relsProductoTiendas = producto.getTiendas();

            if (relsProductoTiendas.size() > 0) {
                for (ProductosTiendas rel : relsProductoTiendas) {
                    Tienda tienda = rel.getTienda();

                    System.out.println(tienda.getId() + ". "
                            + tienda.getNombre()
                            + " - " + tienda.getCiudad()
                            + " (" + tienda.getProvincia().getNome() + ")"
                            + " (stock: " + rel.getStock() + ")"
                    );
                }
            } else {
                System.out.println("No hay tiendas en las que se venda el producto seleccionado");
            }
        }
    }

    /**
     * Permite seleccionar una tienda y uno de sus productos y muestra su
     * stock.
     */
    private static void showStock() {
        System.out.println();

        Tienda tienda = selectTienda();

        if (tienda != null) {
            ProductosTiendas relProductoTienda = selectProductoFromTienda(tienda, false);

            if (relProductoTienda != null) {
                Producto producto = relProductoTienda.getProducto();

                System.out.println();
                System.out.println("   Producto: "
                        + producto.getNombre()
                        + " - " + producto.getDescripcion());
                System.out.println("      Stock: "
                        + relProductoTienda.getStock());
            }
        }
    }

    /**
     * Permite al usuario indicar el nombre de un fichero y genera un informe del
     * stock de productos (para un producto, se muestra su stock en cada tienda)
     * en dicho fichero en formato JSON.
     */
    private static void reportStockProductos() {
        System.out.println();

        String filename = KeyboardReader.readString(
                "Introduzca el nombre del fichero donde generar el informe: ",
                "Error con el nombre del fichero introducido",
                false);

        if (Files.exists(Paths.get(filename)) && !confirmarSiNo(
                "El fichero ya existe, ¿desea sobreescribirlo? (S/N): ")) {
            return;
        }

        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonArray stock = new JsonArray();

            for (Producto p : empresa.getProductos()) {
                JsonObject producto = new JsonObject();
                producto.addProperty("id_producto", p.getId());
                producto.addProperty("nombre_producto", p.getNombre());

                JsonArray tiendas = new JsonArray();

                for (ProductosTiendas t : p.getTiendas()) {
                    JsonObject tienda = new JsonObject();
                    tienda.addProperty("id_tienda", t.getId().getTienda_id());
                    tienda.addProperty("nombre_tienda", t.getTienda().getNombre());
                    tienda.addProperty("stock", t.getStock());

                    tiendas.add(tienda);
                }

                producto.add("stock_tiendas", tiendas);
                stock.add(producto);
            }

            FileWriter outputFile = new FileWriter(filename);
            gson.toJson(stock, outputFile);
            outputFile.close();

            System.out.println();
            System.out.println("El informe de stock se ha generado satisfactoriamente");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println();
            System.out.println("No se ha podido generar el informe de stock");
        }
    }

    /**
     * Permite al usuario indicar el nombre de un fichero y genera un informe del
     * stock de las tiendas (para cada tienda, se muestra el stock de cada producto)
     * en dicho fichero en formato JSON.
     */
    private static void reportStockTiendas() {
        System.out.println();

        String filename = KeyboardReader.readString(
                "Introduzca el nombre del fichero donde generar el informe: ",
                "Error con el nombre del fichero introducido",
                false);

        if (Files.exists(Paths.get(filename)) && !confirmarSiNo(
                "El fichero ya existe, ¿desea sobreescribirlo? (S/N): ")) {
            return;
        }

        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonArray stock = new JsonArray();

            for (Tienda t : empresa.getTiendas()) {
                JsonObject tienda = new JsonObject();
                tienda.addProperty("id_tienda", t.getId());
                tienda.addProperty("nombre_tienda", t.getNombre());

                JsonArray productos = new JsonArray();

                for (ProductosTiendas p : t.getProductos()) {
                    JsonObject producto = new JsonObject();
                    producto.addProperty("id_producto", p.getId().getProducto_id());
                    producto.addProperty("nombre_producto", p.getProducto().getNombre());
                    producto.addProperty("stock", p.getStock());

                    productos.add(producto);
                }

                tienda.add("stock_productos", productos);
                stock.add(tienda);
            }

            FileWriter outputFile = new FileWriter(filename);
            gson.toJson(stock, outputFile);
            outputFile.close();

            System.out.println();
            System.out.println("El informe de stock se ha generado satisfactoriamente");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println();
            System.out.println("No se ha podido generar el informe de stock");
        }
    }

    /**
     * Permite al usuario indicar el nombre de un fichero y genera un informe del
     * stock de productos de manera global, sumando para cada producto su stock
     * en cada tienda y mostrando el total en el informe.
     */
    private static void reportStockGlobal() {
        System.out.println();

        String filename = KeyboardReader.readString(
                "Introduzca el nombre del fichero donde generar el informe: ",
                "Error con el nombre del fichero introducido",
                false);

        if (Files.exists(Paths.get(filename)) && !confirmarSiNo(
                "El fichero ya existe, ¿desea sobreescribirlo? (S/N): ")) {
            return;
        }

        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonArray stock = new JsonArray();

            for (Producto p : empresa.getProductos()) {
                JsonObject producto = new JsonObject();
                producto.addProperty("id_producto", p.getId());
                producto.addProperty("nombre_producto", p.getNombre());

                int stockTotal = 0;

                for (ProductosTiendas t : p.getTiendas()) {
                    stockTotal += t.getStock();
                }

                producto.addProperty("stock_total", stockTotal);
                stock.add(producto);
            }

            FileWriter outputFile = new FileWriter(filename);
            gson.toJson(stock, outputFile);
            outputFile.close();

            System.out.println();
            System.out.println("El informe de stock se ha generado satisfactoriamente");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println();
            System.out.println("No se ha podido generar el informe de stock");
        }
    }


    /* CLIENTES */

    /**
     * Solicita al usuario los datos de un nuevo cliente y lo añade a la base
     * de datos.
     */
    private static void addCliente() {
        System.out.println();

        String nombre = KeyboardReader.readString(
                "Introduzca el nombre del cliente: ",
                "Error con el nombre introducido",
                false);
        String apellidos = KeyboardReader.readString(
                "Introduzca los apellidos del cliente: ",
                "Error con los apellidos introducidos",
                false);
        String mail = KeyboardReader.readString(
                "Introduzca el correo electrónico del cliente: ",
                "Error con el correo electrónico introducido",
                false);

        if (confirmarSiNo("¿Guardar los datos? (S/N): ")) {
            System.out.println();

            if (empresa.addCliente(nombre, apellidos, mail)) {
                System.out.println("Cliente añadido correctamente");
            } else {
                System.out.println("Error al añadir el cliente");
                System.out.println("Revise el registro de la aplicación");
            }
        }
    }

    /**
     * Solicita al usuario un cliente y lo elimina de la base de datos.
     */
    private static void delCliente() {
        System.out.println();

        Cliente cliente = selectCliente();

        if (cliente != null && confirmarSiNo(
                "¿Realmente quiere eliminar el cliente? (S/N): ")) {
            System.out.println();
            if (empresa.delCliente(cliente)) {
                System.out.println("Cliente eliminado correctamente");
            } else {
                System.out.println("Error al eliminar el cliente");
                System.out.println("Revise el registro de la aplicación");
            }
        }
    }

    /**
     * Muestra un listado con todos los clientes de la base de datos.
     */
    private static void listClientes() {
        System.out.println();

        List<Cliente> clientes = empresa.getClientes();

        if (clientes.size() > 0) {
            showList(clientes);
        } else {
            System.out.println("No hay clientes en la base de datos");
        }
    }


    /* EMPLEADOS */

    /**
     * Solicita al usuario los datos de un nuevo empleado y lo añade a la base
     * de datos.
     */
    private static void addEmpleado() {
        System.out.println();

        String nombre = KeyboardReader.readString(
                "Introduzca el nombre del empleado: ",
                "Error con el nombre introducido",
                false);
        String apellidos = KeyboardReader.readString(
                "Introduzca los apellidos del empleado: ",
                "Error con los apellidos introducidos",
                false);

        if (confirmarSiNo("¿Guardar los datos? (S/N): ")) {
            System.out.println();

            if (empresa.addEmpleado(nombre, apellidos)) {
                System.out.println("Empleado añadido correctamente");
            } else {
                System.out.println("Error al añadir el empleado");
                System.out.println("Revise el registro de la aplicación");
            }
        }
    }

    /**
     * Añade un producto al stock de una tienda tras solicitar al usuario los
     * datos adecuados.
     */
    private static void addEmpleadoToTienda() {
        System.out.println();

        Empleado empleado = selectEmpleado();

        if (empleado != null) {
            Tienda tienda = selectTienda();

            if (tienda != null) {
                Float horas = KeyboardReader.readFloat(
                        "Introduzca el número de horas semanales del empleado en la tienda: ",
                        "Error con las horas introducidas");

                if (confirmarSiNo("¿Guardar los datos? (S/N): ")) {
                    System.out.println();

                    if (empresa.addEmpleadoToTienda(empleado, tienda, horas)) {
                        System.out.println("Empleado añadido a la tienda correctamente");
                    } else {
                        System.out.println("Error al añadir el empleado a la tienda");
                        System.out.println("Revise el registro de la aplicación");
                    }
                }
            }
        }
    }

    /**
     * Solicita al usuario un cliente y lo elimina de la base de datos.
     */
    private static void delEmpleado() {
        System.out.println();

        Empleado empleado = selectEmpleado();

        if (empleado != null && confirmarSiNo(
                "¿Realmente quiere eliminar el empleado? (S/N): ")) {
            System.out.println();
            if (empresa.delEmpleado(empleado)) {
                System.out.println("Empleado eliminado correctamente");
            } else {
                System.out.println("Error al eliminar el empleado");
                System.out.println("Revise el registro de la aplicación");
            }
        }
    }

    /**
     * Solicita una tienda al usuario y permite eliminar uno de sus empleados.
     */
    private static void delEmpleadoFromTienda() {
        System.out.println();

        Tienda tienda = selectTienda();

        if (tienda != null) {
            EmpleadosTiendas relEmpleadoTienda = selectEmpleadoFromTienda(tienda, true);

            if (relEmpleadoTienda != null && confirmarSiNo(
                    "¿Realmente quiere eliminar el empleado? (S/N): ")) {
                System.out.println();

                if (empresa.delEmpleadoFromTienda(relEmpleadoTienda)) {
                    // Si se ha eliminado la relación de la base de datos, se
                    // tiene que eliminar también de la propia tienda.
                    tienda.delEmpleado(relEmpleadoTienda.getEmpleado());
                    System.out.println("Empleado eliminado de la tienda correctamente");
                } else {
                    System.out.println("Error al eliminar el empleado");
                    System.out.println("Revise el registro de la aplicación");
                }
            }
        }
    }

    /**
     * Solicita una tienda al usuario y permite actualizar la jornada de uno de
     * sus empleados.
     */
    private static void updateJornada() {
        System.out.println();

        Tienda tienda = selectTienda();

        if (tienda != null) {
            EmpleadosTiendas relEmpleadoTienda = selectEmpleadoFromTienda(tienda, true);

            if (relEmpleadoTienda != null) {
                Float newJornada = KeyboardReader.readFloat(
                        "Introduzca la nueva jornada del empleado: ",
                        "Error con la jornada introducido");

                if (confirmarSiNo("¿Guardar los datos? (S/N): ")) {
                    System.out.println();

                    if (empresa.updateJornada(relEmpleadoTienda, newJornada)) {
                        System.out.println("Jornada actualizada correctamente");
                    } else {
                        System.out.println("Error al actualizar la jornada");
                        System.out.println("Revise el registro de la aplicación");
                    }
                }
            }
        }
    }

    /**
     * Muestra un listado con todos los empleados de la base de datos.
     */
    private static void listEmpleados() {
        System.out.println();

        List<Empleado> empleados = empresa.getEmpleados();

        if (empleados.size() > 0) {
            showList(empleados);
        } else {
            System.out.println("No hay empleados en la base de datos");
        }
    }

    /**
     * Muestra un listado con todos los empleados de una tienda seleccionada.
     */
    private static void listEmpleadosFromTienda() {
        System.out.println();

        Tienda tienda = selectTienda();
        System.out.println();

        if (tienda != null) {
            List<EmpleadosTiendas> empleados = tienda.getEmpleados();

            if (empleados.size() > 0) {
                for (EmpleadosTiendas e : empleados) {
                    Empleado empleado = e.getEmpleado();

                    System.out.println(empleado.getId() + ". "
                            + empleado.getNombreCompleto()
                            + " (jornada: " + e.getJornada() + ")"
                    );
                }
            } else {
                System.out.println("No hay empleados en la tienda seleccionada");
            }
        }
    }

    /**
     * Muestra un listado con todas las tiendas en las que se vende un producto
     * seleccionado.
     */
    private static void listTiendasWithEmpleado() {
        System.out.println();

        Empleado empleado = selectEmpleado();
        System.out.println();

        if (empleado != null) {
            List<EmpleadosTiendas> relsEmpleadoTiendas = empleado.getTiendas();

            if (relsEmpleadoTiendas.size() > 0) {
                for (EmpleadosTiendas rel : relsEmpleadoTiendas) {
                    Tienda tienda = rel.getTienda();

                    System.out.println(tienda.getId() + ". "
                            + tienda.getNombre()
                            + " - " + tienda.getCiudad()
                            + " (" + tienda.getProvincia().getNome() + ")"
                            + " (jornada: " + rel.getJornada() + ")"
                    );
                }
            } else {
                System.out.println("No hay tiendas en las que trabaje el empleado seleccionado");
            }
        }
    }

    /**
     * Permite seleccionar una tienda y uno de sus empleados y muestra su
     * jornada.
     */
    private static void showJornada() {
        System.out.println();

        Tienda tienda = selectTienda();

        if (tienda != null) {
            EmpleadosTiendas relEmpleadoTienda = selectEmpleadoFromTienda(tienda, false);

            if (relEmpleadoTienda != null) {
                Empleado empleado = relEmpleadoTienda.getEmpleado();

                System.out.println();
                System.out.println("   Empleado: "
                        + empleado.getNombreCompleto());
                System.out.println("    Jornada: "
                        + relEmpleadoTienda.getJornada());
            }
        }
    }


    /**
     * Descarga el contenido del feed RSS de El País a una cadena de texto.
     * Usando la librería Sax, se analiza dicha cadena y se muestran los
     * titulares de las noticias.
     */
    private static void titulares() {
        // En primer lugar se descarga el contenido del feed RSS.
        try (Scanner scanner = new Scanner(new URL("http://ep00.epimg.net/rss/elpais/portada.xml").openStream(),
                StandardCharsets.UTF_8.toString())) {
            scanner.useDelimiter("\\A");
            String noticiasXML = scanner.hasNext() ? scanner.next() : "";

            // Si se ha obtenido una cadena no vacía...
            if (!noticiasXML.equals("")) {
                SAXParserFactory factory = SAXParserFactory.newInstance();

                try {
                    SAXParser parser = factory.newSAXParser();

                    // El handler se encargará de buscar todos los elementos de tipo "title" que se encuentren.
                    DefaultHandler handler = new DefaultHandler() {
                        boolean title = false;

                        public void startElement(String uri, String localName, String qName, Attributes attributes) {
                            if (qName.equalsIgnoreCase("title")) {
                                title = true;
                            }
                        }

                        public void characters(char[] ch, int start, int length) {
                            if (title) {
                                String title = new String(ch, start, length);
                                System.out.println(title);
                                this.title = false;
                            }
                        }
                    };

                    // Parseamos la cadena del feed y el handler se encargará de mostrar el contenido de los titulares.
                    parser.parse(new InputSource(new StringReader(noticiasXML)), handler);
                } catch (SAXException | ParserConfigurationException e) {
                    System.err.println(e.toString());
                }
            } else {
                System.out.println();
                System.out.println("No se han podido obtener los titulares");
            }
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }


    /* MÉTODOS AUXILIARES */

    /**
     * Permite al usuario seleccionar una tienda existente de la base de datos.
     *
     * @return La tienda seleccionada (o null si no se selecciona ninguna).
     */
    private static Tienda selectTienda() {
        if (empresa.getTiendas().size() > 0) {
            while (true) {
                String idTienda = KeyboardReader.readString(
                        "Introduzca el número de la tienda (L para listarlas, 0 para salir): ",
                        "Error con los datos introducidos",
                        false);

                if (idTienda.equals("0")) {
                    return null;
                } else if (idTienda.equalsIgnoreCase("L")) {
                    System.out.println();
                    showList(empresa.getTiendas());
                    System.out.println();
                } else {
                    try {
                        Tienda tienda = empresa.getTienda(
                                Long.parseLong(idTienda));

                        if (tienda != null) {
                            return tienda;
                        } else {
                            System.out.println("El código introducido no corresponde a ninguna tienda");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Error con los datos introducidos");
                    }
                }
            }
        } else {
            System.out.println("No hay tiendas en la base de datos");
            return null;
        }
    }

    /**
     * Permite al usuario seleccionar un producto existente de la base de datos.
     *
     * @return El producto seleccionado (o null si no se selecciona ninguno).
     */
    private static Producto selectProducto() {
        if (empresa.getProductos().size() > 0) {
            while (true) {
                String idProducto = KeyboardReader.readString(
                        "Introduzca el número del producto (L para listarlos, 0 para salir): ",
                        "Error con los datos introducidos",
                        false);

                if (idProducto.equals("0")) {
                    return null;
                } else if (idProducto.equalsIgnoreCase("L")) {
                    System.out.println();
                    showList(empresa.getProductos());
                    System.out.println();
                } else {
                    try {
                        Producto producto = empresa.getProducto(
                                Long.parseLong(idProducto));

                        if (producto != null) {
                            return producto;
                        } else {
                            System.out.println("El código introducido no corresponde a ningún producto");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Error con los datos introducidos");
                    }
                }
            }
        } else {
            System.out.println("No hay productos en la base de datos");
            return null;
        }
    }

    /**
     * Permite al usuario seleccionar un producto existente en una tienda.
     *
     * @param tienda Tienda de la que seleccionar el producto.
     * @param showStock Permite ocultar el stock de productos en el listado.
     * @return El producto seleccionado (o null si no se selecciona ninguno).
     */
    private static ProductosTiendas selectProductoFromTienda(Tienda tienda, boolean showStock) {
        if (tienda.getProductos().size() > 0) {
            while (true) {
                String idProducto = KeyboardReader.readString(
                        "Introduzca el número del producto (L para listarlos, 0 para salir): ",
                        "Error con los datos introducidos",
                        false);

                if (idProducto.equals("0")) {
                    return null;
                } else if (idProducto.equalsIgnoreCase("L")) {
                    System.out.println();

                    for (ProductosTiendas rel : tienda.getProductos()) {
                        Producto producto = rel.getProducto();

                        System.out.print(producto.getId() + ". "
                                + producto.getNombre()
                                + " - " + producto.getDescripcion()
                                + " - " + producto.getPrecio() + "€");

                        if (showStock) {
                            System.out.println(" (stock: " + rel.getStock() + ")");
                        } else {
                            System.out.println();
                        }
                    }
                    System.out.println();
                } else {
                    try {
                        ProductosTiendas relProductoTienda =
                                empresa.getRelProductoTienda(
                                        new ProductoTiendaID(
                                                empresa.getProducto(
                                                        Long.parseLong(idProducto)),
                                                tienda));

                        if (relProductoTienda != null) {
                            return relProductoTienda;
                        } else {
                            System.out.println("El código introducido no corresponde a ningún producto");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Error con los datos introducidos");
                    }
                }
            }
        } else {
            System.out.println();
            System.out.println("No hay productos en la tienda seleccionada");
            return null;
        }
    }

    /**
     * Permite al usuario seleccionar un cliente existente de la base de datos.
     *
     * @return El cliente seleccionado (o null si no se selecciona ninguno).
     */
    private static Cliente selectCliente() {
        if (empresa.getClientes().size() > 0) {
            while (true) {
                String idCliente = KeyboardReader.readString(
                        "Introduzca el número del cliente (L para listarlos, 0 para salir): ",
                        "Error con los datos introducidos",
                        false);

                if (idCliente.equals("0")) {
                    return null;
                } else if (idCliente.equalsIgnoreCase("L")) {
                    System.out.println();
                    showList(empresa.getClientes());
                    System.out.println();
                } else {
                    try {
                        Cliente cliente = empresa.getCliente(
                                Long.parseLong(idCliente));

                        if (cliente != null) {
                            return cliente;
                        } else {
                            System.out.println("El código introducido no corresponde a ningún cliente");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Error con los datos introducidos");
                    }
                }
            }
        } else {
            System.out.println("No hay clientes en la base de datos");
            return null;
        }
    }

    /**
     * Permite al usuario seleccionar un cliente existente de la base de datos.
     *
     * @return El cliente seleccionado (o null si no se selecciona ninguno).
     */
    private static Empleado selectEmpleado() {
        if (empresa.getEmpleados().size() > 0) {
            while (true) {
                String idEmpleado = KeyboardReader.readString(
                        "Introduzca el número del empleado (L para listarlos, 0 para salir): ",
                        "Error con los datos introducidos",
                        false);

                if (idEmpleado.equals("0")) {
                    return null;
                } else if (idEmpleado.equalsIgnoreCase("L")) {
                    System.out.println();
                    showList(empresa.getEmpleados());
                    System.out.println();
                } else {
                    try {
                        Empleado empleado = empresa.getEmpleado(
                                Long.parseLong(idEmpleado));

                        if (empleado != null) {
                            return empleado;
                        } else {
                            System.out.println("El código introducido no corresponde a ningún empleado");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Error con los datos introducidos");
                    }
                }
            }
        } else {
            System.out.println("No hay empleados en la base de datos");
            return null;
        }
    }

    /**
     * Permite al usuario seleccionar un empleado existente en una tienda.
     *
     * @param tienda Tienda de la que seleccionar el empleado.
     * @param showJornada Permite ocultar la jornada del empleado en el listado.
     * @return El empleado seleccionado (o null si no se selecciona ninguno).
     */
    private static EmpleadosTiendas selectEmpleadoFromTienda(Tienda tienda, boolean showJornada) {
        if (tienda.getEmpleados().size() > 0) {
            while (true) {
                String idEmpleado = KeyboardReader.readString(
                        "Introduzca el número del empleado (L para listarlos, 0 para salir): ",
                        "Error con los datos introducidos",
                        false);

                if (idEmpleado.equals("0")) {
                    return null;
                } else if (idEmpleado.equalsIgnoreCase("L")) {
                    System.out.println();

                    for (EmpleadosTiendas e : tienda.getEmpleados()) {
                        Empleado empleado = e.getEmpleado();

                        System.out.print(empleado.getId() + ". "
                                + empleado.getNombreCompleto());

                        if (showJornada) {
                            System.out.println(" (jornada: " + e.getJornada() + ")");
                        } else {
                            System.out.println();
                        }
                    }
                    System.out.println();
                } else {
                    try {
                        EmpleadosTiendas relEmpleadoTienda =
                                empresa.getRelEmpleadoTienda(
                                        new EmpleadoTiendaID(
                                                empresa.getEmpleado(
                                                        Long.parseLong(idEmpleado)),
                                                tienda));

                        if (relEmpleadoTienda != null) {
                            return relEmpleadoTienda;
                        } else {
                            System.out.println("El código introducido no corresponde a ningún empleado");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Error con los datos introducidos");
                    }
                }
            }
        } else {
            System.out.println();
            System.out.println("No hay empleados en la tienda seleccionada");
            return null;
        }
    }

    /**
     * Permite al usuario seleccionar una provincia existente de la base de datos.
     *
     * @return La provincia seleccionada (o null si no se selecciona ninguna).
     */
    private static Provincia selectProvincia() {
        if (empresa.getProvincias().size() > 0) {
            while (true) {
                String idProvincia = KeyboardReader.readString(
                        "Introduzca el código de la provincia (L para listarlas, 0 para salir): ",
                        "Error con los datos introducidos",
                        false);

                if (idProvincia.equals("0")) {
                    return null;
                } else if (idProvincia.equalsIgnoreCase("L")) {
                    System.out.println();
                    showList(empresa.getProvincias());
                    System.out.println();
                } else {
                    try {
                        Provincia provincia = empresa.getProvincia(
                                Long.parseLong(idProvincia));

                        if (provincia != null) {
                            return provincia;
                        } else {
                            System.out.println("El código introducido no corresponde a ninguna provincia");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Error con los datos introducidos");
                    }
                }
            }
        } else {
            System.out.println("No hay provincias en la base de datos");
            return null;
        }
    }

    /**
     * Muestra una pregunta personalizable y espera una respuesta de sí o no
     * del usuario.
     *
     * @param pregunta La pregunta que se formula al usuario.
     * @return True si el usuario selecciona "S" o "s".
     */
    private static boolean confirmarSiNo(String pregunta) {
        String seguro = KeyboardReader.readPattern(pregunta,
                "Opción no reconocida",
                "[SsNn]");

        if (seguro.equalsIgnoreCase("S")) {
            return true;
        } else {
            System.out.println();
            System.out.println("Operación cancelada");
            return false;
        }
    }

    /**
     * Dada una lista de cualquier tipo de elementos, la imprime en pantalla.
     *
     * @param l Lista de elementos.
     * @param <T> Tipo (genérico) de los elementos.
     */
    private static <T> void showList(List<T> l) {
        for (T t : l) {
            System.out.println(t.toString());
        }
    }
}
