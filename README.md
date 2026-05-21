# Anime-listing-Android-App
This is a simple anime watchlist application created using kotlin and a sqlite3 database backend.
## Project Goals
Made as a requirement for Applied Operating Systems, The goal is to create an anime app that will show a watchlist of anime that the user will Choose. The primary user of this app are anime enthusiasts.
## User Stories
1. I want to add anime to my watchlist and mark it complete in the app.
2. I want to search for anime.
3. I want to see anime from the database.

## Data Models
1. Anime
2. Genre
3. Studio
4. Themes
5. Watchlist
6. Airing Information
7. Images
8. Score

## Entity Documentation
Majority of the data modeling is from kaggle dataset called Anime Database 2025
https://www.kaggle.com/datasets/sazzadsiddiquelikhon/myanimelist-anime-database-july-2025


| Entity | Attributes |
| :--- | :--- |
| **ANIME** | Mal_id, Url, Approved, Title, Title_english, Title_japanese, Episodes, Status, duration, rating, Synopsis |
| **IMAGE** | mal_id, Image_jpg_small_url, Image_jpg_large_url, Trailer_youtube_id, Trailer_url |
| **SCORE** | anime_id/Mal_id, Score, Scored_by, Rank, Favorites |
| **AIRING INFORMATION** | Aired_prop_from_month, Aired_prop_from_year, Aired_prop_to_month, Aired_prop_to_year, Aired_string, Season, year, broadcast_day, Broadcast_time |
| **GENRES** | Genres_id, Genres_name |
| **STUDIOS** | Studios_id, studios_name |
| **THEMES** | Themes_id, Themes_name |
| **Watchlist** | Anime_id, completed |


## UI/UX PROTOTYPE
<p align="center">
  <img src="./anilist/Group 17.png" alt="Image 1" width="33%" />
  <img src="./anilist/Group 18.png" alt="Image 2" width="33%" />
  <img src="./anilist/Group 19.png" alt="Image 3" width="33%" />
</p>

## DOWNLOAD
Check the releases for the APK download
