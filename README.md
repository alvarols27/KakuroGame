# 🎲 Kakuro Master (Java Swing + PostgreSQL)

Kakuro Master is a desktop puzzle game built in Java with Swing, following an MVC architecture with a clean separation between models, views, and controllers. It lets players log in or play as a guest, solve randomly generated Kakuro grids across three difficulty levels, and track their progress, best scores, and saved games in a PostgreSQL database. The application was originally developed against SQLite and has since been migrated to PostgreSQL.

## 📦 Technologies

- **Language:** Java (JDK 17+)
- **UI Framework:** Java Swing / AWT
- **Architecture:** MVC (Model–View–Controller) with interface-based UI contracts
- **Database:** PostgreSQL (migrated from SQLite)
- **Data Access:** Plain JDBC with DAO pattern (`PlayerDAO`, `GameDAO`, `SavedGameDAO`)
- **Serialization:** Gson (grid state stored as JSON for saved games)
- **Preferences:** `java.util.prefs.Preferences` (theme + default difficulty per user)
- **Build/Run:** VS Code Java extension (no Maven/Gradle; JDBC driver in `lib/`)

## ⚙️ Features

- **Authentication:** Login, register, and guest mode (guest progress is not persisted).
- **Random Puzzle Generation:** Structured grids for Easy (5×5), Medium (7×7), and Hard (9×9) with unique-digit row/column constraints.
- **Real-Time Validation:** Rows and columns checked on every keystroke; clue cells light up green when a run is correct, red on duplicates or sum overflow.
- **Hint System:** Reveals a correct cell; on Hard difficulty it requires solving a timed math challenge, with a time penalty on failure.
- **Undo / Redo:** Move history tracked per session via `MoveRecord` stacks.
- **Note Mode:** Toggle input so numbers are treated as notes rather than committed values.
- **Timer:** Live per-session timer, with periodic roast messages for slow solves and a victory message on completion.
- **Save & Load:** Game state (grid + timer + hints) serialized to JSON and persisted to PostgreSQL; resumable from the dashboard.
- **Dashboard:** Personal statistics, per-difficulty best scores, full game history, and saved-game management (load / delete).
- **Themes:** Light and Dark modes applied via `UIManager` and persisted per installation.
- **Settings:** Per-user default difficulty remembered across sessions.

## 🧑‍🍳 The Process

- **Architecture:** Split the app into `model/`, `view/`, `controller/`, `interfaces/`, and `database/` packages, with controllers depending only on UI interfaces — never on concrete Swing classes.
- **Grid Generation:** Built `GridGenerator` to lay out a read-only clue skeleton, then fill playable cells with digits that respect Kakuro's no-duplicate rule per run.
- **Validation Engine:** Implemented real-time row/column checks in `GameController`, tracking sums and duplicates and propagating colour state to clue cells in `GameView`.
- **Persistence Layer:** Designed a normalized PostgreSQL schema (`players`, `game_results`, `best_scores`, `saved_games`) with foreign keys, cascade deletes, and check constraints on difficulty values.
- **DAO Pattern:** Isolated all SQL behind static DAO classes, keeping controllers free of JDBC code.
- **Session Model:** Encapsulated timer, hints, undo/redo stacks, and save state in `GameSession`, so controllers stay thin.
- **Migration to PostgreSQL:** Swapped the JDBC driver and URL, rewrote the SQL script (`INTEGER PRIMARY KEY AUTOINCREMENT` → `SERIAL`, `DATETIME` → `TIMESTAMP`, `BOOLEAN DEFAULT 0` → `BOOLEAN DEFAULT FALSE`), and verified inserts against the new database.

## 🧠 What I Learnt

- **MVC with Interface Contracts:** Using UI interfaces (`GameUI`, `LoginUI`, etc.) to decouple controllers from Swing meant views could be swapped without touching business logic.
- **JDBC & DAO Pattern:** Wrote and debugged prepared statements, generated keys, and result-set mapping by hand — no ORM, which made SQL and Java types explicit.
- **Database Migration:** Translated SQLite-specific DDL (`AUTOINCREMENT`, `DATETIME`, integer booleans) into PostgreSQL equivalents (`SERIAL`, `TIMESTAMP`, `BOOLEAN`).
- **Driver Classpath Issues:** Learned that `Class.forName("org.postgresql.Driver")` only works when the JDBC jar is actually on the runtime classpath — and how VS Code's Java "Referenced Libraries" configures that.
- **Real-Time UI Validation:** Handled document listeners carefully to avoid recursion and to keep grid state, session history, and Swing text fields in sync.
- **JSON Serialization:** Used Gson to persist complex grid objects, including handling no-arg constructors and nested cell arrays.
- **State Persistence:** Learned to use `java.util.prefs.Preferences` for lightweight per-user settings (theme, default difficulty).
- **Debugging UX Bugs:** Tracked down NPEs that only surfaced when the database connection failed, and made error paths fail loudly instead of silently.

## 🚥 Running the Project

**Prerequisites:** Java 17+ and PostgreSQL 14 or newer.

1. Clone the repository and open the folder in VS Code (with the Java extension pack installed).

2. Create the database:

   ```bash
   psql -d postgres -c "CREATE DATABASE kakurogame;"
   psql -d kakurogame -f database/kakuro_db_script_pg.sql
   ```

3. Download the PostgreSQL JDBC driver into `lib/`:

   ```bash
   mkdir -p lib
   curl -L -o lib/postgresql-42.7.4.jar \
     https://repo1.maven.org/maven2/org/postgresql/postgresql/42.7.4/postgresql-42.7.4.jar
   ```

4. In VS Code, open **Java Projects → Referenced Libraries → +** and add `lib/postgresql-42.7.4.jar`.

5. Update the connection constants in `src/database/DatabaseConnection.java`:

   ```java
   private static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/kakurogame";
   private static final String USER         = "your_postgres_user";
   private static final String PASSWORD     = "";
   ```

6. Run `src/Main.java` (▷ Run button in VS Code).

7. Register a new account, or choose **Continue as Guest** to play without saving progress.

## 🎛 Design Decision: MVC with Interfaces, and SQLite → PostgreSQL

The application was built with an interface-driven MVC split: each view implements a UI interface (`GameUI`, `LoginUI`, `DashboardUI`, `SettingsUI`, `RegisterUI`), and controllers only ever reference those interfaces. This kept the controllers testable and made the Swing layer replaceable.

**Why PostgreSQL:** The project started on SQLite for zero-config local development, but SQLite's limited type system and lack of a real server made it a poor fit for showing a proper database-backed workflow. Migrating to PostgreSQL preserved:

- **Provider swap:** `org.sqlite.JDBC` → `org.postgresql.Driver`, and a JDBC URL change.
- **Schema translation:** `AUTOINCREMENT` → `SERIAL`, `DATETIME` → `TIMESTAMP`, integer booleans → `BOOLEAN`, and `ON CONFLICT` upserts rewritten with `EXCLUDED.*`.
- **Driver management:** SQLite's jar was bundled implicitly; PostgreSQL's required explicitly adding the JDBC jar to the project's referenced libraries.

The trade-off accepted was a small amount of extra setup (installing PostgreSQL and adding the driver) in exchange for a realistic, server-backed database layer and a more credible portfolio piece.

## 🍿 Video Demonstration
https://github.com/user-attachments/assets/50ddc376-9671-4da7-af7f-4c40cc0cd7f9
