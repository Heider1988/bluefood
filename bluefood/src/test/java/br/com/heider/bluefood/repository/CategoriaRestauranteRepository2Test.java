package br.com.heider.bluefood.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import br.com.heider.bluefood.domain.restaurante.CategoriaRestaurante;

@DataJpaTest
@ActiveProfiles("test")
class CategoriaRestauranteRepository2Test {

	@Autowired
	private TestEntityManager em;

	@Test
	public void testInsertAndDelete() {

		assertThat(em).isNotNull();

		CategoriaRestaurante cr = new CategoriaRestaurante();
		cr.setNome("Chinesa");
		cr.setImagem("chinesa.png");
		em.persistAndFlush(cr);

		assertThat(cr.getId()).isNotNull();

		CategoriaRestaurante cr2 = em.find(CategoriaRestaurante.class, cr.getId());
		assertThat(cr.getNome()).isEqualTo(cr2.getNome());

		em.remove(cr);
		assertThat(em.find(CategoriaRestaurante.class, cr.getId())).isNull();

	}

}
