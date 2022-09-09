package xyz.cssxsh.mirai.unit

import io.ktor.client.utils.*
import kotlinx.coroutines.*
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.console.command.CommandSender.Companion.toCommandSender
import net.mamoe.mirai.event.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.utils.*
import xyz.cssxsh.baidu.unit.*
import xyz.cssxsh.baidu.unit.data.*
import xyz.cssxsh.mirai.unit.data.*
import kotlin.coroutines.*

public object MiraiUnitBot : BaiduUnitClient(config = UnitBotConfig), ListenerHost, CoroutineScope {

    private val logger: MiraiLogger by lazy {
        try {
            MiraiUnitPlugin.logger
        } catch (_: ExceptionInInitializerError) {
            MiraiLogger.Factory.create(this::class, "MiraiUnitBot")
        }
    }

    override val coroutineContext: CoroutineContext by lazy {
        try {
            MiraiUnitPlugin.coroutineContext + CoroutineName("MiraiUnitBot")
        } catch (_: ExceptionInInitializerError) {
            CoroutineExceptionHandler { context, throwable ->
                if (throwable.unwrapCancellationException() !is CancellationException) {
                    logger.error("Exception in $context", throwable)
                }
            } + CoroutineName("MiraiUnitBot")
        }
    }

    private val switch: Regex = """Unit在吗""".toRegex()

    public suspend fun CommandSender.push(action: Action): MessageReceipt<*>? {
        val prefix = user?.let { At(it) } ?: emptyMessageChain()
        return when (action.type) {
            ActionType.clarify -> sendMessage(prefix + action.say)
            ActionType.satisfy -> sendMessage(prefix + action.say)
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
        if (this is MessageSyncEvent) return
        val content = message.findIsInstance<PlainText>()?.content ?: return
        if ((content matches switch).not()) return
        val serviceId = UnitBotService.service[subject] ?: return
        val terminalId = UnitBotService.serialize(sender)
        if (terminalId in sessions) return

        launch(CoroutineName(terminalId)) {
            val target = toCommandSender()
            val channel = globalEventChannel()
            target.sendMessage(message = message.quote() + "$serviceId 为你服务")

            while (isActive) {
                val text = channel.syncFromEvent { event: MessageEvent ->
                    if (event.sender == target.user) {
                        event.message.findIsInstance<PlainText>()?.content
                    } else {
                        null
                    }
                }
                if ("." == text) break

                val data = query(text = text, terminalId = terminalId, serviceId = serviceId)

                loop@ for (response in data.responses) {
                    if (response.status != 0) continue
                    for (action in response.actions) {
                        target.push(action = action) ?: continue
                        break@loop
                    }
                }
            }
        }.invokeOnCompletion {
            sessions.remove(terminalId)
        }
    }
}