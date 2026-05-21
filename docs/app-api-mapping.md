# English Learning App API Mapping

## 页面到后端资源映射

- 首页推荐课程: `video_album` as `course`
- 每日听读分类: `category`
- 课程目录中的章节: `video` as `lesson`
- 播放器逐句脚本: `video_transcript_segment`
- 播放媒体: `video_media_asset`
- 书架收藏: `user_favorite`
- 我的缓存: `user_download_record`
- 已购课程: `user_course_access`
- 购买订单: `user_order`

## 已补齐的 app 接口

### 认证

- `POST /api/app/auth/quick-login`
  用于 app 一键登录，返回 `token + user`

### 游客可访问

- `GET /api/app/home/index`
- `GET /api/app/courses/recommend`
- `GET /api/app/courses/daily-reading/categories`
- `GET /api/app/courses/daily-reading`
- `GET /api/app/courses/{courseId}`
- `GET /api/app/courses/{courseId}/lessons`

### 登录后访问

- `GET /api/app/courses/purchased`
- `POST /api/app/courses/{courseId}/purchase`
- `GET /api/app/courses/{courseId}/lessons/{lessonId}/player`
- `POST /api/app/courses/{courseId}/lessons/{lessonId}/favorite`
- `DELETE /api/app/courses/{courseId}/lessons/{lessonId}/favorite`
- `POST /api/app/courses/{courseId}/lessons/{lessonId}/download`
- `GET /api/app/user/profile-card`
- `GET /api/app/user/bookshelf`
- `GET /api/app/user/cache`

## 关键返回结构

### 课程卡片 `AppCourseCardVo`

- `id`
- `title`
- `subtitle`
- `description`
- `imageUrl`
- `vocabularyCount`
- `playCount`
- `tags`
- `isVip`
- `themeColor`
- `author`
- `price`
- `purchased`

### 章节 `AppLessonVo`

- `id`
- `title`
- `duration`
- `isLearned`
- `downloaded`
- `collected`

### 播放页 `AppLessonPlayerVo`

- `lesson`
- `audioUrl`
- `progressSeconds`
- `playbackRate`
- `canDownload`
- `showChinese`
- `tabs`
- `transcript`

### 逐句脚本 `AppLessonSentenceVo`

- `id`
- `text`
- `translation`
- `startTime`
- `duration`

## 数据库相关变更

本轮已补充或扩展：

- `category`: 父子级和分类类型
- `video_album`: 课程展示字段
- `video`: 章节/播放字段
- `user_order`: 课程订单字段
- `user_course_access`: 已购课程
- `user_download_record`: 缓存记录

## 说明

- AI 助教页当前直接在前端调用 Gemini SDK，没有走本后端。
- `mvn compile` 已通过。
- `mvn test` 在当前环境失败，原因是 Mockito 的 ByteBuddy agent 无法附加到当前 JVM，不是本轮接口代码编译错误。
