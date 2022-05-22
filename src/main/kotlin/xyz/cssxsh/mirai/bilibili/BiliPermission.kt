package xyz.cssxsh.mirai.bilibili

import net.mamoe.mirai.console.permission.*
import net.mamoe.mirai.console.plugin.jvm.*

internal fun AbstractJvmPlugin.registerPermission(name: String, description: String): Permission {
    return PermissionService.INSTANCE.register(permissionId(name), description, parentPermission)
}