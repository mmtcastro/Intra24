package br.com.tdec.intra.empresas.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.tdec.intra.abs.AbstractModelDoc;
import br.com.tdec.intra.abs.AbstractService;
import br.com.tdec.intra.compras.service.CompraService;
import br.com.tdec.intra.empresas.model.GrupoEconomico;
import br.com.tdec.intra.sales.service.NegocioService;
import lombok.Getter;
import lombok.Setter;

@Service
@Getter
@Setter
public class GrupoEconomicoService extends AbstractService<GrupoEconomico> {

	@Autowired
	private NegocioService negocioService; // Repositório que verifica se existem negócios ligados ao grupo

	@Autowired
	private CompraService compraService; // Repositório que verifica se existem compras ligadas ao grupo

	public GrupoEconomicoService() {
		super();
	}

	@Override
	public DeleteResponse delete(AbstractModelDoc grupoEconomico) {
		// Verifica se há negócios vinculados ao Grupo Econômico
		if (negocioService.grupoEconomicoFezNegocio(grupoEconomico.getCodigo())) {
			return createDeleteErrorResponse(grupoEconomico,
					"Este Grupo Econômico não pode ser apagado porque existem negócios associados a ele.");
		}

		// Verifica se há negócios vinculados a Parcerias associadas ao Grupo Econômico
		if (negocioService.parceriaFezNegocio(grupoEconomico.getCodigo())) {
			return createDeleteErrorResponse(grupoEconomico,
					"Este Grupo Econômico não pode ser apagado porque há Parcerias com negócios vinculados.");
		}

		// Verifica se há negócios vinculados a Fornecedores associados ao Grupo
		// Econômico
		if (compraService.fornecedorFezNegocio(grupoEconomico.getCodigo())) {
			return createDeleteErrorResponse(grupoEconomico,
					"Este Grupo Econômico não pode ser apagado porque há Fornecedores com negócios vinculados.");
		}

		// Se passou por todas as verificações, tenta excluir o documento
		try {
			DeleteResponse response = super.delete(grupoEconomico); // Tentativa de exclusão real

			// Adiciona a mensagem de sucesso se a exclusão ocorreu corretamente
			if (response != null
					&& ("200".equals(response.getStatus()) || "OK".equalsIgnoreCase(response.getStatus()))) {
				response.setMessage("Grupo Econômico excluído com sucesso!");
			}

			return response;
		} catch (Exception e) {
			// Caso ocorra um erro inesperado na exclusão
			return createDeleteErrorResponse(grupoEconomico,
					"Erro inesperado ao tentar excluir o Grupo Econômico: " + e.getMessage());
		}

	}

	/**
	 * Método auxiliar para criar uma resposta de erro padronizada ao tentar excluir
	 * um Grupo Econômico.
	 */
	private DeleteResponse createDeleteErrorResponse(AbstractModelDoc grupoEconomico, String errorMessage) {
		DeleteResponse response = new DeleteResponse();
		response.setStatus("403"); // Código HTTP 403 - Forbidden
		response.setStatusText("Erro ao excluir");
		response.setMessage(errorMessage);
		response.setUnid(grupoEconomico.getUnid());
		return response;
	}

}
