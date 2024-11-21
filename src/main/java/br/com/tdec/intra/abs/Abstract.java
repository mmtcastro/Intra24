package br.com.tdec.intra.abs;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Abstract {

	protected static final Logger logger = LoggerFactory.getLogger(Abstract.class);

	public static void print(Object object) {
		System.out.println(object);
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	/**
	 * Retorna o logger associado à classe concreta.
	 */
	protected Logger getLogger() {
		return LoggerFactory.getLogger(this.getClass());
	}

}