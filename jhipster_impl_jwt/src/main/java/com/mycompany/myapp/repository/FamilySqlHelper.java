package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class FamilySqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("refering_parent_name", table, columnPrefix + "_refering_parent_name"));
        columns.add(Column.aliased("refering_parent_surname", table, columnPrefix + "_refering_parent_surname"));
        columns.add(Column.aliased("telephone_number", table, columnPrefix + "_telephone_number"));
        columns.add(Column.aliased("postal_adress", table, columnPrefix + "_postal_adress"));

        return columns;
    }
}
