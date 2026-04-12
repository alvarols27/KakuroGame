--PLAYERS TABLE
CREATE TABLE IF NOT EXISTS players (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    is_guest BOOLEAN DEFAULT 0
);

--GAME RESULTS TABLE
CREATE TABLE IF NOT EXISTS game_results (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    player_id INTEGER NOT NULL,
    time_played INTEGER NOT NULL,
    difficulty TEXT NOT NULL CHECK(difficulty IN ('Easy', 'Medium', 'Hard')),
    hints_used INTEGER DEFAULT 0,                       
    date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE
);

--BEST SCORES TABLE
CREATE TABLE IF NOT EXISTS best_scores (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    player_id INTEGER NOT NULL,
    difficulty TEXT NOT NULL CHECK(difficulty IN ('Easy', 'Medium', 'Hard')),
    best_time INTEGER NOT NULL,
    date_achieved DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE,
    UNIQUE(player_id, difficulty)
);

--SAVED GAMES TABLE
CREATE TABLE IF NOT EXISTS saved_games (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    player_id INTEGER NOT NULL,
    difficulty TEXT NOT NULL CHECK(difficulty IN ('Easy', 'Medium', 'Hard')),
    timer INTEGER NOT NULL,
    grid_data TEXT NOT NULL,
    hints_remaining INTEGER NOT NULL,
    hints_used INTEGER DEFAULT 0,
    start_date DATETIME,
    last_saved DATETIME DEFAULT CURRENT_TIMESTAMP,
    move_history TEXT,
    FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE
);