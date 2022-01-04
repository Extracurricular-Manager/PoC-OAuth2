package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ChildSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("name", table, columnPrefix + "_name"));
        columns.add(Column.aliased("surname", table, columnPrefix + "_surname"));
        columns.add(Column.aliased("birthday", table, columnPrefix + "_birthday"));
        columns.add(Column.aliased("grade_level", table, columnPrefix + "_grade_level"));
        columns.add(Column.aliased("classroom", table, columnPrefix + "_classroom"));
        columns.add(Column.aliased("adelphie", table, columnPrefix + "_adelphie"));
        columns.add(Column.aliased("diet", table, columnPrefix + "_diet"));

        columns.add(Column.aliased("classroom_id", table, columnPrefix + "_classroom_id"));
        columns.add(Column.aliased("adelphie_id", table, columnPrefix + "_adelphie_id"));
        columns.add(Column.aliased("grade_level_id", table, columnPrefix + "_grade_level_id"));
        return columns;
    }
}
