package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Classroom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Classroom entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClassroomRepository extends R2dbcRepository<Classroom, Long>, ClassroomRepositoryInternal {
    // just to avoid having unambigous methods
    @Override
    Flux<Classroom> findAll();

    @Override
    Mono<Classroom> findById(Long id);

    @Override
    <S extends Classroom> Mono<S> save(S entity);
}

interface ClassroomRepositoryInternal {
    <S extends Classroom> Mono<S> insert(S entity);
    <S extends Classroom> Mono<S> save(S entity);
    Mono<Integer> update(Classroom entity);

    Flux<Classroom> findAll();
    Mono<Classroom> findById(Long id);
    Flux<Classroom> findAllBy(Pageable pageable);
    Flux<Classroom> findAllBy(Pageable pageable, Criteria criteria);
}
