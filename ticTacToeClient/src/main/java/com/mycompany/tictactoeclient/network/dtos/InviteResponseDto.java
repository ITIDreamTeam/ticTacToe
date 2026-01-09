package com.mycompany.tictactoeclient.network.dtos;

public class InviteResponseDto {
    private final boolean accepted;

    public InviteResponseDto(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isAccepted() {
        return accepted;
    }
}
