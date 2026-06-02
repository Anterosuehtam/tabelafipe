package br.com.antero.tabelafipe.service;

import br.com.antero.tabelafipe.model.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {
    @Value("${JWT_SECRET}")
    private String secret;

    public String gerarToken(Usuario usuario) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(secret);

            return JWT.create()
                    .withIssuer("FipeAnalyzer API") // Quem está emitindo o token
                    .withSubject(usuario.getEmail()) // A quem este token pertence
                    .withExpiresAt(dataExpiracao()) // Quando ele perde a validade
                    .sign(algoritmo);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar o token JWT", exception);
        }
    }

    //O token expira em 2 horas
    private Instant dataExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }

    public String getSubject(String tokenJWT) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(secret);
            return JWT.require(algoritmo)
                    .withIssuer("FipeAnalyzer API")
                    .build()
                    .verify(tokenJWT) // Se o token estiver expirado ou adulterado, estoura erro aqui
                    .getSubject();
        } catch (com.auth0.jwt.exceptions.JWTVerificationException exception) {
            throw new RuntimeException("Token JWT inválido ou expirado!", exception);
        }
    }
}
