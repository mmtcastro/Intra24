package br.com.tdec.intra.empresas.services;

import org.springframework.stereotype.Service;

import br.com.tdec.intra.abs.AbstractService;
import br.com.tdec.intra.empresas.model.Empresa;

@Service
public class EmpresaService extends AbstractService<Empresa> {

	public EmpresaService() {
		super(Empresa.class);
	}

//	@Override
//	public Empresa findById(String id) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Empresa findByUnid(String unid) {
//		Empresa ret = null;
//		try {
//			ret = webClient.get()
//					.uri("/document/" + unid + "?dataSource=" + scope
//							+ "&computeWithForm=false&richTextAs=markdown&mode=default")
//					.header("Authorization", "Bearer " + getUser().getToken()).retrieve().bodyToMono(Empresa.class)
//					.block();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		return ret;
//	}

	@Override
	public Empresa createModel() {
		return new Empresa();
	}

	@Override
	public SaveResponse save(Empresa model) {
		// TODO Auto-generated method stub
		return null;
	}

}
