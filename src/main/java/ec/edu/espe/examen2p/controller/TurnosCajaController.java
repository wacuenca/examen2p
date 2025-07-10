package ec.edu.espe.examen2p.controller;

import ec.edu.espe.examen2p.controller.dto.TurnosCajaDTO;
import ec.edu.espe.examen2p.controller.dto.TransaccionesTurnoDTO;
import ec.edu.espe.examen2p.service.TurnosCajaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/turnos-caja")
public class TurnosCajaController {

    private static final Logger log = LoggerFactory.getLogger(TurnosCajaController.class);

    private final TurnosCajaService turnosCajaService;

    public TurnosCajaController(TurnosCajaService turnosCajaService) {
        this.turnosCajaService = turnosCajaService;
    }

    @PostMapping("/abrir")
    public ResponseEntity<String> abrirTurno(@RequestBody TurnosCajaDTO turnoDto) {
        try {
            log.info("Solicitud para abrir turno: {}", turnoDto);
            turnosCajaService.abrirTurno(turnoDto);
            return ResponseEntity.status(201).body("Turno abierto exitosamente.");
        } catch (Exception e) {
            log.error("Error al abrir turno: {}", e.getMessage());
            return ResponseEntity.status(400).body("Error al abrir turno: " + e.getMessage());
        }
    }

    @PostMapping("/transaccion")
    public ResponseEntity<String> registrarTransaccion(@RequestBody TransaccionesTurnoDTO transaccionDto) {
        try {
            log.info("Solicitud para registrar transacción: {}", transaccionDto);
            turnosCajaService.registrarTransaccion(transaccionDto);
            return ResponseEntity.status(201).body("Transacción registrada exitosamente.");
        } catch (Exception e) {
            log.error("Error al registrar transacción: {}", e.getMessage());
            return ResponseEntity.status(400).body("Error al registrar transacción: " + e.getMessage());
        }
    }

    @PostMapping("/cerrar/{codigoTurno}")
    public ResponseEntity<String> cerrarTurno(@PathVariable String codigoTurno, 
                                            @RequestBody List<TransaccionesTurnoDTO> transaccionesFinalesDto) {
        try {
            log.info("Solicitud para cerrar turno con código: {}", codigoTurno);
            turnosCajaService.cerrarTurno(codigoTurno, transaccionesFinalesDto);
            return ResponseEntity.ok("Turno cerrado exitosamente.");
        } catch (Exception e) {
            log.error("Error al cerrar turno: {}", e.getMessage());
            return ResponseEntity.status(400).body("Error al cerrar turno: " + e.getMessage());
        }
    }
}