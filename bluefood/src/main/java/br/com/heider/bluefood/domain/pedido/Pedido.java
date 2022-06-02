package br.com.heider.bluefood.domain.pedido;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.com.heider.bluefood.domain.cliente.Cliente;
import br.com.heider.bluefood.domain.pagamento.Pagamento;
import br.com.heider.bluefood.domain.restaurante.Restaurante;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "pedido")
public class Pedido implements Serializable {

	public enum Status {
		PRODUCAO(1, "Em produção", false), ENTREGA(2, "Saiu para a entrega", false), CONCLUIDO(3, "Concluído", true);

		private Status(int ordem, String descricao, boolean ultimo) {
			this.ordem = ordem;
			this.descricao = descricao;
			this.ultimo = ultimo;
		}

		int ordem;
		String descricao;
		boolean ultimo;

		public int getOrdem() {
			return ordem;
		}

		public String getDescricao() {
			return descricao;
		}

		public boolean isUltimo() {
			return ultimo;
		}

		public static Status fromOrdem(int ordem) {
			for (Status status : Status.values()) {
				if (status.getOrdem() == ordem) {
					return status;
				}

			}

			return null;
		}

	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotNull
	private LocalDateTime data;

	@NotNull
	private Status status;

	@NotNull
	@ManyToOne
	private Cliente cliente;

	@NotNull
	@ManyToOne
	private Restaurante restaurante;

	@NotNull
	private BigDecimal subTotal;

	@NotNull
	@Column(name = "taxa_entrega")
	private BigDecimal taxaEntrega;

	@NotNull
	private BigDecimal total;

	@OneToMany(mappedBy = "id.pedido", fetch = FetchType.EAGER)
	private Set<ItemPedido> itens;

	@OneToOne(mappedBy = "pedido")
	private Pagamento pagamento;

	public String getFormattedId() {

		return String.format("#%04d", this.id);

	}

	public void definirProximoStatus() {
		int ordem = this.status.getOrdem();

		Status newStatus = Status.fromOrdem(ordem + 1);

		if (newStatus != null) {
			this.status = newStatus;
		}

	}

}