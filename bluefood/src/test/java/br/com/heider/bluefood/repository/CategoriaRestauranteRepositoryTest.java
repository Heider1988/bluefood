package br.com.heider.bluefood.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import br.com.heider.bluefood.domain.restaurante.CategoriaRestaurante;
import br.com.heider.bluefood.domain.restaurante.CategoriaRestauranteRepository;

@DataJpaTest
@ActiveProfiles("test")
class CategoriaRestauranteRepositoryTest {

	@Autowired
	private CategoriaRestauranteRepository categoriaRestauranteRepository;

	@Test
	public void testInsertAndDelete() {

		assertThat(categoriaRestauranteRepository).isNotNull();

		CategoriaRestaurante cr = new CategoriaRestaurante();
		cr.setNome("Chinesa");
		cr.setImagem("chinesa.png");
		categoriaRestauranteRepository.saveAndFlush(cr);

		assertThat(cr.getId()).isNotNull();

		CategoriaRestaurante cr2 = categoriaRestauranteRepository.findById(cr.getId()).orElseThrow();
		assertThat(cr.getNome()).isEqualTo(cr2.getNome());

		assertThat(categoriaRestauranteRepository.findAll().size()).isEqualTo(7);

		categoriaRestauranteRepository.delete(cr);

		assertThat(categoriaRestauranteRepository.findAll().size()).isEqualTo(6);

	}

}
