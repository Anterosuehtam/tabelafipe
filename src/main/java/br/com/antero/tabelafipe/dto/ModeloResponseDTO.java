package br.com.antero.tabelafipe.dto;

import br.com.antero.tabelafipe.model.Dados;
import java.util.List;

public record ModeloResponseDTO(
        String tipoVeiculo,
        String codigoMarca,
        List<Dados> modelos
) {
}
