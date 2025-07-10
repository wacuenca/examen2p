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
public class AbrirTurnoRequestDTO {
    private TurnosCajaDTO turno;
    private Map<Denominaciones, Integer> billetesRecibidos;
}
