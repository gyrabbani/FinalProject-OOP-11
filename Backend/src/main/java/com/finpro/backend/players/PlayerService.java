package com.finpro.backend.players;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PlayerService {

    private final PlayerRepository repo;

    public PlayerService(PlayerRepository repo) {
        this.repo = repo;
    }

    public Player loginOrRegister(String usernameRaw) {
        String username = sanitizeUsername(usernameRaw);

        return repo.findByUsernameIgnoreCase(username)
                .orElseGet(() -> {
                    Player p = new Player();
                    p.setUsername(username);
                    p.setHighScore(0);
                    return repo.save(p);
                });
    }

    public Player submitGameOver(UUID uuid, int score) {
        if (score < 0) throw new IllegalArgumentException("score must be >= 0");

        Player p = repo.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("player not found"));

        if (score > p.getHighScore()) {
            p.setHighScore(score);
            p = repo.save(p);
        }
        return p;
    }

    public List<Player> leaderboard(int limit) {
        int safe = Math.min(Math.max(limit, 1), 100);
        return repo.findAllByOrderByHighScoreDesc(PageRequest.of(0, safe));
    }

    private String sanitizeUsername(String u) {
        if (u == null) throw new IllegalArgumentException("username required");
        String s = u.trim();
        if (s.isEmpty()) throw new IllegalArgumentException("username required");
        if (s.length() > 32) throw new IllegalArgumentException("username max 32 chars");
        return s;
    }
}
