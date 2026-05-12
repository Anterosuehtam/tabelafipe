package br.com.antero.tabelafipe.principal;

import br.com.antero.tabelafipe.service.ConsumoAPI;

import java.util.Scanner;

public class Principal {
    Scanner scanner = new Scanner(System.in);
    private ConsumoAPI consumo = new ConsumoAPI();

    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";

    public void exibeMenu() {
        var menu = """
                OPÇÕES
                - Carro
                - Moto
                - Caminhão
                
                Digite uma das opções para consultar: 
                """;

        System.out.println(menu);

        var opcao = scanner.nextLine();
        String endereco;

        if (opcao.toLowerCase().contains("carr")) {
            endereco = URL_BASE + "carros/marcas";
        } else if (opcao.toLowerCase().contains("mot")) {
            endereco = URL_BASE + "motos/marcas";
        } else {
            endereco = URL_BASE + "caminhoes/marcas";
        }

        var json = consumo.obterDados(endereco);
        System.out.println(json);
    }
}