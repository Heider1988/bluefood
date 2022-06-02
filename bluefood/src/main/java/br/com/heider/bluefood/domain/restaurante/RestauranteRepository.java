package br.com.heider.bluefood.domain.restaurante;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RestauranteRepository extends JpaRepository<Restaurante, Integer> {

	public Restaurante findByEmail(String email);

	// MÉTODO DO CURSO - BUG AO REALIZAR BUSCAS COM AS PRIMEIRAS LETRAS DE UM RESTAURANTE
	// public List<Restaurante> findByNomeIgnoreCaseContaining(String nome);

	// MÉTODO QUE CORRIGIU O BUG AO REALIZAR BUSCAS UTILIZANDO APENAS AS PRIMEIRAS LETRAS
	@Query("select r from Restaurante r where r.nome like %:nome%")
	public List<Restaurante> findByRestauranteNomeContaining(@Param("nome") String nome);

	public List<Restaurante> findByCategorias_Id(Integer categoriaId);

}
