package br.com.heider.bluefood.util;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import br.com.heider.bluefood.domain.cliente.Cliente;
import br.com.heider.bluefood.domain.restaurante.Restaurante;
import br.com.heider.bluefood.infrastructure.web.security.LoggedUser;

public class SecurityUtils {

	public static LoggedUser loggedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication instanceof AnonymousAuthenticationToken) {
			return null;
		}

		return (LoggedUser) authentication.getPrincipal();

	}

	public static Cliente loggedCliente() {
		LoggedUser loggedUser = loggedUser();
		if (loggedUser == null) {
			throw new IllegalStateException("Não há cliente logado");
		}

		if (!(loggedUser.getUsuario() instanceof Cliente)) {
			throw new IllegalStateException("O usuário não é um cliente");
		}

		return (Cliente) loggedUser.getUsuario();
	}

	public static Restaurante loggedRestaurante() {
		LoggedUser loggedUser = loggedUser();
		if (loggedUser == null) {
			throw new IllegalStateException("Não há cliente logado");
		}

		if (!(loggedUser.getUsuario() instanceof Restaurante)) {
			throw new IllegalStateException("O usuário não é um restaurante");
		}

		return (Restaurante) loggedUser.getUsuario();
	}
	
	

}
