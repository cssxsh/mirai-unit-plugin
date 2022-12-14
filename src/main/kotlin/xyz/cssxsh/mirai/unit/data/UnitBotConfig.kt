package xyz.cssxsh.mirai.unit.data

import net.mamoe.mirai.console.data.*
import xyz.cssxsh.baidu.unit.*

public object UnitBotConfig : ReadOnlyPluginConfig("UnitBotConfig"), BaiduUnitConfig {
    @ValueName("app_name")
    @ValueDescription("百度AI客户端 APP_NAME")
    override val appName: String by value(System.getProperty("xyz.cssxsh.mirai.tts.name", ""))

    @ValueName("app_id")
    @ValueDescription("百度AI客户端 APP_ID")
    override val appId: Long by value(System.getProperty("xyz.cssxsh.mirai.tts.id", "0").toLong())

    @ValueName("api_key")
    @ValueDescription("百度AI客户端 API_KEY")
    override val appKey: String by value(System.getProperty("xyz.cssxsh.mirai.tts.key", ""))

    @ValueName("secret_key")
    @ValueDescription("百度AI客户端 SECRET_KEY")
    override val secretKey: String by value(System.getProperty("xyz.cssxsh.mirai.tts.secret", ""))

    @ValueName("host")
    @ValueDescription("Unit API HOST")
    override val host: String by value(BaiduUnitConfig.HOST.DEV)
}