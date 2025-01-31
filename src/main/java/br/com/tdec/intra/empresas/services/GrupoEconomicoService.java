package br.com.tdec.intra.empresas.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.tdec.intra.abs.AbstractModelDoc;
import br.com.tdec.intra.abs.AbstractService;
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

	public GrupoEconomicoService() {
		super();
	}

	@Override
	public DeleteResponse delete(AbstractModelDoc grupoEconomico) {
		System.out.println("Iniciando - GrupoEconomicoService.delete() grupoEconomico: " + model.getForm()
				+ " - codigo = " + grupoEconomico.getCodigo());

		// List<Negocio> negocios = negocioService.findAllByCodigoGrupoEconomico(1, 1,
		// null, grupoEconomico.getCodigo());

		// Se encontrar um negócio vinculado, bloqueia a exclusão
		if (!negocioService.grupoEconomicoFezNegocio(grupoEconomico.getCodigo())) {
			DeleteResponse response = new DeleteResponse();
			response.setStatus("403"); // Código HTTP 403 - Forbidden
			response.setStatusText("Erro ao excluir");
			response.setMessage("Este Grupo Econômico não pode ser apagado porque existem negócios associados a ele.");
			response.setUnid(grupoEconomico.getUnid());
			return response;
		}
		System.err.println("GrupoEconomicoService.delete() - Não há negócios vinculados ao Grupo Econômico.");

		// Se não houver negócios vinculados, chama o delete do AbstractService
		// return super.delete(grupoEconomico);
		return null;
	}

}
