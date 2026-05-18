package br.com.antero.tabelafipe.service;

import br.com.antero.tabelafipe.dto.AvaliacaoDTO;
import br.com.antero.tabelafipe.dto.HistoricoResponseDTO;
import br.com.antero.tabelafipe.dto.ModeloResponseDTO;
import br.com.antero.tabelafipe.model.Dados;
import br.com.antero.tabelafipe.model.Modelos;
import br.com.antero.tabelafipe.model.Veiculo;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class VeiculoService {

    private final ConsumoAPI consumo = new ConsumoAPI();
    private final ConverteDados conversor = new ConverteDados();
    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";

    private String validarEMapearCategoria(String tipo) {
        String tipoFormatado = tipo.toLowerCase();
        if (!tipoFormatado.contains("car") && !tipoFormatado.contains("mot") && !tipoFormatado.contains("cam")) {
            throw new IllegalArgumentException("Tipo de veículo inválido. Use: carros, motos ou caminhoes.");
        }
        if (tipoFormatado.contains("car")) return "carros";
        if (tipoFormatado.contains("mot")) return "motos";
        return "caminhoes";
    }

    public List<Dados> obterMarcas(String tipo) {
        String categoria = validarEMapearCategoria(tipo);
        String endereco = URL_BASE + categoria + "/marcas";
        try {
            var json = consumo.obterDados(endereco);
            return conversor.obterLista(json, Dados.class);
        } catch (RuntimeException e) {
            throw new RuntimeException("Falha ao comunicar com a API da Tabela Fipe.");
        }
    }

    public ModeloResponseDTO obterModelosPorMarca(String tipo, String codigoMarca) {
        String categoria = validarEMapearCategoria(tipo);
        String endereco = URL_BASE + categoria + "/marcas/" + codigoMarca + "/modelos";

        try {
            var json = consumo.obterDados(endereco);
            var modelosApi = conversor.obterDados(json, Modelos.class);

            return new ModeloResponseDTO(tipo, codigoMarca, modelosApi.modelos());
        } catch (RuntimeException e) {
            throw new RuntimeException("Falha ao comunicar com a API da Tabela Fipe ou marca não encontrada.");
        }
    }

    public HistoricoResponseDTO obterHistoricoCompleto(String tipo, String codigoMarca, String codigoModelo) {
        String categoria = validarEMapearCategoria(tipo);
        String enderecoBase = URL_BASE + categoria + "/marcas/" + codigoMarca + "/modelos/" + codigoModelo;

        try {
            var jsonAnos = consumo.obterDados(enderecoBase + "/anos");
            List<Dados> anos = conversor.obterLista(jsonAnos, Dados.class);

            List<AvaliacaoDTO> avaliacoes = new java.util.ArrayList<>();
            String nomeMarca = "";
            String nomeModelo = "";

            for (Dados ano : anos) {
                var jsonVeiculo = consumo.obterDados(enderecoBase + "/anos/" + ano.codigo());
                Veiculo veiculoApi = conversor.obterDados(jsonVeiculo, Veiculo.class);

                if (nomeMarca.isEmpty()) {
                    nomeMarca = veiculoApi.marca();
                    nomeModelo = veiculoApi.modelo();
                }

                String anoFormatado = veiculoApi.ano() == 32000 ? "Zero KM" : String.valueOf(veiculoApi.ano());

                avaliacoes.add(new AvaliacaoDTO(anoFormatado, veiculoApi.valor(), veiculoApi.tipoCombustivel()));
            }

            return new HistoricoResponseDTO(nomeMarca, nomeModelo, avaliacoes);

        } catch (RuntimeException e) {
            throw new RuntimeException("Falha ao buscar o histórico de avaliações do veículo.");
        }
    }
}
