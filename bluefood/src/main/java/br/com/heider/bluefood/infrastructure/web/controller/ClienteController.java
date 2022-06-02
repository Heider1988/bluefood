package br.com.heider.bluefood.infrastructure.web.controller;

import java.util.Iterator;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.heider.bluefood.aplication.service.ClienteService;
import br.com.heider.bluefood.aplication.service.RestauranteService;
import br.com.heider.bluefood.aplication.service.ValidationException;
import br.com.heider.bluefood.domain.cliente.Cliente;
import br.com.heider.bluefood.domain.cliente.ClienteRepository;
import br.com.heider.bluefood.domain.pedido.Pedido;
import br.com.heider.bluefood.domain.pedido.PedidoRepository;
import br.com.heider.bluefood.domain.restaurante.CategoriaRestaurante;
import br.com.heider.bluefood.domain.restaurante.CategoriaRestauranteRepository;
import br.com.heider.bluefood.domain.restaurante.ItemCardapio;
import br.com.heider.bluefood.domain.restaurante.ItemCardapioRepository;
import br.com.heider.bluefood.domain.restaurante.Restaurante;
import br.com.heider.bluefood.domain.restaurante.RestauranteComparator;
import br.com.heider.bluefood.domain.restaurante.RestauranteRepository;
import br.com.heider.bluefood.domain.restaurante.SearchFilter;
import br.com.heider.bluefood.util.SecurityUtils;

@Controller
@RequestMapping(path = "/cliente")
public class ClienteController {

	@Autowired
	private ClienteRepository clienteRepository;

	@Autowired
	private ClienteService clienteService;

	@Autowired
	private CategoriaRestauranteRepository categoriaRestauranteRepository;

	@Autowired
	private RestauranteService restauranteService;

	@Autowired
	private RestauranteRepository restauranteRepository;

	@Autowired
	private ItemCardapioRepository itemCardapioRepository;
	
	@Autowired
	private PedidoRepository pedidoRepository;

	@GetMapping(path = "/home")
	public String home(Model model) {

		List<CategoriaRestaurante> categorias = categoriaRestauranteRepository.findAll(Sort.by("nome"));

		model.addAttribute("categorias", categorias);
		model.addAttribute("searchFilter", new SearchFilter());
		
		List<Pedido> pedidos =  pedidoRepository.listPedidosByCliente(SecurityUtils.loggedCliente().getId());
		model.addAttribute("pedidos", pedidos);

		return "cliente-home";
	}

	@GetMapping(path = "/edit")
	public String edit(Model model) {

		Integer id = SecurityUtils.loggedCliente().getId();
		Cliente cliente = clienteRepository.findById(id).orElseThrow();

		model.addAttribute("cliente", cliente);
		ControllerHelper.setEditMode(model, true);

		return "cliente-cadastro";

	}

	@PostMapping("/save")
	public String saveCliente(@ModelAttribute("cliente") @Valid Cliente cliente, Errors errors, Model model) {

		if (!errors.hasErrors()) {
			try {
				clienteService.saveCliente(cliente);
				model.addAttribute("msg", "Cliente gravado com sucesso!");
			} catch (ValidationException e) {
				errors.rejectValue("email", null, e.getMessage());
			}

		}

		ControllerHelper.setEditMode(model, true);
		return "cliente-cadastro";
	}

	@GetMapping(path = "/search")
	public String search(@ModelAttribute("searchFilter") SearchFilter filter,
			@RequestParam(value = "cmd", required = false) String command, Model model) {

		filter.proccesFilter(command);

		List<Restaurante> restaurantes = restauranteService.search(filter);
		model.addAttribute("restaurantes", restaurantes);

		ControllerHelper.addCategoriasToRequest(categoriaRestauranteRepository, model);

		model.addAttribute("searchFilter", filter);
		model.addAttribute("cep", SecurityUtils.loggedCliente().getCep());

		Iterator<Restaurante> it = restaurantes.iterator();
		while (it.hasNext()) {

			Restaurante restaurante = it.next();
			double taxaEntrega = restaurante.getTaxaEntrega().doubleValue();

			if (filter.isEntregaGratis() && taxaEntrega > 0 || !filter.isEntregaGratis() && taxaEntrega == 0) {
				it.remove();
			}

		}

		RestauranteComparator comparator = new RestauranteComparator(filter, SecurityUtils.loggedCliente().getCep());
		restaurantes.sort(comparator);

		return "cliente-busca";
	}

	@GetMapping(path = "/restaurante")
	public String viewRestaurante(@RequestParam("restauranteId") Integer restauranteId,
			@RequestParam(value = "categoria", required = false) String categoria, Model model) {

		Restaurante restaurante = restauranteRepository.findById(restauranteId).orElseThrow();
		model.addAttribute("restaurante", restaurante);

		model.addAttribute("cep", SecurityUtils.loggedCliente().getCep());

		List<String> categorias = itemCardapioRepository.findCategorias(restauranteId);
		model.addAttribute("categorias", categorias);

		List<ItemCardapio> itensCardapiosDestaques;
		List<ItemCardapio> itensCardapiosNaoDestaques;

		if (categoria == null) {
			itensCardapiosDestaques = itemCardapioRepository.findByRestaurante_IdAndDestaqueOrderByNome(restauranteId,
					true);

			itensCardapiosNaoDestaques = itemCardapioRepository
					.findByRestaurante_IdAndDestaqueOrderByNome(restauranteId, false);

		} else {
			itensCardapiosDestaques = itemCardapioRepository
					.findByRestaurante_IdAndDestaqueAndCategoriaOrderByNome(restauranteId, true, categoria);

			itensCardapiosNaoDestaques = itemCardapioRepository
					.findByRestaurante_IdAndDestaqueAndCategoriaOrderByNome(restauranteId, false, categoria);

		}

		model.addAttribute("itensCardapiosDestaques", itensCardapiosDestaques);
		model.addAttribute("itensCardapiosNaoDestaques", itensCardapiosNaoDestaques);
		model.addAttribute("categoriaSelecionada", categoria);

		return "cliente-restaurante";

	}
	
}
