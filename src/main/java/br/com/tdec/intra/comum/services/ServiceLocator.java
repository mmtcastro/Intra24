package br.com.tdec.intra.comum.services;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import br.com.tdec.intra.abs.AbstractModelDoc;
import br.com.tdec.intra.abs.AbstractService;
import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class ServiceLocator implements ApplicationContextAware {

	private ApplicationContext ctx;
	private final Map<String, AbstractService<?>> cache = new ConcurrentHashMap<>();

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.ctx = applicationContext;
	}

	@SuppressWarnings("unchecked")
	public <C extends AbstractModelDoc> AbstractService<C> find(Class<C> clazz, String database) {
		String key = clazz.getName() + "::" + database;
		return (AbstractService<C>) cache.computeIfAbsent(key, k -> {
			return ctx.getBeansOfType(AbstractService.class).values().stream()
					.filter(svc -> svc.getModelClass().equals(clazz) && database.equalsIgnoreCase(svc.getScope()))
					.findFirst().orElseThrow(() -> new IllegalStateException(
							"Nenhum service para " + clazz.getSimpleName() + " em " + database));
		});
	}

}
