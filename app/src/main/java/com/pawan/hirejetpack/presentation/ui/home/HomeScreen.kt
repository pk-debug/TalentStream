package com.pawan.hirejetpack.presentation.ui.home

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.pawan.hirejetpack.presentation.state.HomeViewModel

/**
 * [HomeScreenContent] — the Job Feed tab's body: search bar + filters + job list.
 *
 * Staff note: Added a [FilterChip] row and [SearchHistory] display.
 */
@Composable
fun HomeScreenContent(
    viewModel: HomeViewModel,
    onJobClick: (String) -> Unit
) {
    val homeState by viewModel.uiState.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        JobSearchField(
            onQueryChanged = viewModel::onSearchQueryChanged,
            onSearchTriggered = viewModel::onSearchTriggered,
            onClear = viewModel::onClearSearch
        )

        // Filter Tags Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(homeState.availableTags) { tag ->
                val isSelected = tag in homeState.selectedTags
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.toggleTag(tag) },
                    label = { Text(tag) },
                    leadingIcon = if (isSelected) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = null,
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else null
                )
            }
        }

        when {
            homeState.jobs.isNotEmpty() -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(homeState.jobs, key = { it.id }) { job ->
                        JobCard(
                            job = job,
                            isBookmarked = job.id in homeState.bookmarkedIds,
                            onClick = { onJobClick(job.id) },
                            onBookmarkClick = { viewModel.toggleBookmark(job.id) }
                        )
                    }
                }
            }

            homeState.searchQuery.isBlank() && recentSearches.isNotEmpty() -> {
                SearchHistory(
                    history = recentSearches,
                    onSearchClick = { query ->
                        viewModel.onSearchQueryChanged(query)
                        viewModel.onSearchTriggered(query)
                    }
                )
            }

            homeState.searchQuery.isNotBlank() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No jobs match \"${homeState.searchQuery}\".")
                }
            }

            else -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No jobs available right now.")
                }
            }
        }
    }
}

/**
 * [SearchHistory] — shows recent search terms.
 */
@Composable
private fun SearchHistory(
    history: List<String>,
    onSearchClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Recent Searches",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        history.forEach { query ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.History,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(
                    onClick = { onSearchClick(query) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = query,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

/**
 * [JobSearchField] — updated to handle [onSearchTriggered].
 */
@Composable
private fun JobSearchField(
    onQueryChanged: (String) -> Unit,
    onSearchTriggered: (String) -> Unit,
    onClear: () -> Unit
) {
    var text by remember { mutableStateOf("") }

    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
            onQueryChanged(it)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text("Search title, company, location, skill...") },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        trailingIcon = {
            if (text.isNotEmpty()) {
                IconButton(onClick = {
                    text = ""
                    onClear()
                }) {
                    Icon(Icons.Filled.Clear, contentDescription = "Clear search")
                }
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            onSearchTriggered(text)
        })
    )
}