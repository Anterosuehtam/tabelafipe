package br.com.antero.tabelafipe.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/*O JSON de modelos traz também "anos", e o record Modelos só declara modelos.
Sem essa anotação, o Jackson lançaria erro por causa do campo desconhecido. Com ela, ele
ignora o que não foi declarado. Também protege se a FIPE adicionar campos novos amanhã.*/
@JsonIgnoreProperties(ignoreUnknown = true)
public record Modelos(List<Dados> modelos) {
}
