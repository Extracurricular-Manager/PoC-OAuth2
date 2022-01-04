package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.GradeLevel;
import com.mycompany.myapp.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link GradeLevel}, with proper type conversions.
 */
@Service
public class GradeLevelRowMapper implements BiFunction<Row, String, GradeLevel> {

    private final ColumnConverter converter;

    public GradeLevelRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link GradeLevel} stored in the database.
     */
    @Override
    public GradeLevel apply(Row row, String prefix) {
        GradeLevel entity = new GradeLevel();
        entity.setId(converter.fromRow(row, prefix + "_id", String.class));
        return entity;
    }
}
