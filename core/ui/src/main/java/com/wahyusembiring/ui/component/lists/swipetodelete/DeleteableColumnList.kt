package com.wahyusembiring.ui.component.lists.swipetodelete

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.wahyusembiring.ui.theme.spacing

@Composable
fun <T> DeleteableColumnList(
   items: List<T>,
   key: ((index: Int) -> Any)? = null,
   onItemDeleteRequest: (item: T) -> Unit,
   content: @Composable (item: T) -> Unit
) {
   LazyColumn(
      modifier = Modifier.fillMaxSize()
   ) {
      items(
         count = items.size,
         key = key
      ) {
         SwipeToDeleteContainer(
            item = items[it],
            onDelete = { item ->
               onItemDeleteRequest(item)
            },
            content = content
         )
         HorizontalDivider(modifier = Modifier.padding(horizontal = MaterialTheme.spacing.Large))
      }
   }
}