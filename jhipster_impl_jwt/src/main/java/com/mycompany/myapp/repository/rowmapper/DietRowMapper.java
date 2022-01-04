package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Diet;
import com.mycompany.myapp.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Diet}, with proper type conversions.
 */
@Service
public class DietRowMapper implements BiFunction<Row, String, Diet> {

    private final ColumnConverter converter;

    public DietRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Diet} stored in the database.
     */
    @Override
    public Diet apply(Row row, String prefix) {
        Diet entity = new Diet();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        return entity;
    }
}
