package com.example.myapplication.data

import android.content.Context
import androidx.room.Database
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        AnimeEntity::class,
        ScoreEntity::class,
        ImageEntity::class,
        WatchlistEntity::class,
        AiringInformationEntity::class,
        GenresEntity::class,
        ThemesEntity::class,
        StudiosEntity::class,
        AnimeGenresMappingEntity::class,
        AnimeThemesMappingEntity::class,
        AnimeStudiosMappingEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AnimeDatabase : RoomDatabase() {

    abstract fun animeDao(): AnimeDao

    companion object {
        @Volatile
        private var INSTANCE: AnimeDatabase? = null

        // Migration from version 1 → 2: adds the `completed` column to watchlist.
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE watchlist ADD COLUMN completed INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getDatabase(context: Context, scope: CoroutineScope): AnimeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AnimeDatabase::class.java,
                    "anime_database"
                )
                .createFromAsset("anime.db")
                .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

@Entity(tableName = "score", foreignKeys = [
    ForeignKey(entity = AnimeEntity::class, parentColumns = ["mal_id"], childColumns = ["mal_id"])
])
data class ScoreEntity(
    @PrimaryKey val mal_id: Int?,
    val score: Double?,
    val scored_by: Double?,
    val rank: Double?,
    val favorites: Int?
)

@Entity(tableName = "image", foreignKeys = [
    ForeignKey(entity = AnimeEntity::class, parentColumns = ["mal_id"], childColumns = ["mal_id"])
])
data class ImageEntity(
    @PrimaryKey val mal_id: Int?,
    val image_jpg_url: String?,
    val image_jpg_small_url: String?,
    val image_jpg_large_url: String?,
    val trailer_youtube_id: String?,
    val trailer_url: String?
)

@Entity(tableName = "watchlist", foreignKeys = [
    ForeignKey(entity = AnimeEntity::class, parentColumns = ["mal_id"], childColumns = ["anime_id"])
])
data class WatchlistEntity(
    @PrimaryKey val anime_id: Int?,
    val completed: Boolean = false
)

