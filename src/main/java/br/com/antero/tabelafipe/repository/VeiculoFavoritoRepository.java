package br.com.antero.tabelafipe.repository;

import br.com.antero.tabelafipe.model.VeiculoFavorito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VeiculoFavoritoRepository extends JpaRepository<VeiculoFavorito, UUID> {

    List<VeiculoFavorito> findAllByUsuarioId (UUID usuarioId); // SELECT * FROM veiculos_favoritos WHERE usuario_id = ?

    Optional<VeiculoFavorito> findByIdAndUsuarioId(UUID id, UUID usuarioId);
}
