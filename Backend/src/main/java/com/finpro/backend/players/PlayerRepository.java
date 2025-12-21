package com.finpro.backend.players;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlayerRepository extends JpaRepository<Player, UUID> {
    Optional<Player> findByUsernameIgnoreCase(String username);
    List<Player> findAllByOrderByHighScoreDesc(Pageable pageable);
}
