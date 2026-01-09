package com.mycompany.tictactoeclient.network.dtos;

public class InviteRequestDto {
    private final String opponentName;

    public InviteRequestDto(String opponentName) {
        this.opponentName = opponentName;
    }

    public String getOpponentName() {
        return opponentName;
    }
}
