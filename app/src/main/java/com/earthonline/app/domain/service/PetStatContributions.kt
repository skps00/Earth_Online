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
        // === checkin ===
        "checkin_1" to Weights(agility = 0.6f, charisma = 0.4f),
        "checkin_3" to Weights(agility = 0.5f, charisma = 0.3f, intelligence = 0.2f),
        "checkin_5" to Weights(agility = 0.5f, charisma = 0.3f, intelligence = 0.2f),
        "checkin_10" to Weights(agility = 0.4f, charisma = 0.3f, intelligence = 0.2f, vitality = 0.1f),
        "checkin_25" to Weights(agility = 0.35f, charisma = 0.3f, intelligence = 0.2f, vitality = 0.15f),
        "checkin_50" to Weights(agility = 0.3f, charisma = 0.3f, intelligence = 0.2f, vitality = 0.2f),

        // === auto-track explore ===
        "explore_5countries" to Weights(agility = 0.4f, charisma = 0.3f, intelligence = 0.3f),
        "explore_10countries" to Weights(agility = 0.4f, charisma = 0.3f, intelligence = 0.3f),
        "explore_50countries" to Weights(agility = 0.35f, charisma = 0.35f, intelligence = 0.3f),
        "explore_3continents" to Weights(agility = 0.4f, charisma = 0.3f, intelligence = 0.3f),
        "explore_7continents" to Weights(agility = 0.35f, strength = 0.35f, intelligence = 0.3f),

        // === explore — specific locations ===
        "explore_dateline" to Weights(agility = 0.5f, intelligence = 0.3f, charisma = 0.2f),
        "explore_missed_flight" to Weights(agility = 0.3f, charisma = 0.4f, intelligence = 0.3f),
        "explore_ocean" to Weights(strength = 0.4f, agility = 0.4f, vitality = 0.2f),
        "explore_first_abroad" to Weights(agility = 0.4f, charisma = 0.4f, intelligence = 0.2f),
        "explore_japan" to Weights(agility = 0.3f, charisma = 0.4f, intelligence = 0.3f),
        "explore_europe" to Weights(agility = 0.3f, charisma = 0.3f, intelligence = 0.4f),
        "explore_africa" to Weights(agility = 0.4f, strength = 0.3f, vitality = 0.3f),
        "explore_south_america" to Weights(agility = 0.4f, charisma = 0.3f, strength = 0.3f),
        "explore_antarctica" to Weights(strength = 0.4f, agility = 0.4f, vitality = 0.2f),
        "explore_australia" to Weights(agility = 0.4f, charisma = 0.3f, strength = 0.3f),
        "explore_asia" to Weights(agility = 0.3f, intelligence = 0.4f, charisma = 0.3f),
        "explore_north_america" to Weights(agility = 0.3f, charisma = 0.4f, intelligence = 0.3f),
        "explore_oceania" to Weights(agility = 0.4f, charisma = 0.3f, strength = 0.3f),

        // === explore — landmarks & activities ===
        "explore_capital" to Weights(intelligence = 0.4f, charisma = 0.3f, agility = 0.3f),
        "explore_unesco" to Weights(intelligence = 0.5f, charisma = 0.3f, agility = 0.2f),
        "explore_temple" to Weights(intelligence = 0.4f, charisma = 0.4f, agility = 0.2f),
        "explore_night_market" to Weights(charisma = 0.5f, vitality = 0.3f, agility = 0.2f),
        "explore_hot_spring" to Weights(vitality = 0.5f, charisma = 0.3f, agility = 0.2f),
        "explore_beach" to Weights(charisma = 0.4f, vitality = 0.3f, agility = 0.3f),
        "explore_museum" to Weights(intelligence = 0.6f, charisma = 0.2f, agility = 0.2f),
        "explore_airport" to Weights(agility = 0.3f, vitality = 0.4f, intelligence = 0.3f),
        "explore_cruise" to Weights(charisma = 0.4f, agility = 0.3f, vitality = 0.3f),
        "explore_border" to Weights(agility = 0.5f, intelligence = 0.3f, charisma = 0.2f),
        "explore_lake" to Weights(vitality = 0.4f, charisma = 0.3f, agility = 0.3f),
        "explore_tower" to Weights(agility = 0.4f, charisma = 0.3f, intelligence = 0.3f),
        "explore_tokyo_tower" to Weights(charisma = 0.5f, intelligence = 0.3f, agility = 0.2f),
        "explore_great_wall" to Weights(strength = 0.4f, agility = 0.3f, intelligence = 0.3f),
        "explore_venice" to Weights(charisma = 0.5f, agility = 0.3f, intelligence = 0.2f),
        "explore_bucket_list" to Weights(charisma = 0.4f, intelligence = 0.3f, strength = 0.3f),
        "explore_sky_lantern" to Weights(charisma = 0.6f, agility = 0.2f, intelligence = 0.2f),
        "explore_snow" to Weights(charisma = 0.5f, agility = 0.3f, vitality = 0.2f),

        // === oceans (hidden) ===
        "ocean_pacific" to Weights(strength = 0.3f, agility = 0.3f, charisma = 0.2f, vitality = 0.2f),
        "ocean_atlantic" to Weights(strength = 0.3f, agility = 0.3f, charisma = 0.2f, vitality = 0.2f),
        "ocean_indian" to Weights(strength = 0.3f, agility = 0.3f, intelligence = 0.2f, vitality = 0.2f),
        "ocean_arctic" to Weights(strength = 0.5f, vitality = 0.3f, agility = 0.2f),
        "ocean_southern" to Weights(strength = 0.5f, vitality = 0.3f, agility = 0.2f),

        // === career ===
        "career_house" to Weights(charisma = 0.5f, intelligence = 0.3f, vitality = 0.2f),
        "career_first_job" to Weights(intelligence = 0.4f, charisma = 0.3f, vitality = 0.3f),

        // === daily ===
        "daily_lottery" to Weights(charisma = 0.6f, agility = 0.4f),
        "daily_earlybird" to Weights(vitality = 0.5f, intelligence = 0.3f, strength = 0.2f),
        "daily_binge" to Weights(charisma = 0.4f, vitality = 0.3f, intelligence = 0.3f),
        "daily_allnighter" to Weights(vitality = 0.5f, intelligence = 0.3f, strength = 0.2f),
        "daily_no_phone" to Weights(intelligence = 0.5f, charisma = 0.3f, vitality = 0.2f),
        "daily_stranger" to Weights(charisma = 0.7f, vitality = 0.3f),
        "daily_umbrella" to Weights(agility = 0.5f, charisma = 0.5f),
        "daily_late" to Weights(agility = 0.6f, charisma = 0.4f),
        "daily_spicy" to Weights(strength = 0.5f, vitality = 0.5f),
        "daily_karaoke" to Weights(charisma = 0.7f, agility = 0.3f),
        "daily_fall_down" to Weights(vitality = 0.4f, charisma = 0.3f, agility = 0.3f),
        "daily_look_up" to Weights(charisma = 0.5f, intelligence = 0.5f),
        "daily_suntan" to Weights(vitality = 0.5f, charisma = 0.5f),
        "daily_chopsticks" to Weights(agility = 0.6f, intelligence = 0.4f),
        "daily_photo_album" to Weights(charisma = 0.5f, intelligence = 0.5f),

        // === epic ===
        "epic_first_date" to Weights(charisma = 0.7f, agility = 0.3f),

        // === health ===
        "health_vegan" to Weights(vitality = 0.5f, intelligence = 0.3f, charisma = 0.2f),

        // === transport ===
        "transport_license" to Weights(intelligence = 0.5f, agility = 0.3f, charisma = 0.2f),
        "transport_first_car" to Weights(charisma = 0.5f, agility = 0.3f, strength = 0.2f),
        "transport_uber_100" to Weights(agility = 0.4f, charisma = 0.3f, intelligence = 0.3f),
    )

    // fallback defaults by prefix
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
