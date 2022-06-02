package br.com.heider.bluefood.domain.restaurante;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import br.com.heider.bluefood.domain.usuario.Usuario;
import br.com.heider.bluefood.infrastructure.web.validator.UploadConstraint;
import br.com.heider.bluefood.util.FileType;
import br.com.heider.bluefood.util.StringUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@SuppressWarnings("serial")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity
@Table(name = "restaurante")
public class Restaurante extends Usuario {

	@NotBlank(message = "O CNPJ não pode ser vazio")
	@Pattern(regexp = "[0-9]{14}", message = "CNPJ deve conter 14 dígitos")
	@Column(length = 14)
	private String cnpj;

	@Size(max = 80)
	private String logoTipo;

	@UploadConstraint(acceptedTypes = FileType.PNG, message = "O arquivo não é uma IMG em PNG")
	private transient MultipartFile logotipoFile;

	@NotNull(message = "A taxa de entrega não pode estar vázia")
	@Min(0)
	@Max(99)
	private BigDecimal taxaEntrega;

	@NotNull(message = "O temppo de entrega não pode estar vázia")
	@Min(0)
	@Max(120)
	private Integer tempoEntregaBase;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "restaurante_has_categoria", joinColumns = @JoinColumn(name = "restaurante_id"), inverseJoinColumns = @JoinColumn(name = "categoria_restaurante_id"))

	@Size(min = 1, message = "Precisa ter pelo menos 1 categ�ria")
	@ToString.Exclude
	private Set<CategoriaRestaurante> categorias = new HashSet<>(0);

	@OneToMany(mappedBy = "restaurante", fetch = FetchType.EAGER)
	private Set<ItemCardapio> itensCardapio = new HashSet<>(0);

	public void setLogotipoFilename() {
		if (getId() == null) {
			throw new IllegalStateException("Primeiro é preciso gravar o registro");
		}

		this.logoTipo = String.format("%04d-logo.%s", getId(),
				FileType.of(logotipoFile.getContentType()).getExtension());
	}

	public String getCategoriaAsText() {
		Set<String> strings = new LinkedHashSet<>();

		for (CategoriaRestaurante categoria : categorias) {
			strings.add(categoria.getNome());
		}

		return StringUtils.concatenate(strings);

	}

	public Integer calcularTempoEntrega(String cep) {

		int soma = 0;

		for (char c : cep.toCharArray()) {
			int v = Character.getNumericValue(c);
			if (v > 0) {
				soma += soma;
			}
		}

		return (soma /= 2) + this.tempoEntregaBase;

	}
	

}
