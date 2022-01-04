package com.mycompany.myapp.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.mycompany.myapp.domain.GradeLevel;
import com.mycompany.myapp.repository.rowmapper.GradeLevelRowMapper;
import com.mycompany.myapp.service.EntityManager;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoin;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the GradeLevel entity.
 */
@SuppressWarnings("unused")
class GradeLevelRepositoryInternalImpl implements GradeLevelRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final GradeLevelRowMapper gradelevelMapper;

    private static final Table entityTable = Table.aliased("grade_level", EntityManager.ENTITY_ALIAS);

    public GradeLevelRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        GradeLevelRowMapper gradelevelMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.gradelevelMapper = gradelevelMapper;
    }

    @Override
    public Flux<GradeLevel> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<GradeLevel> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<GradeLevel> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = GradeLevelSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);

        String select = entityManager.createSelect(selectFrom, GradeLevel.class, pageable, criteria);
        String alias = entityTable.getReferenceName().getReference();
        String selectWhere = Optional
            .ofNullable(criteria)
            .map(crit ->
                new StringBuilder(select)
                    .append(" ")
                    .append("WHERE")
                    .append(" ")
                    .append(alias)
                    .append(".")
                    .append(crit.toString())
                    .toString()
            )
            .orElse(select); // TODO remove once https://github.com/spring-projects/spring-data-jdbc/issues/907 will be fixed
        return db.sql(selectWhere).map(this::process);
    }

    @Override
    public Flux<GradeLevel> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<GradeLevel> findById(String id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private GradeLevel process(Row row, RowMetadata metadata) {
        GradeLevel entity = gradelevelMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends GradeLevel> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends GradeLevel> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(numberOfUpdates -> {
                    if (numberOfUpdates.intValue() <= 0) {
                        throw new IllegalStateException("Unable to update GradeLevel with id = " + entity.getId());
                    }
                    return entity;
                });
        }
    }

    @Override
    public Mono<Integer> update(GradeLevel entity) {
        /*
         * Entity doesn't contain any updatable data, ignore update for compatibility.
         * Otherwise it fails with `IllegalArgumentException: UPDATE contains no assignments`
         * https://github.com/spring-projects/spring-data-r2dbc/issues/250#issuecomment-563122844
         */
        return Mono.just(1);
    }
}
