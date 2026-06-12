package br.com.antero.tabelafipe.service;

import br.com.antero.tabelafipe.dto.AnaliseFinanceiraDTO;
import br.com.antero.tabelafipe.dto.VeiculoFavoritoRequestDTO;
import br.com.antero.tabelafipe.dto.VeiculoFavoritoResponseDTO;
import br.com.antero.tabelafipe.model.Usuario;
import br.com.antero.tabelafipe.model.Veiculo;
import br.com.antero.tabelafipe.model.VeiculoFavorito;
import br.com.antero.tabelafipe.repository.VeiculoFavoritoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

//Avisa que o Mockito vai assumir o controle desta classe de testes
@ExtendWith(MockitoExtension.class)
class GaragemServiceTest {

    //@InjectMocks é a classe REAL que estamos testando
    @InjectMocks
    private GaragemService garagemService;

    //@Mock são os DUBLÊS das dependências que o Service precisa
    @Mock
    private VeiculoFavoritoRepository garagemRepository;

    @Mock
    private ConsumoAPI consumoAPI;

    @Mock
    private ConverteDados conversor;

    @Test
    @DisplayName("Deve retornar a lista de veículos favoritos do usuário logado")
    void deveListarGaragemComSucesso() {
        // --- 1. ARRANGE (Preparar o cenário) ---
        Usuario usuarioLogado = new Usuario();
        usuarioLogado.setId(UUID.randomUUID());

        VeiculoFavorito carroMock = new VeiculoFavorito();
        carroMock.setMarca("Audi");
        carroMock.setModelo("A3 Sedan");
        carroMock.setAno("2019");
        carroMock.setValorSalvo("R$ 149.773,00");

        // Ensinamos o dublê: "Quando alguém buscar por este ID, devolva esta lista falsa"
        when(garagemRepository.findAllByUsuarioId(usuarioLogado.getId()))
                .thenReturn(List.of(carroMock));

        // --- 2. ACT (Agir - Executar o metodo real) ---
        List<VeiculoFavoritoResponseDTO> resultado = garagemService.listarGaragem(usuarioLogado);

        // --- 3. ASSERT (Verificar se o resultado foi o esperado) ---
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Audi", resultado.get(0).marca());
        assertEquals("A3 Sedan", resultado.get(0).modelo());
    }

    @Test
    @DisplayName("Deve calcular a depreciação corretamente quando o valor atual da Fipe for menor")
    void deveCalcularDepreciacao() {
        // --- 1. ARRANGE (Preparar o cenário) ---
        UUID idCarro = UUID.randomUUID();

        // A) Criamos o carro que fingiremos estar salvo no banco de dados (Valor antigo: 100 mil)
        VeiculoFavorito carroSalvo = new VeiculoFavorito();
        carroSalvo.setTipoVeiculo("carros");
        carroSalvo.setCodigoMarca("6");
        carroSalvo.setCodigoModelo("8560");
        carroSalvo.setCodigoAno("2019-1");
        carroSalvo.setValorSalvo("R$ 100.000,00");

        // Ensinamos o banco de dados falso a retornar este carro
        when(garagemRepository.findById(idCarro)).thenReturn(java.util.Optional.of(carroSalvo));

        // B) Simulamos a resposta da internet (API da Fipe)
        String jsonFalso = "{\"Valor\":\"R$ 90.000,00\"}";
        // Usamos anyString() para dizer: "Qualquer URL que o Service tentar acessar, devolva esse JSON"
        when(consumoAPI.obterDados(org.mockito.ArgumentMatchers.anyString())).thenReturn(jsonFalso);

        // C) Simulamos o conversor Jackson transformando o JSON no objeto Veiculo (Valor atual: 90 mil)
        Veiculo veiculoAtualizado = new Veiculo("R$ 90.000,00", "Audi", "A3 Sedan", 2019, "Gasolina");
        when(conversor.obterDados(jsonFalso, Veiculo.class)).thenReturn(veiculoAtualizado);


        // --- 2. ACT (Executar a nossa matemática) ---
        AnaliseFinanceiraDTO resultado = garagemService.analisarVeiculo(idCarro);


        // --- 3. ASSERT (Verificar se os cálculos ficaram perfeitos) ---
        assertNotNull(resultado);
        assertEquals("R$ 100.000,00", resultado.valorSalvo());
        assertEquals("R$ 90.000,00", resultado.valorAtual());

        // Aqui o JUnit garante que o seu código fez a conta certa (-10 mil e -10%)
        assertEquals("R$-10000,00", resultado.diferencaValor().replace(" ", ""));
        assertEquals("-10,00%", resultado.diferencaPorcentagem().replace(" ", ""));
        assertEquals("DESVALORIZOU", resultado.status());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar analisar um veículo que não existe na garagem")
    void deveLancarExcecaoQuandoVeiculoNaoExistir() {
        // --- 1. ARRANGE (Preparar o cenário) ---
        UUID idInvalido = UUID.randomUUID();

        // Ensinamos o banco falso a dizer: "Não encontrei nenhum carro com esse ID"
        when(garagemRepository.findById(idInvalido)).thenReturn(java.util.Optional.empty());

        // --- 2 & 3. ACT & ASSERT (Agir e Verificar juntos) ---
        // O JUnit 'segura' a exceção esperada para o teste não quebrar a suíte
        IllegalArgumentException excecaoCapturada = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> garagemService.analisarVeiculo(idInvalido)
        );

        // Verificamos se a mensagem do erro é exatamente a que você programou
        org.junit.jupiter.api.Assertions.assertEquals("Veículo não encontrado na garagem.", excecaoCapturada.getMessage());
    }

    @Test
    @DisplayName("Deve consultar a Fipe e salvar um novo veículo na garagem com sucesso")
    void deveSalvarVeiculoComSucesso() {
        // --- 1. ARRANGE (Preparar o cenário) ---
        Usuario usuarioLogado = new Usuario();
        usuarioLogado.setId(UUID.randomUUID());

        // A) Simulamos o DTO chegando do Controller (A requisição do usuário)
        VeiculoFavoritoRequestDTO requestDTO = new VeiculoFavoritoRequestDTO("carros", "6", "8560", "2019-1");

        // B) Simulamos a internet devolvendo o JSON daquele carro
        String jsonFalso = "{\"Valor\":\"R$ 100.000,00\", \"Marca\":\"Audi\", \"Modelo\":\"A3 Sedan\"}";
        when(consumoAPI.obterDados(org.mockito.ArgumentMatchers.anyString())).thenReturn(jsonFalso);

        // C) Simulamos o conversor transformando o JSON em objeto
        Veiculo veiculoFipe = new Veiculo("R$ 100.000,00", "Audi", "A3 Sedan", 2019, "Gasolina");
        when(conversor.obterDados(jsonFalso, Veiculo.class)).thenReturn(veiculoFipe);

        // D) Simulamos o banco de dados salvando e devolvendo o carro com um ID gerado
        VeiculoFavorito carroSalvo = new VeiculoFavorito();
        carroSalvo.setId(UUID.randomUUID());
        carroSalvo.setMarca("Audi");
        carroSalvo.setModelo("A3 Sedan");
        carroSalvo.setValorSalvo("R$ 100.000,00");

        // Ensinamos o dublê: "Qualquer carro que mandarem você salvar, devolva este carroSalvo"
        when(garagemRepository.save(org.mockito.ArgumentMatchers.any(VeiculoFavorito.class))).thenReturn(carroSalvo);


        // --- 2. ACT (Agir) ---
        VeiculoFavoritoResponseDTO resultado = garagemService.salvarFavorito(requestDTO, usuarioLogado);


        // --- 3. ASSERT (Verificar) ---
        assertNotNull(resultado);
        assertEquals("Audi", resultado.marca());
        assertEquals("R$ 100.000,00", resultado.valorSalvo());

        // A MÁGICA: O JUnit garante que o metodo .save() do banco foi acionado EXATAMENTE 1 VEZ
        org.mockito.Mockito.verify(garagemRepository, org.mockito.Mockito.times(1))
                .save(org.mockito.ArgumentMatchers.any(VeiculoFavorito.class));
    }
}