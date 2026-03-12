package org.murat.orion.invest_service.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum InvestType {
    STOCK("stock"),
    GOLD("gold"),
    CRYPTO("crypto");

    private final String value;

    InvestType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static InvestType fromValue(String value) {
        for (InvestType type : InvestType.values()) {
            if (type.value.equalsIgnoreCase(value) || type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown InvestType: " + value);
    }
}
