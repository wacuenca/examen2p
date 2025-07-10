package ec.edu.espe.examen2p.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import ec.edu.espe.examen2p.model.TransaccionesTurno;
@Repository
public interface TransaccionesTurnoRepository extends MongoRepository<TransaccionesTurno,String>{

}
