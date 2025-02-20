package com.xfei33.quicktodo.ui.todo

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.xfei33.quicktodo.components.SearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    showTodayOnly: Boolean,
    onToggleView: () -> Unit,
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
                TextButton(
                    onClick = onToggleView,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onBackground
                    )
                ) {
                    Text(
                        text = if (showTodayOnly) "今日" else "日程",
                        style = MaterialTheme.typography.headlineLarge,
                        overflow = TextOverflow.Ellipsis
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "切换视图",
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
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
                    Icon(Icons.Default.Close, contentDescription = "关闭搜索")
                } else {
                    Icon(Icons.Default.Search, contentDescription = "搜索")
                }
            }
        }
    )
}
