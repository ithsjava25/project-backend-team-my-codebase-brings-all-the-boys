package org.example.projectbackendteammycodebasebringsalltheboys.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Map;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Converter
public class JsonMapConverter implements AttributeConverter<Map<String, Object>, String> {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(Map<String, Object> attribute) {
    try {
      return attribute == null ? null : objectMapper.writeValueAsString(attribute);
    } catch (JacksonException e) {
      throw new RuntimeException("Could not serialize JSON", e);
    }
  }

  @Override
  public Map<String, Object> convertToEntityAttribute(String dbData) {
    try {
      if (dbData == null) return null;
      return objectMapper.readValue(dbData, new TypeReference<>() {});
    } catch (JacksonException e) {
      throw new RuntimeException("Could not deserialize JSON", e);
    }
  }
}
