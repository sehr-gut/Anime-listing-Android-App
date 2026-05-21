package com.example.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(tableName = "anime_base")
data class AnimeEntity(
    @PrimaryKey val mal_id: Int?,
    val url: String?,
    val title: String?,
    val title_japanese: String?,
    val duration: String?,
    val episodes: Double?,
    val rating: String?,
    val status: String?,
    val synopsis: String?
)

/**
 * A POJO used for the result of the JOIN query.
 * This keeps the database 'anime_base' table clean while allowing the UI to get all data at once.
 */
data class AnimeWithDetails(
    val mal_id: Int?,
    val url: String?,
    val title: String?,
    val title_japanese: String?,
    val duration: String?,
    val episodes: Double?,
    val rating: String?,
    val status: String?,
    val score: Double?,
    val image_jpg_large_url: String?,
    val isInWatchlist: Boolean,
    val synopsis: String?,
    val background: String? = null,
    val type: String? = null,
    val source: String? = null,
    val aired_string: String? = null,
    val season: String? = null,
    val year: Double? = null,
    val studios: String? = null,
    val genres: String? = null,
    val themes: String? = null,
    val trailer_url: String? = null,
    val isCompleted: Boolean = false
)

// The following entities are kept in case they exist in the DB, 
// but anime_base is now aligned with the actual schema found in Logcat.

@Entity(tableName = "airing_information", primaryKeys = ["mal_id"], foreignKeys = [
    ForeignKey(entity = AnimeEntity::class, parentColumns = ["mal_id"], childColumns = ["mal_id"])
])
data class AiringInformationEntity(
    val mal_id: Int?,
    val aired_prop_from_month: Double?,
    val aired_prop_from_year: Double?,
    val aired_prop_to_month: Double?,
    val aired_prop_to_year: Double?,
    val aired_string: String?,
    val season: String?,
    val year: Double?,
    val broadcast_day: String?,
    val broadcast_time: String?,
    val producers: String?,
    val licensors: String?,
    val studios: String?
)

@Entity(tableName = "genres")
data class GenresEntity(
    @PrimaryKey val genres_id: Int,
    val genres_name: String?
)

@Entity(tableName = "themes")
data class ThemesEntity(
    @PrimaryKey val themes_id: Int,
    val themes_name: String?
)

@Entity(tableName = "studios")
data class StudiosEntity(
    @PrimaryKey val studios_id: Int,
    val studios_name: String?
)

@Entity(tableName = "anime_genres_mapping", primaryKeys = ["mal_id", "genres_id"], foreignKeys = [
    ForeignKey(entity = AnimeEntity::class, parentColumns = ["mal_id"], childColumns = ["mal_id"]),
    ForeignKey(entity = GenresEntity::class, parentColumns = ["genres_id"], childColumns = ["genres_id"])
], indices = [
    Index(value = ["genres_id"])
])
data class AnimeGenresMappingEntity(
    val mal_id: Int,
    val genres_id: Int
)

@Entity(tableName = "anime_themes_mapping", primaryKeys = ["mal_id", "themes_id"], foreignKeys = [
    ForeignKey(entity = AnimeEntity::class, parentColumns = ["mal_id"], childColumns = ["mal_id"]),
    ForeignKey(entity = ThemesEntity::class, parentColumns = ["themes_id"], childColumns = ["themes_id"])
], indices = [
    Index(value = ["themes_id"])
])
data class AnimeThemesMappingEntity(
    val mal_id: Int,
    val themes_id: Int
)

@Entity(tableName = "anime_studios_mapping", primaryKeys = ["mal_id", "studios_id"], foreignKeys = [
    ForeignKey(entity = AnimeEntity::class, parentColumns = ["mal_id"], childColumns = ["mal_id"]),
    ForeignKey(entity = StudiosEntity::class, parentColumns = ["studios_id"], childColumns = ["studios_id"])
], indices = [
    Index(value = ["studios_id"])
])
data class AnimeStudiosMappingEntity(
    val mal_id: Int,
    val studios_id: Int
)
