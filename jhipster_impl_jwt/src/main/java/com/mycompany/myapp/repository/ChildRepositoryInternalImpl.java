package com.mycompany.myapp.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.mycompany.myapp.domain.Child;
import com.mycompany.myapp.domain.Diet;
import com.mycompany.myapp.repository.rowmapper.ChildRowMapper;
import com.mycompany.myapp.repository.rowmapper.ClassroomRowMapper;
import com.mycompany.myapp.repository.rowmapper.FamilyRowMapper;
import com.mycompany.myapp.repository.rowmapper.GradeLevelRowMapper;
import com.mycompany.myapp.service.EntityManager;
import com.mycompany.myapp.service.EntityManager.LinkTable;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.ZonedDateTime;
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
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the Child entity.
 */
@SuppressWarnings("unused")
class ChildRepositoryInternalImpl implements ChildRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ClassroomRowMapper classroomMapper;
    private final FamilyRowMapper familyMapper;
    private final GradeLevelRowMapper gradelevelMapper;
    private final ChildRowMapper childMapper;

    private static final Table entityTable = Table.aliased("child", EntityManager.ENTITY_ALIAS);
    private static final Table classroomTable = Table.aliased("classroom", "classroom");
    private static final Table adelphieTable = Table.aliased("family", "adelphie");
    private static final Table gradeLevelTable = Table.aliased("grade_level", "gradeLevel");

    private static final EntityManager.LinkTable dietLink = new LinkTable("rel_child__diet", "child_id", "diet_id");

    public ChildRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ClassroomRowMapper classroomMapper,
        FamilyRowMapper familyMapper,
        GradeLevelRowMapper gradelevelMapper,
        ChildRowMapper childMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.classroomMapper = classroomMapper;
        this.familyMapper = familyMapper;
        this.gradelevelMapper = gradelevelMapper;
        this.childMapper = childMapper;
    }

    @Override
    public Flux<Child> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Child> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Child> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = ChildSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ClassroomSqlHelper.getColumns(classroomTable, "classroom"));
        columns.addAll(FamilySqlHelper.getColumns(adelphieTable, "adelphie"));
        columns.addAll(GradeLevelSqlHelper.getColumns(gradeLevelTable, "gradeLevel"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(classroomTable)
            .on(Column.create("classroom_id", entityTable))
            .equals(Column.create("id", classroomTable))
            .leftOuterJoin(adelphieTable)
            .on(Column.create("adelphie_id", entityTable))
            .equals(Column.create("id", adelphieTable))
            .leftOuterJoin(gradeLevelTable)
            .on(Column.create("grade_level_id", entityTable))
            .equals(Column.create("id", gradeLevelTable));

        String select = entityManager.createSelect(selectFrom, Child.class, pageable, criteria);
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
    public Flux<Child> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Child> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    @Override
    public Mono<Child> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Child> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Child> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Child process(Row row, RowMetadata metadata) {
        Child entity = childMapper.apply(row, "e");
        entity.setClassroom(classroomMapper.apply(row, "classroom"));
        entity.setAdelphie(familyMapper.apply(row, "adelphie"));
        entity.setGradeLevel(gradelevelMapper.apply(row, "gradeLevel"));
        return entity;
    }

    @Override
    public <S extends Child> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends Child> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity).flatMap(savedEntity -> updateRelations(savedEntity));
        } else {
            return update(entity)
                .map(numberOfUpdates -> {
                    if (numberOfUpdates.intValue() <= 0) {
                        throw new IllegalStateException("Unable to update Child with id = " + entity.getId());
                    }
                    return entity;
                })
                .then(updateRelations(entity));
        }
    }

    @Override
    public Mono<Integer> update(Child entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }

    @Override
    public Mono<Void> deleteById(Long entityId) {
        return deleteRelations(entityId)
            .then(r2dbcEntityTemplate.delete(Child.class).matching(query(where("id").is(entityId))).all().then());
    }

    protected <S extends Child> Mono<S> updateRelations(S entity) {
        Mono<Void> result = entityManager.updateLinkTable(dietLink, entity.getId(), entity.getDiets().stream().map(Diet::getId)).then();
        return result.thenReturn(entity);
    }

    protected Mono<Void> deleteRelations(Long entityId) {
        return entityManager.deleteFromLinkTable(dietLink, entityId);
    }
}
