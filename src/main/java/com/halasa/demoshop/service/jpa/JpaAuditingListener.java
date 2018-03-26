package com.halasa.demoshop.service.jpa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class JpaAuditingListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(JpaAuditingListener.class);

    @PrePersist
    public void setCreationTime(Object entity) throws IllegalAccessException {
        final ZonedDateTime now = ZonedDateTime.now();
        Optional<Field> optionalField = getFieldWithAnnotation(entity.getClass(), CreatedDate.class);
        if (optionalField.isPresent()) {
            optionalField.get().set(entity, now);
        }
        setUpdateTime(entity, now);
    }

    @PreUpdate
    public void setUpdateTime(Object entity) throws IllegalAccessException {
        setUpdateTime(entity, ZonedDateTime.now());
    }

    private void setUpdateTime(Object entity, ZonedDateTime dateTime) throws IllegalAccessException {
        Optional<Field> optionalField = getFieldWithAnnotation(entity.getClass(), LastModifiedDate.class);
        if (optionalField.isPresent()) {
            optionalField.get().set(entity, dateTime);
        }
    }

    private <A extends Annotation> Optional<Field> getFieldWithAnnotation(Class<?> entityClass, Class<A> annotationClass) {
        List<Field> fields = getAllFields(new ArrayList<>(), entityClass);
        for (Field field : fields) {
            final A annotation = field.getAnnotation(annotationClass);
            if (annotation != null) {
                return Optional.of(field);
            }
        }
        LOGGER.debug("Class {} has no field with {} annotation.", entityClass, annotationClass);
        return Optional.empty();
    }

    private List<Field> getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));
        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
        }
        return fields;
    }

}
