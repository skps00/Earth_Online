package com.earthonline.app.domain.service

object PetStatContributions {

    data class Weights(
        val strength: Float = 0f,
        val agility: Float = 0f,
        val intelligence: Float = 0f,
        val charisma: Float = 0f,
        val vitality: Float = 0f
    )

    private val SPECIFIC = mapOf(
        "health_marathon" to Weights(strength = 0.4f, vitality = 0.6f),
        "health_10k" to Weights(strength = 0.3f, agility = 0.3f, vitality = 0.4f),
        "health_pushup_50" to Weights(strength = 0.8f, vitality = 0.2f),
        "health_plank_5min" to Weights(strength = 0.7f, vitality = 0.3f),
        "health_weight_loss" to Weights(agility = 0.2f, vitality = 0.8f),
        "health_blood" to Weights(charisma = 0.5f, vitality = 0.5f),
        "health_yoga" to Weights(agility = 0.6f, vitality = 0.4f),
        "health_gym" to Weights(strength = 0.5f, vitality = 0.5f),
        "health_meditate" to Weights(intelligence = 0.7f, charisma = 0.3f),
        "health_no_sugar" to Weights(vitality = 0.6f, intelligence = 0.4f),
        "health_sleep_8h" to Weights(vitality = 0.5f, intelligence = 0.3f, charisma = 0.2f),

        "career_phd" to Weights(intelligence = 0.7f, charisma = 0.3f),
        "career_masters" to Weights(intelligence = 0.6f, charisma = 0.4f),
        "career_bachelor" to Weights(intelligence = 0.5f, charisma = 0.5f),
        "career_graduate" to Weights(intelligence = 0.6f, charisma = 0.4f),
        "career_startup" to Weights(intelligence = 0.5f, charisma = 0.3f, strength = 0.2f),
        "career_promotion" to Weights(intelligence = 0.4f, charisma = 0.6f),
        "career_raise" to Weights(charisma = 0.7f, intelligence = 0.3f),
        "career_presentation" to Weights(charisma = 0.7f, intelligence = 0.3f),
        "career_365_ontime" to Weights(vitality = 0.5f, strength = 0.3f, intelligence = 0.2f),
        "career_fired" to Weights(charisma = 0.5f, intelligence = 0.5f),
        "career_quit" to Weights(charisma = 0.4f, strength = 0.3f, agility = 0.3f),
        "career_overtime_hell" to Weights(vitality = 0.6f, strength = 0.4f),
        "career_meeting_hell" to Weights(intelligence = 0.5f, charisma = 0.5f),

        "explore_mountain" to Weights(agility = 0.5f, vitality = 0.3f, strength = 0.2f),
        "explore_solo" to Weights(charisma = 0.6f, agility = 0.4f),
        "explore_jungle" to Weights(agility = 0.6f, vitality = 0.4f),
        "explore_desert" to Weights(agility = 0.4f, vitality = 0.6f),
        "explore_underwater" to Weights(agility = 0.6f, strength = 0.4f),
        "explore_camping" to Weights(agility = 0.4f, vitality = 0.3f, charisma = 0.3f),
        "explore_volcano" to Weights(agility = 0.5f, strength = 0.5f),
        "explore_canyon" to Weights(agility = 0.5f, vitality = 0.5f),
        "explore_island" to Weights(agility = 0.5f, charisma = 0.5f),

        "daily_cook" to Weights(intelligence = 0.5f, charisma = 0.5f),
        "daily_read_10" to Weights(intelligence = 1.0f),
        "daily_exercise_30" to Weights(vitality = 0.7f, strength = 0.3f),
        "daily_social" to Weights(charisma = 1.0f),
        "daily_diy" to Weights(intelligence = 0.6f, strength = 0.4f),
        "daily_fix" to Weights(intelligence = 0.7f, vitality = 0.3f),
        "daily_plant" to Weights(charisma = 0.5f, vitality = 0.5f),
        "daily_pets" to Weights(charisma = 1.0f),
        "daily_tattoo" to Weights(charisma = 0.7f, strength = 0.3f),

        "transport_bike" to Weights(agility = 0.5f, vitality = 0.5f),
        "transport_bike_100" to Weights(agility = 0.4f, vitality = 0.6f),
        "transport_roadtrip" to Weights(agility = 0.5f, charisma = 0.5f),
        "transport_no_accident" to Weights(intelligence = 0.5f, vitality = 0.5f),

        "epic_concert" to Weights(charisma = 0.7f, agility = 0.3f),
        "epic_northernlights" to Weights(charisma = 0.6f, agility = 0.4f),
        "epic_meteor" to Weights(charisma = 0.5f, intelligence = 0.5f),
        "epic_double_rainbow" to Weights(charisma = 0.7f, agility = 0.3f),
        "epic_survive" to Weights(strength = 0.7f, vitality = 0.3f),
        "epic_newborn" to Weights(charisma = 0.7f, vitality = 0.3f),
        "epic_eclipse" to Weights(intelligence = 0.6f, charisma = 0.4f),
        "epic_milkyway" to Weights(charisma = 0.5f, intelligence = 0.5f),
        "epic_earthquake" to Weights(strength = 0.5f, vitality = 0.5f),
    )

    private val epicWeights = Weights(strength = 0.40f, agility = 0.15f, intelligence = 0.20f, charisma = 0.15f, vitality = 0.10f)
    private val oceanWeights = Weights(strength = 0.40f, agility = 0.25f, charisma = 0.20f, vitality = 0.15f)
    private val exploreWeights = Weights(agility = 0.40f, charisma = 0.30f, vitality = 0.20f, intelligence = 0.10f)
    private val careerWeights = Weights(intelligence = 0.45f, charisma = 0.35f, vitality = 0.10f, strength = 0.10f)
    private val dailyWeights = Weights(charisma = 0.45f, agility = 0.20f, vitality = 0.20f, intelligence = 0.15f)
    private val healthWeights = Weights(vitality = 0.50f, strength = 0.30f, intelligence = 0.10f, charisma = 0.10f)
    private val transportWeights = Weights(vitality = 0.35f, agility = 0.35f, strength = 0.15f, intelligence = 0.15f)
    private val checkinWeights = Weights(strength = 0.20f, agility = 0.20f, intelligence = 0.20f, charisma = 0.20f, vitality = 0.20f)

    fun getWeights(achievementId: String): Weights {
        SPECIFIC[achievementId]?.let { return it }
        return when {
            achievementId.startsWith("ocean_") -> oceanWeights
            achievementId.startsWith("epic_") -> epicWeights
            achievementId.startsWith("explore_") -> exploreWeights
            achievementId.startsWith("career_") -> careerWeights
            achievementId.startsWith("daily_") -> dailyWeights
            achievementId.startsWith("health_") -> healthWeights
            achievementId.startsWith("transport_") -> transportWeights
            else -> checkinWeights
        }
    }
}
