package datos;

import java.util.Locale;

/**
 * Decide que implementacion de ProductoRepositorio usar segun el entorno.
 * MOTOR=FIRESTORE -> Cloud Firestore
 * MOTOR=MONGO (o sin definir) -> MongoDB Atlas
 */
public final class FabricaRepositorio {

    private FabricaRepositorio() { }

    public static String motorActivo() {
        String motor = System.getenv("MOTOR");
        if (motor == null || motor.isBlank()) {
            return "MONGO";
        }
        return motor.trim().toUpperCase(Locale.ROOT);
    }

    public static ProductoRepositorio crear() {
        String motor = motorActivo();
        return switch (motor) {
            case "FIRESTORE" -> new FirestoreProductoRepositorio(
                    ConexionFirestore.getFirestore());
            case "MONGO" -> new MongoProductoRepositorio(
                    ConexionMongo.getBaseDeDatos());
            default -> throw new IllegalStateException(
                    "Valor no valido para MOTOR: " + motor + " (use MONGO o FIRESTORE)");
        };
    }

    /** Cierra solo los recursos del motor que se abrio. */
    public static void cerrar() {
        if ("FIRESTORE".equals(motorActivo())) {
            ConexionFirestore.cerrar();
        } else {
            ConexionMongo.cerrar();
        }
    }
}
