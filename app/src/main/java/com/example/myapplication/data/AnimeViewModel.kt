package com.example.myapplication.data

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class AnimeViewModel(application: Application) : AndroidViewModel(application) {

    private val animeDao: AnimeDao = AnimeDatabase.getDatabase(application, viewModelScope).animeDao()

    val allAnime: Flow<List<AnimeWithDetails>> = animeDao.getAllAnime()
    val watchlist: Flow<List<AnimeWithDetails>> = animeDao.getWatchlist()
    val allGenres: Flow<List<String>> = animeDao.getAllGenres()
    fun searchAnime(query: String): Flow<List<AnimeWithDetails>> {
        return animeDao.searchAnime(query)
    }

    fun getAnimeById(animeId: Int): Flow<AnimeWithDetails?> {
        return animeDao.getAnimeById(animeId)
    }

    fun toggleWatchlist(animeId: Int, isAdded: Boolean) {
        viewModelScope.launch {
            if (isAdded) {
                animeDao.addToWatchlist(animeId)
            } else {
                animeDao.removeFromWatchlist(animeId)
            }
        }
    }

    fun toggleCompleted(animeId: Int, completed: Boolean) {
        viewModelScope.launch {
            animeDao.setCompleted(animeId, completed)
        }
    }
}
