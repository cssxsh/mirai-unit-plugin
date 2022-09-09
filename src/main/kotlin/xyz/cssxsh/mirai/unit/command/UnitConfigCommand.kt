package xyz.cssxsh.mirai.unit.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.console.util.ContactUtils.render
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.message.data.*
import xyz.cssxsh.mirai.unit.*
import xyz.cssxsh.mirai.unit.data.*

public object UnitConfigCommand : CompositeCommand(
    owner = MiraiUnitPlugin,
    "unit",
    description = "Unit Bot 配置指令"
) {

    @SubCommand
    @Description("配置 bot 服务")
    public suspend fun CommandSender.bot(contact: Contact, service: String) {
        if (service.isEmpty()) {
            UnitBotService.service.remove(contact)

            sendMessage(message = "移除完成 ${contact.render()} ")
        } else {
            UnitBotService.service[contact] = service

            sendMessage(message = "设置完成 ${contact.render()} - $service ")
        }
    }

    @SubCommand
    @Description("列出 bot 服务")
    public suspend fun CommandSender.service(page: Int) {
        val result = MiraiUnitBot.service.list(pageNo = page, pageSize = 10)
        val message = buildMessageChain {
            appendLine("第 ${result.currentPage} 页")
            for (service in result.services) {
                appendLine("${service.id} - ${service.name} - ${service.description}")
            }
        }
        sendMessage(message = message)
    }

    @SubCommand
    @Description("重新载入 unit 配置")
    public suspend fun CommandSender.reload() {
        with(MiraiUnitPlugin) {
            UnitBotConfig.reload()
        }
        sendMessage("重载完成，当前的 app 为 ${UnitBotConfig.appId} - ${UnitBotConfig.appName}")
    }
}