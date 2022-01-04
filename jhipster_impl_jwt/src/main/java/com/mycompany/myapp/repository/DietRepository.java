package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Diet;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Diet entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DietRepository extends R2dbcRepository<Diet, Long>, DietRepositoryInternal {
    // just to avoid having unambigous methods
    @Override
    Flux<Diet> findAll();

    @Override
    Mono<Diet> findById(Long id);

    @Override
    <S extends Diet> Mono<S> save(S entity);
}

interface DietRepositoryInternal {
    <S extends Diet> Mono<S> insert(S entity);
    <S extends Diet> Mono<S> save(S entity);
    Mono<Integer> update(Diet entity);

    Flux<Diet> findAll();
    Mono<Diet> findById(Long id);
    Flux<Diet> findAllBy(Pageable pageable);
    Flux<Diet> findAllBy(Pageable pageable, Criteria criteria);
}
