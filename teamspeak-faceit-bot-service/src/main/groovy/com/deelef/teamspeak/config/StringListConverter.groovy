package com.deelef.teamspeak.config

import com.google.common.base.Joiner

import javax.persistence.AttributeConverter
import javax.persistence.Converter

/**
 * @author Adam Gontarek <adam.gontarek@outlook.com>
 * @since 24/07/2017
 */
@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    @Override
    public String convertToDatabaseColumn(List<String> list) {
        // Java 8
        if (list) {
            return String.join(",", list)
        } else {
            return null
        }
//        return Joiner.on(',').join(list);
    }

    @Override
    public List<String> convertToEntityAttribute(String joined) {
        if (joined) {
            return new ArrayList<>(Arrays.asList(joined.split(",")));
        } else {
            return new ArrayList<String>()
        }

    }

}
