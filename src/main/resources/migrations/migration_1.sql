CREATE TABLE IF NOT EXISTS rp_player ( 
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    created TEXT NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    uuid VARCHAR(255) NOT NULL UNIQUE,
    session_start TEXT DEFAULT NULL,
    played INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS rp_select (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    created TEXT NOT NULL,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS rp_attribute (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    created TEXT NOT NULL,
    name VARCHAR(255) NOT NULL UNIQUE,
    display VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    default_value VARCHAR(255),
    format VARCHAR(255) DEFAULT NULL,
    identity INTEGER NOT NULL DEFAULT 0,
    marker INTEGER NOT NULL DEFAULT 0,
    title INTEGER NOT NULL DEFAULT 0
);
