package ec.edu.espe.examen2p.controller;

import ec.edu.espe.examen2p.controller.dto.TurnosCajaDTO;
import ec.edu.espe.examen2p.controller.dto.TransaccionesTurnoDTO;
import ec.edu.espe.examen2p.controller.dto.AbrirTurnoRequestDTO;
import ec.edu.espe.examen2p.controller.dto.RegistrarTransaccionRequestDTO;
import ec.edu.espe.examen2p.controller.dto.CerrarTurnoRequestDTO;
import ec.edu.espe.examen2p.model.TurnosCaja;
import ec.edu.espe.examen2p.model.TransaccionesTurno;
import ec.edu.espe.examen2p.service.TurnosCajaService;
import ec.edu.espe.examen2p.enums.Denominaciones;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/turnos-caja")
public class TurnosCajaController {

    private static final Logger log = LoggerFactory.getLogger(TurnosCajaController.class);

    private final TurnosCajaService turnosCajaService;

    public TurnosCajaController(TurnosCajaService turnosCajaService) {
        this.turnosCajaService = turnosCajaService;
    }

    @PostMapping("/abrir")
    public ResponseEntity<String> abrirTurno(@RequestBody TurnosCajaDTO turnoDto, 
                                            @RequestParam Map<Denominaciones, Integer> billetesRecibidos) {
        try {
            log.info("Solicitud para abrir turno: {} con billetes: {}", turnoDto, billetesRecibidos);
            
            // Convertir DTO a entidad (necesitarás implementar esto en el mapper)
            TurnosCaja turno = convertirDtoAEntidad(turnoDto);
            
            turnosCajaService.abrirTurno(turno, List.of(), billetesRecibidos);
            return ResponseEntity.status(201).body("Turno abierto exitosamente.");
        } catch (Exception e) {
            log.error("Error al abrir turno: {}", e.getMessage());
            return ResponseEntity.status(400).body("Error al abrir turno: " + e.getMessage());
        }
    }

    @PostMapping("/transaccion")
    public ResponseEntity<String> registrarTransaccion(@RequestBody TransaccionesTurnoDTO transaccionDto,
                                                      @RequestParam Map<Denominaciones, Integer> billetes) {
        try {
            log.info("Solicitud para registrar transacción: {} con billetes: {}", transaccionDto, billetes);
            
            // Convertir DTO a entidad (necesitarás implementar esto en el mapper)
            TransaccionesTurno transaccion = convertirTransaccionDtoAEntidad(transaccionDto);
            
            turnosCajaService.registrarTransaccion(transaccion, billetes);
            return ResponseEntity.status(201).body("Transacción registrada exitosamente.");
        } catch (Exception e) {
            log.error("Error al registrar transacción: {}", e.getMessage());
            return ResponseEntity.status(400).body("Error al registrar transacción: " + e.getMessage());
        }
    }

    @PostMapping("/cerrar/{codigoTurno}")
    public ResponseEntity<String> cerrarTurno(@PathVariable String codigoTurno, 
                                            @RequestBody List<TransaccionesTurnoDTO> transaccionesFinalesDto,
                                            @RequestParam Map<Denominaciones, Integer> denominacionesMap) {
        try {
            log.info("Solicitud para cerrar turno con código: {}", codigoTurno);
            turnosCajaService.cerrarTurno(codigoTurno, transaccionesFinalesDto, denominacionesMap);
            return ResponseEntity.ok("Turno cerrado exitosamente.");
        } catch (Exception e) {
            log.error("Error al cerrar turno: {}", e.getMessage());
            return ResponseEntity.status(400).body("Error al cerrar turno: " + e.getMessage());
        }
    }

    // Métodos de conversión temporal (idealmente deberían estar en un mapper)
    private TurnosCaja convertirDtoAEntidad(TurnosCajaDTO dto) {
        TurnosCaja turno = new TurnosCaja();
        turno.setCodigoCaja(dto.getCodigoCaja());
        turno.setCodigoCajero(dto.getCodigoCajero());
        turno.setInicioTurno(dto.getInicioTurno());
        turno.setMontoInicial(dto.getMontoInicial());
        // Agregar otros campos según sea necesario
        return turno;
    }

    private TransaccionesTurno convertirTransaccionDtoAEntidad(TransaccionesTurnoDTO dto) {
        TransaccionesTurno transaccion = new TransaccionesTurno();
        transaccion.setCodigoCaja(dto.getCodigoCaja());
        transaccion.setCodigoCajero(dto.getCodigoCajero());
        transaccion.setMonto(dto.getMonto());
        transaccion.setFecha(dto.getFecha());
        transaccion.setCodigoTurno(dto.getCodigoTurno());
        transaccion.setTipoTransaccion(dto.getTipoTransaccion());
        transaccion.setMontoTotal(dto.getMontoTotal());
        transaccion.setDescripcion(dto.getDescripcion());
        transaccion.setEstadoTransaccion(dto.getEstadoTransaccion());
        // Agregar otros campos según sea necesario
        return transaccion;
    }
}