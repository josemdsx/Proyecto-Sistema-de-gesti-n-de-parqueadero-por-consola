package parqueadero;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

public class Main {
    static final int CAPACIDAD_TOTAL = 10;
    static final double TARIFA_HORA_CARRO = 4000;
    static final double TARIFA_DIA_CARRO = 30000;
    static final double TARIFA_HORA_MOTO = 2000;
    static final double TARIFA_DIA_MOTO = 15000;
    static final int UMBRAL_HORAS_DIA = 8;
    static final int HORA_APERTURA = 8;
    static final int HORA_CIERRE = 22;
    static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    static final Scanner SCANNER = new Scanner(System.in);

    static String[] tiposVehiculo = new String[CAPACIDAD_TOTAL];
    static LocalDateTime[] horasIngreso = new LocalDateTime[CAPACIDAD_TOTAL];
    static double recaudoCarros = 0;
    static double recaudoMotos = 0;

    public static void main(String[] args) {
        boolean salir = false;
        while (!salir) {
            System.out.println("\n--- SISTEMA DE PARQUEADERO ---");
            System.out.println("1. Registrar Entrada");
            System.out.println("2. Registrar Salida");
            System.out.println("3. Reporte de Ocupación");
            System.out.println("4. Reporte Financiero");
            System.out.println("5. Salir");
            System.out.print("Seleccione una opción: ");

            String opcion = SCANNER.nextLine().trim();
            switch (opcion) {
                case "1": // registrarEntrada(); break;
                case "2": // registrarSalida(); break;
                case "3": // reporteOcupacion(); break;
                case "4": // reporteFinanciero(); break;
                case "5":
                    salir = true;
                    System.out.println("Cerrando sistema...");
                    break;
                default:
                    imprimir("Opción inválida.");
            }
        }
        SCANNER.close();
    }

    static void imprimir(String mensaje) { System.out.println(mensaje); }


    static void registrarEntrada() {}
    static int obtenerCeldaDisponible() { return -1; }
    static String validarTipoVehiculo() { return ""; }
    static LocalDateTime leerFechaHora(String msj) { return null; }
    static boolean formatoFechaHoraValido(String txt) { return false; }
    static int leerDigitos(String txt, int ini, int cant) { return 0; }
    static void registrarSalida() {}
    static void liquidar(int c, LocalDateTime i, LocalDateTime s) {}
    static long contarDiasCompletos(LocalDateTime i, LocalDateTime s) { return 0; }
    static long calcularHorasRestantes(LocalDateTime c, LocalDateTime s) { return 0; }
    static void reporteOcupacion() {}
    static void reporteFinanciero() {}
}