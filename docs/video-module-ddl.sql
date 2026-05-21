-- Reference schema for the video module v2.
-- This file is a full design draft for review.
-- It is not an in-place migration script for the current database.

CREATE TABLE IF NOT EXISTS category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    parent_id BIGINT NOT NULL DEFAULT 0,
    name VARCHAR(64) NOT NULL,
    code VARCHAR(64) NOT NULL UNIQUE,
    category_type VARCHAR(32) NOT NULL DEFAULT 'channel',
    level_no TINYINT NOT NULL DEFAULT 1,
    icon_url VARCHAR(255) NULL,
    banner_url VARCHAR(255) NULL,
    description VARCHAR(500) NULL,
    sort_no INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_category_parent (parent_id),
    KEY idx_category_type_status (category_type, status)
);

CREATE TABLE IF NOT EXISTS video_source (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    source_name VARCHAR(128) NOT NULL,
    source_code VARCHAR(64) NOT NULL UNIQUE,
    source_platform VARCHAR(32) NOT NULL DEFAULT 'upload',
    source_url VARCHAR(255) NULL,
    description VARCHAR(500) NULL,
    status TINYINT NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS video_album (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_id BIGINT NOT NULL,
    album_type VARCHAR(32) NOT NULL DEFAULT 'series',
    title VARCHAR(255) NOT NULL,
    subtitle VARCHAR(255) NULL,
    source_name VARCHAR(128) NULL,
    source_platform VARCHAR(32) NULL,
    cover_url VARCHAR(255) NULL,
    banner_url VARCHAR(255) NULL,
    description TEXT NULL,
    publish_date DATE NULL,
    view_count BIGINT NOT NULL DEFAULT 0,
    favorite_count BIGINT NOT NULL DEFAULT 0,
    share_count BIGINT NOT NULL DEFAULT 0,
    word_count INT NOT NULL DEFAULT 0,
    is_featured TINYINT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    sort_no INT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_album_category (category_id),
    KEY idx_album_status_sort (status, sort_no)
);

CREATE TABLE IF NOT EXISTS video (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    source_id BIGINT NULL,
    title_cn VARCHAR(255) NOT NULL,
    title_en VARCHAR(255) NULL,
    title_origin VARCHAR(255) NULL,
    short_title VARCHAR(255) NULL,
    cover_url VARCHAR(255) NULL,
    poster_url VARCHAR(255) NULL,
    source_platform VARCHAR(32) NOT NULL DEFAULT 'upload',
    source_url VARCHAR(255) NULL,
    source_video_code VARCHAR(128) NULL,
    duration_seconds INT NOT NULL DEFAULT 0,
    file_size_bytes BIGINT NOT NULL DEFAULT 0,
    difficulty_level TINYINT NOT NULL DEFAULT 1,
    speaker_summary VARCHAR(255) NULL,
    summary TEXT NULL,
    published_at DATETIME NULL,
    view_count BIGINT NOT NULL DEFAULT 0,
    favorite_count BIGINT NOT NULL DEFAULT 0,
    share_count BIGINT NOT NULL DEFAULT 0,
    word_count INT NOT NULL DEFAULT 0,
    subtitle_status VARCHAR(32) NOT NULL DEFAULT 'none',
    translation_status VARCHAR(32) NOT NULL DEFAULT 'none',
    access_level VARCHAR(16) NOT NULL DEFAULT 'free',
    translation_access_level VARCHAR(16) NOT NULL DEFAULT 'vip',
    allow_download TINYINT NOT NULL DEFAULT 0,
    trial_seconds INT NOT NULL DEFAULT 0,
    publish_status TINYINT NOT NULL DEFAULT 0,
    review_status TINYINT NOT NULL DEFAULT 0,
    sort_no INT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_video_source (source_id),
    KEY idx_video_publish (publish_status),
    KEY idx_video_status_sort (publish_status, sort_no),
    KEY idx_video_published_at (published_at)
);

CREATE TABLE IF NOT EXISTS video_album_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    album_id BIGINT NOT NULL,
    video_id BIGINT NOT NULL,
    sort_no INT NOT NULL DEFAULT 0,
    is_default_album TINYINT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_album_video (album_id, video_id),
    KEY idx_album_item_video (video_id)
);

CREATE TABLE IF NOT EXISTS video_media_asset (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    video_id BIGINT NOT NULL,
    asset_type VARCHAR(32) NOT NULL,
    file_url VARCHAR(255) NOT NULL,
    storage_provider VARCHAR(32) NOT NULL DEFAULT 'local',
    container_format VARCHAR(32) NULL,
    resolution VARCHAR(32) NULL,
    bitrate_kbps INT NULL,
    size_bytes BIGINT NOT NULL DEFAULT 0,
    duration_seconds INT NOT NULL DEFAULT 0,
    is_default TINYINT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_media_video_type (video_id, asset_type),
    KEY idx_media_status (status)
);

CREATE TABLE IF NOT EXISTS video_subtitle_track (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    video_id BIGINT NOT NULL,
    track_type VARCHAR(32) NOT NULL,
    language_code VARCHAR(16) NOT NULL,
    file_format VARCHAR(16) NOT NULL DEFAULT 'json',
    subtitle_url VARCHAR(255) NULL,
    is_default TINYINT NOT NULL DEFAULT 0,
    is_ai_generated TINYINT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_track_video (video_id),
    KEY idx_track_type_lang (track_type, language_code)
);

CREATE TABLE IF NOT EXISTS video_transcript_segment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    video_id BIGINT NOT NULL,
    subtitle_track_id BIGINT NULL,
    track_group VARCHAR(64) NULL,
    segment_no INT NOT NULL,
    start_ms INT NOT NULL DEFAULT 0,
    end_ms INT NOT NULL DEFAULT 0,
    speaker_name VARCHAR(128) NULL,
    speaker_role VARCHAR(64) NULL,
    origin_text TEXT NULL,
    translation_text TEXT NULL,
    note_text TEXT NULL,
    keywords_json JSON NULL,
    is_translation_locked TINYINT NOT NULL DEFAULT 0,
    sort_no INT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_segment_video_sort (video_id, sort_no),
    KEY idx_segment_track (subtitle_track_id),
    KEY idx_segment_time (video_id, start_ms)
);

CREATE TABLE IF NOT EXISTS video_content (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    video_id BIGINT NOT NULL,
    content_type VARCHAR(32) NOT NULL,
    title VARCHAR(255) NOT NULL,
    content_body LONGTEXT NULL,
    extra_json JSON NULL,
    sort_no INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_content_video_type (video_id, content_type)
);

CREATE TABLE IF NOT EXISTS video_tag (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tag_name VARCHAR(64) NOT NULL,
    tag_code VARCHAR(64) NOT NULL UNIQUE,
    status TINYINT NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS video_tag_rel (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    video_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_video_tag (video_id, tag_id),
    KEY idx_video_tag_rel_tag (tag_id)
);

CREATE TABLE IF NOT EXISTS video_person (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    person_name VARCHAR(128) NOT NULL,
    person_name_en VARCHAR(128) NULL,
    avatar_url VARCHAR(255) NULL,
    profile TEXT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS video_person_rel (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    video_id BIGINT NOT NULL,
    person_id BIGINT NOT NULL,
    role_code VARCHAR(32) NOT NULL DEFAULT 'guest',
    sort_no INT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_video_person (video_id, person_id, role_code),
    KEY idx_video_person_rel_person (person_id)
);

CREATE TABLE IF NOT EXISTS user_favorite (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    target_type VARCHAR(16) NOT NULL DEFAULT 'video',
    target_id BIGINT NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_target (user_id, target_type, target_id),
    KEY idx_favorite_target (target_type, target_id)
);

CREATE TABLE IF NOT EXISTS playback_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    video_id BIGINT NOT NULL,
    album_id BIGINT NULL,
    media_asset_id BIGINT NULL,
    progress_seconds INT NOT NULL DEFAULT 0,
    last_segment_no INT NOT NULL DEFAULT 0,
    playback_rate DECIMAL(4, 2) NOT NULL DEFAULT 1.00,
    finished TINYINT NOT NULL DEFAULT 0,
    device_id VARCHAR(64) NULL,
    last_played_at DATETIME NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_playback_user_video (user_id, video_id),
    KEY idx_playback_album (album_id),
    KEY idx_playback_last_played_at (last_played_at)
);

CREATE TABLE IF NOT EXISTS user_download_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    video_id BIGINT NOT NULL,
    subtitle_track_id BIGINT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'queued',
    download_url VARCHAR(255) NULL,
    file_size_bytes BIGINT NOT NULL DEFAULT 0,
    expired_at DATETIME NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_download_user_status (user_id, status),
    KEY idx_download_video (video_id)
);
