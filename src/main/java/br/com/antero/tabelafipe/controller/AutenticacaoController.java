package br.com.antero.tabelafipe.controller;

import br.com.antero.tabelafipe.dto.AutenticacaoRequestDTO;
import br.com.antero.tabelafipe.dto.TokenResponseDTO;
import br.com.antero.tabelafipe.model.Usuario;
import br.com.antero.tabelafipe.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AutenticacaoController {
    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> efetuarLogin(@RequestBody AutenticacaoRequestDTO dados) {

        //Empacota as credenciais no formato padrão que o Spring Security entende
        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());

        //Aciona o gerente para validar a senha (se errar a senha, ele lança exceção sozinho aqui)
        var authentication = manager.authenticate(authenticationToken);

        //Pega o usuário autenticado e manda para a fábrica de crachás(JWT)
        var tokenJWT = tokenService.gerarToken((Usuario) authentication.getPrincipal());

        //Devolve o token envelopado no DTO
        return ResponseEntity.ok(new TokenResponseDTO(tokenJWT));
    }
}
