package app;

import datos.FabricaRepositorio;
import datos.ProductoRepositorio;
import datos.RepositorioException;
import modelo.Producto;

public class PruebaCrud {
    public static void main(String[] args) {
        try {
            ProductoRepositorio repo = FabricaRepositorio.crear();
            System.out.println("Motor activo: " + FabricaRepositorio.motorActivo());

            // 0. Limpieza: la prueba se puede ejecutar varias veces
            for (Producto p : DatosDemo.productos()) {
                repo.eliminar(p.getCodigo());
            }
            repo.eliminar("P-009");
            repo.eliminar("P-100");
            System.out.println("0) Limpieza lista");

            // ---- PARTE B: CREATE y DELETE ----
            // 1. CREATE: en lote y de uno en uno
            int n = repo.insertarVarios(DatosDemo.productos());
            System.out.println("1) Carga inicial: " + n + " productos");
            repo.insertar(new Producto("P-009", "Azucar blanca 1 kg",
                    "Abarrotes", 1.10, 150));
            System.out.println("   Insertado: P-009");
            try {
                repo.insertar(new Producto("P-001", "Duplicado", "Abarrotes", 1.00, 1));
            } catch (RepositorioException e) {
                System.out.println("   Rechazado por el motor: " + e.getMessage());
            }
            try {
                repo.insertar(new Producto("P-010", "Error", "Abarrotes", -5.00, 1));
            } catch (IllegalArgumentException e) {
                System.out.println("   Rechazado por Java: " + e.getMessage());
            }

            // ---- Tu propia prueba de inserción permanente ----
            repo.insertar(new Producto("P-100", "Producto Personalizado", "Abarrotes", 2.50, 50));
            System.out.println("¡Mi nuevo producto P-100 ha sido insertado y se quedará en la nube!");

            // 2. DELETE
            System.out.println("2) P-009 eliminado: " + repo.eliminar("P-009"));
            System.out.println("   P-009 eliminado otra vez: " + repo.eliminar("P-009"));

        } catch (RepositorioException | IllegalStateException e) {
            System.err.println("Error de datos: " + e.getMessage());
        } finally {
            FabricaRepositorio.cerrar();
        }
    }
}
