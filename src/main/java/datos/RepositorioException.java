package datos;

public class RepositorioException extends RuntimeException {
    public RepositorioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

    /** NUEVO: regla de negocio que detecta el propio repositorio. */
    public RepositorioException(String mensaje) {
        super(mensaje);
    }
}