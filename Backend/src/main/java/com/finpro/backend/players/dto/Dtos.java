package com.finpro.backend.players.dto;

import java.util.UUID;

public class Dtos {
    public record LoginRequest(String username) {}
    public record GameOverRequest(int score) {}

    public record PlayerResponse(UUID uuid, String username, int highScore) {}
    public record LeaderboardRow(int rank, String username, int highScore) {}
}
