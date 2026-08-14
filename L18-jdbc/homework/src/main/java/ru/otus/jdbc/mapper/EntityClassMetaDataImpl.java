package ru.otus.jdbc.mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import ru.otus.crm.annotation.Id;

public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {
    private final Class<T> clazz;

    public EntityClassMetaDataImpl(Class<T> clazz) {

        this.clazz = clazz;
    }

    @Override
    public String getName() {
        return clazz.getSimpleName().toLowerCase();
    }

    @Override
    public Constructor getConstructor() {
        try {
            Class<?>[] paramTypes = getAllFields().stream().map(Field::getType).toArray(Class<?>[]::new);
            return clazz.getDeclaredConstructor(paramTypes);
        } catch (NoSuchMethodException exception) {
            throw new RuntimeException("Err: Constructor was not found for class: " + clazz.getName(), exception);
        }
    }

    @Override
    public Field getIdField() {
        return getAllDeclaredFields().stream()
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow();
    }

    @Override
    public List<Field> getAllFields() {
        return getAllDeclaredFields();
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        return getAllDeclaredFields().stream()
                .filter(field -> !field.isAnnotationPresent(Id.class))
                .toList();
    }

    private List<Field> getAllDeclaredFields() {
        return List.of(clazz.getDeclaredFields());
    }
}
