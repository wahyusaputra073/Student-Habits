package com.wahyusembiring.ui.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.wahyusembiring.ui.R
import com.wahyusembiring.ui.theme.spacing

@Composable
fun ChooseColorButton(
   color: Color,
   onClick: () -> Unit
) {
   ListItem(
      modifier = Modifier
         .clickable(onClick = onClick),
      colors = ListItemDefaults.colors(containerColor = Color.Transparent),
      leadingContent = {
         Box(
            modifier = Modifier
               .size(MaterialTheme.spacing.Medium)
               .background(
                  color = color,
                  shape = RoundedCornerShape(50)
               )
               .clip(RoundedCornerShape(50))
               .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(50))
         )
      },
      headlineContent = {
         Text(
            text = stringResource(R.string.color),
            color = color,
//            style = TextStyle(
//               shadow = Shadow(
//                  color = Color.Black,
//                  offset = Offset(2f, 2f),
//                  blurRadius = 4f
//               )
//            )
         )
      }
   )
}