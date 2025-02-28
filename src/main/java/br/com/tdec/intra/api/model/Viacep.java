package br.com.tdec.intra.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Viacep {
	@Pattern(regexp = "\\d{5}-\\d{3}", message = "O CEP deve estar no formato 00000-000")
	private String cep;

	private String logradouro;
	private String complemento;
	private String unidade;
	private String bairro;
	private String localidade;

	@Size(min = 2, max = 2, message = "O UF deve ter exatamente 2 caracteres")
	private String uf;

	@JsonProperty("estado")
	private String estado;

	@JsonProperty("regiao")
	private String regiao;

	@Pattern(regexp = "\\d+", message = "O código IBGE deve conter apenas números")
	private String ibge;

	private String gia;

	@Pattern(regexp = "\\d{2}", message = "O DDD deve ter 2 dígitos")
	private String ddd;

	@Pattern(regexp = "\\d+", message = "O código SIAFI deve conter apenas números")
	private String siafi;
}
