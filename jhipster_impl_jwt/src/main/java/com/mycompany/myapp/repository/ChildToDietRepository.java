package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.ChildToDiet;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the ChildToDiet entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ChildToDietRepository extends R2dbcRepository<ChildToDiet, Long>, ChildToDietRepositoryInternal {
    @Override
    Mono<ChildToDiet> findOneWithEagerRelationships(Long id);

    @Override
    Flux<ChildToDiet> findAllWithEagerRelationships();

    @Override
    Flux<ChildToDiet> findAllWithEagerRelationships(Pageable page);

    @Override
    Mono<Void> deleteById(Long id);

    @Query(
        "SELECT entity.* FROM child_to_diet entity JOIN rel_child_to_diet__id_child joinTable ON entity.id = joinTable.child_to_diet_id WHERE joinTable.id_child_id = :id"
    )
    Flux<ChildToDiet> findByIdChild(Long id);

    @Query(
        "SELECT entity.* FROM child_to_diet entity JOIN rel_child_to_diet__id_diet joinTable ON entity.id = joinTable.child_to_diet_id WHERE joinTable.id_diet_id = :id"
    )
    Flux<ChildToDiet> findByIdDiet(Long id);

    // just to avoid having unambigous methods
    @Override
    Flux<ChildToDiet> findAll();

    @Override
    Mono<ChildToDiet> findById(Long id);

    @Override
    <S extends ChildToDiet> Mono<S> save(S entity);
}

interface ChildToDietRepositoryInternal {
    <S extends ChildToDiet> Mono<S> insert(S entity);
    <S extends ChildToDiet> Mono<S> save(S entity);
    Mono<Integer> update(ChildToDiet entity);

    Flux<ChildToDiet> findAll();
    Mono<ChildToDiet> findById(Long id);
    Flux<ChildToDiet> findAllBy(Pageable pageable);
    Flux<ChildToDiet> findAllBy(Pageable pageable, Criteria criteria);

    Mono<ChildToDiet> findOneWithEagerRelationships(Long id);

    Flux<ChildToDiet> findAllWithEagerRelationships();

    Flux<ChildToDiet> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
