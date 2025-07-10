package ec.edu.espe.examen2p.controller;

import ec.edu.espe.examen2p.controller.dto.AbrirTurnoRequestDTO;
import ec.edu.espe.examen2p.controller.dto.RegistrarTransaccionRequestDTO;
import ec.edu.espe.examen2p.controller.dto.CerrarTurnoRequestDTO;
import ec.edu.espe.examen2p.controller.mapper.TurnosCajaMapper;
import ec.edu.espe.examen2p.controller.mapper.TransaccionesTurnoMapper;
import ec.edu.espe.examen2p.model.TurnosCaja;
import ec.edu.espe.examen2p.model.TransaccionesTurno;
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
    private final TurnosCajaMapper turnosCajaMapper;
    private final TransaccionesTurnoMapper transaccionesTurnoMapper;

    public TurnosCajaController(TurnosCajaService turnosCajaService,
                              TurnosCajaMapper turnosCajaMapper,
                              TransaccionesTurnoMapper transaccionesTurnoMapper) {
        this.turnosCajaService = turnosCajaService;
        this.turnosCajaMapper = turnosCajaMapper;
        this.transaccionesTurnoMapper = transaccionesTurnoMapper;
    }

    @PostMapping("/abrir")
    public ResponseEntity<String> abrirTurno(@RequestBody AbrirTurnoRequestDTO request) {
        try {
            log.info("Solicitud para abrir turno: {} con billetes: {}", request.getTurno(), request.getBilletesRecibidos());
            
            TurnosCaja turno = turnosCajaMapper.toEntity(request.getTurno());
            
            turnosCajaService.abrirTurno(turno, List.of(), request.getBilletesRecibidos());
            return ResponseEntity.status(201).body("Turno abierto exitosamente.");
        } catch (Exception e) {
            log.error("Error al abrir turno: {}", e.getMessage());
            return ResponseEntity.status(400).body("Error al abrir turno: " + e.getMessage());
        }
    }

    @PostMapping("/transaccion")
    public ResponseEntity<String> registrarTransaccion(@RequestBody RegistrarTransaccionRequestDTO request) {
        try {
            log.info("Solicitud para registrar transacción: {} con billetes: {}", request.getTransaccion(), request.getBilletes());
            
            TransaccionesTurno transaccion = transaccionesTurnoMapper.toEntity(request.getTransaccion());
            
            turnosCajaService.registrarTransaccion(transaccion, request.getBilletes());
            return ResponseEntity.status(201).body("Transacción registrada exitosamente.");
        } catch (Exception e) {
            log.error("Error al registrar transacción: {}", e.getMessage());
            return ResponseEntity.status(400).body("Error al registrar transacción: " + e.getMessage());
        }
    }

    @PostMapping("/cerrar/{codigoTurno}")
    public ResponseEntity<String> cerrarTurno(@PathVariable String codigoTurno, 
                                            @RequestBody CerrarTurnoRequestDTO request) {
        try {
            log.info("Solicitud para cerrar turno con código: {}", codigoTurno);
            turnosCajaService.cerrarTurno(codigoTurno, request.getTransaccionesFinales(), request.getBilletesFinales());
            return ResponseEntity.ok("Turno cerrado exitosamente.");
        } catch (Exception e) {
            log.error("Error al cerrar turno: {}", e.getMessage());
            return ResponseEntity.status(400).body("Error al cerrar turno: " + e.getMessage());
        }
    }
}