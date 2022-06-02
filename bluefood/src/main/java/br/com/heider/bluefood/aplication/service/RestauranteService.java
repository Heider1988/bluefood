package br.com.heider.bluefood.aplication.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.heider.bluefood.domain.cliente.Cliente;
import br.com.heider.bluefood.domain.cliente.ClienteRepository;
import br.com.heider.bluefood.domain.restaurante.ItemCardapio;
import br.com.heider.bluefood.domain.restaurante.ItemCardapioRepository;
import br.com.heider.bluefood.domain.restaurante.Restaurante;
import br.com.heider.bluefood.domain.restaurante.RestauranteRepository;
import br.com.heider.bluefood.domain.restaurante.SearchFilter;
import br.com.heider.bluefood.domain.restaurante.SearchFilter.SearchType;

@Service
public class RestauranteService {

	@Autowired
	private RestauranteRepository restauranteRepository;

	@Autowired
	private ClienteRepository clienteRepository;

	@Autowired
	private ImageService imageService;

	@Autowired
	private ItemCardapioRepository itemCardapioRepository;

	@Transactional
	public void saveRestaurante(Restaurante restaurante) throws ValidationException {

		if (!validateEmail(restaurante.getEmail(), restaurante.getId())) {
			throw new ValidationException("O email está duplicado");
		}

		if (restaurante.getId() != null) {

			Restaurante restauranteDb = restauranteRepository.findById(restaurante.getId()).orElseThrow();
			restaurante.setSenha(restauranteDb.getSenha());
			restaurante.setLogoTipo(restauranteDb.getLogoTipo());
			restauranteRepository.save(restaurante);

		} else {

			restaurante.encryptPassword();
			restaurante = restauranteRepository.save(restaurante);
			restaurante.setLogotipoFilename();

			imageService.uploadLogotipo(restaurante.getLogotipoFile(), restaurante.getLogoTipo());

		}

	}

	private boolean validateEmail(String email, Integer id) {

		Cliente cliente = clienteRepository.findByEmail(email);

		if (cliente != null) {
			return false;
		}

		Restaurante restaurante = restauranteRepository.findByEmail(email);

		if (restaurante != null) {
			if (id == null) {
				return false;
			}

			if (!restaurante.getId().equals(id)) {
				return false;
			}

		}

		return true;

	}

	public List<Restaurante> search(SearchFilter filter) {

		List<Restaurante> restaurantes;

		if (filter.getSearchType() == SearchType.TEXTO) {
			restaurantes = restauranteRepository.findByRestauranteNomeContaining(filter.getTexto());
		} else if (filter.getSearchType() == SearchType.CATEGORIA) {
			restaurantes = restauranteRepository.findByCategorias_Id(filter.getCategoriaId());
		} else {
			throw new IllegalStateException("O tipo de busca " + filter.getSearchType() + " não é suportado.");
		}

		return restaurantes;

	}

	@Transactional
	public void saveItemCardapio(ItemCardapio itemCardapio) {
		itemCardapio = itemCardapioRepository.save(itemCardapio);
		itemCardapio.setImagemFileName();
		imageService.uploadComida(itemCardapio.getImagemFile(), itemCardapio.getImagem());

	}

}
