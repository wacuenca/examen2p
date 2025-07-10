package ec.edu.espe.examen2p.enums;

public enum Denominaciones {
    BILLETE_1(1.0),
    BILLETE_5(5.0),
    BILLETE_10(10.0),
    BILLETE_20(20.0),
    BILLETE_50(50.0),
    BILLETE_100(100.0);

    private final double valor;

    Denominaciones(double valor) {
        this.valor = valor;
    }

    public double getValor() {
        return valor;
    }
}
