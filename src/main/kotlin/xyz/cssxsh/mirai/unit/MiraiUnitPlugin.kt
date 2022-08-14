package xyz.cssxsh.mirai.unit

import kotlinx.coroutines.*
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.event.*
import xyz.cssxsh.mirai.unit.command.*
import xyz.cssxsh.mirai.unit.data.*

public object MiraiUnitPlugin : KotlinPlugin(
    JvmPluginDescription(
        id = "xyz.cssxsh.mirai.plugin.mirai-unit-plugin",
        name = "mirai-unit-plugin",
        version = "1.0.0-dev-2",
    ) {
        author("cssxsh")
    }
) {
    override fun onEnable() {
        UnitBotConfig.reload()
        UnitBotToken.reload()
        UnitBotService.reload()
        logger.info(UnitBotService.service.toString())

        UnitConfigCommand.register()

        MiraiUnitBot.registerTo(globalEventChannel())
    }

    override fun onDisable() {
        MiraiUnitBot.cancel()
        UnitConfigCommand.unregister()
    }
}