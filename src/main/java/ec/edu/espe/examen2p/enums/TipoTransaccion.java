package ec.edu.espe.examen2p.enums;

public enum TipoTransaccion {
    DEPOSITO("DEPOSITO", 1),      // Suma al saldo de caja
    RETIRO("RETIRO", -1),         // Resta del saldo de caja
    INICIO("INICIO", 0),          // Sin efecto en cálculo
    CIERRE("CIERRE", 0);          // Sin efecto en cálculo

    private final String descripcion;
    private final int multiplicador; // 1 para sumar, -1 para restar, 0 para neutro

    TipoTransaccion(String descripcion, int multiplicador) {
        this.descripcion = descripcion;
        this.multiplicador = multiplicador;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getMultiplicador() {
        return multiplicador;
    }

    public boolean esTransaccionOperativa() {
        return this == DEPOSITO || this == RETIRO;
    }

    public boolean esDeposito() {
        return this == DEPOSITO;
    }

    public boolean esRetiro() {
        return this == RETIRO;
    }
}
