package ec.edu.espe.examen2p.service;

import ec.edu.espe.examen2p.controller.dto.TransaccionesTurnoDTO;
import ec.edu.espe.examen2p.controller.mapper.TransaccionesTurnoMapper;
import ec.edu.espe.examen2p.exception.ValidacionException;
import ec.edu.espe.examen2p.model.TransaccionesTurno;
import ec.edu.espe.examen2p.model.TurnosCaja;
import ec.edu.espe.examen2p.repository.TransaccionesTurnoRepository;
import ec.edu.espe.examen2p.repository.TurnosCajaRepository;
import ec.edu.espe.examen2p.enums.Denominaciones;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TurnosCajaService {

    private static final Logger log = LoggerFactory.getLogger(TurnosCajaService.class);

    private final TurnosCajaRepository turnosCajaRepository;
    private final TransaccionesTurnoRepository transaccionesTurnoRepository;
    private final TransaccionesTurnoMapper transaccionesTurnoMapper;

    public TurnosCajaService(TurnosCajaRepository turnosCajaRepository,
                            TransaccionesTurnoRepository transaccionesTurnoRepository,
                            TransaccionesTurnoMapper transaccionesTurnoMapper) {
        this.turnosCajaRepository = turnosCajaRepository;
        this.transaccionesTurnoRepository = transaccionesTurnoRepository;
        this.transaccionesTurnoMapper = transaccionesTurnoMapper;
    }

    @Transactional
    public void abrirTurno(TurnosCaja turno, List<TransaccionesTurno> transacciones, Map<Denominaciones, Integer> billetesRecibidos) {
        try {
            log.info("Intentando abrir turno para caja: {}", turno.getCodigoCaja());

            // Validar que se registren billetes de la bóveda
            if (billetesRecibidos == null || billetesRecibidos.isEmpty()) {
                throw new ValidacionException("Debe registrar los billetes recibidos de la bóveda del banco.");
            }

            String codigoTurno = generarCodigoTurno(turno.getCodigoCaja(), turno.getCodigoCajero());
            turno.setCodigoTurno(codigoTurno);

            Optional<TurnosCaja> turnoExistente = turnosCajaRepository.findByCodigoTurno(codigoTurno);
            if (turnoExistente.isPresent()) {
                throw new ValidacionException("El turno ya existe.");
            }

            turno.setTransacciones(transacciones != null ? transacciones : List.of());
            turno.setBilletesRecibidos(billetesRecibidos);
            turno.setEstado("ABIERTO");
            turnosCajaRepository.save(turno);

            log.info("Turno abierto exitosamente con código: {}", codigoTurno);
        } catch (Exception e) {
            log.error("Error al abrir turno: {}", e.getMessage(), e);
            throw new ValidacionException("Error al abrir turno: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void registrarTransaccion(TransaccionesTurno transaccion, Map<Denominaciones, Integer> billetes) {
        try {
            log.info("Registrando transacción: {}", transaccion);
            
            // Validar que se registren billetes en la transacción
            if (billetes == null || billetes.isEmpty()) {
                throw new ValidacionException("Debe registrar la cantidad de billetes y denominaciones en la transacción.");
            }
            
            transaccion.setBilletes(billetes);
            transaccionesTurnoRepository.save(transaccion);
            log.info("Transacción registrada exitosamente.");
        } catch (Exception e) {
            log.error("Error al registrar transacción: {}", e.getMessage(), e);
            throw new ValidacionException("Error al registrar transacción: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void cerrarTurno(String codigoTurno, List<TransaccionesTurnoDTO> transaccionesDto, Map<Denominaciones, Integer> billetesFinales) {
        try {
            log.info("Intentando cerrar turno con código: {}", codigoTurno);

            TurnosCaja turno = turnosCajaRepository.findByCodigoTurno(codigoTurno)
                    .orElseThrow(() -> new ValidacionException("Turno no encontrado."));

            // Validar que se ingresen billetes finales
            if (billetesFinales == null || billetesFinales.isEmpty()) {
                throw new ValidacionException("Debe ingresar la cantidad de billetes de cada denominación al cerrar el turno.");
            }

            List<TransaccionesTurno> transaccionesFinales = transaccionesDto.stream()
                    .map(transaccionesTurnoMapper::toEntity)
                    .toList();

            // Calcular monto esperado basado en billetes iniciales y transacciones
            double montoInicialBilletes = calcularMontoFromBilletes(turno.getBilletesRecibidos());
            double montoTransacciones = calcularMontoTotal(turno.getTransacciones());
            double montoEsperado = montoInicialBilletes + montoTransacciones;
            
            // Calcular monto real de billetes finales
            double montoFinalReal = calcularMontoFromBilletes(billetesFinales);

            if (Math.abs(montoEsperado - montoFinalReal) > 0.01) { // Tolerancia para decimales
                log.error("ALERTA: Discrepancia detectada en el monto final del turno. Esperado: {}, Real: {}", montoEsperado, montoFinalReal);
                throw new ValidacionException("ALERTA: Discrepancia en el monto final del turno. Esperado: " + montoEsperado + ", Real: " + montoFinalReal);
            }

            turno.setTransacciones(transaccionesFinales);
            turno.setBilletesFinales(billetesFinales);
            turno.setEstado("CERRADO");
            turnosCajaRepository.save(turno);
            log.info("Turno cerrado exitosamente.");
        } catch (Exception e) {
            log.error("Error al cerrar turno: {}", e.getMessage(), e);
            throw new ValidacionException("Error al cerrar turno: " + e.getMessage(), e);
        }
    }



    private String generarCodigoTurno(String codigoCaja, String codigoCajero) {
        LocalDate fechaActual = LocalDate.now();
        return codigoCaja + codigoCajero + fechaActual.getYear() + fechaActual.getMonthValue() + fechaActual.getDayOfMonth();
    }

    private double calcularMontoTotal(List<TransaccionesTurno> transacciones) {
        if (transacciones == null || transacciones.isEmpty()) {
            return 0.0;
        }
        return transacciones.stream()
                .mapToDouble(transaccion -> {
                    try {
                        return Double.parseDouble(transaccion.getMontoTotal());
                    } catch (NumberFormatException e) {
                        log.warn("Monto inválido en transacción: {}", transaccion.getMontoTotal());
                        return 0.0;
                    }
                })
                .sum();
    }

    private double calcularMontoFromBilletes(Map<Denominaciones, Integer> billetes) {
        if (billetes == null || billetes.isEmpty()) {
            return 0.0;
        }
        return billetes.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getValor() * entry.getValue())
                .sum();
    }
}