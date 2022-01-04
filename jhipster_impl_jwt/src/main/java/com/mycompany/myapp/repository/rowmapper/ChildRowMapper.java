package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Child;
import com.mycompany.myapp.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.time.ZonedDateTime;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Child}, with proper type conversions.
 */
@Service
public class ChildRowMapper implements BiFunction<Row, String, Child> {

    private final ColumnConverter converter;

    public ChildRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Child} stored in the database.
     */
    @Override
    public Child apply(Row row, String prefix) {
        Child entity = new Child();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setSurname(converter.fromRow(row, prefix + "_surname", String.class));
        entity.setBirthday(converter.fromRow(row, prefix + "_birthday", ZonedDateTime.class));
        entity.setGradeLevel(converter.fromRow(row, prefix + "_grade_level", String.class));
        entity.setClassroom(converter.fromRow(row, prefix + "_classroom", Long.class));
        entity.setAdelphie(converter.fromRow(row, prefix + "_adelphie", Long.class));
        entity.setDiet(converter.fromRow(row, prefix + "_diet", Long.class));
        entity.setClassroomId(converter.fromRow(row, prefix + "_classroom_id", Long.class));
        entity.setAdelphieId(converter.fromRow(row, prefix + "_adelphie_id", Long.class));
        entity.setGradeLevelId(converter.fromRow(row, prefix + "_grade_level_id", String.class));
        return entity;
    }
}
