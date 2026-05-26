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

        Veiculo veiculoFipe;
        try {
            String json = consumo.obterDados(urlFipe);
            veiculoFipe = conversor.obterDados(json, Veiculo.class);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Veículo não encontrado na Tabela Fipe. Verifique os códigos enviados.");
        }

        String anoFormatado = veiculoFipe.ano() == 32000 ? "Zero KM" : String.valueOf(veiculoFipe.ano());

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
}
