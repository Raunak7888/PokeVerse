package com.poke.matrix.controller;

import com.poke.matrix.dto.CreateMattrixDto;
import com.poke.matrix.dto.ValidateDto;
import com.poke.matrix.model.MatrixStructure;
import com.poke.matrix.model.Player;
import com.poke.matrix.service.MatrixService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/matrix")
public class MatrixController {

    private static final Logger logger = LoggerFactory.getLogger(MatrixController.class);
    private final MatrixService matrixService;

    public MatrixController(MatrixService matrixService) {
        this.matrixService = matrixService;
    }

    @PostMapping("/generate")
    public ResponseEntity<MatrixStructure> generateMatrix(@RequestBody CreateMattrixDto dto) {
        logger.info("Received request to generate matrix with {} rows and {} columns", dto.getRows(), dto.getColumns());
        return ResponseEntity.ok(matrixService.generateMatrix(dto));
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateSolution(ValidateDto dto) {
        logger.info("Received solution for validation...");
        return ResponseEntity.ok(matrixService.validateSolution(dto));
    }

    @PostMapping("/create/player")
    public ResponseEntity<?> createPlayer(@RequestParam Long userId, @RequestParam String name) {
        logger.info("Received player creation request...");
        Player createdPlayer = matrixService.createPlayer(userId,name); // Placeholder
        return ResponseEntity.ok(createdPlayer);
    }
}
