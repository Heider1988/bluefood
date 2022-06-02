package br.com.heider.bluefood.domain.pagamento;

public enum StatusPagamento {

	AUTORIZADO("PAGAMENTO AUTORIZADO"), NAO_AUTORIZADO("PAGAMENTO NÃO AUTORIZADO PELO FINANCEIRO"),
	CARTAO_INVALIDO("NÚMERO DO CARTÃO É INVÁLIDO OU BLOQUEADO");

	String descricao;

	private StatusPagamento(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
}
