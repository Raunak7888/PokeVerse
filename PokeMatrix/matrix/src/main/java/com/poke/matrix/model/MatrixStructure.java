package com.poke.matrix.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "matrix_structure")
public class MatrixStructure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rows;
    private int cols;

    @ElementCollection
    @CollectionTable(name = "matrix_rows", joinColumns = @JoinColumn(name = "matrix_id"))
    @Column(name = "row_criteria")
    private List<String> rowsCriteria;

    @ElementCollection
    @CollectionTable(name = "matrix_columns", joinColumns = @JoinColumn(name = "matrix_id"))
    @Column(name = "column_criteria")
    private List<String> columnsCriteria;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
