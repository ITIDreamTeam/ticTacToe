package com.mycompany.tictactoeserver.network.dtos;

public class GameStartDto {
    private final String opponentName;
    private final boolean isPlayerX;

    public GameStartDto(String opponentName, boolean isPlayerX) {
        this.opponentName = opponentName;
        this.isPlayerX = isPlayerX;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public boolean isIsPlayerX() {
        return isPlayerX;
    }
}
