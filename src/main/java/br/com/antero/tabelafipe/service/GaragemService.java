package br.com.antero.tabelafipe.service;

import br.com.antero.tabelafipe.dto.AnaliseFinanceiraDTO;
import br.com.antero.tabelafipe.dto.VeiculoFavoritoRequestDTO;
import br.com.antero.tabelafipe.dto.VeiculoFavoritoResponseDTO;
import br.com.antero.tabelafipe.model.Usuario;
import br.com.antero.tabelafipe.model.Veiculo;
import br.com.antero.tabelafipe.model.VeiculoFavorito;
import br.com.antero.tabelafipe.repository.UsuarioRepository;
import br.com.antero.tabelafipe.repository.VeiculoFavoritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class GaragemService {

    @Autowired
    private VeiculoFavoritoRepository garagemRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ConsumoAPI consumo;

    @Autowired
    private ConverteDados conversor;

    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";

    public VeiculoFavoritoResponseDTO salvarFavorito(VeiculoFavoritoRequestDTO dados) {
        Usuario usuario = usuarioRepository.findById(dados.usuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado no sistema."));

        String urlFipe = URL_BASE + dados.tipoVeiculo() + "/marcas/" + dados.codigoMarca() +
                "/modelos/" + dados.codigoModelo() + "/anos/" + dados.codigoAno();

        String json = consumo.obterDados(urlFipe);
        System.out.println("JSON RETORNADO PELA FIPE: " + json);

        if (json.contains("error")) {
            throw new IllegalArgumentException("Veículo não encontrado na Tabela Fipe. Verifique se os códigos de Marca, Modelo e Ano estão corretos.");
        }

        Veiculo veiculoFipe;
        try {
            veiculoFipe = conversor.obterDados(json, Veiculo.class);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Erro ao interpretar os dados da Fipe.");
        }

        if (veiculoFipe.ano() == null) {
            throw new IllegalArgumentException("A API da Fipe não retornou o ano do veículo.");
        }

        Integer anoVeiculo = veiculoFipe.ano();
        String anoFormatado = anoVeiculo == 32000 ? "Zero KM" : String.valueOf(anoVeiculo);

        VeiculoFavorito favorito = new VeiculoFavorito(
                usuario,
                dados.tipoVeiculo(),
                dados.codigoMarca(),
                dados.codigoModelo(),
                dados.codigoAno(),
                veiculoFipe.marca(),
                veiculoFipe.modelo(),
                anoFormatado,
                veiculoFipe.valor()
        );

        VeiculoFavorito salvo = garagemRepository.save(favorito);

        return new VeiculoFavoritoResponseDTO(
                salvo.getId(),
                salvo.getMarca(),
                salvo.getModelo(),
                salvo.getAno(),
                salvo.getValorSalvo()
        );
    }

    public List<VeiculoFavoritoResponseDTO> listarGaragem(UUID usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }

        List<VeiculoFavorito> favoritos = garagemRepository.findAllByUsuarioId(usuarioId);

        return favoritos.stream()
                .map(veiculo -> new VeiculoFavoritoResponseDTO(
                        veiculo.getId(),
                        veiculo.getMarca(),
                        veiculo.getModelo(),
                        veiculo.getAno(),
                        veiculo.getValorSalvo()
                ))
                .toList();
    }

    public AnaliseFinanceiraDTO analisarVeiculo(UUID idFavorito) {

        VeiculoFavorito favorito = garagemRepository.findById(idFavorito)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado na garagem."));

        String urlFipe = URL_BASE + favorito.getTipoVeiculo() + "/marcas/" + favorito.getCodigoMarca() +
                "/modelos/" + favorito.getCodigoModelo() + "/anos/" + favorito.getCodigoAno();

        String json = consumo.obterDados(urlFipe);
        if (json.contains("error")) {
            throw new IllegalArgumentException("Não foi possível consultar o valor atualizado na Fipe.");
        }

        Veiculo veiculoAtualizado = conversor.obterDados(json, Veiculo.class);

        BigDecimal valorSalvo = br.com.antero.tabelafipe.util.FormatadorMoeda.converterParaBigDecimal(favorito.getValorSalvo());
        BigDecimal valorAtual = br.com.antero.tabelafipe.util.FormatadorMoeda.converterParaBigDecimal(veiculoAtualizado.valor());

        BigDecimal diferenca = valorAtual.subtract(valorSalvo);

        BigDecimal porcentagem = diferenca
                .divide(valorSalvo, 4, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));

        String status;
        int comparacao = diferenca.compareTo(BigDecimal.ZERO);
        if (comparacao > 0) {
            status = "VALORIZOU";
        } else if (comparacao < 0) {
            status = "DESVALORIZOU";
        } else {
            status = "ESTAVEL";
        }

        return new AnaliseFinanceiraDTO(
                favorito.getMarca(),
                favorito.getModelo(),
                favorito.getAno(),
                favorito.getValorSalvo(),
                veiculoAtualizado.valor(),
                String.format("R$ %.2f", diferenca),
                String.format("%.2f%%", porcentagem),
                status
        );
    }
}
