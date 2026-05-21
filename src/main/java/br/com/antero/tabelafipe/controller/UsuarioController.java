package br.com.antero.tabelafipe.controller;

import br.com.antero.tabelafipe.dto.UsuarioRequestDTO;
import br.com.antero.tabelafipe.dto.UsuarioResponseDTO;
import br.com.antero.tabelafipe.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> cadastrar(@RequestBody UsuarioRequestDTO dados,
                                                        UriComponentsBuilder uriBuilder) {

        UsuarioResponseDTO resposta = service.cadastrar(dados);

        var uri = uriBuilder.path("/api/usuarios/{id}").buildAndExpand(resposta.id()).toUri();

        return ResponseEntity.created(uri).body(resposta);
    }
}
