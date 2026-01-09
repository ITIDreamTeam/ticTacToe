package com.mycompany.tictactoeserver.network.dtos;

public class InviteResponseDto {
    private final boolean accepted;

    public InviteResponseDto(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isAccepted() {
        return accepted;
    }
}
