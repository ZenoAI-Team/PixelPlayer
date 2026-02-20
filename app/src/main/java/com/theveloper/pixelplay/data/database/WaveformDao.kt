package com.theveloper.pixelplay.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WaveformDao {
    @Query("SELECT * FROM waveforms WHERE song_id = :songId")
    suspend fun getWaveformBySongId(songId: Long): WaveformEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaveform(waveform: WaveformEntity)

    @Query("DELETE FROM waveforms WHERE song_id = :songId")
    suspend fun deleteWaveformBySongId(songId: Long)

    @Query("DELETE FROM waveforms")
    suspend fun clearAll()
}
