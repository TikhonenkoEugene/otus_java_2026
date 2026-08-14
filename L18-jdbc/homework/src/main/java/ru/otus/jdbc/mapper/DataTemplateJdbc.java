package ru.otus.jdbc.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import ru.otus.core.repository.DataTemplate;
import ru.otus.core.repository.executor.DbExecutor;

/** Сохратяет объект в базу, читает объект из базы */
@SuppressWarnings("java:S1068")
public class DataTemplateJdbc<T> implements DataTemplate<T> {
    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData entitySQLMetaData;
    private final EntityClassMetaData<T> entityClassMetaData;

    public DataTemplateJdbc(
            DbExecutor dbExecutor, EntitySQLMetaData entitySQLMetaData, EntityClassMetaData<T> entityClassMetaData) {
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        return dbExecutor.executeSelect(connection, entitySQLMetaData.getSelectByIdSql(), List.of(id), rs -> {
            try {
                if (rs.next()) {
                    return createInstance(rs);
                }
                return null;
            } catch (SQLException exception) {
                throw new UnsupportedOperationException(exception);
            }
        });
    }

    @Override
    public List<T> findAll(Connection connection) {
        return dbExecutor
                .executeSelect(connection, entitySQLMetaData.getSelectAllSql(), Collections.emptyList(), rs -> {
                    List<T> resultList = new ArrayList<>();
                    try {
                        while (rs.next()) {
                            resultList.add(createInstance(rs));
                        }
                        return resultList;
                    } catch (SQLException exception) {
                        throw new UnsupportedOperationException(exception);
                    }
                })
                .orElse(Collections.emptyList());
    }

    @Override
    public long insert(Connection connection, T client) {
        try {
            List<Object> params = getFieldValues(client, entityClassMetaData.getFieldsWithoutId());
            long generatedId = dbExecutor.executeStatement(connection, entitySQLMetaData.getInsertSql(), params);
            Field idField = entityClassMetaData.getIdField();
            idField.setAccessible(true);
            idField.set(client, generatedId);
            return generatedId;
        } catch (Exception exception) {
            throw new UnsupportedOperationException(exception);
        }
    }

    @Override
    public void update(Connection connection, T client) {
        try {
            List<Object> params = getFieldValues(client, entityClassMetaData.getFieldsWithoutId());
            Field idField = entityClassMetaData.getIdField();
            idField.setAccessible(true);
            params.add(idField.get(client));

            dbExecutor.executeStatement(connection, entitySQLMetaData.getUpdateSql(), params);
        } catch (Exception exception) {
            throw new UnsupportedOperationException(exception);
        }
    }

    private T createInstance(ResultSet rs) {
        try {
            List<Field> allFields = entityClassMetaData.getAllFields();
            Object[] args = new Object[allFields.size()];

            for (int i = 0; i < allFields.size(); i++) {
                Field field = allFields.get(i);
                args[i] = rs.getObject(field.getName());
            }

            var constructor = entityClassMetaData.getConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (SQLException | InstantiationException | IllegalAccessException | InvocationTargetException exception) {
            throw new UnsupportedOperationException(exception);
        }
    }

    private List<Object> getFieldValues(T object, List<Field> fields) {
        List<Object> values = new ArrayList<>(fields.size());
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                values.add(field.get(object));
            } catch (IllegalAccessException exception) {
                throw new UnsupportedOperationException(exception);
            }
        }
        return values;
    }
}
