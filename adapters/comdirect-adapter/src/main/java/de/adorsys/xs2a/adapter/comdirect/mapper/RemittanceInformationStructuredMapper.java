package de.adorsys.xs2a.adapter.comdirect.mapper;

import de.adorsys.xs2a.adapter.api.model.RemittanceInformationStructured;

public interface RemittanceInformationStructuredMapper {
    default String map(RemittanceInformationStructured value) {
        return value == null ? null : value.getReference();
    }
}
