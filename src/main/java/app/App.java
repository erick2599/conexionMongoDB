package app;

import datos.FabricaRepositorio;
import datos.ProductoRepositorio;
import datos.RepositorioException;
import modelo.Producto;
import modelo.ResumenCategoria;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Aplicacion de consola de inventario.
 * Funciona con MongoDB o Cloud Firestore sin cambiar una linea de negocio.
 */
public class App {

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            ProductoRepositorio repo = FabricaRepositorio.crear();
            System.out.println("Motor de datos activo: " + FabricaRepositorio.motorActivo());

            int opcion;
            do {
                mostrarMenu();
                opcion = leerEntero(sc, "Opcion: ");
                try {
                    ejecutar(opcion, repo, sc);
                } catch (RepositorioException | IllegalArgumentException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            } while (opcion != 0);

        } catch (IllegalStateException e) {
            System.err.println("Error de configuracion: " + e.getMessage());
        } finally {
            FabricaRepositorio.cerrar();
        }
    }

    private static void mostrarMenu() {
        System.out.println("\n=== INVENTARIO DISTRISALVA ===");
        System.out.println("1. Listar todos");
        System.out.println("2. Buscar por codigo");
        System.out.println("3. Listar por categoria");
        System.out.println("4. Buscar por nombre");
        System.out.println("5. Buscar por rango de precio");
        System.out.println("6. Insertar producto");
        System.out.println("7. Actualizar producto");
        System.out.println("8. Ajustar existencia (entrada / salida)");
        System.out.println("9. Eliminar producto");
        System.out.println("10. Resumen por categoria");
        System.out.println("11. Cargar datos de demostracion");
        System.out.println("0. Salir");
    }

    private static void ejecutar(int op, ProductoRepositorio repo, Scanner sc) {
        switch (op) {
            case 1 -> imprimirProductos(repo.listarTodos());
            case 2 -> {
                String cod = leerTexto(sc, "Codigo: ");
                Optional<Producto> p = repo.buscarPorCodigo(cod);
                System.out.println(p.map(Producto::toString).orElse("No encontrado."));
            }
            case 3 -> imprimirProductos(repo.listarPorCategoria(leerTexto(sc, "Categoria: ")));
            case 4 -> imprimirProductos(repo.buscarPorNombre(leerTexto(sc, "Texto en nombre: ")));
            case 5 -> {
                double min = leerDecimal(sc, "Precio minimo: ");
                double max = leerDecimal(sc, "Precio maximo: ");
                imprimirProductos(repo.buscarPorRangoDePrecio(min, max));
            }
            case 6 -> {
                Producto nuevo = leerProducto(sc, leerTexto(sc, "Codigo (ej. P-009): "));
                repo.insertar(nuevo);
                System.out.println("Producto insertado.");
            }
            case 7 -> {
                String cod = leerTexto(sc, "Codigo del producto a actualizar: ");
                if (repo.buscarPorCodigo(cod).isEmpty()) {
                    System.out.println("No existe un producto con ese codigo.");
                    return;
                }
                boolean ok = repo.actualizar(leerProducto(sc, cod));
                System.out.println(ok ? "Producto actualizado." : "No se actualizo.");
            }
            case 8 -> {
                String cod = leerTexto(sc, "Codigo: ");
                int cant = leerEntero(sc, "Cantidad (+ entrada / - salida): ");
                boolean ok = repo.ajustarExistencia(cod, cant);
                System.out.println(ok ? "Existencia actualizada."
                        : "No se aplico (no existe o existencia insuficiente).");
            }
            case 9 -> {
                String cod = leerTexto(sc, "Codigo a eliminar: ");
                System.out.println(repo.eliminar(cod) ? "Eliminado." : "No existia.");
            }
            case 10 -> {
                List<ResumenCategoria> lista = repo.resumenPorCategoria();
                if (lista.isEmpty()) {
                    System.out.println("(sin datos)");
                }
                for (ResumenCategoria r : lista) {
                    System.out.printf("%-12s %d productos %5d u. $%9.2f%n",
                            r.categoria(), r.productos(), r.unidades(), r.valorInventario());
                }
            }
            case 11 -> System.out.println("Insertados: " + repo.insertarVarios(DatosDemo.productos()));
            case 0 -> System.out.println("Saliendo...");
            default -> System.out.println("Opcion no valida.");
        }
    }

    private static Producto leerProducto(Scanner sc, String codigo) {
        String nombre = leerTexto(sc, "Nombre: ");
        String categoria = leerTexto(sc, "Categoria: ");
        double precio = leerDecimal(sc, "Precio: ");
        int existencia = leerEntero(sc, "Existencia: ");
        return new Producto(codigo, nombre, categoria, precio, existencia);
    }

    private static void imprimirProductos(List<Producto> lista) {
        if (lista.isEmpty()) {
            System.out.println("(sin resultados)");
            return;
        }
        lista.forEach(System.out::println);
    }

    private static String leerTexto(Scanner sc, String etiqueta) {
        System.out.print(etiqueta);
        return sc.nextLine().trim();
    }

    private static int leerEntero(Scanner sc, String etiqueta) {
        while (true) {
            try {
                System.out.print(etiqueta);
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un numero entero.");
            }
        }
    }

    private static double leerDecimal(Scanner sc, String etiqueta) {
        while (true) {
            try {
                System.out.print(etiqueta);
                return Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un numero decimal.");
            }
        }
    }
}
