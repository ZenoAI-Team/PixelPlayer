package com.theveloper.pixelplay.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4

@Entity(tableName = "songs_fts")
@Fts4(contentEntity = SongEntity::class)
data class SongFtsEntity(
    val title: String,
    @ColumnInfo(name = "artist_name") val artistName: String,
    @ColumnInfo(name = "album_name") val albumName: String,
    val genre: String?,
    val lyrics: String?
)
