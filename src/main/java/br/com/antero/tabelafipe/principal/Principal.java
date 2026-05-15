package br.com.antero.tabelafipe.principal;

import br.com.antero.tabelafipe.model.Dados;
import br.com.antero.tabelafipe.model.Modelos;
import br.com.antero.tabelafipe.model.Veiculo;
import br.com.antero.tabelafipe.service.ConsumoAPI;
import br.com.antero.tabelafipe.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    Scanner scanner = new Scanner(System.in);
    private ConsumoAPI consumo = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();

    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";

    public void exibeMenu() {
        var menu = """
                ===================================
                🚗 🏍️ 🚛  CONSULTA TABELA FIPE 
                ===================================
                
                Escolha o tipo de veículo para buscar:
                
                ▶ Carro
                ▶ Moto
                ▶ Caminhão
                
                👉 Digite a opção desejada: 
                """;

        String endereco = "";

        while (endereco.isEmpty()) {
            System.out.println(menu);
            var opcao = scanner.nextLine();

            if (opcao.toLowerCase().contains("carr")) {
                endereco = URL_BASE + "carros/marcas";
            } else if (opcao.toLowerCase().contains("mot")) {
                endereco = URL_BASE + "motos/marcas";
            } else if (opcao.toLowerCase().contains("cam")) {
                endereco = URL_BASE + "caminhoes/marcas";
            } else {
                System.out.println("\n Opção inválida! Por favor, digite apenas Carro, Moto ou Caminhão.");
            }
        }

        var json = consumo.obterDados(endereco);

        var marcas = conversor.obterLista(json, Dados.class);
        marcas.stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        String codigoMarca = "";
        boolean marcaEncontrada = false;

        while (!marcaEncontrada) {
            System.out.println("\nInforme o código da marca para consulta: ");
            codigoMarca = scanner.nextLine();

            String codigoDigitado = codigoMarca;

            marcaEncontrada = marcas.stream()
                    .anyMatch(marca -> marca.codigo().equals(codigoDigitado));

            if (!marcaEncontrada) {
                System.out.println("Código inválido! Por favor, digite um código válido da lista acima.");
            }
        }

        endereco = endereco + "/" + codigoMarca + "/modelos";
        json = consumo.obterDados(endereco);
        var modeloLista = conversor.obterDados(json, Modelos.class);

        System.out.println("\nModelos dessa marca: ");
        modeloLista.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("\nDigite um trecho do nome do carro a ser buscado: ");
        var nomeVeiculo = scanner.nextLine();

        List<Dados> modelosFiltrados = modeloLista.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                .collect(Collectors.toList());

        System.out.println("\nModelos filtrados: ");
        modelosFiltrados.forEach(System.out::println);

        System.out.println("\nDigite o código do modelo para buscar os valores de avaliação: ");
        var codigoModelo = scanner.nextLine();

        endereco = endereco + "/" + codigoModelo + "/anos";
        json = consumo.obterDados(endereco);

        List<Dados> anos = conversor.obterLista(json, Dados.class);

        List<Veiculo> veiculos = new ArrayList<>();

        for (int i = 0; i < anos.size(); i++) {
         var enderecoAnos = endereco + "/" + anos.get(i).codigo();
         json = consumo.obterDados(enderecoAnos);
         Veiculo veiculo = conversor.obterDados(json, Veiculo.class);
         veiculos.add(veiculo);
        }

        System.out.println("\nTodos os veículso filtrados com avaliações por ano: ");
        veiculos.forEach(System.out::println);
    }
}