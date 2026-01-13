// ui/components/SearchBar.kt (new)
package com.mubashshir.lokalmusic.ui.screens.search

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mubashshir.lokalmusic.ui.theme.CornerMedium
import com.mubashshir.lokalmusic.ui.theme.PaddingSmall
import com.mubashshir.lokalmusic.ui.theme.PrimaryOrange

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
)
{
    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search") },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = null
            )
        },
        trailingIcon = {
            if (query.isNotEmpty())
            {
                IconButton(onClick = onClear) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = null
                    )
                }
            }
        },
        shape = RoundedCornerShape(CornerMedium),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = PrimaryOrange,
            unfocusedIndicatorColor = PrimaryOrange
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(PaddingSmall)
    )
}