package ru.otus.jdbc.mapper;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.stream.Collectors;

public class EntitySQLMetaDataImpl<T> implements EntitySQLMetaData {
    private final EntityClassMetaData<T> entityClassMetaData;

    public EntitySQLMetaDataImpl(EntityClassMetaData<T> entityClassMetaData) {
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public String getSelectAllSql() {
        return String.format("SELECT * FROM %s", entityClassMetaData.getName());
    }

    @Override
    public String getSelectByIdSql() {
        return String.format(
                "SELECT * FROM %s WHERE %s = ?",
                entityClassMetaData.getName(), entityClassMetaData.getIdField().getName());
    }

    @Override
    public String getInsertSql() {
        return String.format(
                "INSERT INTO %s (%s) values (%s)",
                entityClassMetaData.getName(),
                entityClassMetaData.getFieldsWithoutId().stream()
                        .map(Field::getName)
                        .collect(Collectors.joining(", ")),
                String.join(
                        ", ",
                        Collections.nCopies(
                                entityClassMetaData.getFieldsWithoutId().size(), "?")));
    }

    @Override
    public String getUpdateSql() {
        return String.format(
                "UPDATE %s SET %s WHERE %s = ?",
                entityClassMetaData.getName(),
                entityClassMetaData.getFieldsWithoutId().stream()
                        .map(field -> field.getName() + " = ?")
                        .collect(Collectors.joining(", ")),
                entityClassMetaData.getIdField().getName());
    }
}
