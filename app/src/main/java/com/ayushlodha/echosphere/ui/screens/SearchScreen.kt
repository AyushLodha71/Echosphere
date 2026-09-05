package com.ayushlodha.echosphere.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.ayushlodha.echosphere.data.remote.toSong
import com.ayushlodha.echosphere.viewmodel.PlayerViewModel
import com.ayushlodha.echosphere.viewmodel.SearchViewModel

@Composable
fun SearchScreen(
    playerViewModel: PlayerViewModel,
    searchViewModel: SearchViewModel = viewModel()
) {
    var queryText by remember { mutableStateOf("") }

    val results by searchViewModel.results.collectAsState()
    val status by searchViewModel.status.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        OutlinedTextField(
            value = queryText,
            onValueChange = { queryText = it },
            label = { Text("Search songs") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = { searchViewModel.search(queryText) }
            )
        )

        if (status.isNotEmpty()) {
            Text(
                text = status,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        LazyColumn(modifier = Modifier.padding(top = 12.dp)) {
            items(results) { result ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val songs = results.map { it.toSong() }
                            playerViewModel.play(songs, results.indexOf(result))
                        }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = "https://i.ytimg.com/vi/${result.thumbnailId}/hqdefault.jpg",
                        contentDescription = result.title,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = result.title)
                        Text(text = result.artist)
                    }
                }
            }
        }
    }
}