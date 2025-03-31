package com.poke.matrix.service;

import com.poke.matrix.dto.CreateMattrixDto;
import com.poke.matrix.dto.Criteria;
import com.poke.matrix.dto.ValidateDto;
import com.poke.matrix.model.MatrixStructure;
import com.poke.matrix.model.Player;
import com.poke.matrix.model.PlayerMatrix;
import com.poke.matrix.model.Pokemon;
import com.poke.matrix.repository.MatrixStructureRepository;
import com.poke.matrix.repository.PlayerMatrixRepository;
import com.poke.matrix.repository.PlayerRepository;
import com.poke.matrix.repository.PokemonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MatrixService {

    private static final Logger logger = LoggerFactory.getLogger(MatrixService.class);
    private final MatrixStructureRepository matrixStructureRepository;
    private final PokemonRepository pokemonRepository;
    private final PlayerMatrixRepository playerMatrixRepository;
    private final PlayerRepository playerRepository;
    private final Criteria criteria;

    public MatrixService(MatrixStructureRepository matrixStructureRepository, PokemonRepository pokemonRepository, PlayerMatrixRepository playerMatrixRepository, PlayerRepository playerRepository, Criteria criteria) {
        this.matrixStructureRepository = matrixStructureRepository;
        this.pokemonRepository = pokemonRepository;
        this.playerMatrixRepository = playerMatrixRepository;
        this.playerRepository = playerRepository;
        this.criteria = criteria;
    }

    /**
     * Generates a PokeMatrix puzzle with a grid of Pokémon following random criteria.
     */
    public MatrixStructure generateMatrix(CreateMattrixDto dto) {
        logger.info("Generating PokeMatrix with {} rows and {} columns...", dto.getRows(), dto.getColumns());

        List<String> shuffledCriteria = new ArrayList<>(Arrays.asList(criteria.getCriteria()));
        Collections.shuffle(shuffledCriteria);

        List<String> rowCriteriaTypes = new ArrayList<>();
        List<String> colCriteriaTypes = new ArrayList<>();

        for (int i = 0; i < dto.getRows(); i++) {
            rowCriteriaTypes.add(shuffledCriteria.removeFirst());
        }
        for (int j = 0; j < dto.getColumns(); j++) {
            colCriteriaTypes.add(shuffledCriteria.removeFirst());
        }

        List<String> rowCriteriaValues = new ArrayList<>();
        List<String> colCriteriaValues = new ArrayList<>();

        for (String type : rowCriteriaTypes) {
            rowCriteriaValues.add(getRandomValue(criteria.getDataMap().get(type)));
        }
        for (String type : colCriteriaTypes) {
            colCriteriaValues.add(getRandomValue(criteria.getDataMap().get(type)));
        }

        MatrixStructure matrix = new MatrixStructure();
        matrix.setRows(dto.getRows());
        matrix.setCols(dto.getColumns());
        matrix.setRowsCriteria(rowCriteriaValues);
        matrix.setColumnsCriteria(colCriteriaValues);

        matrixStructureRepository.save(matrix);

        logger.info("Generated matrix criteria: Rows = {}, Columns = {}", rowCriteriaValues, colCriteriaValues);
        return matrix;
    }

    /**
     * Helper method to select a random value from a given array.
     */
    private String getRandomValue(String[] values) {
        if (values == null || values.length == 0) return "Unknown";
        return values[new Random().nextInt(values.length)];
    }

    public ResponseEntity<?> validateSolution(ValidateDto dto) {
        Optional<MatrixStructure> matrixOpt = matrixStructureRepository.findById(dto.getMatrixId());
        Optional<Pokemon> pokemonOpt = pokemonRepository.findById(dto.getPokemonId());

        if (matrixOpt.isEmpty() || pokemonOpt.isEmpty()) {
            logger.error("Matrix or Pokémon not found for IDs: Matrix = {}, Pokemon = {}", dto.getMatrixId(), dto.getPokemonId());
            return ResponseEntity.badRequest().body("Invalid Pokemon or Matrix");
        }

        MatrixStructure matrix = matrixOpt.get();
        Pokemon pokemon = pokemonOpt.get();

        String rowCriteriaValue = matrix.getRowsCriteria().get(Math.toIntExact(dto.getRowId()));
        String colCriteriaValue = matrix.getColumnsCriteria().get(Math.toIntExact(dto.getColId()));

        String rowCriteriaType = findCategoryByValue(rowCriteriaValue);
        String colCriteriaType = findCategoryByValue(colCriteriaValue);

        boolean rowValid = validateAttribute(rowCriteriaType, rowCriteriaValue, pokemon);
        boolean colValid = validateAttribute(colCriteriaType, colCriteriaValue, pokemon);

        logger.info("Validating Pokémon ID {}: Row [{}] = {}, Column [{}] = {} -> Row Match: {}, Col Match: {}",
                dto.getPokemonId(), dto.getRowId(), rowCriteriaValue, dto.getColId(), colCriteriaValue, rowValid, colValid);

        boolean isValid = rowValid && colValid;

        // Check if PlayerMatrix already exists
        Player player = playerRepository.findByUserId(dto.getUserId());
        Optional<PlayerMatrix> existingPlayerMatrix = playerMatrixRepository.findByPlayerAndMatrixAndRowIdAndColId(
                player, matrix, Math.toIntExact(dto.getRowId()), Math.toIntExact(dto.getColId())
        );

        PlayerMatrix playerMatrix;
        if (existingPlayerMatrix.isPresent()) {
            // Update existing record
            playerMatrix = existingPlayerMatrix.get();
        } else {
            // Create new entry
            playerMatrix = new PlayerMatrix();
            playerMatrix.setPlayer(player);
            playerMatrix.setMatrix(matrix);
            playerMatrix.setRowId(Math.toIntExact(dto.getRowId()));
            playerMatrix.setColId(Math.toIntExact(dto.getColId()));
        }
        playerMatrix.setPokemonId(pokemon);
        playerMatrix.setValid(isValid);

        playerMatrixRepository.save(playerMatrix);

        // Prepare response DTO
        ValidateDto validation = new ValidateDto();
        validation.setMatrixId(dto.getMatrixId());
        validation.setPokemonId(dto.getPokemonId());
        validation.setRowId(dto.getRowId());
        validation.setColId(dto.getColId());
        validation.setValid(isValid);
        validation.setUserId(dto.getUserId());

        return ResponseEntity.ok(validation);
    }


    /**
     * Finds the category (key) in the dataMap that corresponds to a given value.
     */
    public String findCategoryByValue(String value) {
        for (Map.Entry<String, String[]> entry : criteria.getDataMap().entrySet()) {
            for (String val : entry.getValue()) {
                if (val.equalsIgnoreCase(value)) { // Case-insensitive comparison
                    return entry.getKey();
                }
            }
        }
        return "Not Found"; // If value is not found in any category
    }

    /**
     * Validates if a Pokémon matches the given criteria type and value.
     * Handles both string-based attributes and numeric attributes (size, weight).
     */
    private boolean validateAttribute(String criteriaType, String criteriaValue, Pokemon pokemon) {
        if (criteriaType.equalsIgnoreCase("sizesInMeterBy10") || criteriaType.equalsIgnoreCase("weightsInKgBy10")) {
            int extractedValue = extractNumbers(criteriaValue);
            int pokemonValue = Integer.parseInt(getPokemonAttribute(pokemon, criteriaType));
            return pokemonValue == extractedValue;
        }
        return getPokemonAttribute(pokemon, criteriaType).equalsIgnoreCase(criteriaValue);
    }

    /**
     * Retrieves Pokémon attribute based on criteria.
     */
    private String getPokemonAttribute(Pokemon pokemon, String criteriaType) {
        return switch (criteriaType) {
            case "regions" -> pokemon.getRegion();
            case "abilities" -> pokemon.getAbilities().isEmpty() ? "Unknown" : pokemon.getAbilities().getFirst();
            case "evolutionStages" -> pokemon.getEvolutionStage();
            case "legendaryStatus" -> pokemon.getLegendaryStatus();
            case "bodyShapes" -> pokemon.getBodyShape();
            case "colors" -> pokemon.getColor();
            case "sizesInMeterBy10" -> String.valueOf(pokemon.getHeight() / 10);
            case "weightsInKgBy10" -> String.valueOf(pokemon.getWeight() / 10.0);
            default -> "Unknown";
        };
    }

    /**
     * Extracts the first numerical value from an alphanumeric string.
     */
    public int extractNumbers(String input) {
        String numStr = input.replaceAll("\\D+", ""); // Remove all non-numeric characters
        return numStr.isEmpty() ? 0 : Integer.parseInt(numStr); // Handle empty case
    }

    public Player createPlayer(Long userId, String name) {
        // Fetch player from database
        Player existingPlayer = playerRepository.findByUserId(userId);

        // If player exists, return it
        if (existingPlayer != null && Objects.equals(existingPlayer.getName(), name)) {
            return existingPlayer;
        }

        // Otherwise, create a new player
        Player newPlayer = new Player();
        newPlayer.setUserId(userId);
        newPlayer.setName(name);
        newPlayer.setLastSolvedLevel(0L);
        newPlayer.setRating(0L);

        return playerRepository.save(newPlayer);
    }

}
