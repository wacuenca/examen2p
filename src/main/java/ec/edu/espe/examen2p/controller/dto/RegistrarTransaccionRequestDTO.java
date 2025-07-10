package ec.edu.espe.examen2p.controller.dto;

import ec.edu.espe.examen2p.enums.Denominaciones;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RegistrarTransaccionRequestDTO {
    private TransaccionesTurnoDTO transaccion;
    private Map<Denominaciones, Integer> billetes;
}
