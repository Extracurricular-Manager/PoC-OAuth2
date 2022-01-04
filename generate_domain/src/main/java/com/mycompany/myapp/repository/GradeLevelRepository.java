package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.GradeLevel;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the GradeLevel entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GradeLevelRepository extends JpaRepository<GradeLevel, String> {}
