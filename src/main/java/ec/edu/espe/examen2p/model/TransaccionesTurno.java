package ec.edu.espe.examen2p.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@CompoundIndex(name = "codigoCaja_codigoTurno_fecha_idx", def = "{'codigoCaja': 1, 'codigoTurno': 1, 'fecha': 1}")
@Document(collection = "transacciones_turno")
public class TransaccionesTurno {

    @Id
    private String id;

    @Indexed
    private String codigoCaja;
    private String codigoCajero;
    private String monto;
    private LocalDate fecha;
    private String codigoTurno;

    @Indexed
    private String tipoTransaccion; 
    private String montoTotal;
    private String denominaciones; 

    private String descripcion;
    private String estadoTransaccion; 
    private Long version;

}
