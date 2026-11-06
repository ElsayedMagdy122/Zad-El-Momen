package dev.sayed.mehrabalmomen.data.reminders.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.sayed.mehrabalmomen.data.reminders.local.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminders ORDER BY id ASC")
    fun getAllReminders(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders")
    suspend fun getAllRemindersSync(): List<ReminderEntity>

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getReminderById(id: Long): ReminderEntity?

    @Query("SELECT COUNT(*) FROM reminders")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: ReminderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reminders: List<ReminderEntity>)

    @Update
    suspend fun update(reminder: ReminderEntity)

    @Query("UPDATE reminders SET isEnabled = :isEnabled WHERE id = :id")
    suspend fun updateEnabled(id: Long, isEnabled: Boolean)

    @Query("UPDATE reminders SET hour = :hour, minute = :minute WHERE id = :id")
    suspend fun updateTime(id: Long, hour: Int, minute: Int)

    @Query("UPDATE reminders SET recurrence = :recurrence WHERE id = :id")
    suspend fun updateRecurrence(id: Long, recurrence: String)

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun delete(id: Long)
}
