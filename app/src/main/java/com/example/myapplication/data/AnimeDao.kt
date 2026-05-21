package com.example.myapplication.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimeDao {

    @Query("""
        SELECT a.mal_id, a.url, a.title, a.title_japanese, a.duration, a.episodes, a.rating, a.status,
               s.score, i.image_jpg_large_url,
               CASE WHEN w.anime_id IS NOT NULL THEN 1 ELSE 0 END AS isInWatchlist,
               a.synopsis, NULL AS background, NULL AS type, NULL AS source,
               ai.aired_string, ai.season, ai.year,
               i.trailer_url,
               (SELECT GROUP_CONCAT(g.genres_name, ',') FROM anime_genres_mapping gm JOIN genres g ON gm.genres_id = g.genres_id WHERE gm.mal_id = a.mal_id) AS genres,
               (SELECT GROUP_CONCAT(t.themes_name, ',') FROM anime_themes_mapping tm JOIN themes t ON tm.themes_id = t.themes_id WHERE tm.mal_id = a.mal_id) AS themes,
               (SELECT GROUP_CONCAT(st.studios_name, ',') FROM anime_studios_mapping sm JOIN studios st ON sm.studios_id = st.studios_id WHERE sm.mal_id = a.mal_id) AS studios,
               COALESCE(w.completed, 0) AS isCompleted
        FROM anime_base a
        LEFT JOIN score s ON a.mal_id = s.mal_id
        LEFT JOIN image i ON a.mal_id = i.mal_id
        LEFT JOIN watchlist w ON a.mal_id = w.anime_id
        LEFT JOIN airing_information ai ON a.mal_id = ai.mal_id
    """)
    fun getAllAnime(): Flow<List<AnimeWithDetails>>

    @Query("""
        SELECT a.mal_id, a.url, a.title, a.title_japanese, a.duration, a.episodes, a.rating, a.status,
               s.score, i.image_jpg_large_url,
               1 AS isInWatchlist,
               a.synopsis, NULL AS background, NULL AS type, NULL AS source,
               ai.aired_string, ai.season, ai.year,
               i.trailer_url,
               (SELECT GROUP_CONCAT(g.genres_name, ',') FROM anime_genres_mapping gm JOIN genres g ON gm.genres_id = g.genres_id WHERE gm.mal_id = a.mal_id) AS genres,
               (SELECT GROUP_CONCAT(t.themes_name, ',') FROM anime_themes_mapping tm JOIN themes t ON tm.themes_id = t.themes_id WHERE tm.mal_id = a.mal_id) AS themes,
               (SELECT GROUP_CONCAT(st.studios_name, ',') FROM anime_studios_mapping sm JOIN studios st ON sm.studios_id = st.studios_id WHERE sm.mal_id = a.mal_id) AS studios,
               COALESCE(w.completed, 0) AS isCompleted
        FROM anime_base a
        INNER JOIN watchlist w ON a.mal_id = w.anime_id
        LEFT JOIN score s ON a.mal_id = s.mal_id
        LEFT JOIN image i ON a.mal_id = i.mal_id
        LEFT JOIN airing_information ai ON a.mal_id = ai.mal_id
    """)
    fun getWatchlist(): Flow<List<AnimeWithDetails>>

    @Query("""
        SELECT a.mal_id, a.url, a.title, a.title_japanese, a.duration, a.episodes, a.rating, a.status,
               s.score, i.image_jpg_large_url,
               CASE WHEN w.anime_id IS NOT NULL THEN 1 ELSE 0 END AS isInWatchlist,
               a.synopsis, NULL AS background, NULL AS type, NULL AS source,
               ai.aired_string, ai.season, ai.year,
               i.trailer_url,
               (SELECT GROUP_CONCAT(g.genres_name, ',') FROM anime_genres_mapping gm JOIN genres g ON gm.genres_id = g.genres_id WHERE gm.mal_id = a.mal_id) AS genres,
               (SELECT GROUP_CONCAT(t.themes_name, ',') FROM anime_themes_mapping tm JOIN themes t ON tm.themes_id = t.themes_id WHERE tm.mal_id = a.mal_id) AS themes,
               (SELECT GROUP_CONCAT(st.studios_name, ',') FROM anime_studios_mapping sm JOIN studios st ON sm.studios_id = st.studios_id WHERE sm.mal_id = a.mal_id) AS studios,
               COALESCE(w.completed, 0) AS isCompleted
        FROM anime_base a
        LEFT JOIN score s ON a.mal_id = s.mal_id
        LEFT JOIN image i ON a.mal_id = i.mal_id
        LEFT JOIN watchlist w ON a.mal_id = w.anime_id
        LEFT JOIN airing_information ai ON a.mal_id = ai.mal_id
        WHERE a.title LIKE '%' || :searchQuery || '%' OR a.title_japanese LIKE '%' || :searchQuery || '%'
    """)
    fun searchAnime(searchQuery: String): Flow<List<AnimeWithDetails>>

    @Query("""
        SELECT a.mal_id, a.url, a.title, a.title_japanese, a.duration, a.episodes, a.rating, a.status,
               s.score, i.image_jpg_large_url,
               CASE WHEN w.anime_id IS NOT NULL THEN 1 ELSE 0 END AS isInWatchlist,
               a.synopsis, NULL AS background, NULL AS type, NULL AS source,
               ai.aired_string, ai.season, ai.year,
               i.trailer_url,
               (SELECT GROUP_CONCAT(g.genres_name, ',') FROM anime_genres_mapping gm JOIN genres g ON gm.genres_id = g.genres_id WHERE gm.mal_id = a.mal_id) AS genres,
               (SELECT GROUP_CONCAT(t.themes_name, ',') FROM anime_themes_mapping tm JOIN themes t ON tm.themes_id = t.themes_id WHERE tm.mal_id = a.mal_id) AS themes,
               (SELECT GROUP_CONCAT(st.studios_name, ',') FROM anime_studios_mapping sm JOIN studios st ON sm.studios_id = st.studios_id WHERE sm.mal_id = a.mal_id) AS studios,
               COALESCE(w.completed, 0) AS isCompleted
        FROM anime_base a
        LEFT JOIN score s ON a.mal_id = s.mal_id
        LEFT JOIN image i ON a.mal_id = i.mal_id
        LEFT JOIN watchlist w ON a.mal_id = w.anime_id
        LEFT JOIN airing_information ai ON a.mal_id = ai.mal_id
        WHERE a.mal_id = :animeId
    """)
    fun getAnimeById(animeId: Int): Flow<AnimeWithDetails?>

    @Query("SELECT genres_name FROM genres ORDER BY genres_name")
    fun getAllGenres(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnime(anime: AnimeEntity)

    @Query("INSERT OR IGNORE INTO watchlist (anime_id, completed) VALUES (:animeId, 0)")
    suspend fun addToWatchlist(animeId: Int)

    @Query("DELETE FROM watchlist WHERE anime_id = :animeId")
    suspend fun removeFromWatchlist(animeId: Int)

    @Query("UPDATE watchlist SET completed = :completed WHERE anime_id = :animeId")
    suspend fun setCompleted(animeId: Int, completed: Boolean)
}
