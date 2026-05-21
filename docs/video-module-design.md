# 视频数据管理设计

## 1. 前端页面拆解

结合图片里的两个页面，后端数据对象建议按下面的层级组织：

1. 分类 `category`
   例如“双语精选”“演员对谈”所属的大栏目、子栏目。
2. 专题/合集 `video_album`
   对应“演员对谈”这种有标题、简介、浏览量、发布日期、词汇量、收藏状态的集合页。
3. 视频 `video`
   对应合集中的具体条目，例如某一期演员对谈视频。
4. 播放资产 `video_media_asset`
   对应播放器真正加载的视频流、音频流、封面、预览图。
5. 字幕轨 `video_subtitle_track`
   对应原文、译文、双语、外挂字幕文件。
6. 字幕分段 `video_transcript_segment`
   对应详情页里按时间轴显示的句子、译文、说话人、VIP 锁定。
7. 扩展内容 `video_content`
   对应“笔记”“词汇”“精讲”“练习”等详情扩展区块。
8. 用户行为
   对应继续播放、收藏、下载、最近播放、VIP 权限判断。

页面和后端对象的映射关系如下：

| 前端区域 | 后端对象 |
| --- | --- |
| 顶部“演员对谈”标题、简介、313万播放、发布日期、词汇量 | `video_album` |
| “双语精选”标签页 | `category` |
| 列表里的每个视频卡片 | `video` |
| 视频时长、文件大小、发布日期、是否已翻译 | `video` |
| 播放页的视频流、时长、倍速 | `video_media_asset` + `playback_record` |
| 原文/译文逐句显示 | `video_transcript_segment` |
| “翻译功能已禁用，点击开启VIP权限” | `video` 权限字段或 `video_access_policy` |
| “笔记”页签 | `video_content` |
| 收藏、继续播放、下载 | `user_favorite`、`playback_record`、`user_download_record` |

## 2. 推荐的业务层级

推荐用下面这条主链路来管理内容：

`分类 -> 专题/合集 -> 视频 -> 字幕轨 -> 字幕分段/笔记`

这样可以同时覆盖两类场景：

1. 运营视角
   可以先建“演员对谈”专题，再往里面挂多条视频。
2. 学习视角
   每条视频可以继续拆成原文、译文、笔记、词汇、练习。

## 3. 核心实体设计

### 3.1 分类表 `category`

用途：
管理顶部栏目、子栏目、专题推荐入口。

关键字段：

- `id`: 主键
- `parent_id`: 父级分类，支持树形结构
- `name`: 分类名称
- `code`: 分类编码
- `category_type`: 分类类型，`channel`/`topic`/`filter`
- `banner_url`: 分类页横幅
- `icon_url`: 图标
- `description`: 分类简介
- `sort_no`: 排序
- `status`: 状态

### 3.2 专题/合集表 `video_album`

用途：
承载图片一里的专题详情页。

关键字段：

- `id`: 主键
- `category_id`: 所属分类
- `album_type`: `series`/`playlist`/`special`
- `title`: 专题标题
- `subtitle`: 专题副标题
- `source_name`: 来源节目，例如 `Actors on Actors`
- `source_platform`: 来源平台，例如 `YouTube`
- `description`: 专题介绍
- `cover_url`: 封面
- `banner_url`: 横幅
- `publish_date`: 专题发布日期
- `view_count`: 浏览量
- `favorite_count`: 收藏量
- `share_count`: 分享量
- `word_count`: 词汇量
- `is_featured`: 是否推荐
- `status`: 上下线状态

### 3.3 专题视频关联表 `video_album_item`

用途：
支持一个视频进入多个专题，不把视频强绑定到单一合集。

关键字段：

- `album_id`
- `video_id`
- `sort_no`
- `is_default_album`: 是否主专题

### 3.4 视频主表 `video`

用途：
承载列表页和详情页的视频主体信息。

关键字段：

- `id`: 主键
- `source_id`: 来源节目 ID
- `title_cn`: 中文标题
- `title_en`: 英文标题
- `title_origin`: 原始标题
- `short_title`: 列表短标题
- `cover_url`: 列表封面
- `poster_url`: 详情海报
- `source_platform`: 来源平台
- `source_url`: 外部原始链接
- `source_video_code`: 外部平台视频 ID
- `duration_seconds`: 时长
- `file_size_bytes`: 文件大小
- `difficulty_level`: 难度
- `speaker_summary`: 嘉宾/主持人摘要
- `summary`: 视频简介
- `published_at`: 原视频发布时间
- `view_count`: 播放量
- `favorite_count`: 收藏量
- `share_count`: 分享量
- `word_count`: 词汇量
- `subtitle_status`: `none`/`processing`/`ready`
- `translation_status`: `none`/`ai_only`/`reviewed`
- `access_level`: `free`/`vip`/`paid`
- `translation_access_level`: `free`/`vip`
- `allow_download`: 是否允许下载
- `trial_seconds`: 非会员试看时长
- `publish_status`: 发布状态
- `review_status`: 审核状态

### 3.5 媒体资产表 `video_media_asset`

用途：
播放器只依赖这一层拿播放地址，不直接耦合 `video.video_url`。

关键字段：

- `video_id`
- `asset_type`: `video`/`audio`/`cover`/`waveform`
- `file_url`
- `storage_provider`
- `container_format`
- `resolution`
- `bitrate_kbps`
- `size_bytes`
- `duration_seconds`
- `is_default`
- `status`

### 3.6 字幕轨表 `video_subtitle_track`

用途：
区分“原文轨”“译文轨”“双语轨”“外挂 SRT/VTT 文件”。

关键字段：

- `video_id`
- `track_type`: `origin`/`translation`/`bilingual`
- `language_code`: `en`/`zh-CN`
- `file_format`: `srt`/`vtt`/`json`
- `subtitle_url`
- `is_default`
- `is_ai_generated`
- `status`

### 3.7 字幕分段表 `video_transcript_segment`

用途：
这是详情页最关键的一张表，直接驱动逐句阅读和跟读。

关键字段：

- `video_id`
- `track_group`: 一组原文和译文共用一个 segment
- `segment_no`: 段落序号
- `start_ms`: 起始时间
- `end_ms`: 结束时间
- `speaker_name`: 说话人
- `speaker_role`: 主持人/演员/嘉宾
- `origin_text`: 原文
- `translation_text`: 译文
- `note_text`: 补充说明
- `keywords_json`: 关键词/词汇点
- `is_translation_locked`: 是否需要 VIP
- `sort_no`: 排序

### 3.8 视频扩展内容表 `video_content`

用途：
保留你项目里已有的 `video_content` 思路，升级成统一承载笔记、词汇、语法点、练习。

推荐 `content_type`：

- `note`
- `vocabulary`
- `grammar`
- `summary`
- `exercise`

### 3.9 标签和人物

辅助维度建议拆表，不把 `tags`、`speaker` 全塞字符串：

- `video_tag`
- `video_tag_rel`
- `video_person`
- `video_person_rel`

这样可以支持：

1. 按演员筛选
2. 按节目标签筛选
3. 人物页反查全部视频

## 4. 用户行为与权限设计

### 4.1 收藏

建议把现有 `user_favorite` 扩成通用收藏：

- `target_type`: `album`/`video`
- `target_id`

这样专题页和视频页都能收藏。

### 4.2 播放记录 `playback_record`

建议补充：

- `album_id`: 来自哪个专题
- `media_asset_id`: 上次播放的清晰度或媒体资源
- `last_segment_no`: 停留到哪一句
- `playback_rate`: 倍速
- `device_id`: 设备标识
- `last_played_at`

这样可以精准实现“继续播放”。

### 4.3 下载记录 `user_download_record`

图片里列表页已经出现下载/离线的操作位，建议单独建表：

- `user_id`
- `video_id`
- `subtitle_track_id`
- `status`: `queued`/`processing`/`done`/`expired`
- `download_url`
- `file_size_bytes`
- `expired_at`

### 4.4 权限

结合你项目已有的 `permission_group` 和 `membership_package`，建议在视频粒度保留 3 个字段：

- `access_level`
- `translation_access_level`
- `allow_download`

如果后面规则复杂，再单独加 `video_access_policy`。

## 5. 推荐接口返回结构

### 5.1 分类页

`GET /api/app/categories/{categoryId}/albums`

```json
{
  "category": {
    "id": 10,
    "name": "双语精选"
  },
  "albums": [
    {
      "id": 101,
      "title": "演员对谈",
      "coverUrl": "https://...",
      "viewCount": 3130000,
      "wordCount": 6300,
      "publishDate": "2025-04-12",
      "isFavorited": true
    }
  ]
}
```

### 5.2 专题详情页

`GET /api/app/albums/{albumId}`

```json
{
  "album": {
    "id": 101,
    "title": "演员对谈",
    "subtitle": "Actors on Actors",
    "description": "选自 Youtube 的 Variety 频道...",
    "viewCount": 3130000,
    "publishDate": "2025-04-12",
    "wordCount": 6300,
    "isFavorited": true,
    "canShare": true
  },
  "continuePlayback": {
    "videoId": 9001,
    "title": "《幕府将军》泽井杏奈对话《洛基》...",
    "progressSeconds": 183
  },
  "videos": [
    {
      "id": 9001,
      "titleCn": "《幕府将军》泽井杏奈对话《洛基》汤姆·希德勒斯顿",
      "titleEn": "Anna Sawai & Tom Hiddleston",
      "coverUrl": "https://...",
      "durationSeconds": 2306,
      "fileSizeBytes": 133798297,
      "publishedAt": "2024-08-20",
      "translationStatus": "ready"
    }
  ]
}
```

### 5.3 视频详情页

`GET /api/app/videos/{videoId}`

```json
{
  "video": {
    "id": 9001,
    "titleCn": "《幕府将军》泽井杏奈对话《洛基》汤姆·希德勒斯顿",
    "titleEn": "Anna Sawai & Tom Hiddleston",
    "durationSeconds": 2306,
    "accessLevel": "free",
    "translationAccessLevel": "vip"
  },
  "media": {
    "playUrl": "https://...",
    "coverUrl": "https://..."
  },
  "tabs": [
    { "key": "origin", "name": "原文" },
    { "key": "note", "name": "笔记" }
  ],
  "transcriptSegments": [
    {
      "segmentNo": 1,
      "startMs": 0,
      "endMs": 3840,
      "speakerName": "Anna Sawai",
      "originText": "There's a theme in \"Shogun\" about freedom.",
      "translationText": "《幕府将军》里有一个关于自由的主题。",
      "translationLocked": true
    }
  ],
  "contents": [
    {
      "contentType": "note",
      "title": "文化点",
      "contentBody": "..."
    }
  ],
  "userState": {
    "isFavorited": false,
    "progressSeconds": 3,
    "playbackRate": "1.0x",
    "canDownload": false
  }
}
```

## 6. 管理后台建议

后台建议按以下模块拆分：

1. 分类管理
   维护栏目树、排序、banner、上下线。
2. 专题管理
   维护专题简介、封面、来源、统计、推荐位。
3. 视频管理
   维护视频主数据、发布日期、难度、权限。
4. 媒体资产管理
   维护播放地址、音频、封面、多清晰度资源。
5. 字幕管理
   维护字幕轨和逐句分段。
6. 人物/标签管理
   维护演员、主持人、节目标签。
7. 学习内容管理
   维护笔记、词汇、练习。

## 7. 对当前项目的落地建议

你现在项目里的表已经有这些基础：

- `category`
- `video`
- `video_subtitle`
- `video_content`
- `playback_record`
- `user_favorite`

建议落地顺序如下：

1. 先补 `video_album`、`video_album_item`
   先把专题页建出来。
2. 再扩 `video`
   加上统计、权限、来源、文件大小、发布时间。
3. 再新增 `video_media_asset`、`video_transcript_segment`
   把详情页和播放器真正撑起来。
4. 最后扩用户行为
   加专题收藏和下载记录。

这样改造成本最低，也能兼容你当前的 Spring Boot + MyBatis Plus 结构。
