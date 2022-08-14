package xyz.cssxsh.mirai.unit.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.console.permission.*
import xyz.cssxsh.mirai.unit.*
import xyz.cssxsh.mirai.unit.data.*

public object UnitConfigCommand : CompositeCommand(
    owner = MiraiUnitPlugin,
    "unit",
    description = "Unit Bot 配置指令"
) {

    @SubCommand
    @Description("重新载入 unit 配置")
    public suspend fun CommandSender.service(id: PermitteeId, service: String) {
        if (service.isEmpty()) {
            UnitBotService.service.remove(id)

            sendMessage("移除完成 $id ")
        } else {
            UnitBotService.service[id] = service

            sendMessage("设置完成 $id - $service ")
        }
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