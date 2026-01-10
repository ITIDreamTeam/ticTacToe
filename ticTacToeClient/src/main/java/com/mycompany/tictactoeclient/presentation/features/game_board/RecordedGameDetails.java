
package com.mycompany.tictactoeclient.presentation.features.game_board;

import com.mycompany.tictactoeclient.data.models.MoveRecord;
import java.util.List;

public class RecordedGameDetails {
    private final String playerXName;
    private final String playerOName;
    private final String gameDate;
    private final List<MoveRecord> moves;

    public RecordedGameDetails(String playerXName, String playerOName, String gameDate, List<MoveRecord> moves) {
        this.playerXName = playerXName;
        this.playerOName = playerOName;
        this.gameDate = gameDate;
        this.moves = moves;
    }

    public String getPlayerXName() {
        return playerXName;
    }

    public String getPlayerOName() {
        return playerOName;
    }

    public String getGameDate() {
        return gameDate;
    }

    public List<MoveRecord> getMoves() {
        return moves;
    }
}
