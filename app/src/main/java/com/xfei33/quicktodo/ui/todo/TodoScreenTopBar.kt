package com.xfei33.quicktodo.ui.todo

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.xfei33.quicktodo.components.SearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    onSearchTextChange: (String) -> Unit
) {
    var isSearchBarVisible by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            if (isSearchBarVisible) {
                SearchBar(
                    onSearchTextChange = { searchText ->
                        onSearchTextChange(searchText)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = "今日",
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        },
        actions = {
            IconButton(onClick = {
                isSearchBarVisible = !isSearchBarVisible
                if (!isSearchBarVisible) {
                    onSearchTextChange("")
                }
            }) {
                if (isSearchBarVisible) {
                    Icon(Icons.Default.Close, contentDescription = "Close search bar")
                } else {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            }
        }
    )
}

@Preview
@Composable
fun AppTopBarPreview() {
    AppTopBar(onSearchTextChange = { /*TODO*/ })
}
