package mirai.command

import net.mamoe.mirai.console.command.CommandOwner
import net.mamoe.mirai.console.permission.Permission
import net.mamoe.mirai.console.permission.PermissionId
import net.mamoe.mirai.console.permission.RootPermission

object TempCommandOwner : CommandOwner {
    override val parentPermission: Permission
        get() = RootPermission

    override fun permissionId(name: String): PermissionId =
        PermissionId(namespace = "temp", name = name)
}