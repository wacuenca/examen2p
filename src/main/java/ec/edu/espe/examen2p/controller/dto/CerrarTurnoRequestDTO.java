package ec.edu.espe.examen2p.controller.dto;

import ec.edu.espe.examen2p.enums.Denominaciones;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CerrarTurnoRequestDTO {
    private List<TransaccionesTurnoDTO> transaccionesFinales;
    private Map<Denominaciones, Integer> billetesFinales;
}
