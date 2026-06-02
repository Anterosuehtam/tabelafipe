package br.com.antero.tabelafipe.infra.security;

import br.com.antero.tabelafipe.repository.UsuarioRepository;
import br.com.antero.tabelafipe.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository repository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        //Extrai o token do cabeçalho "Authorization"
        String tokenJWT = recuperarToken(request);

        if (tokenJWT != null) {
            //Valida o token e resgata o e-mail do usuário
            String subject = tokenService.getSubject(tokenJWT);

            //Busca o usuário no banco para garantir que ele ainda existe
            var usuario = repository.findByEmail(subject).orElse(null);

            if (usuario != null) {
                //Cria o objeto de autenticação oficial do Spring
                var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());

                //Força a autenticação no contexto do Spring para esta requisição
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        //Passa a requisição para a frente (para o próximo filtro ou para o Controller)
        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            //O padrão de mercado para tokens é começar com o prefixo "Bearer "
            return authorizationHeader.replace("Bearer ", "").trim();
        }
        return null;
    }
}