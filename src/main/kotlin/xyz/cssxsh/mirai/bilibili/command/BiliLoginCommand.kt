package xyz.cssxsh.mirai.bilibili.command

import com.google.zxing.*
import com.google.zxing.client.j2se.*
import com.google.zxing.qrcode.*
import kotlinx.coroutines.*
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import xyz.cssxsh.bilibili.api.*
import xyz.cssxsh.mirai.bilibili.*
import xyz.cssxsh.mirai.bilibili.data.*
import java.nio.file.*
import javax.imageio.*

object BiliLoginCommand : SimpleCommand(
    owner = BiliHelperPlugin,
    "bili-login", "B登录",
    description = "B站登录指令",
    overrideContext = BiliCommandArgumentContext
) {

    @Handler
    fun FriendCommandSender.handle() {
        launch {
            client.login { url ->
                val temp = Files.createTempFile("qrcode", ".png").toFile()
                launch(Dispatchers.IO) {
                    with(QRCodeWriter()) {
                        val matrix = encode(url, BarcodeFormat.QR_CODE, 250, 250)
                        val image = MatrixToImageWriter.toBufferedImage(matrix)
                        runInterruptible(Dispatchers.IO) {
                            ImageIO.write(image, "PNG", temp)
                        }
                    }
                    BiliHelperPlugin.logger.info(temp.path)
                    val message = temp.toExternalResource().use { subject.uploadImage(it) }
                    subject.sendMessage(message)
                }.invokeOnCompletion { cause ->
                    if (cause != null) BiliHelperPlugin.logger.error(cause)
                    temp.delete()
                }
            }
        }.invokeOnCompletion { cause ->
            launch {
                if (cause == null) {
                    subject.sendMessage("登录成功")
                } else {
                    subject.sendMessage(cause.message ?: "登陆失败")
                }
            }
        }
    }
}