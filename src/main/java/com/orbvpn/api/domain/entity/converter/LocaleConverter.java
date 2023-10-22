package com.orbvpn.api.domain.entity.converter;

import java.util.Locale;
import javax.persistence.AttributeConverter;

public class LocaleConverter implements AttributeConverter<Locale, String> {

  @Override
  public String convertToDatabaseColumn(Locale locale) {
    if (locale != null) {
      return locale.toLanguageTag();
    }
    return null;
  }

  @Override
  public Locale convertToEntityAttribute(String languageTag) {
    if (languageTag != null && !languageTag.isEmpty()) {
      return Locale.forLanguageTag(languageTag);
    }
    return null;
  }
}