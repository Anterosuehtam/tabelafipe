package br.com.antero.tabelafipe.service;

import br.com.antero.tabelafipe.dto.UsuarioRequestDTO;
import br.com.antero.tabelafipe.dto.UsuarioResponseDTO;
import br.com.antero.tabelafipe.model.Usuario;
import br.com.antero.tabelafipe.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UsuarioResponseDTO cadastrar(UsuarioRequestDTO dados) {
        if (repository.findByEmail(dados.email()).isPresent()) {
            throw new IllegalArgumentException("Esse e-mail já está em uso.");
        }

        String senhaCriptografada = passwordEncoder.encode(dados.senha());

        Usuario novoUsuario = new Usuario(dados.nome(), dados.email(), senhaCriptografada);

        Usuario usuarioSalvo = repository.save(novoUsuario);

        return new UsuarioResponseDTO(
                usuarioSalvo.getId(),
                usuarioSalvo.getNome(),
                usuarioSalvo.getEmail()
        );
    }

}
