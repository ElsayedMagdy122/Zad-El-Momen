package dev.sayed.mehrabalmomen.domain.repository

import dev.sayed.mehrabalmomen.domain.entity.Prayer

interface PrayerRepository {
    suspend fun getPrayerTimes(): List<Prayer>
}