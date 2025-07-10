package ec.edu.espe.examen2p.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import ec.edu.espe.examen2p.model.TurnosCaja;

public interface TurnosCajaRepository extends MongoRepository<TurnosCaja, String> {
    
}
