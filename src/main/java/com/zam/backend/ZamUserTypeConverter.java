package com.zam.backend;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ZamUserTypeConverter implements AttributeConverter<ZamUserType, String> {

    @Override
    public String convertToDatabaseColumn(ZamUserType zamUserType) {
        return zamUserType.name();
    }

    @Override
    public ZamUserType convertToEntityAttribute(String s) {
        return ZamUserType.valueOf(s);
    }
}
