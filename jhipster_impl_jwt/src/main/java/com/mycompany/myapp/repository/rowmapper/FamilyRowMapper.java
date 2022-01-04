package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Family;
import com.mycompany.myapp.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Family}, with proper type conversions.
 */
@Service
public class FamilyRowMapper implements BiFunction<Row, String, Family> {

    private final ColumnConverter converter;

    public FamilyRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Family} stored in the database.
     */
    @Override
    public Family apply(Row row, String prefix) {
        Family entity = new Family();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setReferingParentName(converter.fromRow(row, prefix + "_refering_parent_name", String.class));
        entity.setReferingParentSurname(converter.fromRow(row, prefix + "_refering_parent_surname", String.class));
        entity.setTelephoneNumber(converter.fromRow(row, prefix + "_telephone_number", String.class));
        entity.setPostalAdress(converter.fromRow(row, prefix + "_postal_adress", String.class));
        return entity;
    }
}
