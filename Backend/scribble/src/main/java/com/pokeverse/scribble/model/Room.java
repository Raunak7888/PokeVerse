package com.pokeverse.scribble.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "rooms")
@AllArgsConstructor
@NoArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_name", nullable = false)
    private String roomName;

    @Column(name = "host_id", nullable = false)
    private Long hostId;

    @Column(name = "max_player")
    private int maxPlayer;

    @Column(name = "max_rounds")
    private int maxRounds;

    @Column(name = "is_public")
    private boolean isPublic;

    @Column(name = "password")
    private String password;

    @Column(name = "status")
    private String status = "WAITING"; // Possible values: "WAITING", "IN_PROGRESS", "COMPLETED"

    @Column(name = "current_round")
    private int currentRound = 0;

    @Column(name = "is_Started")
    private boolean isStarted = false;

    @Column(name = "is_ended")
    private boolean isEnded = false;

    @OneToOne
    @JoinColumn(name = "current_player_id")
    private Player drawer;

    // List of players in the room
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Player> players = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
