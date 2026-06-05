package br.com.antero.tabelafipe.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                //Diz ao Spring que a nossa API não guarda estado na memória (Sessão Stateless)
                .sessionManagement(sm -> sm.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))

                //Define as regras de quem pode acessar o quê
                .authorizeHttpRequests(req -> {
                    //O Login tem que ser público
                    req.requestMatchers(org.springframework.http.HttpMethod.POST, "/api/auth/login").permitAll();
                    //O Cadastro de Usuários tem que ser público
                    req.requestMatchers(org.springframework.http.HttpMethod.POST, "/api/usuarios").permitAll();

                    req.requestMatchers("/error").permitAll();

                    //QUALQUER outra rota (incluindo todas as da Garagem) precisa de autenticação
                    req.anyRequest().authenticated();
                })

                //Avisa ao Spring para rodar o NOSSO filtro ANTES do filtro padrão dele
                .addFilterBefore(securityFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        //Cria o provedor já injetando quem busca o usuário diretamente no construtor
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);

        //Ensina qual algoritmo descriptografa a senha (O BCrypt)
        authProvider.setPasswordEncoder(passwordEncoder);

        //Retorna o gerente montado dentro de uma Lista (Exigência do Spring Security 6)
        return new ProviderManager(List.of(authProvider));
    }
}