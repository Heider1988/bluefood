package br.com.heider.bluefood.infrastructure.web.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.heider.bluefood.aplication.service.ClienteService;
import br.com.heider.bluefood.aplication.service.RestauranteService;
import br.com.heider.bluefood.aplication.service.ValidationException;
import br.com.heider.bluefood.domain.cliente.Cliente;
import br.com.heider.bluefood.domain.restaurante.CategoriaRestauranteRepository;
import br.com.heider.bluefood.domain.restaurante.Restaurante;

@Controller
@RequestMapping(path = "/public")
public class PublicController {

	@Autowired
	private ClienteService clienteService;

	@Autowired
	private RestauranteService restauranteService;

	@Autowired
	private CategoriaRestauranteRepository categoriaRestauranteRepository;

	@GetMapping("/cliente/new")
	public String newCliente(Model model) {

		model.addAttribute("cliente", new Cliente());
		ControllerHelper.setEditMode(model, false);
		return "cliente-cadastro";
	}

	@GetMapping(path = "/restaurante/new")
	public String restauranteCadastro(Model model) {
		model.addAttribute("restaurante", new Restaurante());
		ControllerHelper.setEditMode(model, false);

		ControllerHelper.addCategoriasToRequest(categoriaRestauranteRepository, model);

		return "restaurante-cadastro";
	}

	@PostMapping("/cliente/save")
	public String saveCliente(@ModelAttribute("cliente") @Valid Cliente cliente, Errors errors, Model model) {

		if (!errors.hasErrors()) {
			try {
				clienteService.saveCliente(cliente);
				model.addAttribute("msg", "Cliente gravado com sucesso!");
			} catch (ValidationException e) {
				errors.rejectValue("email", null, e.getMessage());
			}

		}

		ControllerHelper.setEditMode(model, false);
		return "cliente-cadastro";
	}

	@PostMapping("/restaurante/save")
	public String saveRestaurante(@ModelAttribute("restaurante") @Valid Restaurante restaurante, Errors errors,
			Model model) {

		if (!errors.hasErrors()) {
			try {
				restauranteService.saveRestaurante(restaurante);
				model.addAttribute("msg", "Restaurante gravado com sucesso!");
			} catch (ValidationException e) {
				errors.rejectValue("email", null, e.getMessage());
			}

		}

		ControllerHelper.setEditMode(model, false);
		ControllerHelper.addCategoriasToRequest(categoriaRestauranteRepository, model);
		return "restaurante-cadastro";
	}

}
