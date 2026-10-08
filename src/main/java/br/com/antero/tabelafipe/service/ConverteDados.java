package br.com.antero.tabelafipe.service;

import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.type.CollectionType;

import java.util.List;

@Component
public class ConverteDados implements IConverteDados{
    private ObjectMapper mapper = new ObjectMapper(); // A ferramenta do Jackson que lê JSON e cria objetos.

    @Override
    // Metodo genérico. T é "um tipo que será definido na chamada". Você passa Veiculo.class e recebe um Veiculo;
    // passa Modelos.class e recebe Modelos. Uma função só serve para todos.
    public <T> T obterDados(String json, Class<T> classe) {
        return mapper.readValue(json, classe);
    }

    @Override
    // Mesma ideia, mas o JSON é um array. Por causa do type erasure do Java, List<T> não guarda o T em tempo
    // de execução, então constructCollectionType diz ao Jackson o tipo dos elementos.
    public <T> List<T> obterLista(String json, Class<T> classe) {
        CollectionType lista = mapper.getTypeFactory()
                .constructCollectionType(List.class, classe);
        return mapper.readValue(json, lista);
    }
}