CREATE TABLE IF NOT EXISTS rp_select_option ( 
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    select_id INTEGER NOT NULL,
    created TEXT NOT NULL,
    name VARCHAR(255) NOT NULL,
    display VARCHAR(255) NOT NULL,
    color VARCHAR(255),
    FOREIGN KEY (select_id) REFERENCES rp_select ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS rp_player_attribute (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    created TEXT NOT NULL,
    player_id INTEGER NOT NULL,
    attribute_id INTEGER NOT NULL,
    value VARCHAR(255),
    FOREIGN KEY (player_id) REFERENCES rp_player ON DELETE CASCADE,
    FOREIGN KEY (attribute_id) REFERENCES rp_attribute ON DELETE CASCADE,
    UNIQUE (player_id, attribute_id) ON CONFLICT IGNORE
);
