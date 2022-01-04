package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.GradeLevel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the GradeLevel entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GradeLevelRepository extends R2dbcRepository<GradeLevel, String>, GradeLevelRepositoryInternal {
    // just to avoid having unambigous methods
    @Override
    Flux<GradeLevel> findAll();

    @Override
    Mono<GradeLevel> findById(String id);

    @Override
    <S extends GradeLevel> Mono<S> save(S entity);
}

interface GradeLevelRepositoryInternal {
    <S extends GradeLevel> Mono<S> insert(S entity);
    <S extends GradeLevel> Mono<S> save(S entity);
    Mono<Integer> update(GradeLevel entity);

    Flux<GradeLevel> findAll();
    Mono<GradeLevel> findById(String id);
    Flux<GradeLevel> findAllBy(Pageable pageable);
    Flux<GradeLevel> findAllBy(Pageable pageable, Criteria criteria);
}
