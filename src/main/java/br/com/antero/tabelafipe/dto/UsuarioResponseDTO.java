package br.com.antero.tabelafipe.dto;

import java.util.UUID;

public record UsuarioResponseDTO(UUID id,
                                 String nome,
                                 String email) {
}
