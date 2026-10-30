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
                case "1": registrarEntrada(); break;
                case "2": registrarSalida(); break;
                case "3": reporteOcupacion(); break;
                case "4": reporteFinanciero(); break;
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

    static void registrarEntrada() {
        int celda = obtenerCeldaDisponible();
        if (celda == -1) {
            imprimir("El parqueadero está lleno. No hay celdas disponibles.");
            return;
        }
        String tipo = validarTipoVehiculo();
        LocalDateTime fechaHora = leerFechaHora("Ingrese fecha y hora de entrada (yyyy-MM-dd HH:mm): ");
        tiposVehiculo[celda] = tipo;
        horasIngreso[celda] = fechaHora;
        imprimir("Vehículo registrado exitosamente en la celda " + (celda + 1));
    }

    static int obtenerCeldaDisponible() {
        for (int i = 0; i < CAPACIDAD_TOTAL; i++) {
            if (tiposVehiculo[i] == null) return i;
        }
        return -1;
    }

    static String validarTipoVehiculo() {
        while (true) {
            System.out.print("Ingrese tipo de vehículo (carro/moto): ");
            String tipo = SCANNER.nextLine().trim().toLowerCase();
            if (tipo.equals("carro") || tipo.equals("moto")) return tipo;
            imprimir("Tipo inválido. Debe ser 'carro' o 'moto'.");
        }
    }

    static LocalDateTime leerFechaHora(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = SCANNER.nextLine().trim();
            if (!formatoFechaHoraValido(texto)) {
                imprimir("Fecha u hora inválidas. Use yyyy-MM-dd HH:mm y el horario 08:00-22:00.");
                continue;
            }
            try {
                LocalDateTime fechaParseada = LocalDateTime.parse(texto, FORMATO_FECHA_HORA);
                if (!fechaParseada.format(FORMATO_FECHA_HORA).equals(texto)) {
                    imprimir("Fecha inexistente en el calendario. Intente de nuevo.");
                    continue;
                }
                return fechaParseada;
            } catch (Exception e) {
                imprimir("Fecha inexistente en el calendario. Intente de nuevo.");
            }
        }
    }

    static boolean formatoFechaHoraValido(String texto) {
        if (texto.length() != 16 || texto.charAt(4) != '-' || texto.charAt(7) != '-'
                || texto.charAt(10) != ' ' || texto.charAt(13) != ':') return false;
        int anio = leerDigitos(texto, 0, 4);
        int mes = leerDigitos(texto, 5, 2);
        int dia = leerDigitos(texto, 8, 2);
        int hora = leerDigitos(texto, 11, 2);
        int minuto = leerDigitos(texto, 14, 2);
        return anio >= 1 && mes >= 1 && mes <= 12 && dia >= 1 && dia <= 31
                && hora >= HORA_APERTURA && hora <= HORA_CIERRE
                && (hora != HORA_CIERRE || minuto <= 0) && minuto >= 0 && minuto <= 59;
    }

    static int leerDigitos(String texto, int inicio, int cantidad) {
        int valor = 0;
        for (int i = inicio; i < inicio + cantidad; i++) {
            char caracter = texto.charAt(i);
            if (caracter < '0' || caracter > '9') return -1;
            valor = valor * 10 + caracter - '0';
        }
        return valor;
    }

    static void registrarSalida() {
        System.out.print("Ingrese el número de celda a liberar (1-" + CAPACIDAD_TOTAL + "): ");
        String celdaStr = SCANNER.nextLine().trim();
        int celda;
        try {
            celda = Integer.parseInt(celdaStr) - 1;
        } catch (NumberFormatException e) {
            imprimir("Dato inválido. Debe ingresar un número entero.");
            return;
        }
        if (celda < 0 || celda >= CAPACIDAD_TOTAL) {
            imprimir("Posición inválida. Debe estar entre 1 y " + CAPACIDAD_TOTAL + ".");
            return;
        }
        if (tiposVehiculo[celda] == null) {
            imprimir("La celda indicada actualmente está vacía.");
            return;
        }
        LocalDateTime fechaIngreso = horasIngreso[celda];
        LocalDateTime fechaSalida = leerFechaHora("Ingrese fecha y hora de salida (yyyy-MM-dd HH:mm): ");
        if (fechaSalida.isBefore(fechaIngreso)) {
            imprimir("Error: La fecha y hora de salida no puede ser anterior al ingreso.");
            return;
        }
        liquidar(celda, fechaIngreso, fechaSalida);
    }

    static void liquidar(int celda, LocalDateTime ingreso, LocalDateTime salida) {
        long diasACobrar = contarDiasCompletos(ingreso, salida);
        LocalDateTime cursor = ingreso.plusDays(diasACobrar);
        long horasACobrar = calcularHorasRestantes(cursor, salida);

        if (horasACobrar >= UMBRAL_HORAS_DIA) {
            diasACobrar++;
            horasACobrar = 0;
        }

        String tipo = tiposVehiculo[celda];
        double totalDia = (tipo.equals("carro") ? TARIFA_DIA_CARRO : TARIFA_DIA_MOTO) * diasACobrar;
        double totalHora = (tipo.equals("carro") ? TARIFA_HORA_CARRO : TARIFA_HORA_MOTO) * horasACobrar;
        double totalPagar = totalDia + totalHora;

        if (tipo.equals("carro")) recaudoCarros += totalPagar;
        else recaudoMotos += totalPagar;

        imprimir("\n--- FACTURA DE SALIDA ---");
        imprimir("Celda liberada: " + (celda + 1));
        imprimir("Tipo de vehículo: " + tipo);
        imprimir("Ingreso: " + ingreso.format(FORMATO_FECHA_HORA));
        imprimir("Salida: " + salida.format(FORMATO_FECHA_HORA));
        imprimir("Cobro por días completos (" + diasACobrar + "): $" + totalDia);
        imprimir("Cobro por horas/fracción (" + horasACobrar + "): $" + totalHora);
        imprimir("TOTAL A PAGAR: $" + totalPagar);
        imprimir("-------------------------");

        tiposVehiculo[celda] = null;
        horasIngreso[celda] = null;
    }

    static long contarDiasCompletos(LocalDateTime ingreso, LocalDateTime salida) {
        return ChronoUnit.DAYS.between(ingreso, salida);
    }

    static long calcularHorasRestantes(LocalDateTime cursor, LocalDateTime salida) {
        long minutosRestantes = Duration.between(cursor, salida).toMinutes();
        return (minutosRestantes + 59) / 60;
    }

    static void reporteOcupacion() {
        int ocupadas = 0;
        for (String tipo : tiposVehiculo) {
            if (tipo != null) ocupadas++;
        }
        int libres = CAPACIDAD_TOTAL - ocupadas;
        imprimir("\n--- REPORTE DE OCUPACIÓN ---");
        imprimir("Cantidad de celdas ocupadas: " + ocupadas);
        imprimir("Cantidad de celdas libres: " + libres);
    }

    static void reporteFinanciero() {
        imprimir("\n--- REPORTE FINANCIERO ---");
        imprimir("Total recaudado por carros: $" + recaudoCarros);
        imprimir("Total recaudado por motos: $" + recaudoMotos);
        imprimir("Total general recaudado: $" + (recaudoCarros + recaudoMotos));
    }
}