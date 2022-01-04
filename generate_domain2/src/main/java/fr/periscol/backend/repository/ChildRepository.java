package fr.periscol.backend.repository;

import fr.periscol.backend.domain.Child;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Child entity.
 */
@Repository
public interface ChildRepository extends JpaRepository<Child, Long> {
    @Query(
        value = "select distinct child from Child child left join fetch child.diets",
        countQuery = "select count(distinct child) from Child child"
    )
    Page<Child> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct child from Child child left join fetch child.diets")
    List<Child> findAllWithEagerRelationships();

    @Query("select child from Child child left join fetch child.diets where child.id =:id")
    Optional<Child> findOneWithEagerRelationships(@Param("id") Long id);
}
