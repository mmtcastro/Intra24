package br.com.tdec.intra.abs;

import org.apache.commons.lang3.builder.ToStringBuilder;

public abstract class Abstract {

	public static void print(Object object) {
		System.out.print(object);
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);

	}
}
