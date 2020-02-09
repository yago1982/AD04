/*
 * NOTA IMPORTANTE: las clases del paquete com.ymourino.ad04.utils son parte de un proyecto personal todavía en desarrollo.
 * Hay código que se deriva del diseño previo realizado para dicho proyecto, pero que todavía no tiene una utilidad real
 * en este momento.
 */
package com.ymourino.ad04.utils;

import java.util.LinkedHashMap;

/**
 * <p>Clase encargada de interactuar con el usuario mediante una línea de comandos muy básica.</p>
 */
public class CommandPrompt {
    private LinkedHashMap<String, Command> commands;
    private boolean customHelp;
    private boolean customQuit;
    private String prompt;

    /**
     * <p>Constructor de la clase. Es privado porque esta clase utiliza <b>method chaining</b> para trabajar.</p></p>
     */
    private CommandPrompt() {
        commands = new LinkedHashMap<>();
        customHelp = false;
        customQuit = false;
        prompt = null;
        /*commands.put("quit",
                Command.withName("quit").withDescription("Finaliza el programa").withMethod(() -> System.exit(0)));*/
    }

    /**
     * <p>Constructor de la clase. Recibe los comandos que podrá ejecutar el programa y los almacena.</p>
     *
     * @param commands Un número indeterminado de objetos de la clase Command que serán los comandos que el usuario
     *                 podrá ejecutar mediante órdenes en la línea de comandos.
     * @return Objeto de la clase CommandPrompt.
     * @throws IllegalArgumentException Se produce una excepción si se intentan almacenar dos comandos con el mismo nombre.
     */
    public static CommandPrompt withCommands(Command... commands)
            throws IllegalArgumentException {
        CommandPrompt commandPrompt = new CommandPrompt();

        for (Command command : commands) {
            if (!commandPrompt.commands.containsKey(command.name())) {
                commandPrompt.commands.put(command.name(), command);
            } else {
                if (!commandPrompt.customHelp && command.name().equals("help")) {
                    commandPrompt.commands.replace(command.name(), command);
                    commandPrompt.customHelp = true;
                } else if (!commandPrompt.customQuit && command.name().equals("quit")) {
                    commandPrompt.commands.replace(command.name(), command);
                    commandPrompt.customQuit = true;
                } else {
                    throw new IllegalArgumentException(
                            "El comando '" + command.name() + "' ya existe.");
                }
            }
        }

        return commandPrompt;
    }

    /**
     * <p>Ejecuta un comando previamente introducido por el usuario en el teclado.</p>
     *
     * @param userInput Comando a ejecutar junto con cualesquiera parámetros adicionales.
     * @throws IllegalArgumentException Se produce una excepción si el comando que se ha introducido no es correcto.
     */
    private void executeCommand(String[] userInput)
            throws IllegalArgumentException {
        String command = userInput[0];
        if (commands.containsKey(command)) {
            commands.get(command).execute(userInput);
        } else {
            throw new IllegalArgumentException(
                    "Comando '" + command + "' no reconocido.");
        }
    }

    /**
     * <p>Solicita un comando al usuario y usa el método <b>executeCommand</b> para ejecutarlo.</p>
     *
     * @param prompt Cadena a mostrar al usuario como prompt.
     * @param messageForErrors Mensaje a mostrar en caso de error en la introducción del comando.
     * @throws IllegalArgumentException Si la ejecución del comando produce alguna excepción.
     */
    private void executeCommandFromUser(String prompt,
                                        String messageForErrors)
            throws IllegalArgumentException {
        executeCommand(getCommandFromUser(prompt, messageForErrors));
    }

    /**
     * <p>Lee la entrada del usuario, la separa según los espacios existentes, y comprueba el comando que se
     * quiere ejecutar. Si el comando no existe se muestra un error, y en caso contrario se devuelve la
     * matriz con el comando y sus parámetros.</p>
     *
     * @param prompt Cadena a mostrar al usuario como prompt.
     * @param messageForErrors Mensaje a mostrar en caso de error en la introducción de datos.
     * @return Matriz con el comando y sus parámetros.
     */
    private String[] getCommandFromUser(String prompt, String messageForErrors) {
        do {
            String[] userInput = KeyboardReader.readString(prompt, messageForErrors, true)
                    .split(" ");
            String command = userInput[0];
            if (commands.containsKey(command)) {
                return userInput;
            } else {
                System.out.println("Comando '" + command + "' no reconocido.");
            }
        } while (true);
    }

    /**
     * <p>Entra en un bucle infinito que pide constantemente comandos al usuario y los va ejecutando. El programa solo
     * terminará si alguno de dichos comandos provoca la salida.</p>
     */
    public void run() {
        while (true) {
            System.out.println();
            System.out.println();
            System.out.println("MENU");
            System.out.println("====");
            System.out.println();

            for (String k : commands.keySet()) {
                System.out.println(commands.get(k).name() + ".\t" + commands.get(k).description());

                if (commands.get(k).hasSeparator()) {
                    System.out.println();
                }
            }

            System.out.println();
            executeCommandFromUser(this.prompt, "ERROR");
        }
    }

    /**
     * <p>Establece la cadena que se mostrará al usuario como prompt.</p>
     *
     * @param prompt Cadena a mostrar al usuario como prompt.
     * @return Se devuelve a sí mismo, implementando el patrón de diseño "method chaining".
     */
    public CommandPrompt withPrompt(String prompt) {
        this.prompt = prompt;
        return this;
    }
}
