# [BiliBili Helper](https://github.com/cssxsh/bilibili-helper)

> 基于 [Mirai Console](https://github.com/mamoe/mirai-console) 的 [哔哩哔哩](https://www.bilibili.com/) 订阅插件

[![Release](https://img.shields.io/github/v/release/cssxsh/bilibili-helper)](https://github.com/cssxsh/bilibili-helper/releases)
[![Downloads](https://img.shields.io/github/downloads/cssxsh/bilibili-helper/total)](https://repo1.maven.org/maven2/xyz/cssxsh/bilibili-helper/)
[![MiraiForum](https://img.shields.io/badge/post-on%20MiraiForum-yellow)](https://mirai.mamoe.net/topic/287)

**使用前应该查阅的相关文档或项目**

* [User Manual](https://github.com/mamoe/mirai/blob/dev/docs/UserManual.md)
* [Permission Command](https://github.com/mamoe/mirai/blob/dev/mirai-console/docs/BuiltInCommands.md#permissioncommand)
* [Chat Command](https://github.com/project-mirai/chat-command)

从 1.2.5 版本开始 将截图部分功能的转移至 [mirai-selenium-plugin](https://github.com/cssxsh/mirai-selenium-plugin)  
截图功能的启用需要其作为前置插件  
关于截图支持的环境请浏览 [运行平台支持](https://github.com/cssxsh/mirai-selenium-plugin#%E8%BF%90%E8%A1%8C%E5%B9%B3%E5%8F%B0%E6%94%AF%E6%8C%81)  
自动触发URL解析 详见 [信息解析](#信息解析指令) 指令  
消息 [模板](#Template) 详见配置(自 1.4.0 起截图功能由模板内容来配置)  
从 1.7.0 版本开始 提供 `/bili-login` 指令以登录个人账号，解决B站反爬问题

## 指令

注意: 使用前请确保可以 [在聊天环境执行指令](https://github.com/project-mirai/chat-command)  
带括号的`/`前缀是可选的  
`<...>`中的是指令名，由空格隔开表示或，选择其中任一名称都可执行例如`/B视频 添加 11783021`  
`[...]`表示参数，当`[...]`后面带`?`时表示参数可选  
`{...}`表示连续的多个参数

本插件指令权限ID 格式为 `xyz.cssxsh.mirai.plugin.bilibili-helper:command.*`, `*` 是指令的第一指令名  
例如 `/B动态 添加 496371957` 的权限ID为 `xyz.cssxsh.mirai.plugin.bilibili-helper:command.bili-dynamic`

参数 `uid` 例如 `https://space.bilibili.com/508963009/` 的数字 `508963009`  
参数 `contact` 为QQ号或者群号，可以省略，会从当前聊天环境获取，  
比如群聊中会自动填充为当前群号, 但控制台中必须填充  

子指令 `sleep/at` 的 `target` 是 [被许可人 ID](https://github.com/mamoe/mirai/blob/dev/mirai-console/docs/Permissions.md#%E8%A2%AB%E8%AE%B8%E5%8F%AF%E4%BA%BA-id)   
处于休眠时间的订阅将不会推送  
举例:  
* `/bili-dynamic sleep g12345 00:00 06:00`, `00:00~06:00` 时间段内将不会推送  

处于艾特时间的订阅将会添加艾特  
举例:  
* `/bili-live at g12345 19:00 23:00`, `19:00 23:00` 时间段内将会艾特全体
* `/bili-live at u456789 19:00 23:00`, `19:00 23:00` 时间段内将会艾特用户456789

子指令 `cron` 的 `cron` 参数是 CRON 表达式, 由 `秒 分钟 小时 日 月 周` 组成  
举例：
* `/bili-dynamic time 508963009 "0 0 14 * * ?"`, `0 0 14 * * ?` 表示每天 14:00 都执行一次  
* `/bili-video time 508963009 "0 0 18 ? * 2-6"`, `0 0 18 ? * 2-6` 表示星期一至星期五 每天 18:00 执行一次
可以使用在线编辑器生成 <https://www.bejson.com/othertools/cron/>  
为防止被 空格 分成多个参数，请使用 `"` 包裹参数  

### 动态订阅指令

| 指令                                                      | 描述           |
|:--------------------------------------------------------|:-------------|
| `/<bili-dynamic B动态> <add 添加> [uid] [contact]?`         | 添加一个b站动态订阅   |
| `/<bili-dynamic B动态> <stop 停止> [uid] [contact]?`        | 停止一个b站动态订阅   |
| `/<bili-dynamic B动态> <time 定时> [uid] [cron]?`           | 设置任务的定时信息    |
| `/<bili-dynamic B动态> <list 列表> [contact]?`              | 列出当前联系人的动态订阅 |
| `/<bili-dynamic B动态> <filter 过滤> [type] [add]?`         | 过滤特定类型的动态    |
| `/<bili-dynamic B动态> <forbid 屏蔽> [pattern] [add]?`      | 添加一个动态正则屏蔽   |
| `/<bili-dynamic B动态> <sleep 休眠> [target] [start] [end]` | 添加一个休眠时间     |
| `/<bili-dynamic B动态> <at 艾特> [target] [start] [end]`    | 添加一个艾特时间     |

`/bili-dynamic forbid 转发抽奖` 添加一个正则屏蔽  
`/bili-dynamic forbid 转发抽奖 false` 取消一个正则屏蔽  
`/bili-dynamic filter 视频` 添加一个动态类型过滤  
`/bili-dynamic filter 视频 false` 取消一个动态类型过滤  
`type` 取值 `回复, 图片, 文本, 视频, 专栏, 音乐, 剧集, 删除, 番剧, 电视, 直播`

### 直播订阅指令

| 指令                                                   | 描述           |
|:-----------------------------------------------------|:-------------|
| `/<bili-live B直播> <add 添加> [uid] [contact]?`         | 添加一个b站直播订阅   |
| `/<bili-live B直播> <stop 停止> [uid] [contact]?`        | 停止一个b站直播订阅   |
| `/<bili-live B直播> <time 定时> [uid] [cron]?`           | 设置任务的定时信息    |
| `/<bili-live B直播> <list 列表> [contact]?`              | 列出当前联系人的直播订阅 |
| `/<bili-live B直播> <sleep 休眠> [target] [start] [end]` | 添加一个休眠时间     |
| `/<bili-live B直播> <at 艾特> [target] [start] [end]`    | 添加一个艾特时间     |

### 视频订阅指令

| 指令                                                    | 描述           |
|:------------------------------------------------------|:-------------|
| `/<bili-video B视频> <add 添加> [uid] [contact]?`         | 添加一个b站视频订阅   |
| `/<bili-video B视频> <stop 停止> [uid] [contact]?`        | 停止一个b站视频订阅   |
| `/<bili-video B视频> <time 定时> [uid] [cron]?`           | 设置任务的定时信息    |
| `/<bili-video B视频> <list 列表> [contact]?`              | 列出当前联系人的视频订阅 |
| `/<bili-video B视频> <forbid 屏蔽> [type] [add]?`         | 添加一个视频类型屏蔽   |
| `/<bili-video B视频> <filter 过滤> [tid] [add]?`          | 添加一个视频分区过滤   |
| `/<bili-video B视频> <sleep 休眠> [target] [start] [end]` | 添加一个休眠时间     |
| `/<bili-video B视频> <at 艾特> [target] [start] [end]`    | 添加一个艾特时间     |

`/bili-video forbid 付费` 添加一个类型屏蔽  
`/bili-video forbid 付费 false` 取消一个类型屏蔽  
`/bili-video filter 1` 添加一个分区过滤过滤  
`/bili-video filter 1 false` 取消一个分区过滤过滤  
`type` 取值 `付费, 联合, 回放`  
`tid` 取值请参考 <https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/video/video_zone.md>

视频订阅不宜过多，否则会触发b站反爬策略，导致IP被锁定 动态订阅一般会包含视频内容，推荐以此代替

### 剧集订阅指令

| 指令                                                     | 描述           |
|:-------------------------------------------------------|:-------------|
| `/<bili-season B剧集> <add 添加> [sid] [contact]?`         | 添加一个b站剧集订阅   |
| `/<bili-season B剧集> <stop 停止> [sid] [contact]?`        | 停止一个b站剧集频订阅  |
| `/<bili-season B剧集> <time 定时> [uid] [cron]?`           | 设置任务的定时信息    |
| `/<bili-season B剧集> <list 列表> [contact]?`              | 列出当前联系人的剧集订阅 |
| `/<bili-season B剧集> <sleep 休眠> [target] [start] [end]` | 添加一个休眠时间     |
| `/<bili-season B剧集> <at 艾特> [target] [start] [end]`    | 添加一个艾特时间     |

剧集订阅需要 Season ID 例如 <https://www.bilibili.com/bangumi/play/ss38353> 的 `38353`  
可以通过 [搜索指令](#搜索指令) 搜索番剧 获得链接

### 信息解析指令

| 指令                                | 描述             |
|:----------------------------------|:---------------|
| `/<bili-info B信息> <aid> [id]`     | 根据 avid 获取视频信息 |
| `/<bili-info B信息> <bvid> [id]`    | 根据 bvid 获取视频信息 |
| `/<bili-info B信息> <dynamic> [id]` | 根据 id 获取动态信息   |
| `/<bili-info B信息> <live> [id]`    | 根据 id 获取直播信息   |
| `/<bili-info B信息> <user> [id]`    | 根据 id 获取用户信息   |

因为返回结果包含图片，需要在聊天环境执行指令  
消息中包含 `BV12v411G7dP` `av2` 等等 id 信息时会自动触发解析  
目前会触发的正则表达式

```regexp
// Video
"""(?i)(?<!\w)(?:av(\d+)|(BV[0-9A-z]{10}))"""
// Dynamic
"""(?<=t\.bilibili\.com/(?:h5/dynamic/detail/)?)(\d+)"""
// Live Room
"""(?<=live\.bilibili\.com/)(\d+)"""
// User Space
"""(?<=space\.bilibili\.com/|bilibili\.com/space/)(\d+)"""
// Season
"""(?i)(?<!\w)ss(\d{4,10})"""
// Episode
"""(?i)(?<!\w)eq(\d{4,10})"""
// Media
"""(?i)(?<!\w)md(\d{4,10})"""
// Article
"""(?i)(?<!\w)cv(\d{4,10})"""
"""(?<=bilibili\.com/read/mobile\?id=)(\d+)"""
// Short Link
"""(?<=b23\.tv\\?/)[0-9A-z]+"""
```

### 搜索指令

| 指令                                          | 描述   |
|:--------------------------------------------|:-----|
| `/<bili-search B搜索> <user 用户> [keyword]`    | 搜索用户 |
| `/<bili-search B搜索> <bangumi 番剧> [keyword]` | 搜索番剧 |
| `/<bili-search B搜索> <ft 影视> [keyword]`      | 搜索影视 |

返回结果包含图片，需要在聊天环境执行指令

### 任务列表指令

| 指令                           | 描述   |
|:-----------------------------|:-----|
| `/<bili-task B任务> <all>`     | 全部任务 |
| `/<bili-task B任务> <dynamic>` | 动态任务 |
| `/<bili-task B任务> <live>`    | 直播任务 |
| `/<bili-task B任务> <season>`  | 剧集任务 |
| `/<bili-task B任务> <video>`   | 视频任务 |
| `/<bili-task B任务> <config>`  | 屏蔽设置 |


### 模板配置指令

| 指令                                          | 描述   |
|:--------------------------------------------|:-----|
| `/<bili-template B模板> <datetime> [pattern]` | 日期格式 |
| `/<bili-template B模板> <reload>`             | 重新加载 |

`pattern` 的 取值为 [DateTimeFormatter](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html)  
举例：
  * `/B模板 datetime ISO_LOCAL_DATE_TIME`  
  * `/B模板 datetime yyyy-MM-dd`

### 登录指令

| 指令                                     | 描述    |
|:---------------------------------------|:------|
| `/<bili-login B登录>`                    | 二维码登录 |

## 设置

位于`Mirai-Console`运行目录下的`config/xyz.cssxsh.mirai.plugin.bilibili-helper`文件夹下

### BiliHelperSettings.yml

* `cache` 图片缓存位置, 默认为 `ImageCache`
* `limit` 动态 订阅 输出图片数量上限, 默认为 `16`
* `api` API 访问间隔时间，单位秒, 默认为 `10`
* `video` 视频 订阅 访问间隔时间，单位分钟, 默认为 `10`
* `dynamic` 动态 订阅 访问间隔时间，单位分钟, 默认为 `10`
* `live` 直播 订阅 访问间隔时间，单位分钟, 默认为 `30`
* `season` 番剧 订阅 访问间隔时间，单位分钟, 默认为 `30`
* `refresh` 启动时刷新 last，订阅最后推送将会重新开始记录，未推送内容将会因此丢弃
* `selenium` ~~是否启用截图~~  此配置项废弃，通过 Template 文件内容来确定是否启用截图
* `ban` 排除的自动解析内容，可以放一些不希望被解析的AV号之类的
* `forward` 转发自动解析, 使用转发的形式发送自动解析结果
* `max` 一次性推送的订阅内容上限, 超过这个上限将会尝试合并为转发消息

### BiliCleanerConfig.yml

* `interval` 图片清理的间隔时间，单位 小时
* `expires` 图片缓存过期时间，单位 小时

### SeleniumConfig.yml

* `user_agent` 截图设备UA 网页识别设备类型，进而影响截图的效果  
  iPad `Mozilla/5.0 (iPad; CPU OS 11_0 like Mac OS X) AppleWebKit/604.1.34 (KHTML, like Gecko) Version/11.0 Mobile/15A5341f Safari/604.1 MicroMessenger`  
  iPhone `Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1 MicroMessenger`  
  Mac `Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_8; en-us) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50 MicroMessenger`
* `width` 截图宽度
* `height` 截图高度
* `pixel_ratio` ~~截图像素比~~ 此配置废除
* `headless` 无头模式(后台模式)
* `home` ~~浏览器会保持打开主页，以加快其他页面加载速度~~ 此配置废除
* `hide` 隐藏的web组件(jQ选择器)  
  添加 `".international-header", ".top-bar", ".m-navbar"` 可以屏蔽顶边栏

### ~~LiveAtAll~~

**此设置 1.5.0 废除**  
此配置通过权限设置，权限ID为 `xyz.cssxsh.mirai.plugin.bilibili-helper:live.atall`  
配置对象为群，即 `g*`, `g12345`  
举例，`perm add g12345 xyz.cssxsh.mirai.plugin.bilibili-helper:live.atall`

## Template

位于 `config/xyz.cssxsh.mirai.plugin.bilibili-helper/Template` 目录下的 `template` 文件  
文件名为对应的配置对象（类名），文件内容的 `#...` 是替换标记，将会替换为对应的值  
值的名称请参考 [bilibili/data](src/main/kotlin/xyz/cssxsh/bilibili/data) 中的数据定义  
`#images`, `#detail`, `#screenshot` 为特殊的替换标记，将会替换为特殊的处理结果  
`#images` 将会替换为内容图片  
在 `BiliRoomInfo` 中 `#detail` 将会替换为对应的 `Live`  
在 `DynamicCard` 中 `#detail` 将会替换为对应的 `card` 解析结果    
在 `DynamicInfo` 中 `#screenshot` 将会替换为截图（需要安装 `mirai-selenium-plugin`） 
在 `Article` 中 `#screenshot` 将会替换为截图（需要安装 `mirai-selenium-plugin`） 
例如可以将 `VideoStatus.template` 改为

```
👍 #like 💰 #coin ⭐ #favorite
▶️ #view 💬 #reply 🔄 #share
```

## Cookies

位于 `data/xyz.cssxsh.mirai.plugin.bilibili-helper/cookies.json`  
导入 cookies文件 不是必须的，这是实验性功能，主要是防止b站反爬IP锁定  
从 浏览器插件 [EditThisCookie](http://www.editthiscookie.com/) 导出Json 填入文件  
EditThisCookie 安装地址
[Chrome](https://chrome.google.com/webstore/detail/editthiscookie/fngmhnnpilhplaeedifhccceomclgfbg)
[Firefox](https://addons.mozilla.org/firefox/downloads/file/3449327/editthiscookie2-1.5.0-fx.xpi)
[Edge](https://microsoftedge.microsoft.com/addons/getproductdetailsbycrxid/ajfboaconbpkglpfanbmlfgojgndmhmc?hl=zh-CN&gl=CN)

现在你可以直接使用登录指令刷新 `cokies`

## 安装

### MCL 指令安装

`./mcl --update-package xyz.cssxsh:bilibili-helper --channel maven-stable --type plugins`

### 手动安装

1. 从 [Releases](https://github.com/cssxsh/bilibili-helper/releases) 或者 [Maven](https://repo1.maven.org/maven2/xyz/cssxsh/bilibili-helper/) 下载 `mirai2.jar`
2. 将其放入 `plugins` 文件夹中

### 截图前置

`./mcl --update-package xyz.cssxsh.mirai:mirai-selenium-plugin --channel maven-stable --type plugins`

## TODO

- [x] 完善剧集订阅
- [x] 自动清理图片缓存

## [爱发电](https://afdian.net/@cssxsh)

![afdian](.github/afdian.jpg)