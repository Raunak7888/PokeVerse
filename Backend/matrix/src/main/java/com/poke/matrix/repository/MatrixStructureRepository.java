package com.poke.matrix.repository;

import com.poke.matrix.model.MatrixStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatrixStructureRepository extends JpaRepository<MatrixStructure,Long> {
}
