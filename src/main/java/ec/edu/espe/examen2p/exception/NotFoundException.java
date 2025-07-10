package ec.edu.espe.examen2p.exception;

public class NotFoundException extends ClienteException {
    private static final Integer CODIGO_ERROR_DEFAULT = 3000;
    
    public NotFoundException(String mensaje) {
        super(mensaje, CODIGO_ERROR_DEFAULT);
    }
    
    public NotFoundException(String mensaje, Integer codigoError) {
        super(mensaje, codigoError);
    }
}
