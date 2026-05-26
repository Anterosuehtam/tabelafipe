package br.com.antero.tabelafipe.dto;

import java.util.UUID;

public record VeiculoFavoritoResponseDTO(UUID idFavorito,
                                         String marca,
                                         String modelo,
                                         String ano,
                                         String valorSalvo
                                         ) {
}
