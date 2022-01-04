package com.mycompany.myapp.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.mycompany.myapp.domain.Child;
import com.mycompany.myapp.domain.ChildToDiet;
import com.mycompany.myapp.domain.Diet;
import com.mycompany.myapp.repository.rowmapper.ChildToDietRowMapper;
import com.mycompany.myapp.service.EntityManager;
import com.mycompany.myapp.service.EntityManager.LinkTable;
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
 * Spring Data SQL reactive custom repository implementation for the ChildToDiet entity.
 */
@SuppressWarnings("unused")
class ChildToDietRepositoryInternalImpl implements ChildToDietRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ChildToDietRowMapper childtodietMapper;

    private static final Table entityTable = Table.aliased("child_to_diet", EntityManager.ENTITY_ALIAS);

    private static final EntityManager.LinkTable idChildLink = new LinkTable(
        "rel_child_to_diet__id_child",
        "child_to_diet_id",
        "id_child_id"
    );
    private static final EntityManager.LinkTable idDietLink = new LinkTable("rel_child_to_diet__id_diet", "child_to_diet_id", "id_diet_id");

    public ChildToDietRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ChildToDietRowMapper childtodietMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.childtodietMapper = childtodietMapper;
    }

    @Override
    public Flux<ChildToDiet> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<ChildToDiet> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<ChildToDiet> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = ChildToDietSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);

        String select = entityManager.createSelect(selectFrom, ChildToDiet.class, pageable, criteria);
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
    public Flux<ChildToDiet> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<ChildToDiet> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    @Override
    public Mono<ChildToDiet> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<ChildToDiet> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<ChildToDiet> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private ChildToDiet process(Row row, RowMetadata metadata) {
        ChildToDiet entity = childtodietMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends ChildToDiet> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends ChildToDiet> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity).flatMap(savedEntity -> updateRelations(savedEntity));
        } else {
            return update(entity)
                .map(numberOfUpdates -> {
                    if (numberOfUpdates.intValue() <= 0) {
                        throw new IllegalStateException("Unable to update ChildToDiet with id = " + entity.getId());
                    }
                    return entity;
                })
                .then(updateRelations(entity));
        }
    }

    @Override
    public Mono<Integer> update(ChildToDiet entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }

    @Override
    public Mono<Void> deleteById(Long entityId) {
        return deleteRelations(entityId)
            .then(r2dbcEntityTemplate.delete(ChildToDiet.class).matching(query(where("id").is(entityId))).all().then());
    }

    protected <S extends ChildToDiet> Mono<S> updateRelations(S entity) {
        Mono<Void> result = entityManager
            .updateLinkTable(idChildLink, entity.getId(), entity.getIdChildren().stream().map(Child::getId))
            .then();
        result = result.and(entityManager.updateLinkTable(idDietLink, entity.getId(), entity.getIdDiets().stream().map(Diet::getId)));
        return result.thenReturn(entity);
    }

    protected Mono<Void> deleteRelations(Long entityId) {
        return entityManager.deleteFromLinkTable(idChildLink, entityId).and(entityManager.deleteFromLinkTable(idDietLink, entityId));
    }
}
