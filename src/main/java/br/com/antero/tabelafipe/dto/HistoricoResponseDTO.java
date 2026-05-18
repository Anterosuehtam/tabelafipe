package br.com.antero.tabelafipe.dto;
import java.util.List;

public record HistoricoResponseDTO(
        String marca,
        String modelo,
        List<AvaliacaoDTO> historico
) {
}
