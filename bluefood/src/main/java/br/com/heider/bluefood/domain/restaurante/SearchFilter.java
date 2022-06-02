package br.com.heider.bluefood.domain.restaurante;

import br.com.heider.bluefood.util.StringUtils;
import lombok.Data;

@Data
public class SearchFilter {

	public enum SearchType {
		TEXTO, CATEGORIA
	}

	public enum Order {
		TAXA, TEMPO
	}

	public enum Command {
		ENTREGA_GRATIS, MAIOR_TAXA, MENOR_TAXA, MAIOR_TEMPO, MENOR_TEMPO
	}

	private String texto;
	private SearchType searchType;
	private Integer categoriaId;
	private Order order = Order.TAXA;
	private boolean asc;
	private boolean entregaGratis;

	public void proccesFilter(String cmdString) {

		if (!StringUtils.isEmpty(cmdString)) {
			Command cmd = Command.valueOf(cmdString);

			if (cmd == Command.ENTREGA_GRATIS) {
				entregaGratis = !entregaGratis;

			} else if (cmd == Command.MAIOR_TAXA) {
				order = Order.TAXA;
				asc = false;

			} else if (cmd == Command.MENOR_TAXA) {
				order = Order.TAXA;
				asc = true;

			} else if (cmd == Command.MAIOR_TEMPO) {
				order = Order.TEMPO;
				asc = false;

			} else if (cmd == Command.MENOR_TEMPO) {
				order = Order.TEMPO;
				asc = true;
			}
		}

		if (searchType == SearchType.TEXTO) {
			categoriaId = null;
		} else if (searchType == SearchType.CATEGORIA) {
			texto = null;
		}

	}

}
