package xyz.cssxsh.mirai.unit

import kotlinx.coroutines.*
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CommandSender.Companion.toCommandSender
import net.mamoe.mirai.console.permission.PermitteeId.Companion.hasChild
import net.mamoe.mirai.console.util.ContactUtils.render
import net.mamoe.mirai.event.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.*
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.utils.*
import xyz.cssxsh.baidu.unit.*
import xyz.cssxsh.baidu.unit.data.Action
import xyz.cssxsh.baidu.unit.data.ActionType
import xyz.cssxsh.mirai.unit.data.*
import kotlin.coroutines.*

public object MiraiUnitBot : BaiduUnitClient(config = UnitBotConfig), ListenerHost, CoroutineScope {

    private val logger: MiraiLogger by lazy {
        try {
            MiraiUnitPlugin.logger
        } catch (_: Throwable) {
            MiraiLogger.Factory.create(this::class, "MiraiUnitBot")
        }
    }

    override val coroutineContext: CoroutineContext by lazy {
        try {
            MiraiUnitPlugin.childScopeContext("MiraiUnitBot")
        } catch (_: Throwable) {
            CoroutineExceptionHandler { _, throwable ->
                if (throwable.unwrapCancellationException() !is CancellationException) {
                    logger.error("Exception in coroutine BaiduAipContentCensor", throwable)
                }
            }.childScopeContext("MiraiUnitBot")
        }
    }

    public suspend fun CommandSender.push(action: Action): MessageReceipt<*>? {
        return when (action.type) {
            ActionType.clarify -> sendMessage(At(user?.id ?: 12345) + action.say)
            ActionType.satisfy -> sendMessage(At(user?.id ?: 12345) + action.say)
            ActionType.guide -> sendMessage(action.say)
            ActionType.faqguide -> sendMessage(action.say)
            ActionType.understood -> null
            ActionType.failure -> null
            ActionType.chat -> sendMessage(action.say)
            ActionType.event -> sendMessage(action.say)
        }
    }

    @EventHandler
    public fun MessageEvent.push() {
        val target = toCommandSender()
        val serviceId = with(UnitBotConfig) {
            for ((id, serviceId) in service) {
                if (target.permitteeId.hasChild(id)) return@with serviceId
            }
            return@push
        }
        launch {
            val content = message.contentToString()
            val data = query(text = content, terminalId = sender.render(), serviceId = serviceId)

            for (response in data.responses) {
                if (response.status != 0) continue
                for (action in response.actions) {
                    target.push(action = action) ?: continue
                    //
                    return@launch
                }
            }
        }
    }
}