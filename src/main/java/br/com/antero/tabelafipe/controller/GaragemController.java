package br.com.antero.tabelafipe.controller;

import br.com.antero.tabelafipe.dto.VeiculoFavoritoRequestDTO;
import br.com.antero.tabelafipe.dto.VeiculoFavoritoResponseDTO;
import br.com.antero.tabelafipe.service.GaragemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("api/garagem")
public class GaragemController {

    @Autowired
    private GaragemService service;

    @PostMapping
    public ResponseEntity<VeiculoFavoritoResponseDTO> adicionarFavorito(
            @RequestBody VeiculoFavoritoRequestDTO dados,
            UriComponentsBuilder uriBuilder) {

        VeiculoFavoritoResponseDTO resposta = service.salvarFavorito(dados);

        var uri = uriBuilder.path("/api/garagem/{id}").buildAndExpand(resposta.idFavorito()).toUri();
        return ResponseEntity.created(uri).body(resposta);
    }
}
