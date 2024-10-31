package com.wahyusembiring.ui.component.tab

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrimaryTab(
   titles: List<String>,
   selectedTabState: MutableIntState,
) {
   PrimaryTabRow(selectedTabIndex = selectedTabState.intValue) {
      titles.forEachIndexed { index, title ->
         Tab(
            selected = selectedTabState.intValue == index,
            onClick = { selectedTabState.intValue = index },
            text = { Text(text = title, maxLines = 2, overflow = TextOverflow.Ellipsis) }
         )
      }
   }
}