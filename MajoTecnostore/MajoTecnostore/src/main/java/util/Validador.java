package util;
 
import java.util.Scanner;
 
public class Validador {
 
    // Un solo Scanner para todo el programa (static), asi no se pierde lo que se escribe.
    static Scanner x = new Scanner(System.in);
 
    // ---------- TEXTOS ----------
 
    public String validarTexto(String mensaje, int maxLongitud) {
        System.out.println(mensaje);
        String texto = x.nextLine().trim();
        while (texto.isEmpty() || texto.length() > maxLongitud) {
            System.out.println("Error, el texto no puede estar vacio ni superar " + maxLongitud + " caracteres");
            texto = x.nextLine().trim();
        }
        return texto;
    }
 
    public String validarNombre(String mensaje) {
        String texto = validarTexto(mensaje, 60);
        while (!soloLetras(texto)) {
            System.out.println("Error, solo se permiten letras y espacios");
            texto = validarTexto(mensaje, 60);
        }
        return texto;
    }
 
    public String validarCorreo(String mensaje) {
        String correo = validarTexto(mensaje, 100);
        while (!correoValido(correo)) {
            System.out.println("Error, formato de correo invalido (ejemplo: nombre@dominio.com)");
            correo = validarTexto(mensaje, 100);
        }
        return correo;
    }
 
    public String validarIdentificacion(String mensaje) {
        String id = validarTexto(mensaje, 20);
        while (id.length() < 5 || !soloLetrasYNumeros(id)) {
            System.out.println("Error, debe tener entre 5 y 20 letras o numeros, sin espacios");
            id = validarTexto(mensaje, 20);
        }
        return id;
    }
 
    public String validarTelefono(String mensaje) {
        String telefono = validarTexto(mensaje, 15);
        while (telefono.length() < 7 || !soloNumeros(telefono)) {
            System.out.println("Error, el telefono debe tener entre 7 y 15 numeros");
            telefono = validarTexto(mensaje, 15);
        }
        return telefono;
    }
 
    // ---------- NUMEROS ENTEROS ----------
 
    public int validarEntero(String mensaje) {
        System.out.println(mensaje);
        while (true) {
            try {
                return Integer.parseInt(x.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Error, se espera un valor entero");
            }
        }
    }
 
    public int validarEnteroPositivo(String mensaje) {
        int numero = validarEntero(mensaje);
        while (numero <= 0) {
            System.out.println("Error, el valor debe ser mayor a 0");
            numero = validarEntero(mensaje);
        }
        return numero;
    }
 
    public int validarEnteroNoNegativo(String mensaje) {
        int numero = validarEntero(mensaje);
        while (numero < 0) {
            System.out.println("Error, el valor no puede ser negativo");
            numero = validarEntero(mensaje);
        }
        return numero;
    }
 
    public long validarEnteroGrande(String mensaje) {
        System.out.println(mensaje);
        while (true) {
            try {
                return Long.parseLong(x.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Error, se espera un valor entero");
            }
        }
    }
 
    // ---------- NUMEROS DECIMALES ----------
 
    // Acepta punto o coma (3.5 o 3,5)
    public double validarDecimal(String mensaje) {
        System.out.println(mensaje);
        while (true) {
            try {
                double numero = Double.parseDouble(x.nextLine().trim().replace(',', '.'));
                if (numero >= 0 && numero <= 9999999999.0) {
                    return numero;
                }
                System.out.println("Error, el valor debe estar entre 0 y 9999999999");
            } catch (NumberFormatException e) {
                System.out.println("Error, se espera un valor decimal");
            }
        }
    }
 
    public double validarDecimalPositivo(String mensaje) {
        double numero = validarDecimal(mensaje);
        while (numero <= 0) {
            System.out.println("Error, el valor debe ser mayor a 0");
            numero = validarDecimal(mensaje);
        }
        return numero;
    }
 
    // ---------- SI / NO ----------
 
    public boolean validarSiNo(String mensaje) {
        System.out.println(mensaje);
        String respuesta = x.nextLine().trim().toLowerCase();
        while (!respuesta.equals("s") && !respuesta.equals("si") && !respuesta.equals("n") && !respuesta.equals("no")) {
            System.out.println("Error, responda s o n");
            respuesta = x.nextLine().trim().toLowerCase();
        }
        return respuesta.equals("s") || respuesta.equals("si");
    }
 
    // ---------- METODOS DE APOYO ----------
 
    private boolean soloLetras(String texto) {
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if (!Character.isLetter(c) && c != ' ') {
                return false;
            }
        }
        return true;
    }
 
    private boolean soloNumeros(String texto) {
        for (int i = 0; i < texto.length(); i++) {
            if (!Character.isDigit(texto.charAt(i))) {
                return false;
            }
        }
        return true;
    }
 
    private boolean soloLetrasYNumeros(String texto) {
        for (int i = 0; i < texto.length(); i++) {
            if (!Character.isLetterOrDigit(texto.charAt(i))) {
                return false;
            }
        }
        return true;
    }
 
    // Correo valido: un solo @, texto antes del @, un punto despues del @ y sin espacios
    private boolean correoValido(String correo) {
        int arroba = correo.indexOf('@');
        int punto = correo.lastIndexOf('.');
        return arroba > 0
                && arroba == correo.lastIndexOf('@')
                && punto > arroba + 1
                && punto < correo.length() - 1
                && !correo.contains(" ");
    }
}