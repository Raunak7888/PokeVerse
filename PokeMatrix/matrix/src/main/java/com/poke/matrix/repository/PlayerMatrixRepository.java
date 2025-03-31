package com.poke.matrix.repository;

import com.poke.matrix.model.MatrixStructure;
import com.poke.matrix.model.Player;
import com.poke.matrix.model.PlayerMatrix;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerMatrixRepository extends JpaRepository<PlayerMatrix,Long> {
    Optional<PlayerMatrix> findByPlayerAndMatrixAndRowIdAndColId(Player player, MatrixStructure matrix, int rowId, int colId);

}
