package com.example.myapplication

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.myapplication.ui.theme.*
import kotlinx.coroutines.flow.collectLatest

import com.example.myapplication.data.*
import androidx.lifecycle.viewmodel.compose.viewModel

data class Anime(
    val id: Int = 0,
    val url: String,
    val imageUrl: String,
    val title: String,
    val subTitle: String,
    val duration: String,
    val episodes: Int? = null,
    val rating: String,
    val score: String,
    val status: String? = null,
    val statusColor: Color = Color.Transparent,
    val genres: List<String> = emptyList(),
    val themes: List<String> = emptyList(),
    val isInWatchlist: Boolean = false,
    val isCompleted: Boolean = false,
    val synopsis: String = "",
    val background: String = "",
    val type: String = "",
    val source: String = "",
    val airedString: String = "",
    val season: String = "",
    val year: String = "",
    val studios: List<String> = emptyList(),
    val trailerUrl: String = ""
)

fun AnimeWithDetails.toUiModel(): Anime {
    val parsedGenres = genres?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
    val parsedThemes = themes?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
    val parsedStudios = studios?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()

    return Anime(
        id = mal_id ?: 0,
        url = url ?: "",
        imageUrl = image_jpg_large_url ?: "",
        title = title ?: "Untitled",
        subTitle = title_japanese ?: "",
        duration = duration ?: "",
        episodes = episodes?.toInt(),
        rating = rating ?: "Unrated",
        score = score?.toString() ?: "0.0",
        status = status,
        statusColor = when (status) {
            "in watchlist" -> Color(0xFFD92D20)
            "Completed" -> Color(0xFF32D583)
            else -> Color.Transparent
        },
        genres = parsedGenres,
        themes = parsedThemes,
        isInWatchlist = isInWatchlist,
        isCompleted = isCompleted,
        synopsis = synopsis ?: "No synopsis available.",
        background = background ?: "",
        type = type ?: "",
        source = source ?: "",
        airedString = aired_string ?: "",
        season = season ?: "",
        year = year?.toInt()?.toString() ?: "",
        studios = parsedStudios,
        trailerUrl = trailer_url ?: ""
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AniListScreen(viewModel: AnimeViewModel = viewModel()) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    
    val availableGenres by viewModel.allGenres.collectAsState(initial = emptyList())
    val selectedGenres = remember { mutableStateListOf<String>() }

    LaunchedEffect(availableGenres) {
        if (selectedGenres.isEmpty() && availableGenres.isNotEmpty()) {
            selectedGenres.addAll(availableGenres)
        }
    }
    
    val animeEntities by (if (searchQuery.isEmpty()) {
        viewModel.allAnime
    } else {
        viewModel.searchAnime(searchQuery)
    }).collectAsState(initial = emptyList())
    
    var animeList = animeEntities.map { it.toUiModel() }
    
    if (selectedGenres.isNotEmpty()) {
        animeList = animeList.filter { anime ->
            selectedGenres.any { selected ->
                anime.genres.any { it.equals(selected, ignoreCase = true) }
            }
        }
    } else {
        
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                HeaderSection()
                Spacer(modifier=Modifier.height(24.dp))
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item { 
                        SearchBar(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it }
                        ) 
                    }
                    item {
                        if (selectedGenres.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                maxItemsInEachRow = 4
                            ) {
                                selectedGenres.forEach { genre ->
                                    Surface(
                                        color = MaterialTheme.colorScheme.surface,
                                        shape = RoundedCornerShape(4.dp),
                                        modifier = Modifier.padding(horizontal = 4.dp).clickable {
                                            selectedGenres.remove(genre)
                                        }
                                    ) {
                                        Text(
                                            text = genre,
                                            color = MaterialTheme.colorScheme.primary,
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    item { 
                        FilterSection(
                            selectedGenres = selectedGenres,
                            availableGenres = availableGenres
                        ) 
                    }
                    item { 
                        Text(
                            text = "Discover Anime",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(animeList) { anime ->
                        AnimeCard(anime)
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
                
                BottomBar()
            }
        }
    }
}

@Composable
fun HeaderSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF101010)) // Using requested hex directly for header
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                tint = Color.Unspecified, // Keep original colors if any
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "AniList",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { 
            Text(
                "Search", 
                color = AniListTextGray, 
                style = MaterialTheme.typography.bodyLarge 
            ) 
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(1.dp, Color.DarkGray, RoundedCornerShape(28.dp)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(28.dp),
        textStyle = MaterialTheme.typography.bodyLarge
    )
}

@Composable
fun FilterSection(
    selectedGenres: MutableList<String>,
    availableGenres: List<String>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "filter",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            FilterColumn(
                label = "Genre",
                items = listOf("Add"),
                showAdd = false,
                availableOptions = availableGenres,
                onOptionSelected = { genre ->
                    if (genre !in selectedGenres) {
                        selectedGenres.add(genre)
                    }
                },
                isSingleSelect = true
            )
        }
    }
}

@Composable
fun FilterColumn(
    label: String,
    items: List<String>,
    showAdd: Boolean = false,
    availableOptions: List<String> = emptyList(),
    onOptionSelected: (String) -> Unit = {},
    isSingleSelect: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.labelSmall
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(4.dp))
                    .border(
                        if (isSingleSelect) 0.5.dp else 0.dp, 
                        if (isSingleSelect) Color.Gray else Color.Transparent, 
                        RoundedCornerShape(4.dp)
                    )
                    .padding(4.dp)
                    .then(if (isSingleSelect) Modifier.clickable { expanded = true } else Modifier)
            ) {
                items.forEach { item ->
                    Text(
                        text = item,
                        color = if (item == "Add") MaterialTheme.colorScheme.primary else if (isSingleSelect) MaterialTheme.colorScheme.onSurface else AniListTextGray,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
                if (showAdd) {
                    Surface(
                        onClick = { expanded = true },
                        shape = RoundedCornerShape(4.dp),
                        color = Color.Transparent,
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = "Add",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                availableOptions.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun AnimeCard(anime: Anime) {
    val context = LocalContext.current
    var isClicked by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val intent = Intent(context, AnimeDetailsActivity::class.java).apply {
                    putExtra("mal_id", anime.id)
                    putExtra("title", anime.title)
                    putExtra("subTitle", anime.subTitle)
                    putExtra("url", anime.url)
                    putExtra("image_url", anime.imageUrl)
                    putStringArrayListExtra("genres", ArrayList(anime.genres))
                    putStringArrayListExtra("themes", ArrayList(anime.themes))
                    putExtra("isInWatchlist", anime.isInWatchlist)
                    putExtra("synopsis", anime.synopsis)
                    putExtra("background", anime.background)
                    putExtra("type", anime.type)
                    putExtra("source", anime.source)
                    putExtra("aired_string", anime.airedString)
                    putExtra("season", anime.season)
                    putExtra("year", anime.year)
                    putStringArrayListExtra("studios", ArrayList(anime.studios))
                    putExtra("trailer_url", anime.trailerUrl)
                    putExtra("rating", anime.rating)
                    putExtra("score", anime.score)
                }
                context.startActivity(intent)
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
            ) {
                GlideImage(
                    model = anime.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .width(80.dp)
                        .height(110.dp)
                        .background(Color.DarkGray)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = anime.title,
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = anime.subTitle,
                                color = AniListTextGray,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        
                        if (anime.status != null && anime.status.isNotEmpty()) {
                            Surface(
                                color = anime.statusColor,
                                shape = RoundedCornerShape(2.dp)
                            ) {
                                Text(
                                    text = anime.status,
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        } else if (anime.genres.isNotEmpty()) {
                             Text(
                                text = anime.genres.first(),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    AnimeStatRow("Duration", anime.duration)
                    if (anime.episodes != null) {
                        AnimeStatRow("episodes", anime.episodes.toString())
                    }
                    AnimeStatRow("ratings", anime.rating)
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Score",
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.labelSmall
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = anime.score,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnimeStatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = label,
            color = AniListTextGray,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.width(60.dp)
        )
        Text(
            text = value,
            color = AniListTextGray,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun BottomBar() {
    val context = LocalContext.current
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {
                context.startActivity(Intent(context, WatchlistActivity::class.java))
            }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "See Watchlist",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun AniListScreenPreview() {
    MyApplicationTheme(darkTheme = true) {
        AniListScreen()
    }
}
