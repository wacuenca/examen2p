package ec.edu.espe.examen2p.exception;

public class CreacionException extends ClienteException {
    private static final Integer CODIGO_ERROR_DEFAULT = 1000;
    
    public CreacionException(String mensaje) {
        super(mensaje, CODIGO_ERROR_DEFAULT);
    }
    
    public CreacionException(String mensaje, Integer codigoError) {
        super(mensaje, codigoError);
    }
}