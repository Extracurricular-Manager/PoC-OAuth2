package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Classroom;
import com.mycompany.myapp.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Classroom}, with proper type conversions.
 */
@Service
public class ClassroomRowMapper implements BiFunction<Row, String, Classroom> {

    private final ColumnConverter converter;

    public ClassroomRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Classroom} stored in the database.
     */
    @Override
    public Classroom apply(Row row, String prefix) {
        Classroom entity = new Classroom();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setProfessor(converter.fromRow(row, prefix + "_professor", String.class));
        return entity;
    }
}
