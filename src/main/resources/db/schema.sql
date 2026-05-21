CREATE DATABASE IF NOT EXISTS english_data_manager DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE english_data_manager;

CREATE TABLE IF NOT EXISTS admin_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    nickname VARCHAR(64) NOT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    last_login_at DATETIME NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS admin_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(64) NOT NULL,
    role_code VARCHAR(64) NOT NULL UNIQUE,
    status TINYINT NOT NULL DEFAULT 1,
    description VARCHAR(255) NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS admin_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    parent_id BIGINT NOT NULL DEFAULT 0,
    menu_name VARCHAR(64) NOT NULL,
    menu_code VARCHAR(64) NOT NULL UNIQUE,
    path VARCHAR(128) NULL,
    component VARCHAR(128) NULL,
    icon VARCHAR(64) NULL,
    menu_type TINYINT NOT NULL DEFAULT 1,
    sort_no INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS admin_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    admin_user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_admin_user_role (admin_user_id, role_id)
);

CREATE TABLE IF NOT EXISTS admin_role_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_admin_role_menu (role_id, menu_id)
);

CREATE TABLE IF NOT EXISTS category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    parent_id BIGINT NOT NULL DEFAULT 0,
    name VARCHAR(64) NOT NULL,
    code VARCHAR(64) NOT NULL UNIQUE,
    category_type VARCHAR(32) NOT NULL DEFAULT 'general',
    level_no TINYINT NOT NULL DEFAULT 1,
    sort_no INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    description VARCHAR(255) NULL,
    icon_url VARCHAR(255) NULL,
    banner_url VARCHAR(255) NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS recommendation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(128) NOT NULL,
    position_code VARCHAR(64) NOT NULL,
    video_id BIGINT NULL,
    image_url VARCHAR(255) NULL,
    jump_type VARCHAR(32) NOT NULL DEFAULT 'video',
    jump_value VARCHAR(255) NULL,
    sort_no INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS video (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_id BIGINT NOT NULL,
    title_cn VARCHAR(255) NOT NULL,
    title_en VARCHAR(255) NOT NULL,
    title_origin VARCHAR(255) NULL,
    short_title VARCHAR(255) NULL,
    cover_url VARCHAR(255) NULL,
    poster_url VARCHAR(255) NULL,
    video_url VARCHAR(255) NULL,
    difficulty_level TINYINT NOT NULL DEFAULT 1,
    duration_seconds INT NOT NULL DEFAULT 0,
    source_type VARCHAR(32) NOT NULL DEFAULT 'upload',
    source_platform VARCHAR(32) NOT NULL DEFAULT 'upload',
    source_url VARCHAR(255) NULL,
    source_video_code VARCHAR(128) NULL,
    file_size_bytes BIGINT NOT NULL DEFAULT 0,
    speaker VARCHAR(128) NULL,
    speaker_summary VARCHAR(255) NULL,
    tags VARCHAR(255) NULL,
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
    recommendation_status TINYINT NOT NULL DEFAULT 0,
    sort_no INT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_video_category (category_id),
    KEY idx_video_publish (publish_status),
    KEY idx_video_published_at (published_at)
);

CREATE TABLE IF NOT EXISTS video_subtitle (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    video_id BIGINT NOT NULL,
    language_type VARCHAR(16) NOT NULL,
    subtitle_url VARCHAR(255) NULL,
    subtitle_text LONGTEXT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_subtitle_video (video_id)
);

CREATE TABLE IF NOT EXISTS video_content (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    video_id BIGINT NOT NULL,
    content_type VARCHAR(32) NOT NULL,
    title VARCHAR(255) NOT NULL,
    content_body LONGTEXT NULL,
    sort_no INT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_content_video (video_id)
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
    tags VARCHAR(255) NULL,
    author_name VARCHAR(128) NULL,
    publisher_name VARCHAR(128) NULL,
    price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    access_type VARCHAR(32) NOT NULL DEFAULT 'purchase',
    theme_color VARCHAR(32) NULL,
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

CREATE TABLE IF NOT EXISTS permission_group (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    group_name VARCHAR(64) NOT NULL,
    group_code VARCHAR(64) NOT NULL UNIQUE,
    status TINYINT NOT NULL DEFAULT 1,
    feature_codes VARCHAR(500) NULL,
    description VARCHAR(255) NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS membership_package (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    package_name VARCHAR(64) NOT NULL,
    package_code VARCHAR(64) NOT NULL UNIQUE,
    price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    duration_days INT NOT NULL DEFAULT 30,
    sort_no INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    description VARCHAR(255) NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS app_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    mobile VARCHAR(32) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    nickname VARCHAR(64) NOT NULL,
    avatar_url VARCHAR(255) NULL,
    status TINYINT NOT NULL DEFAULT 1,
    permission_group_id BIGINT NULL,
    vip_expire_at DATETIME NULL,
    last_login_at DATETIME NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_user_group (permission_group_id)
);

CREATE TABLE IF NOT EXISTS user_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(64) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    package_id BIGINT NULL,
    course_id BIGINT NULL,
    course_title VARCHAR(255) NULL,
    order_type VARCHAR(32) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    currency VARCHAR(16) NOT NULL DEFAULT 'CNY',
    pay_status TINYINT NOT NULL DEFAULT 0,
    pay_channel VARCHAR(32) NULL,
    transaction_no VARCHAR(64) NULL,
    paid_at DATETIME NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_order_user (user_id),
    KEY idx_order_status (pay_status)
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

CREATE TABLE IF NOT EXISTS system_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_key VARCHAR(128) NOT NULL UNIQUE,
    config_value VARCHAR(1000) NULL,
    config_group VARCHAR(64) NULL,
    description VARCHAR(255) NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO admin_user (username, password, nickname, status)
VALUES ('admin', '91e6855b534fa0af53a2c982ed99fff6486c1e54f0834eb90bd9711393866210', 'System Admin', 1)
ON DUPLICATE KEY UPDATE nickname = VALUES(nickname);

INSERT INTO admin_role (role_name, role_code, status, description)
VALUES ('Super Admin', 'SUPER_ADMIN', 1, 'Full access for admin backend')
ON DUPLICATE KEY UPDATE description = VALUES(description);

INSERT INTO admin_menu (id, parent_id, menu_name, menu_code, path, component, icon, menu_type, sort_no, status)
VALUES (1, 0, 'Dashboard', 'dashboard', '/dashboard', 'dashboard/index', 'dashboard', 1, 1, 1),
       (2, 0, 'Content', 'content', '/content', 'Layout', 'video', 1, 2, 1),
       (3, 2, 'Category Mgmt', 'category_mgmt', '/content/category', 'content/category/index', 'list', 1, 1, 1),
       (4, 2, 'Recommend Mgmt', 'recommend_mgmt', '/content/recommend', 'content/recommend/index', 'star', 1, 2, 1),
       (5, 2, 'Video Mgmt', 'video_mgmt', '/content/video', 'content/video/index', 'play-circle', 1, 3, 1),
       (6, 0, 'User', 'user', '/user', 'Layout', 'user', 1, 3, 1),
       (7, 6, 'User Mgmt', 'user_mgmt', '/user/list', 'user/list/index', 'team', 1, 1, 1),
       (8, 6, 'Order Mgmt', 'order_mgmt', '/user/order', 'user/order/index', 'wallet', 1, 2, 1),
       (9, 0, 'System', 'system', '/system', 'Layout', 'setting', 1, 4, 1),
       (10, 9, 'Role Mgmt', 'role_mgmt', '/system/role', 'system/role/index', 'safety-certificate', 1, 1, 1),
       (11, 9, 'Menu Mgmt', 'menu_mgmt', '/system/menu', 'system/menu/index', 'menu', 1, 2, 1),
       (12, 9, 'Group Mgmt', 'group_mgmt', '/system/group', 'system/group/index', 'appstore', 1, 3, 1),
       (13, 9, 'Config Mgmt', 'config_mgmt', '/system/config', 'system/config/index', 'tool', 1, 4, 1)
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

INSERT INTO admin_user_role (admin_user_id, role_id)
VALUES (1, 1)
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

INSERT INTO admin_role_menu (role_id, menu_id)
VALUES (1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10), (1, 11), (1, 12), (1, 13)
ON DUPLICATE KEY UPDATE menu_id = VALUES(menu_id);

INSERT INTO permission_group (group_name, group_code, status, feature_codes, description)
VALUES ('Basic', 'BASIC', 1, 'home,video,favorite,playback', 'Default basic learning permissions'),
       ('VIP', 'VIP', 1, 'home,video,favorite,playback,subtitle,download,exam', 'Advanced learning permissions')
ON DUPLICATE KEY UPDATE description = VALUES(description);

INSERT INTO membership_package (id, package_name, package_code, price, duration_days, sort_no, status, description)
VALUES (1, 'VIP 30 Days', 'VIP_30_DAYS', 99.00, 30, 1, 1, '30-day VIP access'),
       (2, 'VIP 90 Days', 'VIP_90_DAYS', 268.00, 90, 2, 1, '90-day VIP access'),
       (3, 'VIP 365 Days', 'VIP_365_DAYS', 899.00, 365, 3, 1, '365-day VIP access')
ON DUPLICATE KEY UPDATE price = VALUES(price), duration_days = VALUES(duration_days);

INSERT INTO app_user (mobile, password, nickname, status, permission_group_id)
VALUES ('13800000000', '7487e6843f9ef9181e02c3476abd89fb4ce0138c93c61ea2e79d18bc0aac49f6', 'Demo User', 1, 1)
ON DUPLICATE KEY UPDATE nickname = VALUES(nickname);

INSERT INTO category (id, parent_id, name, code, category_type, level_no, sort_no, status, description)
VALUES (1, 0, 'Spoken English', 'spoken', 'general', 1, 1, 1, 'Spoken English training'),
       (2, 0, 'Listening', 'listening', 'general', 1, 2, 1, 'Listening practice'),
       (3, 0, 'Business English', 'business', 'general', 1, 3, 1, 'Business English learning'),
       (101, 0, '科学分级', 'daily_science', 'daily_reading', 1, 1, 1, '科学分级阅读'),
       (102, 0, '每日听读', 'daily_listening', 'daily_reading', 1, 2, 1, '每日英语听读'),
       (103, 0, '晨读美文', 'morning_reading', 'daily_reading', 1, 3, 1, '晨读美文'),
       (104, 0, '优美范文', 'essay_reading', 'daily_reading', 1, 4, 1, '优美范文')
ON DUPLICATE KEY UPDATE description = VALUES(description);

INSERT INTO video (id, category_id, title_cn, title_en, title_origin, short_title, cover_url, poster_url, video_url, difficulty_level, duration_seconds, source_type, source_platform, source_url, source_video_code, file_size_bytes, speaker, speaker_summary, tags, summary, published_at, view_count, favorite_count, share_count, word_count, subtitle_status, translation_status, access_level, translation_access_level, allow_download, trial_seconds, publish_status, review_status, recommendation_status, sort_no)
VALUES (2001, 101, '1.A Wonderful Weekend 一个美好的周末.mp3', 'A Wonderful Weekend', 'A Wonderful Weekend', 'A Wonderful Weekend', '/static/demo-cover-1.png', '/static/demo-poster-1.png', '/static/demo-video-1.mp4', 1, 230, 'upload', 'YouTube', 'https://www.youtube.com/watch?v=demo001', 'demo001', 127262720, 'Teacher Amy', 'Teacher Amy', '共137篇,小学,初中', '每日英语听读，150篇精选范文搞定小初2500词。', '2025-04-12 09:18:00', 3130000, 2800, 320, 2500, 'ready', 'reviewed', 'free', 'vip', 1, 30, 1, 1, 1, 1),
       (2002, 101, '2.A Day at School 在学校的一天.MP3', 'A Day at School', 'A Day at School', 'A Day at School', '/static/demo-cover-2.png', '/static/demo-poster-2.png', '/static/demo-video-2.mp4', 1, 245, 'upload', 'YouTube', 'https://www.youtube.com/watch?v=demo002', 'demo002', 120000000, 'Teacher Amy', 'Teacher Amy', '共137篇,小学,初中', '在学校的一天。', '2025-04-11 09:18:00', 2800000, 1800, 220, 2600, 'ready', 'reviewed', 'free', 'vip', 1, 30, 1, 1, 1, 2),
       (2003, 102, '3.What Do You Usually Eat? 你通常吃什么.MP3', 'What Do You Usually Eat?', 'What Do You Usually Eat?', 'What Do You Usually Eat?', '/static/demo-cover-3.png', '/static/demo-poster-3.png', '/static/demo-video-3.mp4', 2, 260, 'upload', 'YouTube', 'https://www.youtube.com/watch?v=demo003', 'demo003', 118000000, 'DailyNews', 'DailyNews', '共7篇,高中,四六级', '饮食主题听读。', '2025-04-10 09:18:00', 980000, 860, 100, 6000, 'ready', 'reviewed', 'vip', 'vip', 1, 30, 1, 1, 1, 3),
       (2004, 102, '4.What''s Your Hobby? 你的爱好是什么?.mp3', 'What''s Your Hobby?', 'What''s Your Hobby?', 'What''s Your Hobby?', '/static/demo-cover-4.png', '/static/demo-poster-4.png', '/static/demo-video-4.mp4', 2, 255, 'upload', 'YouTube', 'https://www.youtube.com/watch?v=demo004', 'demo004', 119000000, 'DailyNews', 'DailyNews', '共7篇,高中,四六级', '兴趣主题听读。', '2025-04-09 09:18:00', 870000, 600, 70, 6000, 'ready', 'reviewed', 'vip', 'vip', 1, 30, 1, 1, 1, 4),
       (2005, 103, '5.My Favorite Season 我最喜欢的季节.mp3', 'My Favorite Season', 'My Favorite Season', 'My Favorite Season', '/static/demo-cover-5.png', '/static/demo-poster-5.png', '/static/demo-video-5.mp4', 1, 215, 'upload', 'YouTube', 'https://www.youtube.com/watch?v=demo005', 'demo005', 98000000, 'TeachMaster', 'TeachMaster', '共60篇,初中', '季节主题听读。', '2025-04-08 09:18:00', 650000, 420, 48, 2000, 'ready', 'ai_only', 'vip', 'vip', 0, 0, 1, 1, 1, 5),
       (2006, 104, '6.Travel Plans 旅行计划.mp3', 'Travel Plans', 'Travel Plans', 'Travel Plans', '/static/demo-cover-6.png', '/static/demo-poster-6.png', '/static/demo-video-6.mp4', 1, 240, 'upload', 'YouTube', 'https://www.youtube.com/watch?v=demo006', 'demo006', 99000000, 'USA Family', 'USA Family', '共78篇,小初高,四六级', '旅行计划主题听读。', '2025-04-07 09:18:00', 430000, 300, 32, 2000, 'ready', 'reviewed', 'vip', 'vip', 0, 0, 1, 1, 1, 6)
ON DUPLICATE KEY UPDATE summary = VALUES(summary);

INSERT INTO recommendation (title, position_code, video_id, image_url, jump_type, jump_value, sort_no, status)
VALUES ('Home Banner', 'HOME_BANNER', 1, '/static/demo-banner.png', 'video', '1', 1, 1)
ON DUPLICATE KEY UPDATE status = VALUES(status);

INSERT INTO video_subtitle (video_id, language_type, subtitle_text)
VALUES (1, 'zh', 'Today we will learn some common greeting expressions.'),
       (1, 'en', 'Hello everyone, today we will learn common greeting expressions.')
ON DUPLICATE KEY UPDATE subtitle_text = VALUES(subtitle_text);

INSERT INTO video_content (video_id, content_type, title, content_body, sort_no)
VALUES (1, 'word', 'Key Words', 'hello, hi, how are you', 1),
       (1, 'note', 'Study Notes', 'Pay attention to tone and usage context.', 2)
ON DUPLICATE KEY UPDATE content_body = VALUES(content_body);

INSERT INTO video_album (id, category_id, album_type, title, subtitle, source_name, source_platform, cover_url, banner_url, description, tags, author_name, publisher_name, price, access_type, theme_color, publish_date, view_count, word_count, is_featured, status, sort_no)
VALUES (1, 2, 'series', '演员对谈', 'Actors on Actors', 'Actors on Actors', 'YouTube', '/static/album-cover.png', '/static/album-banner.png', '选自 Youtube 的 Variety 频道，用一对一访谈方式对职业演员进行深度对谈。', '影视,访谈', 'Variety', 'Variety', 0.00, 'free', 'bg-slate-600', '2025-04-12', 3130000, 6300, 1, 1, 1),
       (1001, 101, 'course', '150篇搞定小初核心2500词', '每日英语听读，150篇精选范文...', 'Mater', 'YouTube', '/static/course-cover-1.png', '/static/course-banner-1.png', '每日英语听读，150篇精选范文搞定小初2500词。', '共137篇,小学,初中', 'Mater', 'Nice', 68.00, 'purchase', 'bg-green-600', '2025-04-12', 7888143, 2500, 1, 1, 2),
       (1002, 102, 'daily_reading', '每日英语播报', '每日英语播报，收集超多经典演...', 'DailyNews', 'YouTube', '/static/course-cover-2.png', '/static/course-banner-2.png', '每天十分钟，听遍全世界。', '共7篇,高中,四六级', 'DailyNews', 'Nice', 0.00, 'free', 'bg-blue-600', '2025-04-10', 221911, 6000, 1, 1, 3),
       (1003, 103, 'daily_reading', 'Grade Eight 八年级', '精选52篇时文热点，学完搞定八...', 'TeachMaster', 'YouTube', '/static/course-cover-3.png', '/static/course-banner-3.png', '紧扣教材，拓展阅读。', '共60篇,初中', 'TeachMaster', 'Nice', 45.00, 'purchase', 'bg-orange-600', '2025-04-09', 632701, 2000, 0, 1, 4),
       (1004, 104, 'daily_reading', '走遍美国（天宇）', '以电视影集形式展现美国国家家庭...', 'USA Family', 'YouTube', '/static/course-cover-4.png', '/static/course-banner-4.png', '经典教材，地道美语。', '共78篇,小初高,四六级', 'USA Family', 'Nice', 99.00, 'vip', 'bg-red-600', '2025-04-08', 15431, 2000, 0, 1, 5)
ON DUPLICATE KEY UPDATE title = VALUES(title), description = VALUES(description);

INSERT INTO video_album_item (album_id, video_id, sort_no, is_default_album)
VALUES (1, 2001, 1, 1), (1, 2002, 2, 0),
       (1001, 2001, 1, 1), (1001, 2002, 2, 0),
       (1002, 2003, 1, 1), (1002, 2004, 2, 0),
       (1003, 2005, 1, 1), (1003, 2002, 2, 0),
       (1004, 2006, 1, 1), (1004, 2001, 2, 0)
ON DUPLICATE KEY UPDATE sort_no = VALUES(sort_no);

INSERT INTO video_media_asset (video_id, asset_type, file_url, storage_provider, container_format, resolution, size_bytes, duration_seconds, is_default, status)
VALUES (2001, 'video', '/static/demo-video-1.mp4', 'local', 'mp4', '1080p', 127262720, 230, 1, 1),
       (2002, 'video', '/static/demo-video-2.mp4', 'local', 'mp4', '1080p', 120000000, 245, 1, 1)
ON DUPLICATE KEY UPDATE file_url = VALUES(file_url);

INSERT INTO video_subtitle_track (video_id, track_type, language_code, file_format, subtitle_url, is_default, is_ai_generated, status)
VALUES (2001, 'origin', 'en', 'json', '/static/demo-video-1-en.json', 1, 0, 1),
       (2001, 'translation', 'zh-CN', 'json', '/static/demo-video-1-zh.json', 0, 1, 1),
       (2002, 'origin', 'en', 'json', '/static/demo-video-2-en.json', 1, 0, 1)
ON DUPLICATE KEY UPDATE subtitle_url = VALUES(subtitle_url);

INSERT INTO video_transcript_segment (video_id, subtitle_track_id, track_group, segment_no, start_ms, end_ms, speaker_name, speaker_role, origin_text, translation_text, note_text, is_translation_locked, sort_no)
VALUES (2001, 1, 'seg-1', 1, 0, 3200, 'Teacher Amy', 'host', 'A wonderful weekend.', '一个美好的周末。', '适合入门听力训练。', 0, 1),
       (2001, 1, 'seg-2', 2, 3200, 6200, 'Teacher Amy', 'host', 'I had a good time last weekend.', '我上周末过得很开心。', '常用过去时表达。', 0, 2),
       (2002, 3, 'seg-1', 1, 0, 3400, 'Teacher Amy', 'host', 'This is a day at school.', '这是在学校的一天。', '学校主题听力。', 0, 1),
       (2003, NULL, 'seg-1', 1, 0, 2800, 'DailyNews', 'host', 'What do you usually eat?', '你通常吃什么？', '饮食主题。', 1, 1),
       (2004, NULL, 'seg-1', 1, 0, 2800, 'DailyNews', 'host', 'What is your hobby?', '你的爱好是什么？', '兴趣主题。', 1, 1),
       (2005, NULL, 'seg-1', 1, 0, 2800, 'TeachMaster', 'host', 'My favorite season is spring.', '我最喜欢的季节是春天。', '季节主题。', 1, 1),
       (2006, NULL, 'seg-1', 1, 0, 2800, 'USA Family', 'host', 'I am making travel plans for summer.', '我在为夏天制定旅行计划。', '旅行主题。', 1, 1)
ON DUPLICATE KEY UPDATE translation_text = VALUES(translation_text), note_text = VALUES(note_text);

INSERT INTO user_order (order_no, user_id, package_id, course_id, course_title, order_type, amount, currency, pay_status, pay_channel, transaction_no, paid_at)
VALUES ('ORD202604090001', 1, 1, NULL, NULL, 'VIP_PACKAGE', 99.00, 'CNY', 1, 'wechat', 'TXN202604090001', NOW()),
       ('ORD202604090002', 1, NULL, 1001, '150篇搞定小初核心2500词', 'COURSE_PURCHASE', 68.00, 'CNY', 1, 'demo', 'COURSE-ORD202604090002', NOW())
ON DUPLICATE KEY UPDATE pay_status = VALUES(pay_status);

INSERT INTO user_course_access (user_id, course_id, source_type, status, granted_at)
VALUES (1, 1001, 'purchase', 1, NOW())
ON DUPLICATE KEY UPDATE status = VALUES(status);

INSERT INTO user_download_record (user_id, video_id, status, download_url, file_size_bytes, expired_at)
VALUES (1, 2001, 'done', '/static/demo-video-1.mp4', 127262720, DATE_ADD(NOW(), INTERVAL 30 DAY))
ON DUPLICATE KEY UPDATE status = VALUES(status), download_url = VALUES(download_url);

INSERT INTO system_config (config_key, config_value, config_group, description)
VALUES ('app.default.language', 'en', 'app', 'Default app language'),
       ('app.video.page-size', '10', 'app', 'Default page size for app video list'),
       ('admin.upload.max-size', '500MB', 'admin', 'Admin upload file size limit')
ON DUPLICATE KEY UPDATE config_value = VALUES(config_value);
