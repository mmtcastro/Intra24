package br.com.tdec.intra.config;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

import com.vaadin.flow.i18n.I18NProvider;

@Component
public class CustomI18NProvider implements I18NProvider {

	private static final long serialVersionUID = 1L;

	private static final String BUNDLE_NAME = "i18n/messages"; // Location of your .properties files
	private static final Locale LOCALE_EN = Locale.ENGLISH; // Use predefined constant for English
	private static final Locale LOCALE_PT = Locale.forLanguageTag("pt"); // Use factory method for Portuguese

	@Override
	public List<Locale> getProvidedLocales() {
		return List.of(LOCALE_EN, LOCALE_PT);
	}

	@Override
	public String getTranslation(String key, Locale locale, Object... params) {
		ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);

		if (bundle.containsKey(key)) {
			String value = bundle.getString(key);
			return (params.length > 0) ? String.format(value, params) : value;
		}

		// Return the key itself if not found
		return key;
	}

}
