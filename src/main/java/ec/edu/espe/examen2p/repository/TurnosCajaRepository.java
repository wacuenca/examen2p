package ec.edu.espe.examen2p.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import ec.edu.espe.examen2p.model.TurnosCaja;

public interface TurnosCajaRepository extends MongoRepository<TurnosCaja, String> {
    Optional<TurnosCaja> findByCodigoTurno(String codigoTurno);
}
