package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Child;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Child entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ChildRepository extends R2dbcRepository<Child, Long>, ChildRepositoryInternal {
    @Override
    Mono<Child> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Child> findAllWithEagerRelationships();

    @Override
    Flux<Child> findAllWithEagerRelationships(Pageable page);

    @Override
    Mono<Void> deleteById(Long id);

    @Query("SELECT * FROM child entity WHERE entity.classroom_id = :id")
    Flux<Child> findByClassroom(Long id);

    @Query("SELECT * FROM child entity WHERE entity.classroom_id IS NULL")
    Flux<Child> findAllWhereClassroomIsNull();

    @Query("SELECT * FROM child entity WHERE entity.adelphie_id = :id")
    Flux<Child> findByAdelphie(Long id);

    @Query("SELECT * FROM child entity WHERE entity.adelphie_id IS NULL")
    Flux<Child> findAllWhereAdelphieIsNull();

    @Query("SELECT * FROM child entity WHERE entity.grade_level_id = :id")
    Flux<Child> findByGradeLevel(Long id);

    @Query("SELECT * FROM child entity WHERE entity.grade_level_id IS NULL")
    Flux<Child> findAllWhereGradeLevelIsNull();

    @Query(
        "SELECT entity.* FROM child entity JOIN rel_child__diet joinTable ON entity.id = joinTable.child_id WHERE joinTable.diet_id = :id"
    )
    Flux<Child> findByDiet(Long id);

    // just to avoid having unambigous methods
    @Override
    Flux<Child> findAll();

    @Override
    Mono<Child> findById(Long id);

    @Override
    <S extends Child> Mono<S> save(S entity);
}

interface ChildRepositoryInternal {
    <S extends Child> Mono<S> insert(S entity);
    <S extends Child> Mono<S> save(S entity);
    Mono<Integer> update(Child entity);

    Flux<Child> findAll();
    Mono<Child> findById(Long id);
    Flux<Child> findAllBy(Pageable pageable);
    Flux<Child> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Child> findOneWithEagerRelationships(Long id);

    Flux<Child> findAllWithEagerRelationships();

    Flux<Child> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
