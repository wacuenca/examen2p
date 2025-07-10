package ec.edu.espe.examen2p.controller.mapper;

import ec.edu.espe.examen2p.model.TransaccionesTurno;
import ec.edu.espe.examen2p.controller.dto.TransaccionesTurnoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TransaccionesTurnoMapper {

    TransaccionesTurnoMapper INSTANCE = Mappers.getMapper(TransaccionesTurnoMapper.class);

    TransaccionesTurnoDTO toDto(TransaccionesTurno transaccionesTurno);

    TransaccionesTurno toEntity(TransaccionesTurnoDTO transaccionesTurnoDto);
}
