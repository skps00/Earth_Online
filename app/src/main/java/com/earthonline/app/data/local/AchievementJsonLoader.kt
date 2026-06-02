package com.earthonline.app.data.local

import android.content.Context
import com.earthonline.app.data.local.entity.AchievementDefinitionEntity
import org.json.JSONArray

object AchievementJsonLoader {

    fun load(context: Context): List<AchievementDefinitionEntity> {
        val resId = context.resources.getIdentifier("achievements", "raw", context.packageName)
        if (resId == 0) return emptyList()
        val json = context.resources.openRawResource(resId).bufferedReader().use { it.readText() }

        val array = JSONArray(json)
        val list = mutableListOf<AchievementDefinitionEntity>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            list.add(
                AchievementDefinitionEntity(
                    achievementId = obj.getString("id"),
                    title = obj.getString("title"),
                    description = obj.getString("description"),
                    iconAsset = obj.getString("icon"),
                    triggerType = obj.getString("type"),
                    triggerGoal = obj.getLong("goal"),
                    isHidden = obj.getBoolean("hidden"),
                    rewardPoints = obj.getInt("points"),
                    hint = obj.optString("hint", "")
                )
            )
        }
        return list
    }
}
