# [BiliBili Helper](https://github.com/cssxsh/bilibili-helper)

> 基于 [Mirai Console](https://github.com/mamoe/mirai-console) 的 [哔哩哔哩](https://www.bilibili.com/) 订阅插件

[![Release](https://img.shields.io/github/v/release/cssxsh/bilibili-helper)](https://github.com/cssxsh/bilibili-helper/releases)
[![Downloads](https://img.shields.io/github/downloads/cssxsh/bilibili-helper/total)](https://shields.io/category/downloads)
[![MiraiForum](https://img.shields.io/badge/post-on%20MiraiForum-yellow)](https://mirai.mamoe.net/topic/287)

## 指令

注意: 使用前请确保可以 [在聊天环境执行指令](https://github.com/project-mirai/chat-command)  
带括号的`/`前缀是可选的  
`<...>`中的是指令名，由空格隔开表示或，选择其中任一名称都可执行例如`/B动态 添加 496371957`  
`[...]`表示参数，当`[...]`后面带`?`时表示参数可选  
`{...}`表示连续的多个参数  
直播 [@全体成员](#LiveAtAll) 详见配置

参数 `uid` 例如 `https://space.bilibili.com/508963009/` 的数字 `508963009`  
参数 `contact` 为QQ号或者群号，可以省略，会从当前聊天环境获取，比如群聊中会自动填充为当前群号

### 动态订阅指令

| 指令                                                 | 描述                     |
|:-----------------------------------------------------|:-------------------------|
| `/<bili-dynamic B动态> <add 添加> [uid] [contact]?`  | 添加一个b站动态订阅      |
| `/<bili-dynamic B动态> <stop 停止> [uid] [contact]?` | 停止一个b站动态订阅      |
| `/<bili-dynamic B动态> <list 列表> [contact]?`       | 列出当前联系人的动态订阅 |

动态订阅会优先使用截图的形式推送内容，截图需要谷歌浏览器或者火狐浏览器 否则将推送文本内容

### 直播订阅指令

| 指令                                              | 描述                     |
|:--------------------------------------------------|:-------------------------|
| `/<bili-live B直播> <add 添加> [uid] [contact]?`  | 添加一个b站直播订阅      |
| `/<bili-live B直播> <stop 停止> [uid] [contact]?` | 停止一个b站直播订阅      |
| `/<bili-live B直播> <list 列表> [contact]?`       | 列出当前联系人的直播订阅 |

### 视频订阅指令

| 指令                                               | 描述                     |
|:---------------------------------------------------|:-------------------------|
| `/<bili-video B视频> <add 添加> [uid] [contact]?`  | 添加一个b站视频订阅      |
| `/<bili-video B视频> <stop 停止> [uid] [contact]?` | 停止一个b站视频订阅      |
| `/<bili-video B视频> <list 列表> [contact]?`       | 列出当前联系人的视频订阅 |

视频订阅不宜过多，否则会触发b站反爬策略，导致IP被锁定 动态订阅一般会包含视频内容，推荐以此代替

### 剧集订阅指令

| 指令                                                | 描述                     |
|:----------------------------------------------------|:-------------------------|
| `/<bili-season B剧集> <add 添加> [sid] [contact]?`  | 添加一个b站剧集订阅      |
| `/<bili-season B剧集> <stop 停止> [sid] [contact]?` | 停止一个b站剧集频订阅    |
| `/<bili-season B剧集> <list 列表> [contact]?`       | 列出当前联系人的剧集订阅 |

剧集订阅需要 Season ID 例如 <https://www.bilibili.com/bangumi/play/ss38353> 的 38353  
可以通过 搜索指令 搜索番剧 获得链接  
目前剧集订阅出于实验性阶段

### 信息解析指令

| 指令                                | 描述                   |
|:------------------------------------|:-----------------------|
| `/<bili-info B信息> <aid> [id]`     | 根据 avid 获取视频信息 |
| `/<bili-info B信息> <bvid> [id]`    | 根据 bvid 获取视频信息 |
| `/<bili-info B信息> <dynamic> [id]` | 根据 id 获取动态信息   |
| `/<bili-info B信息> <live> [id]`    | 根据 id 获取直播信息   |

返回结果包含图片，需要在聊天环境执行指令  
消息中包含 `BV12v411G7dP` `av2` 等等 id 信息时会自动触发解析  
目前会触发的正则表达式

```
val VIDEO_REGEX = """((av|AV)\d+|BV[0-9A-z]{8,12})""".toRegex()
val DYNAMIC_REGEX = """(?<=t\.bilibili\.com/(h5/dynamic/detail/)?)(\d+)""".toRegex()
val ROOM_REGEX = """(?<=live\.bilibili\.com/)(\d+)""".toRegex()
val SHORT_LINK_REGEX = """(?<=b23\.tv\\?/)[0-9A-z]+""".toRegex()
val SPACE_REGEX = """(?<=space\.bilibili\.com/)(\d+)""".toRegex()
val SEASON_REGEX = """(?<=bilibili\.com/bangumi/play/ss)(\d+)""".toRegex()
val EPISODE_REGEX = """(?<=bilibili\.com/bangumi/play/ep)(\d+)""".toRegex()
val MEDIA_REGEX = """(?<=bilibili\.com/bangumi/media/md)(\d+)""".toRegex()
```

### 搜索指令

| 指令                                            | 描述     |
|:------------------------------------------------|:---------|
| `/<bili-search B搜索> <user 用户> [keyword]`    | 搜索用户 |
| `/<bili-search B搜索> <bangumi 番剧> [keyword]` | 搜索番剧 |
| `/<bili-search B搜索> <ft 影视> [keyword]`      | 搜索影视 |

返回结果包含图片，需要在聊天环境执行指令

## 设置

位于`Mirai-Console`运行目录下的`config/bilibili-helper`文件夹下

### BiliHelperSettings.yml

* `cache` 图片缓存位置, 默认为 `ImageCache`
* `limit` 动态 订阅 输出图片数量上限, 默认为 `16`
* `api` API 访问间隔时间，单位秒, 默认为 `10`
* `video` 视频 订阅 访问间隔时间，单位分钟, 默认为 `10`
* `dynamic` 动态 订阅 访问间隔时间，单位分钟, 默认为 `10`
* `live` 直播 订阅 访问间隔时间，单位分钟, 默认为 `30`
* `season` 番剧 订阅 访问间隔时间，单位分钟, 默认为 `30`

### BiliCleanerConfig.yml

* `interval` 图片清理的间隔时间，单位 小时
* `expires` 图片缓存过期时间，单位 小时

### SeleniumConfig.yml

* `user_agent` 截图设备UA 网页识别设备类型，进而影响截图的效果  
  iPad `Mozilla/5.0 (iPad; CPU OS 11_0 like Mac OS X) AppleWebKit/604.1.34 (KHTML, like Gecko) Version/11.0 Mobile/15A5341f Safari/604.1`  
  iPhone `Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1`  
  Mac `Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_8; en-us) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50`  
* `width` 截图宽度
* `height` 截图高度
* `pixel_ratio` 截图像素比
* `headless` 无头模式(后台模式)
* `hide` 隐藏的web组件(jQ选择器)  
  添加 `".international-header", ".top-bar", ".m-navbar"` 可以屏蔽顶边栏
* `setup` 是否启用截图，默认 `true`

### LiveAtAll

此配置通过权限设置，权限ID为 `xyz.cssxsh.mirai.plugin.bilibili-helper:live.atall`  
配置对象为群，即 `g*`, `g12345`  
举例，`perm add g12345 xyz.cssxsh.mirai.plugin.bilibili-helper:live.atall`

## Cookies

位于 `data/bilibili-helper/cookies.json`  
导入 cookies文件 不是必须的，这是实验性功能，主要是防止b站反爬IP锁定  
从 浏览器插件 [EditThisCookie](http://www.editthiscookie.com/) 导出Json 填入文件  
EditThisCookie 安装地址
[Chrome](https://chrome.google.com/webstore/detail/editthiscookie/fngmhnnpilhplaeedifhccceomclgfbg)
[Firefox](https://addons.mozilla.org/firefox/downloads/file/3449327/editthiscookie2-1.5.0-fx.xpi)
[Edge](https://microsoftedge.microsoft.com/addons/getproductdetailsbycrxid/ajfboaconbpkglpfanbmlfgojgndmhmc?hl=zh-CN&gl=CN)

## 安装

### MCL 指令安装

`./mcl --update-package xyz.cssxsh:bilibili-helper --channel stable --type plugin`

### 手动安装

1. 运行 [Mirai Console](https://github.com/mamoe/mirai-console) 生成`plugins`文件夹
1. 从 [Releases](https://github.com/cssxsh/bilibili-helper/releases) 下载`jar`并将其放入`plugins`文件夹中

## TODO

- [x] 完善剧集订阅
- [x] 自动清理图片缓存