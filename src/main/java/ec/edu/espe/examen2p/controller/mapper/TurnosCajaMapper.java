package ec.edu.espe.examen2p.controller.mapper;

import ec.edu.espe.examen2p.model.TurnosCaja;
import ec.edu.espe.examen2p.controller.dto.TurnosCajaDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TurnosCajaMapper {

    TurnosCajaMapper INSTANCE = Mappers.getMapper(TurnosCajaMapper.class);

    TurnosCajaDTO toDto(TurnosCaja turnosCaja);

    TurnosCaja toEntity(TurnosCajaDTO turnosCajaDto);
}
