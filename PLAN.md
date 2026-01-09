# Plan: Online "Play with a Friend" Feature

This document outlines the complete implementation plan for the online multiplayer feature. It is divided into two main phases:

1.  **Phase 1: Core Gameplay with Simple Matchmaking.** A temporary system to allow for immediate testing of the core online game loop.
2.  **Phase 2: Full Invitation System.** The final implementation that allows players to choose and invite specific friends.

---

## High-Level Strategy

The server will act as the authoritative source for all game logic and state. Clients will send player actions to the server and receive state updates to render on their UI. The feature will be built iteratively, starting with a minimal core that can be tested end-to-end.

---

## Part 1: Network Protocol Expansion

The shared `MessageType` enum will be expanded to support all required actions.

**File to Modify:** `MessageType.java` (in the shared module)

### Message Types & DTOs

*   **Phase 1 (Core Gameplay & Matchmaking):**
    *   `FIND_MATCH`: Client requests to be placed in the matchmaking queue.
        *   *Payload: None*
    *   `GAME_START`: Server informs two clients that their game is starting.
        *   *Payload: `GameStartDto.java` (contains `opponentName`, `isPlayerX`)*
    *   `MAKE_MOVE`: Client sends their move to the server.
        *   *Payload: `GameMoveDto.java` (already exists, contains `row`, `col`)*
    *   `UPDATE_BOARD`: Server relays a valid move to the other client.
        *   *Payload: `GameMoveDto.java`*
    *   `GAME_OVER`: Server announces the game's result.
        *   *Payload: `String` (a message like "You Win!", "It's a Draw!")*
    *   `OPPONENT_LEFT`: Server informs a client their opponent has disconnected.
        *   *Payload: None*

*   **Phase 2 (Invitation System):**
    *   `INVITE_PLAYER`: Client sends a request to play with a specific opponent.
        *   *Payload: `InviteRequestDto.java` (contains `opponentName`)*
    *   `GAME_INVITATION`: Server forwards the invite to the target client.
        *   *Payload: `InviteRequestDto.java`*
    *   `INVITE_RESPONSE`: The invited client accepts or declines.
        *   *Payload: `InviteResponseDto.java` (contains `boolean accepted`)*

---

## Part 2: Server-Side Implementation

### Phase 1: Simple Matchmaking (For Testing)

1.  **Implement a Matchmaking Queue in `GameService.java`:**
    *   Add a `private final Queue<ClientSession> matchmakingQueue`.
    *   Create a new synchronized method `findMatch(ClientSession playerSession)`.
    *   **Logic:** Add the player to the queue. If the queue size is 2 or more, poll both players, create an `ActiveGame` instance for them, and send the `GAME_START` message to each.

2.  **Update `MessageRouter.java`:**
    *   Route the `FIND_MATCH` message type to the new `gameService.findMatch()` method.

### Phase 2: Full Invitation System

1.  **Handle Invitations in `GameService.java`:**
    *   The `MessageRouter` will direct `INVITE_PLAYER` messages to a new method, `handleInvitationRequest()`.
    *   This method finds the opponent's `ClientSession` and forwards a `GAME_INVITATION` message.
    *   When an `INVITE_RESPONSE` is received, if "accept", the server creates the `ActiveGame`, updates player states, and sends `GAME_START` to both. If "decline", it notifies the original requester.

### Common Server Logic (Phases 1 & 2)

1.  **Manage Active Games:**
    *   **New Class `ActiveGame.java`:** This class will manage the state of a single game (the two `ClientSession`s, the board state via a `GameEngine` instance, and the current turn).
    *   **Modify `GameService.java`:** Add a `Map` to hold all `ActiveGame` instances.

2.  **Handle Gameplay (`GameService.java`):**
    *   When a `MAKE_MOVE` message is received, find the correct `ActiveGame`, validate the move, update the game state, and send an `UPDATE_BOARD` message to the other player.

3.  **Handle Game Conclusion:**
    *   After a move, check for a win/loss/draw.
    *   If the game is over:
        *   **New Class `GameDAO.java`:** Create this to save the game result to the database.
        *   Update player scores and states (`"In Game"` -> `"Online"`) using `PlayerDaoImpl`.
        *   Send the `GAME_OVER` message to both clients.
        *   Remove the `ActiveGame` from the active games map.

4.  **Handle Disconnections (`ClientSession.java`):**
    *   When a client disconnects, check if they were in an `ActiveGame`. If so, notify the `GameService` to declare the other player the winner and end the game properly.

---

## Part 3: Client-Side Implementation

### Phase 1: Simple Matchmaking (For Testing)

1.  **Modify `HomeController.java` (Client):**
    *   The "With Friend" button will temporarily function as "Find Match".
    *   On click, it will send the `FIND_MATCH` message to the server.
    *   The UI will be updated to show a "Waiting for opponent..." status.

2.  **Handle Game Start in `App.java` (Client):**
    *   Create an `onGameStart` handler in the `NetworkClient`.
    *   When this is triggered, navigate to the `game_board.fxml` screen and pass the initial game data (opponent's name, your symbol) to the `Game_boardController`.

### Phase 2: Full Invitation System

1.  **Implement Invitation UI Flow:**
    *   The "With Friend" button will navigate to the `players_board.fxml` screen.
    *   **`Players_boardController.java`:** Clicking a player card will trigger an "Invite" action, sending an `INVITE_PLAYER` message to the server.
    *   **`App.java`:** The `onGameInvitation` handler (set in `init()`) will display the `request_popup.fxml` to the invited player, allowing them to accept or decline.

### Common Client Logic (Phases 1 & 2)

1.  **Update `NetworkClient.java`:**
    *   Add methods to send the new messages (`sendFindMatchRequest`, `sendMove`, etc.).
    *   Add handlers to process messages received from the server (`setOnGameStart`, `setOnBoardUpdate`, `setOnGameOver`, etc.). These handlers must use `Platform.runLater()` to safely update the JavaFX UI.

2.  **Enhance `Game_boardController.java` for Online Play:**
    *   **`setupOnlineGame(GameStartDto dto)`:** A new method to initialize the board for an online match based on data from the server.
    *   **Modify `handlePlayerMove()`:** Add a check for `GameMode.withFriend`. If it is the local player's turn, send the move to the server via `NetworkClient` and disable the board. Otherwise, ignore the click.
    *   **New Method `applyOpponentMove(GameMoveDto move)`:** This will be called by the `onBoardUpdate` handler. It will update the local game engine and the UI to reflect the opponent's move, then re-enable the board for the local player.
    *   The `onGameOver` and `onOpponentLeft` handlers will trigger the existing `showEndGamePopup()` with the final result message from the server.
