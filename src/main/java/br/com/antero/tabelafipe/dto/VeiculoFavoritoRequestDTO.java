package br.com.antero.tabelafipe.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record VeiculoFavoritoRequestDTO(
        @NotBlank(message = "O tipo de veículo é obrigatório (ex: carros, motos, caminhoes).")
        String tipoVeiculo,

        @NotBlank(message = "O código da marca não pode estar vazio.")
        String codigoMarca,

        @NotBlank(message = "O código do modelo não pode estar vazio.")
        String codigoModelo,

        @NotBlank(message = "O código do ano não pode estar vazio.")
        String codigoAno) {
}
