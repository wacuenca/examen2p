package ec.edu.espe.examen2p.controller.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class DenominacionDTO {
    private BigDecimal valor;
    private Integer cantidad;

}
