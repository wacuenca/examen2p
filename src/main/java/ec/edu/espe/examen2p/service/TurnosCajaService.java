package ec.edu.espe.examen2p.service;

import ec.edu.espe.examen2p.controller.dto.TransaccionesTurnoDTO;
import ec.edu.espe.examen2p.controller.dto.TurnosCajaDTO;
import ec.edu.espe.examen2p.controller.mapper.TransaccionesTurnoMapper;
import ec.edu.espe.examen2p.controller.mapper.TurnosCajaMapper;
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
import java.util.stream.Collectors;

@Service
public class TurnosCajaService {

    private static final Logger log = LoggerFactory.getLogger(TurnosCajaService.class);

    private final TurnosCajaRepository turnosCajaRepository;
    private final TransaccionesTurnoRepository transaccionesTurnoRepository;
    private final TurnosCajaMapper turnosCajaMapper;
    private final TransaccionesTurnoMapper transaccionesTurnoMapper;

    public TurnosCajaService(TurnosCajaRepository turnosCajaRepository,
                            TransaccionesTurnoRepository transaccionesTurnoRepository,
                            TurnosCajaMapper turnosCajaMapper,
                            TransaccionesTurnoMapper transaccionesTurnoMapper) {
        this.turnosCajaRepository = turnosCajaRepository;
        this.transaccionesTurnoRepository = transaccionesTurnoRepository;
        this.turnosCajaMapper = turnosCajaMapper;
        this.transaccionesTurnoMapper = transaccionesTurnoMapper;
    }

    @Transactional
    public void abrirTurno(TurnosCaja turno, List<TransaccionesTurno> transacciones, Map<Denominaciones, Integer> billetesRecibidos) {
        try {
            log.info("Intentando abrir turno para caja: {}", turno.getCodigoCaja());

            String codigoTurno = generarCodigoTurno(turno.getCodigoCaja(), turno.getCodigoCajero());
            turno.setCodigoTurno(codigoTurno);

            Optional<TurnosCaja> turnoExistente = turnosCajaRepository.findByCodigoTurno(codigoTurno);
            if (turnoExistente.isPresent()) {
                throw new ValidacionException("El turno ya existe.");
            }

            turno.setTransacciones(transacciones);
            turno.setBilletesRecibidos(billetesRecibidos);
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
            transaccion.setBilletes(billetes);
            transaccionesTurnoRepository.save(transaccion);
            log.info("Transacción registrada exitosamente.");
        } catch (Exception e) {
            log.error("Error al registrar transacción: {}", e.getMessage(), e);
            throw new ValidacionException("Error al registrar transacción: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void cerrarTurno(String codigoTurno, List<?> transacciones, Map<Denominaciones, Integer> billetesFinales) {
        try {
            log.info("Intentando cerrar turno con código: {}", codigoTurno);

            TurnosCaja turno = turnosCajaRepository.findByCodigoTurno(codigoTurno)
                    .orElseThrow(() -> new ValidacionException("Turno no encontrado."));

            List<TransaccionesTurno> transaccionesFinales;

            if (!transacciones.isEmpty()) {
                if (transacciones.get(0) instanceof TransaccionesTurnoDTO) {
                    List<TransaccionesTurnoDTO> transaccionesDto = transacciones.stream()
                            .filter(TransaccionesTurnoDTO.class::isInstance)
                            .map(TransaccionesTurnoDTO.class::cast)
                            .collect(Collectors.toList());
                    transaccionesFinales = transaccionesDto.stream()
                            .map(transaccionesTurnoMapper::toEntity)
                            .toList();
                } else {
                    transaccionesFinales = transacciones.stream()
                            .filter(TransaccionesTurno.class::isInstance)
                            .map(TransaccionesTurno.class::cast)
                            .toList();
                }
            } else {
                transaccionesFinales = List.of();
            }

            double montoCalculado = calcularMontoTotal(turno.getTransacciones());
            double montoFinal = calcularMontoFinal(transaccionesFinales);

            if (montoCalculado != montoFinal) {
                log.error("Discrepancia detectada en el monto final del turno.");
                throw new ValidacionException("Discrepancia en el monto final del turno.");
            }

            turno.setTransacciones(transaccionesFinales);
            turno.setBilletesFinales(billetesFinales);
            turnosCajaRepository.save(turno);
            log.info("Turno cerrado exitosamente.");
        } catch (Exception e) {
            log.error("Error al cerrar turno: {}", e.getMessage(), e);
            throw new ValidacionException("Error al cerrar turno: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void abrirTurno(TurnosCajaDTO turnoDto) {
        try {
            TurnosCaja turno = turnosCajaMapper.toEntity(turnoDto);
            turnosCajaRepository.save(turno);
        } catch (Exception e) {
            log.error("Error al abrir turno: {}", e.getMessage());
            throw new ValidacionException("Error al abrir turno: " + e.getMessage());
        }
    }

    @Transactional
    public void registrarTransaccion(TransaccionesTurnoDTO transaccionDto) {
        try {
            TransaccionesTurno transaccion = transaccionesTurnoMapper.toEntity(transaccionDto);
            transaccionesTurnoRepository.save(transaccion);
        } catch (Exception e) {
            log.error("Error al registrar transacción: {}", e.getMessage());
            throw new ValidacionException("Error al registrar transacción: " + e.getMessage());
        }
    }

    private String generarCodigoTurno(String codigoCaja, String codigoCajero) {
        LocalDate fechaActual = LocalDate.now();
        return codigoCaja + codigoCajero + fechaActual.getYear() + fechaActual.getMonthValue() + fechaActual.getDayOfMonth();
    }

    private double calcularMontoTotal(List<TransaccionesTurno> transacciones) {
        return transacciones.stream()
                .mapToDouble(transaccion -> Double.parseDouble(transaccion.getMontoTotal()))
                .sum();
    }

    private double calcularMontoFinal(List<TransaccionesTurno> transaccionesFinales) {
        return transaccionesFinales.stream()
                .mapToDouble(transaccion -> Double.parseDouble(transaccion.getMontoTotal()))
                .sum();
    }
}