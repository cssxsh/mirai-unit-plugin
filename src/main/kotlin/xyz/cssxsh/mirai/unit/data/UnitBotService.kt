package xyz.cssxsh.mirai.unit.data

import net.mamoe.mirai.console.data.*
import net.mamoe.mirai.console.data.PluginDataExtensions.mapKeys
import net.mamoe.mirai.console.permission.*

public object UnitBotService : AutoSavePluginConfig("UnitBotService") {
    @ValueName("service")
    @ValueDescription("Unit Bot service")
    public val service: MutableMap<PermitteeId, String> by value<MutableMap<String, String>>()
        .mapKeys(AbstractPermitteeId.Companion::parseFromString, PermitteeId::asString)

}