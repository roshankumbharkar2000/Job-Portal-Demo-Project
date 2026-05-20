package com.MiniProject.Job.Portal.model.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Role, String> {

    @Override
    public String convertToDatabaseColumn(Role role) {
        return role != null ? role.name() : null;
    }

    @Override
    public Role convertToEntityAttribute(String dbData) {
        try {
            return Role.valueOf(dbData.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Role.CANDIDATE;  // Return a default role or handle as appropriate
        }
    }
}
