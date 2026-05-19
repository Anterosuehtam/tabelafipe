package br.com.antero.tabelafipe.controller;

import br.com.antero.tabelafipe.dto.HistoricoResponseDTO;
import br.com.antero.tabelafipe.dto.ModeloResponseDTO;
import br.com.antero.tabelafipe.model.Dados;
import br.com.antero.tabelafipe.service.VeiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/veiculos")
public class VeiculoController {
    @Autowired
    private VeiculoService service;

    @GetMapping("/{tipo}/marcas")
    public ResponseEntity<List<Dados>> listarMarcas(@PathVariable String tipo) {
        List<Dados> marcas = service.obterMarcas(tipo);
        return ResponseEntity.ok(marcas);
    }

    @GetMapping("/{tipo}/{codigoMarca}/modelos")
    public ResponseEntity<ModeloResponseDTO> listarModelos(
            @PathVariable String tipo,
            @PathVariable String codigoMarca,
            @RequestParam(required = false) String nome){

        ModeloResponseDTO resposta = service.obterModelosPorMarca(tipo, codigoMarca, nome);
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/{tipo}/{codigoMarca}/{codigoModelo}/historico")
    public ResponseEntity<HistoricoResponseDTO> obterHistorico(
            @PathVariable String tipo,
            @PathVariable String codigoMarca,
            @PathVariable String codigoModelo) {

        HistoricoResponseDTO resposta = service.obterHistoricoCompleto(tipo, codigoMarca, codigoModelo);
        return ResponseEntity.ok(resposta);
    }
}
