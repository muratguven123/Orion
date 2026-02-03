package org.murat.orion.InvestDomain.Entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum InvestType {
    STOCK("Stock"),
    GOLD("Gold"),
    CRYPTO("Crypto");

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
