package ec.edu.espe.examen2p.exception;

public class ActualizacionException extends ClienteException {
    private static final Integer CODIGO_ERROR_DEFAULT = 2000;
    
    public ActualizacionException(String mensaje) {
        super(mensaje, CODIGO_ERROR_DEFAULT);
    }
    
    public ActualizacionException(String mensaje, Integer codigoError) {
        super(mensaje, codigoError);
    }
}
