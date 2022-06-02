package br.com.heider.bluefood.aplication.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import br.com.heider.bluefood.domain.pagamento.DadosCartao;
import br.com.heider.bluefood.domain.pagamento.Pagamento;
import br.com.heider.bluefood.domain.pagamento.PagamentoRepository;
import br.com.heider.bluefood.domain.pagamento.StatusPagamento;
import br.com.heider.bluefood.domain.pedido.Carrinho;
import br.com.heider.bluefood.domain.pedido.ItemPedido;
import br.com.heider.bluefood.domain.pedido.ItemPedidoPK;
import br.com.heider.bluefood.domain.pedido.ItemPedidoRepository;
import br.com.heider.bluefood.domain.pedido.Pedido;
import br.com.heider.bluefood.domain.pedido.PedidoRepository;
import br.com.heider.bluefood.domain.pedido.Pedido.Status;
import br.com.heider.bluefood.util.SecurityUtils;

@Service
public class PedidoService {

	@Autowired
	private PedidoRepository pedidoRepository;

	@Autowired
	private ItemPedidoRepository itemPedidoRepository;

	@Value("${bluefood.sbpay.url}")
	private String sbPayUrl;

	@Value("${bluefood.sbpay.token}")
	private String sbPayToken;

	@Autowired
	private PagamentoRepository pagamentoRepository;

	@SuppressWarnings("unchecked")
	@Transactional(rollbackFor = PagamentoException.class)
	public Pedido criarEPagar(Carrinho carrinho, String numCartao) throws PagamentoException {

		Pedido pedido = new Pedido();

		pedido.setData(LocalDateTime.now());
		pedido.setCliente(SecurityUtils.loggedCliente());
		pedido.setRestaurante(carrinho.getRestaurante());
		pedido.setStatus(Status.PRODUCAO);

		pedido.setTaxaEntrega(carrinho.getRestaurante().getTaxaEntrega());
		pedido.setSubTotal(carrinho.getPrecoTotal(false));
		pedido.setTotal(carrinho.getPrecoTotal(true));

		pedido = pedidoRepository.save(pedido);

		int ordem = 1;

		for (ItemPedido itemPedido : carrinho.getItens()) {
			itemPedido.setId(new ItemPedidoPK(pedido, ordem++));
			itemPedidoRepository.save(itemPedido);

		}

		DadosCartao dadosCartao = new DadosCartao();
		dadosCartao.setNumCartao(numCartao);

		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("Token", sbPayToken);

		HttpEntity<DadosCartao> requestEntity = new HttpEntity<>(dadosCartao, headers);

		RestTemplate restTemplate = new RestTemplate();

		Map<String, String> response;

		try {
			response = restTemplate.postForObject(sbPayUrl, requestEntity, Map.class);

		} catch (Exception e) {
			throw new PagamentoException("Erro no servidor de pagamento");
		}

		StatusPagamento statusPagamento = StatusPagamento.valueOf(response.get("statusPagamento"));

		if (statusPagamento != StatusPagamento.AUTORIZADO) {
			throw new PagamentoException(statusPagamento.getDescricao());
		}

		Pagamento pagamento = new Pagamento();
		pagamento.setDate(LocalDateTime.now());
		pagamento.setPedido(pedido);
		pagamento.definirNumeroEBandeira(numCartao);

		pagamentoRepository.save(pagamento);

		return pedido;

	}

}
