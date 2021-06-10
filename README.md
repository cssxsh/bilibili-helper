# [BiliBili Helper](https://github.com/cssxsh/bilibili-helper)
> 基于 [Mirai Console](https://github.com/mamoe/mirai-console) 的 [哔哩哔哩](https://www.bilibili.com/) 订阅插件

[![Release](https://img.shields.io/github/v/release/cssxsh/bilibili-helper)](https://github.com/cssxsh/bilibili-helper/releases)
[![Release](https://img.shields.io/github/downloads/cssxsh/bilibili-helper/total)](https://shields.io/category/downloads)

## 指令
注意: 使用前请确保可以 [在聊天环境执行指令](https://github.com/project-mirai/chat-command)  
带括号的`/`前缀是可选的  
`<...>`中的是指令名，由空格隔开表示或，选择其中任一名称都可执行例如`/抽卡 十连`  
`[...]`表示参数，当`[...]`后面带`?`时表示参数可选  
`{...}`表示连续的多个参数

参数 `uid` 例如 `https://space.bilibili.com/508963009/` 的数字 `508963009`

### 动态订阅指令

| 指令                                      | 描述                     |
|:------------------------------------------|:-------------------------|
| `/<bili-dynamic B动态> <add 添加> <uid>`  | 添加一个b站动态订阅      |
| `/<bili-dynamic B动态> <stop 停止> <uid>` | 停止一个b站动态订阅      |
| `/<bili-dynamic B动态> <list 列表>`       | 列出当前联系人的动态订阅 |

动态订阅会优先使用截图的形式推送内容，截图需要谷歌浏览器或者火狐浏览器
否则将推送文本内容

### 直播订阅指令

| 指令                                   | 描述                     |
|:---------------------------------------|:-------------------------|
| `/<bili-live B直播> <add 添加> <uid>`  | 添加一个b站直播订阅      |
| `/<bili-live B直播> <stop 停止> <uid>` | 停止一个b站直播订阅      |
| `/<bili-live B直播> <list 列表>`       | 列出当前联系人的直播订阅 |

### 视频订阅指令

| 指令                                    | 描述                     |
|:----------------------------------------|:-------------------------|
| `/<bili-video B视频> <add 添加> <uid>`  | 添加一个b站视频订阅      |
| `/<bili-video B视频> <stop 停止> <uid>` | 停止一个b站视频订阅      |
| `/<bili-video B视频> <list 列表>`       | 列出当前联系人的视频订阅 |

视频订阅不宜过多，否则会触发b站反爬策略，导致IP被锁定
动态订阅一般会包含视频内容，推荐以此代替

### 剧集订阅指令 (实验性)

| 指令                                     | 描述                     |
|:-----------------------------------------|:-------------------------|
| `/<bili-season B剧集> <add 添加> <sid>`  | 添加一个b站剧集订阅      |
| `/<bili-season B剧集> <stop 停止> <sid>` | 停止一个b站剧集频订阅    |
| `/<bili-season B剧集> <list 列表>`       | 列出当前联系人的剧集订阅 |

剧集订阅需要 Season ID 例如 `https://www.bilibili.com/bangumi/play/ss38353` 的 38353

目前剧集订阅出于实验性阶段

### 信息解析指令

| 指令                                | 描述                   |
|:------------------------------------|:-----------------------|
| `/<bili-info B信息> <aid> [id]`     | 根据 avid 获取视频信息 |
| `/<bili-info B信息> <bvid> [id]`    | 根据 bvid 获取视频信息 |
| `/<bili-info B信息> <dynamic> [id]` | 根据 id 获取动态信息   |
| `/<bili-info B信息> <live> [id]`    | 根据 id 获取直播信息   |

消息中包含 `BV12v411G7dP` `av2` 等等 id 信息时会自动触发解析

## 设置
位于`Mirai-Console`运行目录下的`config/bilibili-helper`文件夹下

### 图片缓存位置
`BiliHelperSettings.yml` 文件中的 `cache` 配置项 默认为 `ImageCache`

## 安装

### 手动安装

1. 运行 [Mirai Console](https://github.com/mamoe/mirai-console) 生成`plugins`文件夹
1. 从 [Releases](https://github.com/cssxsh/bilibili-helper/releases) 下载`jar`并将其放入`plugins`文件夹中

## TODO
- [ ] 完善聚集订阅
- [ ] 自动清理图片缓存
