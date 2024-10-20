import java.time.format.DateTimeFormatter;
import java.util.*;

public class Principal {

    private static List<String> historialConversiones = new ArrayList<>();
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        //importamos clase Scanner
        Scanner teclado = new Scanner(System.in);



        //Solicitamos al usaurio que ingrese la moneda que tiene
        System.out.println("Ingrese la moneda base (por ejemplo, USD, EUR, COP, MXN)");
        String monedaBase = teclado.next().toUpperCase();

        //hacemos uso de consultar api con la monedaBase que el usuario ingreso
        Map<String, Double> tasasDeCambio = ConsultaApi.obtenerTasaCambio(monedaBase);

        if(tasasDeCambio == null || tasasDeCambio.isEmpty()){
            System.out.println("No se pudieron obtener las tasas de cambio. El programa se cerrara");
            return;
        }

        ConversorMonedas conversor = new ConversorMonedas(tasasDeCambio);

        Map<String, Double> tasasPrincipales = new LinkedHashMap<>();
        for(MonedaPrincipal moneda : MonedaPrincipal.values()){
            if(tasasDeCambio.containsKey(moneda.name()) && !moneda.name().equals(monedaBase)){
                tasasPrincipales.put(moneda.name(), tasasDeCambio.get(moneda.name()));
            }
        }

        while (true) {
            mostrarMenuPrincipal(tasasPrincipales);
            System.out.println("Selecciona una opción (0 para salir): ");
            int opcion = teclado.nextInt();

            if (opcion == 0) {
                System.out.println("Gracias por usar el programa - Conversor de Monedas");
                break;
            } else if (opcion == tasasPrincipales.size() + 1) {
                mostrarTodasLasMonedas(monedaBase, tasasDeCambio, conversor, teclado);
            } else if (opcion >= 1 && opcion <= tasasPrincipales.size()) {
                String monedaDestino = (String) tasasPrincipales.keySet().toArray()[opcion - 1];
                realizarConversion(monedaBase, monedaDestino, conversor, teclado);
            } else {
                System.out.println("Opción no válida. Por favor, intente de nuevo.");
            }
        }
        teclado.close();
    }

    private static void mostrarMenuPrincipal(Map<String, Double> tasasPrincipales) {
        System.out.println("\nSeleccione la moneda a la que desea convertir:");
        int i = 1;
        for (String moneda : tasasPrincipales.keySet()) {
            System.out.println(i + ". " + moneda);
            i++;
        }
        System.out.println(i + ". Ver todas las monedas");
    }

    private static void mostrarTodasLasMonedas(String monedaBase, Map<String, Double> tasasDeCambio, ConversorMonedas conversor, Scanner teclado) {
        List<String> monedasDisponibles = new ArrayList<>();
        for (String moneda : tasasDeCambio.keySet()) {
            if (!moneda.equals(monedaBase)) {
                monedasDisponibles.add(moneda);
            }
        }

        while (true) {
            System.out.println("\nTodas las monedas disponibles:");
            for (int i = 0; i < monedasDisponibles.size(); i++) {
                System.out.println((i + 1) + ". " + monedasDisponibles.get(i));
            }
            System.out.println("0. Volver al menú principal");

            System.out.print("Seleccione una opción: ");
            int opcion = teclado.nextInt();

            if (opcion == 0) {
                break;
            } else if (opcion >= 1 && opcion <= monedasDisponibles.size()) {
                String monedaDestino = monedasDisponibles.get(opcion - 1);
                realizarConversion(monedaBase, monedaDestino, conversor, teclado);
                break;  // Vuelve al menú principal después de la conversión
            } else {
                System.out.println("Opción no válida. Por favor, intente de nuevo.");
            }
        }
    }

    private static void realizarConversion(String monedaBase, String monedaDestino, ConversorMonedas conversor, Scanner teclado) {
        System.out.print("Ingrese la cantidad a convertir de " + monedaBase + " a " + monedaDestino + ": ");
        while (!teclado.hasNextDouble()){
            System.out.println("Por favor ingresa un número válido");
            teclado.next();
        }
        double cantidad = teclado.nextDouble();

        double resultado = conversor.convertir(monedaBase, monedaDestino, cantidad);
        System.out.printf("%.2f %s = %.2f %s%n", cantidad, monedaBase, resultado, monedaDestino);
    }
}

class ConversorMonedas {
    private Map<String, Double> tasasDeCambio;

    public ConversorMonedas(Map<String, Double> tasasDeCambio) {
        this.tasasDeCambio = tasasDeCambio;
    }

    public double convertir(String monedaBase, String monedaDestino, double cantidad) {
        return cantidad * tasasDeCambio.get(monedaDestino);
    }
}
