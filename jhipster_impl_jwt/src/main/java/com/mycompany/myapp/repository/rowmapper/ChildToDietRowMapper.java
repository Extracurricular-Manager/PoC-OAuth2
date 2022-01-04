package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.ChildToDiet;
import com.mycompany.myapp.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ChildToDiet}, with proper type conversions.
 */
@Service
public class ChildToDietRowMapper implements BiFunction<Row, String, ChildToDiet> {

    private final ColumnConverter converter;

    public ChildToDietRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ChildToDiet} stored in the database.
     */
    @Override
    public ChildToDiet apply(Row row, String prefix) {
        ChildToDiet entity = new ChildToDiet();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setIdChild(converter.fromRow(row, prefix + "_id_child", Long.class));
        entity.setIdDiet(converter.fromRow(row, prefix + "_id_diet", Long.class));
        return entity;
    }
}
