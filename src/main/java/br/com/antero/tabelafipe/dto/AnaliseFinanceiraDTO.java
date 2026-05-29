package br.com.antero.tabelafipe.dto;

public record AnaliseFinanceiraDTO(String marca,
                                   String modelo,
                                   String ano,
                                   String valorSalvo,
                                   String valorAtual,
                                   String diferencaValor,
                                   String diferencaPorcentagem,
                                   String status) {
}
