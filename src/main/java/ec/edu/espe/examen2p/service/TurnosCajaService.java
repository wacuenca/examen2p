package ec.edu.espe.examen2p.service;

import ec.edu.espe.examen2p.controller.dto.TransaccionesTurnoDTO;
import ec.edu.espe.examen2p.controller.mapper.TransaccionesTurnoMapper;
import ec.edu.espe.examen2p.exception.ValidacionException;
import ec.edu.espe.examen2p.model.TransaccionesTurno;
import ec.edu.espe.examen2p.model.TurnosCaja;
import ec.edu.espe.examen2p.repository.TransaccionesTurnoRepository;
import ec.edu.espe.examen2p.repository.TurnosCajaRepository;
import ec.edu.espe.examen2p.enums.Denominaciones;
import ec.edu.espe.examen2p.enums.TipoTransaccion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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

            if (billetesRecibidos == null || billetesRecibidos.isEmpty()) {
                throw new ValidacionException("Debe registrar los billetes recibidos de la bóveda del banco.");
            }
            
            validarDenominaciones(billetesRecibidos, "apertura de turno");

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
            
            if (billetes == null || billetes.isEmpty()) {
                throw new ValidacionException("Debe registrar la cantidad de billetes y denominaciones en la transacción.");
            }

            if (transaccion.getCodigoTurno() == null || transaccion.getCodigoTurno().trim().isEmpty()) {
                throw new ValidacionException("El código de turno es requerido para registrar la transacción.");
            }

            TurnosCaja turno = turnosCajaRepository.findByCodigoTurno(transaccion.getCodigoTurno())
                    .orElseThrow(() -> new ValidacionException("No se encontró un turno con el código: " + transaccion.getCodigoTurno()));

            if (!"ABIERTO".equals(turno.getEstado())) {
                throw new ValidacionException("No se puede registrar transacciones en un turno que no está abierto. Estado actual: " + turno.getEstado());
            }

            validarYProcesarTipoTransaccion(transaccion, billetes);

            validarDenominaciones(billetes, "registro de transacción");
            
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

            if (billetesFinales == null || billetesFinales.isEmpty()) {
                throw new ValidacionException("Debe ingresar la cantidad de billetes de cada denominación al cerrar el turno.");
            }

            validarDenominaciones(billetesFinales, "cierre de turno");

            List<TransaccionesTurno> transaccionesFinales = transaccionesDto.stream()
                    .map(transaccionesTurnoMapper::toEntity)
                    .toList();

            double montoInicialBilletes = calcularMontoFromBilletes(turno.getBilletesRecibidos());
            double montoTransacciones = calcularMontoTotal(turno.getTransacciones());
            double montoEsperado = montoInicialBilletes + montoTransacciones;
            
            double montoFinalReal = calcularMontoFromBilletes(billetesFinales);

            if (Math.abs(montoEsperado - montoFinalReal) > 0.01) { 
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
                        double monto = Double.parseDouble(transaccion.getMontoTotal());
                        
                        return aplicarMultiplicadorPorTipo(transaccion, monto);
                    } catch (NumberFormatException e) {
                        log.warn("Monto inválido en transacción: {} ID: {}", 
                            transaccion.getMontoTotal(), transaccion.getId());
                        return 0.0;
                    }
                })
                .sum();
    }

    private double aplicarMultiplicadorPorTipo(TransaccionesTurno transaccion, double monto) {
        if (transaccion.getTipoTransaccion() != null) {
            try {
                TipoTransaccion tipo = TipoTransaccion.valueOf(transaccion.getTipoTransaccion().toUpperCase());
                return monto * tipo.getMultiplicador();
            } catch (IllegalArgumentException e) {
                log.warn("Tipo de transacción inválido: {} en transacción ID: {}", 
                    transaccion.getTipoTransaccion(), transaccion.getId());
                return 0.0;
            }
        }
        
        log.warn("Transacción sin tipo definido, ID: {}", transaccion.getId());
        return 0.0;
    }

    private double calcularMontoFromBilletes(Map<Denominaciones, Integer> billetes) {
        if (billetes == null || billetes.isEmpty()) {
            return 0.0;
        }
        return billetes.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getValor() * entry.getValue())
                .sum();
    }

    private void validarDenominaciones(Map<Denominaciones, Integer> billetes, String contexto) {
        if (billetes == null || billetes.isEmpty()) {
            return;
        }
        
        Set<Denominaciones> denominacionesValidas = Set.of(
            Denominaciones.BILLETE_1,
            Denominaciones.BILLETE_5,
            Denominaciones.BILLETE_10,
            Denominaciones.BILLETE_20,
            Denominaciones.BILLETE_50,
            Denominaciones.BILLETE_100
        );
        
        for (Map.Entry<Denominaciones, Integer> entry : billetes.entrySet()) {
            Denominaciones denominacion = entry.getKey();
            Integer cantidad = entry.getValue();
            
            if (!denominacionesValidas.contains(denominacion)) {
                throw new ValidacionException("Denominación inválida en " + contexto + ": " + denominacion + 
                    ". Solo se permiten billetes de $1, $5, $10, $20, $50 y $100.");
            }
            
            if (cantidad == null || cantidad < 0) {
                throw new ValidacionException("La cantidad de billetes debe ser un número positivo para la denominación " + 
                    denominacion + " en " + contexto + ".");
            }
        }
    }

    private void validarYProcesarTipoTransaccion(TransaccionesTurno transaccion, Map<Denominaciones, Integer> billetes) {
        if (transaccion.getTipoTransaccion() == null || transaccion.getTipoTransaccion().trim().isEmpty()) {
            throw new ValidacionException("El tipo de transacción es requerido.");
        }

        TipoTransaccion tipo;
        try {
            tipo = TipoTransaccion.valueOf(transaccion.getTipoTransaccion().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidacionException("Tipo de transacción inválido: " + transaccion.getTipoTransaccion() + 
                ". Los tipos válidos son: DEPOSITO, RETIRO.");
        }

        if (!tipo.esTransaccionOperativa()) {
            throw new ValidacionException("Solo se permiten transacciones de tipo DEPOSITO o RETIRO en turnos activos.");
        }

        double montoCalculado = calcularMontoFromBilletes(billetes);
        
        if (transaccion.getMontoTotal() != null && !transaccion.getMontoTotal().trim().isEmpty()) {
            try {
                double montoRegistrado = Double.parseDouble(transaccion.getMontoTotal());
                if (Math.abs(montoCalculado - montoRegistrado) > 0.01) {
                    throw new ValidacionException("El monto total registrado ($" + montoRegistrado + 
                        ") no coincide con el monto calculado de billetes ($" + montoCalculado + ")");
                }
            } catch (NumberFormatException e) {
                throw new ValidacionException("El monto total debe ser un número válido.");
            }
        } else {
            transaccion.setMontoTotal(String.valueOf(montoCalculado));
        }
        if (tipo.esDeposito()) {
            log.info("Procesando DEPÓSITO por ${} en turno: {}", montoCalculado, transaccion.getCodigoTurno());
        } else if (tipo.esRetiro()) {
            log.info("Procesando RETIRO por ${} en turno: {}", montoCalculado, transaccion.getCodigoTurno());
        }
    }
}