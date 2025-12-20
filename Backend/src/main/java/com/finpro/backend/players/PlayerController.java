package com.finpro.backend.players;

import com.finpro.backend.players.dto.Dtos;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class PlayerController {

    private final PlayerService service;

    public PlayerController(PlayerService service) {
        this.service = service;
    }

    @PostMapping("/players/login")
    public Dtos.PlayerResponse login(@RequestBody Dtos.LoginRequest req) {
        Player p = service.loginOrRegister(req.username());
        return new Dtos.PlayerResponse(p.getId(), p.getUsername(), p.getHighScore());
    }

    @PostMapping("/players/{uuid}/gameover")
    public Dtos.PlayerResponse gameOver(@PathVariable UUID uuid, @RequestBody Dtos.GameOverRequest req) {
        Player p = service.submitGameOver(uuid, req.score());
        return new Dtos.PlayerResponse(p.getId(), p.getUsername(), p.getHighScore());
    }

    @GetMapping("/leaderboard")
    public List<Dtos.LeaderboardRow> leaderboard(@RequestParam(defaultValue = "10") int limit) {
        List<Player> players = service.leaderboard(limit);
        List<Dtos.LeaderboardRow> rows = new ArrayList<>();
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            rows.add(new Dtos.LeaderboardRow(i + 1, p.getUsername(), p.getHighScore()));
        }
        return rows;
    }
}
