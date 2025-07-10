package ec.edu.espe.examen2p.exception;

public class ValidacionException extends ClienteException {
    private static final Integer CODIGO_ERROR_DEFAULT = 4000;

    public ValidacionException(String mensaje) {
        super(mensaje, CODIGO_ERROR_DEFAULT);
    }

    public ValidacionException(String mensaje, Integer codigoError) {
        super(mensaje, codigoError);
    }
}
