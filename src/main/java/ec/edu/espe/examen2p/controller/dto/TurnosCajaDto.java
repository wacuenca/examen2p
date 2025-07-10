package ec.edu.espe.examen2p.controller.dto;

import lombok.Data;
import java.util.List;

@Data
public class TurnosCajaDto {
    private String id;
    private String codigoCaja;
    private String codigoCajero;
    private String codigoTurno;
    private String inicioTurno;
    private String montoInicial;
    private String finTurno;
    private String montoFinal;
    private String estado;
    private String duracionTurno;
    private Integer totalTransacciones;
    private String montoTotalTransacciones;
    private List<TransaccionesTurnoDto> transacciones;
    private Long version;
}
