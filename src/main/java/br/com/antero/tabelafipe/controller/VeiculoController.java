package br.com.antero.tabelafipe.controller;

import br.com.antero.tabelafipe.dto.ModeloResponseDTO;
import br.com.antero.tabelafipe.model.Dados;
import br.com.antero.tabelafipe.service.VeiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/veiculos")
public class VeiculoController {
    @Autowired
    private VeiculoService service;

    @GetMapping("/{tipo}/marcas")
    public ResponseEntity<List<Dados>> listarMarcas(@PathVariable String tipo) {
        // O Controller agora só se preocupa com o caminho feliz!
        List<Dados> marcas = service.obterMarcas(tipo);
        return ResponseEntity.ok(marcas);
    }

    @GetMapping("/{tipo}/{codigoMarca}/modelos")
    public ResponseEntity<ModeloResponseDTO> listarModelos(
            @PathVariable String tipo,
            @PathVariable String codigoMarca) {

        ModeloResponseDTO resposta = service.obterModelosPorMarca(tipo, codigoMarca);
        return ResponseEntity.ok(resposta);
    }
}
