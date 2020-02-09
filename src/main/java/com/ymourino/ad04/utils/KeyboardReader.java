/*
 * NOTA IMPORTANTE: las clases del paquete com.ymourino.ad04.utils son parte de un proyecto personal todavía en desarrollo.
 * Hay código que se deriva del diseño previo realizado para dicho proyecto, pero que todavía no tiene una utilidad real
 * en este momento.
 */
package com.ymourino.ad04.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * <p>Clase con métodos estáticos que usan los diferentes métodos de la clase <b>Scanner</b> para obtener del usuario
 * diferentes tipos de datos.</p>
 * <p>Todos los métodos funcionan igual. Reciben una cadena con el texto que se mostrará para que el usuario introduzca
 * un datos, y una cadena con el mensaje para el caso de producirse algún error. Siguen pidiendo la entrada del usuario
 * hasta que sea correcta, y la devuelve cuando lo es.</p>
 */
public class KeyboardReader {
    private static final int DEFAULT_RADIX = 10;

    public static BigDecimal readBigDecimal(String prompt, String messageForErrors) {
        Scanner input = new Scanner(System.in);
        System.out.print(prompt);

        while (!input.hasNextBigDecimal()) {
            System.out.println(messageForErrors);
            System.out.print(prompt);
            input.next();
        }

        return input.nextBigDecimal();
    }

    public static BigInteger readBigInteger(String prompt, String messageForErrors) {
        return readBigInteger(prompt, messageForErrors, DEFAULT_RADIX);
    }

    public static BigInteger readBigInteger(String prompt, String messageForErrors, int radix) {
        Scanner input = new Scanner(System.in);
        System.out.print(prompt);

        while (!input.hasNextBigInteger(radix)) {
            System.out.println(messageForErrors);
            System.out.print(prompt);
            input.next();
        }

        return input.nextBigInteger(radix);
    }

    public static boolean readBoolean(String prompt, String messageForErrors) {
        Scanner input = new Scanner(System.in);
        System.out.print(prompt);

        while (!input.hasNextBoolean()) {
            System.out.println(messageForErrors);
            System.out.print(prompt);
            input.next();
        }

        return input.nextBoolean();
    }

    public static byte readByte(String prompt, String messageForErrors) {
        return readByte(prompt, messageForErrors, DEFAULT_RADIX);
    }

    public static byte readByte(String prompt, String messageForErrors, int radix) {
        Scanner input = new Scanner(System.in);
        System.out.print(prompt);

        while (!input.hasNextByte(radix)) {
            System.out.println(messageForErrors);
            System.out.print(prompt);
            input.next();
        }

        return input.nextByte(radix);
    }

    public static double readDouble(String prompt, String messageForErrors) {
        Scanner input = new Scanner(System.in);
        System.out.print(prompt);

        while (!input.hasNextDouble()) {
            System.out.println(messageForErrors);
            System.out.print(prompt);
            input.next();
        }

        return input.nextDouble();
    }

    public static float readFloat(String prompt, String messageForErrors) {
        Scanner input = new Scanner(System.in);
        System.out.print(prompt);

        while (!input.hasNextFloat()) {
            System.out.println(messageForErrors);
            System.out.print(prompt);
            input.next();
        }

        return input.nextFloat();
    }

    public static int readInt(String prompt, String messageForErrors) {
        return readInt(prompt, messageForErrors, DEFAULT_RADIX);
    }

    public static int readInt(String prompt, String messageForErrors, int radix) {
        Scanner input = new Scanner(System.in);
        System.out.print(prompt);

        while (!input.hasNextInt(radix)) {
            System.out.println(messageForErrors);
            System.out.print(prompt);
            input.next();
        }

        return input.nextInt(radix);
    }

    public static String readString(String prompt, String messageForErrors, boolean allowEmpty) {
        Scanner input = new Scanner(System.in);
        System.out.print(prompt);

        while (true) {
            while (!input.hasNextLine()) {
                System.out.println(messageForErrors);
                System.out.print(prompt);
                input.next();
            }

            String toret = input.nextLine();

            if (!toret.isBlank() || allowEmpty) {
                return toret;
            } else {
                System.out.println("No se permite un campo vacío");
                System.out.print(prompt);
            }
        }
    }

    public static long readLong(String prompt, String messageForErrors) {
        return readLong(prompt, messageForErrors, DEFAULT_RADIX);
    }

    public static long readLong(String prompt, String messageForErrors, int radix) {
        Scanner input = new Scanner(System.in);
        System.out.print(prompt);

        while (!input.hasNextLong(radix)) {
            System.out.println(messageForErrors);
            System.out.print(prompt);
            input.next();
        }

        return input.nextLong(radix);
    }

    public static String readPattern(String prompt, String messageForErrors, String pattern) {
        return readPattern(prompt, messageForErrors, Pattern.compile(pattern));
    }

    public static String readPattern(String prompt, String messageForErrors, Pattern pattern) {
        Scanner input = new Scanner(System.in);
        System.out.print(prompt);

        while (!input.hasNext(pattern)) {
            System.out.println(messageForErrors);
            System.out.print(prompt);
            input.next();
        }

        return input.next(pattern);
    }

    public static short readShort(String prompt, String messageForErrors) {
        return readShort(prompt, messageForErrors, DEFAULT_RADIX);
    }

    public static short readShort(String prompt, String messageForErrors, int radix) {
        Scanner input = new Scanner(System.in);
        System.out.print(prompt);

        while (!input.hasNextShort(radix)) {
            System.out.println(messageForErrors);
            System.out.print(prompt);
            input.next();
        }

        return input.nextShort(radix);
    }
}
