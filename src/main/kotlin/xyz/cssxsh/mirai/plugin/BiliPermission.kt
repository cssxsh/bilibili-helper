package xyz.cssxsh.mirai.plugin

import net.mamoe.mirai.console.permission.*
import net.mamoe.mirai.console.plugin.jvm.*


private fun AbstractJvmPlugin.registerPermission(name: String, description: String): Permission {
    return PermissionService.INSTANCE.register(permissionId(name), description, parentPermission)
}

internal val LiveAtAll by lazy {
    BiliHelperPlugin.registerPermission("live.atall", "直播 @全体成员")
}