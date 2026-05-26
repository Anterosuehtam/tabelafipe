package br.com.antero.tabelafipe.service;

import br.com.antero.tabelafipe.dto.VeiculoFavoritoRequestDTO;
import br.com.antero.tabelafipe.dto.VeiculoFavoritoResponseDTO;
import br.com.antero.tabelafipe.model.Usuario;
import br.com.antero.tabelafipe.model.Veiculo;
import br.com.antero.tabelafipe.model.VeiculoFavorito;
import br.com.antero.tabelafipe.repository.UsuarioRepository;
import br.com.antero.tabelafipe.repository.VeiculoFavoritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
