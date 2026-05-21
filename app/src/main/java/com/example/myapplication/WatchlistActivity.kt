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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.myapplication.data.AnimeViewModel
import com.example.myapplication.data.AnimeWithDetails
import com.example.myapplication.toUiModel
import com.example.myapplication.ui.theme.MyApplicationTheme

class WatchlistActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                WatchlistScreen()
            }
        }
    }
}

@Composable
fun WatchlistScreen(viewModel: AnimeViewModel = viewModel()) {
    val context = LocalContext.current

    val watchlistEntities by viewModel.watchlist.collectAsState(initial = emptyList())
    val watchlist = watchlistEntities.map { it.toUiModel() }

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
                
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item { 
                        Text(
                            text = "My Watchlist",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(watchlist) { anime ->
                        WatchlistCard(anime, viewModel)
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
                
                BottomBackBar {
                    context.startActivity(Intent(context, MainActivity::class.java))
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun WatchlistCard(anime: Anime, viewModel: AnimeViewModel) {
    val context = LocalContext.current
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

                // ✅ Completed badge overlay on the image
                if (anime.isCompleted) {
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(110.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Surface(
                            color = Color(0xFF12B76A),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "✓ Done",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                                    .fillMaxWidth(),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
                
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
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = anime.subTitle,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        
                        Text(
                            text = anime.genres.firstOrNull() ?: "",
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.labelSmall
                        )
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
            
            // Remove button
            Button(
                onClick = { viewModel.toggleWatchlist(anime.id, false) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                shape = RoundedCornerShape(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.button_onoff_indicator_off),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Remove From Watchlist",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Mark as complete / unmark button
            Button(
                onClick = { viewModel.toggleCompleted(anime.id, !anime.isCompleted) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                shape = RoundedCornerShape(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (anime.isCompleted) Color(0xFF344054) else Color(0xFF12B76A)
                )
            ) {
                Icon(
                    imageVector = if (anime.isCompleted) Icons.Default.Add else Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (anime.isCompleted) "Mark as Watching" else "Mark as Complete",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun BottomBackBar(onBack: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onBack() }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Back to Discover",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun WatchlistScreenPreview() {
    MyApplicationTheme(darkTheme = true) {
        WatchlistScreen()
    }
}
