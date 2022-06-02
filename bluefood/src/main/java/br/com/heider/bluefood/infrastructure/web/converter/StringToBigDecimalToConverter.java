package br.com.heider.bluefood.infrastructure.web.converter;

import java.math.BigDecimal;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import br.com.heider.bluefood.util.StringUtils;

@Component
public class StringToBigDecimalToConverter implements Converter<String, BigDecimal> {

	@Override
	public BigDecimal convert(String source) {

		if (StringUtils.isEmpty(source)) {
			return null;
		}

		source = source.replace(",", ".");

		return BigDecimal.valueOf(Double.valueOf(source));

	}

}
