package ec.edu.espe.examen2p.controller.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class TransaccionesTurnoDTO {
    private String id;
    private String codigoCaja;
    private String codigoCajero;
    private String monto;
    private LocalDate fecha;
    private String codigoTurno;
    private String tipoTransaccion;
    private String montoTotal;
    private String denominaciones;
    private String descripcion;
    private String estadoTransaccion;
    private Long version;
}
