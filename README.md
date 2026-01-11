# Tic-Tac-Toe (Client + Server) — Team Setup Guide

## 1) Required Versions (must match)
**Java**
- `java 23.0.2` (build `23.0.2+7-58`)

**IDE**
- Apache NetBeans IDE **28**

**Database**
- Apache Derby (Java DB)

---

## 2) Repository Structure (expected)
```
/tic-tac-toe-client      (JavaFX client module)
/tic-tac-toe-server      (server module)
/database
  tables.txt             (CREATE TABLE)
```

---

## 3) Database Sharing Policy (how the team shares the DB)
We do **not** upload Derby database files to GitHub.

Instead, we share:
- `database/tables.txt ` (the database schema)

Each teammate creates a local Derby database and runs the SQL script(s). This keeps the project reproducible and consistent across all machines.

---

## 4) Setup Steps (each teammate)

### A) Install tools
1. Install **Java 23.0.2**
2. Install **Apache NetBeans IDE 28**
3. Install **Git**

---

### B) Clone the repository
- Open a terminal in the folder where you want the project:
  - `git clone <REPO_URL>`
  - `cd <REPO_FOLDER>`

---

### C) Create local Derby database in NetBeans
1. Open **NetBeans**
2. Go to **Services** tab → **Databases**
3. Create a new Derby / Java DB connection
4. Create a database with a name like:
   - `ticTacToe`


---

### E) Open and run the modules
1. Open the project in NetBeans (client + server modules)
2. Run the **server** module first
3. Then run the **client** module

---

## 5) Database Schema (save as db/schema.sql)
```sql
CREATE TABLE PLAYER (
    ID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    NAME VARCHAR(150) NOT NULL,
    EMAIL VARCHAR(150) UNIQUE NOT NULL,
    PASSWORD VARCHAR(100) NOT NULL,
    PLAYER_STATE SMALLINT NOT NULL,
    SCORE INT NOT NULL
);
CREATE TABLE GAME (
    PLAYER_ONE_ID INT NOT NULL,
    PLAYER_TWO_ID INT NOT NULL,
    GAME_DATE TIMESTAMP NOT NULL,
    GAME_STATE SMALLINT NOT NULL,
    PRIMARY KEY (PLAYER_ONE_ID, PLAYER_TWO_ID, GAME_DATE),
    CONSTRAINT FK_PLAYER_ONE FOREIGN KEY (PLAYER_ONE_ID)
        REFERENCES PLAYER (ID),
    CONSTRAINT FK_PLAYER_TWO FOREIGN KEY (PLAYER_TWO_ID)
        REFERENCES PLAYER (ID)
);
```

---

## 6) Git Workflow Rules (to avoid problems)
### Branching
- `main` is stable
- Each feature is developed in a separate branch:
  - `feature/<name>`
- Merge to `main` using Pull Requests

### What to commit
- Source code
- SQL scripts in `db/`
- Documentation (README files)

### What NOT to commit
- `build/`, `dist/`, `target/`
- NetBeans private user files
- Derby database directories/files

# Tic-Tac-Toe Network Game

## Team Members
1. Yassen
2. Nadin
3. Basmala
4. Mina

## How to Run the Project
### Prerequisites
- Java 23.0.2 installed.
- Apache Derby Database running on port 1527.
- Database named `ticTacToe` created.

### Steps
1. **Database:** Run the script provided in `database/tables.txt` to create the tables.
2. **Server:**
   - Open terminal in `Executables` folder.
   - Run: `java -jar tictactoe-server.jar`
3. **Client:**
   - Open terminal in `Executables` folder.
   - Run: `java -jar tictactoe-client.jar`
   - (If prompted, enter the Server IP address).

## Project Features
- User Authentication (Login/Register).
- Real-time list of online players.
- Game invitation system.
- Live Tic-Tac-Toe gameplay with score tracking.

---

## 7) Quick Start Checklist (new teammate)
1. Install Java 23.0.2 + NetBeans 28
2. Clone the repo
3. Create local Derby DB: `ticTacToe`
4. Run server, then client
