package ec.edu.espe.examen2p.model;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import java.util.List;
import java.util.Map;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@CompoundIndex(name = "codigoCaja_codigoCajero_idx", def = "{'codigoCaja': 1, 'codigoCajero': 1}")
@Document(collection = "turnos_caja")
public class TurnosCaja {
    
    @Id
    private String id;

    @Indexed
    private String codigoCaja;
    private String codigoCajero;
    private String codigoTurno;
    private String inicioTurno;
    private String montoInicial;
    private String finTurno;
    private String montoFinal;

    @Indexed
    private String estado;

    private String duracionTurno;
    private Integer totalTransacciones;
    private String montoTotalTransacciones;

    private List<TransaccionesTurno> transacciones;

    private Long version;

    private Map<Denominaciones, Integer> billetesRecibidos;
    private Map<Denominaciones, Integer> billetesFinales;

    public Map<Denominaciones, Integer> getBilletesRecibidos() {
        return billetesRecibidos;
    }

    public void setBilletesRecibidos(Map<Denominaciones, Integer> billetesRecibidos) {
        this.billetesRecibidos = billetesRecibidos;
    }

    public Map<Denominaciones, Integer> getBilletesFinales() {
        return billetesFinales;
    }

    public void setBilletesFinales(Map<Denominaciones, Integer> billetesFinales) {
        this.billetesFinales = billetesFinales;
    }
}
