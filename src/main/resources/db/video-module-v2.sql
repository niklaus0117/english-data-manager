-- Incremental schema for the video module v2.
-- Apply this on top of an existing database created from the old schema.sql.

ALTER TABLE user_favorite
    ADD COLUMN IF NOT EXISTS target_type VARCHAR(16) NOT NULL DEFAULT 'video' AFTER user_id,
    ADD COLUMN IF NOT EXISTS target_id BIGINT NULL AFTER target_type;

UPDATE user_favorite
SET target_id = video_id
WHERE target_id IS NULL;

ALTER TABLE user_favorite
    MODIFY COLUMN target_id BIGINT NOT NULL;

ALTER TABLE playback_record
    ADD COLUMN IF NOT EXISTS album_id BIGINT NULL AFTER video_id,
    ADD COLUMN IF NOT EXISTS media_asset_id BIGINT NULL AFTER album_id,
    ADD COLUMN IF NOT EXISTS last_segment_no INT NOT NULL DEFAULT 0 AFTER progress_seconds,
    ADD COLUMN IF NOT EXISTS playback_rate DECIMAL(4, 2) NOT NULL DEFAULT 1.00 AFTER last_segment_no,
    ADD COLUMN IF NOT EXISTS device_id VARCHAR(64) NULL AFTER finished;

ALTER TABLE video
    ADD COLUMN IF NOT EXISTS title_origin VARCHAR(255) NULL AFTER title_en,
    ADD COLUMN IF NOT EXISTS short_title VARCHAR(255) NULL AFTER title_origin,
    ADD COLUMN IF NOT EXISTS poster_url VARCHAR(255) NULL AFTER cover_url,
    ADD COLUMN IF NOT EXISTS source_platform VARCHAR(32) NOT NULL DEFAULT 'upload' AFTER source_type,
    ADD COLUMN IF NOT EXISTS source_url VARCHAR(255) NULL AFTER source_platform,
    ADD COLUMN IF NOT EXISTS source_video_code VARCHAR(128) NULL AFTER source_url,
    ADD COLUMN IF NOT EXISTS file_size_bytes BIGINT NOT NULL DEFAULT 0 AFTER source_video_code,
    ADD COLUMN IF NOT EXISTS speaker_summary VARCHAR(255) NULL AFTER speaker,
    ADD COLUMN IF NOT EXISTS published_at DATETIME NULL AFTER summary,
    ADD COLUMN IF NOT EXISTS view_count BIGINT NOT NULL DEFAULT 0 AFTER published_at,
    ADD COLUMN IF NOT EXISTS favorite_count BIGINT NOT NULL DEFAULT 0 AFTER view_count,
    ADD COLUMN IF NOT EXISTS share_count BIGINT NOT NULL DEFAULT 0 AFTER favorite_count,
    ADD COLUMN IF NOT EXISTS word_count INT NOT NULL DEFAULT 0 AFTER share_count,
    ADD COLUMN IF NOT EXISTS subtitle_status VARCHAR(32) NOT NULL DEFAULT 'none' AFTER word_count,
    ADD COLUMN IF NOT EXISTS translation_status VARCHAR(32) NOT NULL DEFAULT 'none' AFTER subtitle_status,
    ADD COLUMN IF NOT EXISTS access_level VARCHAR(16) NOT NULL DEFAULT 'free' AFTER translation_status,
    ADD COLUMN IF NOT EXISTS translation_access_level VARCHAR(16) NOT NULL DEFAULT 'vip' AFTER access_level,
    ADD COLUMN IF NOT EXISTS allow_download TINYINT NOT NULL DEFAULT 0 AFTER translation_access_level,
    ADD COLUMN IF NOT EXISTS trial_seconds INT NOT NULL DEFAULT 0 AFTER allow_download,
    ADD COLUMN IF NOT EXISTS review_status TINYINT NOT NULL DEFAULT 0 AFTER publish_status,
    ADD COLUMN IF NOT EXISTS sort_no INT NOT NULL DEFAULT 0 AFTER recommendation_status;

ALTER TABLE category
    ADD COLUMN IF NOT EXISTS parent_id BIGINT NOT NULL DEFAULT 0 FIRST,
    ADD COLUMN IF NOT EXISTS category_type VARCHAR(32) NOT NULL DEFAULT 'general' AFTER code,
    ADD COLUMN IF NOT EXISTS level_no TINYINT NOT NULL DEFAULT 1 AFTER category_type,
    ADD COLUMN IF NOT EXISTS banner_url VARCHAR(255) NULL AFTER icon_url;

ALTER TABLE video_album
    ADD COLUMN IF NOT EXISTS tags VARCHAR(255) NULL AFTER description,
    ADD COLUMN IF NOT EXISTS author_name VARCHAR(128) NULL AFTER tags,
    ADD COLUMN IF NOT EXISTS publisher_name VARCHAR(128) NULL AFTER author_name,
    ADD COLUMN IF NOT EXISTS price DECIMAL(10, 2) NOT NULL DEFAULT 0.00 AFTER publisher_name,
    ADD COLUMN IF NOT EXISTS access_type VARCHAR(32) NOT NULL DEFAULT 'purchase' AFTER price,
    ADD COLUMN IF NOT EXISTS theme_color VARCHAR(32) NULL AFTER access_type;

ALTER TABLE user_order
    ADD COLUMN IF NOT EXISTS course_id BIGINT NULL AFTER package_id,
    ADD COLUMN IF NOT EXISTS course_title VARCHAR(255) NULL AFTER course_id;

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

CREATE TABLE IF NOT EXISTS user_course_access (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    source_type VARCHAR(32) NOT NULL DEFAULT 'purchase',
    status TINYINT NOT NULL DEFAULT 1,
    granted_at DATETIME NULL,
    expired_at DATETIME NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_course_access (user_id, course_id)
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

CREATE TABLE IF NOT EXISTS user_note (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    video_id BIGINT NOT NULL,
    video_timestamp INT NOT NULL DEFAULT 0,
    content TEXT NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_note_user_video (user_id, video_id),
    KEY idx_note_update_time (update_time)
);

CREATE TABLE IF NOT EXISTS user_vocabulary (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    source_video_id BIGINT NULL,
    word VARCHAR(128) NOT NULL,
    phonetic VARCHAR(128) NULL,
    translation VARCHAR(500) NULL,
    review_count INT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_word (user_id, word),
    KEY idx_vocab_user_update (user_id, update_time)
);
