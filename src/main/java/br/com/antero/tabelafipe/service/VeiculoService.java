package br.com.antero.tabelafipe.service;

import br.com.antero.tabelafipe.model.Dados;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VeiculoService {

    private final ConsumoAPI consumo = new ConsumoAPI();
    private final ConverteDados conversor = new ConverteDados();
    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";

    public List<Dados> obterMarcas(String tipo) {

        String tipoFormatado = tipo.toLowerCase();
        if (!tipoFormatado.contains("car") && !tipoFormatado.contains("mot") && !tipoFormatado.contains("cam")) {
            throw new IllegalArgumentException("Tipo de veículo inválido. Use: carros, motos ou caminhoes.");
        }

        String categoria = "";
        if (tipoFormatado.contains("car")) categoria = "carros";
        else if (tipoFormatado.contains("mot")) categoria = "motos";
        else if (tipoFormatado.contains("cam")) categoria = "caminhoes";

        String endereco = URL_BASE + categoria + "/marcas";

        try {
            var json = consumo.obterDados(endereco);
            return conversor.obterLista(json, Dados.class);
        } catch (RuntimeException e) {
            throw new RuntimeException("Falha ao comunicar com a API da Tabela Fipe.");
        }
    }
}
