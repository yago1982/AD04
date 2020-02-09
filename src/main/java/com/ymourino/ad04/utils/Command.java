/*
 * NOTA IMPORTANTE: las clases del paquete com.ymourino.ad04.utils son parte de un proyecto personal todavía en desarrollo.
 * Hay código que se deriva del diseño previo realizado para dicho proyecto, pero que todavía no tiene una utilidad real
 * en este momento.
 */
package com.ymourino.ad04.utils;

import java.util.function.Consumer;

/**
 * <p>Clase para guardar los diferentes comandos que forman el menú de la aplicación.</p>
 * <p>Es capaz de recibir un método como parámetro, y ejecutarlo cuando llegue el momento.</p>
 */
public class Command {
    private Consumer consumer;
    private String description;
    private String name;
    private Runnable runnable;
    private boolean needsSeparator;

    /**
     * <p>Constructor de la clase. Es privado porque esta clase utiliza <b>method chaining</b> para trabajar.</p>
     *
     * @param name Cadena que representa el comando, lo que más tarde el usuario tendrá que escribir para ejecutar el método
     *             que almacenemos.
     */
    private Command(String name) {
        this.consumer = null;
        this.name = name;
        this.runnable = null;
        this.needsSeparator = false;
    }

    /**
     * <p>Constructor de la clase. Recibe el nombre del comando y devuelve un nuevo objeto inicializado. Utilizando
     * esta técnica, denominada <b>method chaining</b> podemos inicializar un objeto "de una vez", sin necesidad de
     * sucesivas llamadas a los setters del mismo.</p>
     *
     * @param name Cadena que representa el comando, lo que más tarde el usuario tendrá que escribir para ejecutar el método
     *             que almacenemos.
     * @return Objeto de la clase Command.
     */
    public static Command withName(String name) {
        return new Command(name);
    }

    /**
     * <p>Devuelve la descripción del comando.</p>
     *
     * @return La descripción del comando.
     */
    String description() {
        return description;
    }

    /**
     * <p>Si existe, ejecuta el método asociado a este comando.</p>
     *
     * @param o PARÁMETRO PARA CARACTERÍSTICAS TODAVÍA EN DESARROLLO
     */
    void execute(Object o) {
        if (consumer != null) {
            consumer.accept(o);
        } else if (runnable != null) {
            runnable.run();
        }
    }

    /**
     * <p>Devuelve el nombre del comando.</p>
     *
     * @return El nombre del comando, lo que más tarde el usuario tendrá que escribir para ejecutar el método
     *         que almacenemos.
     */
    String name() {
        return name;
    }

    /**
     * <p>Añade una descripción al comando y se devuelve a sí mismo</p>
     *
     * @param description Descripción del comando
     * @return Se devuelve a sí mismo, implementando el patrón de diseño "method chaining".
     */
    public Command withDescription(String description) {
        this.description = description;

        return this;
    }

    /**
     * <p>Almacena el método que será ejecutado al llamar a este comando.</p>
     *
     * @param consumer Un método lambda que implemente la interfaz Consumer para poder definir qué ejecutará este comando.
     * @return Se devuelve a sí mismo, implementando el patrón de diseño "method chaining".
     * @throws IllegalArgumentException Se produce una excepción si este comando ya tiene un método definido.
     */
    public Command withMethod(Consumer consumer)
            throws IllegalArgumentException {
        if (this.consumer == null && this.runnable == null) {
            this.consumer = consumer;
        } else {
            throw new IllegalArgumentException(
                    "El método para el comando '" + this.name + "' ya está definido.");
        }

        return this;
    }

    /**
     * <p>Almacena el método que será ejecutado al llamar a este comando.</p>
     *
     * @param runnable Un método lambda que implemente la interfaz Runnable para poder definir qué ejecutará este comando.
     * @return Se devuelve a sí mismo, implementando el patrón de diseño "method chaining".
     * @throws IllegalArgumentException Se produce una excepción si este comando ya tiene un método definido.
     */
    public Command withMethod(Runnable runnable)
            throws IllegalArgumentException {
        if (this.consumer == null && this.runnable == null) {
            this.runnable = runnable;
        } else {
            throw new IllegalArgumentException(
                    "El método para el comando '" + this.name + "' ya está definido.");
        }

        return this;
    }

    public Command addSeparator() {
        this.needsSeparator = true;
        return this;
    }

    public boolean hasSeparator() {
        return this.needsSeparator;
    }
}
