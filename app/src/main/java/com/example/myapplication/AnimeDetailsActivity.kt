package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.myapplication.data.AnimeViewModel
import com.example.myapplication.ui.theme.MyApplicationTheme

class AnimeDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val malId = intent.getIntExtra("mal_id", -1)
        val title = intent.getStringExtra("title") ?: "Anime Title"
        val subTitle = intent.getStringExtra("subTitle") ?: "Japanese Title"
        val url = intent.getStringExtra("url") ?: ""
        val imageUrl = intent.getStringExtra("image_url") ?: ""
        val genres = intent.getStringArrayListExtra("genres") ?: arrayListOf()
        val themes = intent.getStringArrayListExtra("themes") ?: arrayListOf()
        val isInWatchlist = intent.getBooleanExtra("isInWatchlist", false)
        
        setContent {
            MyApplicationTheme {
                AnimeDetailsScreen(malId, title, subTitle, url, imageUrl, genres, themes, isInWatchlist)
            }
        }
    }
}

@Composable
fun Hyperlink(text: String, url: String) {
    val annotatedString = buildAnnotatedString {
        withLink(
            LinkAnnotation.Url(
                url = url,
                styles = TextLinkStyles(style = SpanStyle(color = MaterialTheme.colorScheme.primary))
            )
        ) {
            append(text)
        }
    }
    Text(
        text = annotatedString,
        style = MaterialTheme.typography.titleMedium
    )
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalGlideComposeApi::class)
@Composable
fun AnimeDetailsScreen(
    malId: Int,
    title: String, 
    subTitle: String, 
    url: String, 
    imageUrl: String,
    genres: List<String>, 
    themes: List<String>,
    initialIsInWatchlist: Boolean,
    viewModel: AnimeViewModel = viewModel()
) {
    val context = LocalContext.current
    val intent = (context as? ComponentActivity)?.intent

    val dbAnimeState by viewModel.getAnimeById(malId).collectAsState(initial = null)
    val dbAnime = dbAnimeState?.toUiModel()

    val currentTitle = dbAnime?.title ?: title
    val currentSubTitle = dbAnime?.subTitle ?: subTitle
    val currentUrl = dbAnime?.url ?: url
    val currentImageUrl = dbAnime?.imageUrl ?: imageUrl
    val currentGenres = dbAnime?.genres ?: genres
    val currentThemes = dbAnime?.themes ?: themes
    val currentIsInWatchlist = dbAnime?.isInWatchlist ?: initialIsInWatchlist
    val currentIsCompleted = dbAnime?.isCompleted ?: false
    val currentSynopsis = dbAnime?.synopsis ?: intent?.getStringExtra("synopsis") ?: "No synopsis available."
    val currentBackground = dbAnime?.background ?: intent?.getStringExtra("background") ?: ""
    val currentType = dbAnime?.type ?: intent?.getStringExtra("type") ?: ""
    val currentSource = dbAnime?.source ?: intent?.getStringExtra("source") ?: ""
    val currentAiredString = dbAnime?.airedString ?: intent?.getStringExtra("aired_string") ?: ""
    val currentSeason = dbAnime?.season ?: intent?.getStringExtra("season") ?: ""
    val currentYear = dbAnime?.year ?: intent?.getStringExtra("year") ?: ""
    val currentStudios = dbAnime?.studios ?: intent?.getStringArrayListExtra("studios") ?: emptyList()
    val currentTrailerUrl = dbAnime?.trailerUrl ?: intent?.getStringExtra("trailer_url") ?: ""
    val currentRating = dbAnime?.rating ?: intent?.getStringExtra("rating") ?: "Unrated"
    val currentScore = dbAnime?.score ?: intent?.getStringExtra("score") ?: "0.0"
    val currentStatus = dbAnime?.status ?: intent?.getStringExtra("status") ?: "Unknown"
    val currentDuration = dbAnime?.duration ?: intent?.getStringExtra("duration") ?: ""

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
                
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Banner Image
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .background(Color.DarkGray)
                    ) {
                        if (currentImageUrl.isNotEmpty()) {
                            GlideImage(
                                model = currentImageUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        }
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = currentStatus,
                                    color = Color.Black,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                            // ✅ Show "Completed" badge if the user has marked it done
                            if (currentIsCompleted) {
                                Surface(
                                    color = Color(0xFF12B76A),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "✓ Completed",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                        if (currentStudios.isNotEmpty()) {
                            Text(
                                text = currentStudios.joinToString(", "),
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentTitle,
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = currentSubTitle,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(horizontalAlignment = Alignment.End) {
                            DetailStatRow("Score", currentScore)
                            if (currentYear.isNotEmpty()) {
                                DetailStatRow("Year", currentYear)
                            }
                            if (currentSeason.isNotEmpty()) {
                                DetailStatRow("Season", currentSeason)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (currentDuration.isNotEmpty()) {
                        Text(
                            text = currentDuration,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    if (currentAiredString.isNotEmpty()) {
                        Text(
                            text = "Aired: $currentAiredString",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    if (currentRating.isNotEmpty() && currentRating != "Unrated") {
                        Text(
                            text = "Rating: $currentRating",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Synopsis",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Text(
                        text = currentSynopsis,
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 20.sp
                    )
                    
                    if (currentBackground.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Background Info",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Text(
                            text = currentBackground,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 20.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    if (url.isNotEmpty() || currentUrl.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Hyperlink(
                                text = "MyAnimeList Link",
                                url = if (url.isNotEmpty()) url else currentUrl
                            )
                        }
                    }
                    
                    if (currentTrailerUrl.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Hyperlink(
                                text = "Watch Trailer",
                                url = currentTrailerUrl
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (currentGenres.isNotEmpty()) {
                        Text(
                            text = "Genres",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        FlowRow(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            currentGenres.forEach { genre ->
                                SuggestionChip(
                                    onClick = { },
                                    label = { Text(genre, style = MaterialTheme.typography.labelSmall) },
                                    colors = SuggestionChipDefaults.suggestionChipColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                    }

                    if (currentThemes.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Themes",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        FlowRow(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            currentThemes.forEach { theme ->
                                SuggestionChip(
                                    onClick = { },
                                    label = { Text(theme, style = MaterialTheme.typography.labelSmall) },
                                    colors = SuggestionChipDefaults.suggestionChipColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        labelColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Bottom Buttons
                if (!currentIsInWatchlist) {
                    Button(
                        onClick = { 
                            viewModel.toggleWatchlist(malId, true)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Add to watchlist",
                            color = Color.Black,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(horizontal = 16.dp),
                        color = Color.Transparent,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Added to watchlist",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                
                BottomBar() // Reusing See Watchlist Bar
            }
        }
    }
}

@Composable
fun DetailStatRow(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            color = Color.Gray,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = value,
            color = Color.Gray,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun AnimeDetailsPreview() {
    MyApplicationTheme(darkTheme = true) {
        AnimeDetailsScreen(
            1,
            "Initial D Fourth Stage", 
            "頭文字 〈イニシャル〉 D FOURTH STAGE", 
            "",
            "",
            listOf("Action", "Drama"),
            listOf("Racing", "Cars"),
            false
        )
    }
}

