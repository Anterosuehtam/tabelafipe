package br.com.antero.tabelafipe.service;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component // Diz ao Spring: "crie e gerencie uma instância desta classe". Outras classes podem receber ela pronta.
public class ConsumoAPI {
    public String obterDados(String endereco) {
        HttpClient client = HttpClient.newHttpClient(); // O cliente HTTP nativo do Java (desde o 11). É o "navegador" do programa.
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endereco))
                .build();
        HttpResponse<String> response = null;
        try {
            response = client
                    .send(request, HttpResponse.BodyHandlers.ofString()); // Envia e espera a resposta, de forma síncrona. O corpo vem como String.
        } catch (IOException e) {
            throw new RuntimeException("Falha ao chamar " + endereco, e);
        } catch (InterruptedException e) {
            throw new RuntimeException("Falha ao chamar " + endereco, e);
        }

        String json = response.body(); // Devolve o JSON cru, ainda como texto.
        return json;
    }
}
