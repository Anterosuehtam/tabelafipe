package br.com.antero.tabelafipe.dto;

import java.util.UUID;

public record VeiculoFavoritoRequestDTO(UUID usuarioId,
                                        String tipoVeiculo,
                                        String codigoMarca,
                                        String codigoModelo,
                                        String codigoAno) {
}
