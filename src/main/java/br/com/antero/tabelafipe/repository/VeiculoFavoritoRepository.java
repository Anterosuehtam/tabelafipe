package br.com.antero.tabelafipe.repository;

import br.com.antero.tabelafipe.model.VeiculoFavorito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VeiculoFavoritoRepository extends JpaRepository<VeiculoFavorito, UUID> {

    List<VeiculoFavorito> findAllByUsuarioId (UUID usuarioId);
}
