-- PLAYERS TABLE
CREATE TABLE IF NOT EXISTS players (
    id          SERIAL PRIMARY KEY,
    username    TEXT NOT NULL UNIQUE,
    password    TEXT NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_guest    BOOLEAN DEFAULT FALSE
);

-- GAME RESULTS TABLE
CREATE TABLE IF NOT EXISTS game_results (
    id          SERIAL PRIMARY KEY,
    player_id   INTEGER NOT NULL,
    time_played INTEGER NOT NULL,
    difficulty  TEXT NOT NULL CHECK (difficulty IN ('Easy', 'Medium', 'Hard')),
    hints_used  INTEGER DEFAULT 0,
    date        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE
);

-- BEST SCORES TABLE
CREATE TABLE IF NOT EXISTS best_scores (
    id            SERIAL PRIMARY KEY,
    player_id     INTEGER NOT NULL,
    difficulty    TEXT NOT NULL CHECK (difficulty IN ('Easy', 'Medium', 'Hard')),
    best_time     INTEGER NOT NULL,
    date_achieved TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE,
    UNIQUE (player_id, difficulty)
);

-- SAVED GAMES TABLE
CREATE TABLE IF NOT EXISTS saved_games (
    id              SERIAL PRIMARY KEY,
    player_id       INTEGER NOT NULL,
    difficulty      TEXT NOT NULL CHECK (difficulty IN ('Easy', 'Medium', 'Hard')),
    timer           INTEGER NOT NULL,
    grid_data       TEXT NOT NULL,
    hints_remaining INTEGER NOT NULL,
    hints_used      INTEGER DEFAULT 0,
    start_date      TIMESTAMP,
    last_saved      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    move_history    TEXT,
    FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE
);