package com.mycompany.tictactoeclient.network.dtos;

public class GameStartDto {
    private final String opponentName;
    private final boolean isPlayerX;
    private final boolean isRecorded;

    public GameStartDto(String opponentName, boolean isPlayerX, boolean isRecorded) {
        this.opponentName = opponentName;
        this.isPlayerX = isPlayerX;
        this.isRecorded = isRecorded;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public boolean isIsPlayerX() {
        return isPlayerX;
    }

    public boolean isIsRecorded() {
        return isRecorded;
    }
}
