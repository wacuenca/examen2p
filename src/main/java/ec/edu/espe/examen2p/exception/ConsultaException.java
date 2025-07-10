package ec.edu.espe.examen2p.exception;

public class ConsultaException extends RuntimeException {
    private final int code;

    public ConsultaException(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}