package org.murat.orion.invest_service.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class InvestTypeConverter implements AttributeConverter<InvestType, String> {

    @Override
    public String convertToDatabaseColumn(InvestType investType) {
        if (investType == null) {
            return null;
        }
        return investType.getValue(); // "Stock", "Gold", "Crypto" döndürür
    }

    @Override
    public InvestType convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        return InvestType.fromValue(dbData);
    }
}

