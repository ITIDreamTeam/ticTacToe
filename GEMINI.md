# Gemini Project Context: Tic-Tac-Toe Game

This document provides a comprehensive overview of the Tic-Tac-Toe project, including its architecture, technologies, and instructions for building and running the application.

## Project Overview

This is a client-server implementation of the classic Tic-Tac-Toe game. The project is built using Java and consists of two main modules:

*   `ticTacToeServer`: A server application that manages game state, player data, and communication with clients. It includes a basic JavaFX interface for viewing server logs.
*   `ticTacToeClient`: A JavaFX-based client application that provides the user interface for players to connect to the server and play the game.

The client and server communicate over a network socket connection, exchanging JSON messages for game actions and updates.

### Key Technologies

*   **Programming Language**: Java (The `pom.xml` specifies Java 11, while the `README.md` mentions Java 23. It is recommended to use Java 11 for compatibility with the build configuration).
*   **Frameworks**:
    *   **JavaFX**: Used for the graphical user interface on both the client and server applications.
    *   **Maven**: For project build management and dependencies.
*   **Database**:
    *   **Apache Derby**: The relational database used to store player and game information.
    *   The database schema is defined in `database/tables.txt`.
*   **Libraries**:
    *   **Gson**: For serializing and deserializing Java objects to and from JSON for network communication.

## Building and Running the Project

The project is managed with Maven. The server must be running before the client can connect.

### Prerequisites

*   Java Development Kit (JDK) 11 or later.
*   Apache Maven.
*   An Apache Derby database instance running and accessible. The connection details found in `ticTacToeServer/src/main/java/com/mycompany/tictactoeserver/util/DBConnection.java` are:
    *   **URL**: `jdbc:derby://localhost:1527/ticTacToev`
    *   **User**: `yassen`
    *   **Password**: `yassen`

### 1. Build the Project

To build both modules, run the following command from the project root directory:

```sh
mvn clean install
```

### 2. Run the Server

Open a new terminal and run the server using the Maven JavaFX plugin:

```sh
mvn -pl ticTacToeServer clean javafx:run
```

The server will start and listen on port `5005`.

### 3. Run the Client

Open another terminal and run the client:

```sh
mvn -pl ticTacToeClient clean javafx:run
```

The client application will launch, and you can connect to the server to play.

## Development Conventions

*   **Modular Architecture**: The project is split into `client` and `server` Maven modules to separate concerns.
*   **Network Communication**: Client-server communication is handled via JSON messages over TCP sockets. The message structure is defined in the `network` packages of both modules.
*   **Database Schema**: The database schema is provided in `database/tables.txt`. Developers are expected to set up their own local Derby database using this schema.
*   **Entry Points**:
    *   Server: `com.mycompany.tictactoeserver.App`
    *   Client: `com.mycompany.tictactoeclient.App`
