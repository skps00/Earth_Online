package com.earthonline.app.domain.service

object PetStatContributions {

    data class Weights(
        val strength: Float = 0f,
        val agility: Float = 0f,
        val intelligence: Float = 0f,
        val charisma: Float = 0f,
        val vitality: Float = 0f
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
