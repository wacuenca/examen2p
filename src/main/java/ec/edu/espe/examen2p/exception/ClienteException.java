package ec.edu.espe.examen2p.exception;

public class ClienteException extends RuntimeException {
    private final Integer codigoError;
    
    public ClienteException(String mensaje, Integer codigoError) {
        super(mensaje);
        this.codigoError = codigoError;
    }
    
    @Override
    public String getMessage() {
        return "Código de error: " + this.codigoError + ", Mensaje: " + super.getMessage();
    }
    
    public Integer getCodigoError() {
        return codigoError;
    }
}