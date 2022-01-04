package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Family;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Family entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FamilyRepository extends R2dbcRepository<Family, Long>, FamilyRepositoryInternal {
    // just to avoid having unambigous methods
    @Override
    Flux<Family> findAll();

    @Override
    Mono<Family> findById(Long id);

    @Override
    <S extends Family> Mono<S> save(S entity);
}

interface FamilyRepositoryInternal {
    <S extends Family> Mono<S> insert(S entity);
    <S extends Family> Mono<S> save(S entity);
    Mono<Integer> update(Family entity);

    Flux<Family> findAll();
    Mono<Family> findById(Long id);
    Flux<Family> findAllBy(Pageable pageable);
    Flux<Family> findAllBy(Pageable pageable, Criteria criteria);
}
