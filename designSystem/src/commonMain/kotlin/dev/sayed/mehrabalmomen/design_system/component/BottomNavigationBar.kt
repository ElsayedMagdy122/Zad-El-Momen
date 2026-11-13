package dev.sayed.mehrabalmomen.design_system.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import dev.sayed.mehrabalmomen.design_system.theme.MehrabTheme
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.ic_home_selected
import dev.sayed.mehrabalmomen.designsystem.resources.ic_home_not_selected
import dev.sayed.mehrabalmomen.designsystem.resources.ic_azan_selected
import dev.sayed.mehrabalmomen.designsystem.resources.ic_azan_not_selected
import dev.sayed.mehrabalmomen.designsystem.resources.ic_radio_selected
import dev.sayed.mehrabalmomen.designsystem.resources.ic_radio_not_selected
import org.jetbrains.compose.resources.painterResource


@Composable
fun BottomNavigationBar(
    items: List<NavItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier.height(74.dp)
    ) {

        val itemWidth = maxWidth / items.size
        val indicatorWidth = itemWidth - 40.dp
        val indicatorOffset by animateDpAsState(
            targetValue = selectedIndex * itemWidth,
            label = ""
        )

        Row(
            Modifier
                .fillMaxWidth()
                .background(Theme.color.surfaces.surfaceLow)
        ) {
            items.forEachIndexed { index, item ->

                val isSelected = index == selectedIndex
                val iconTint by animateColorAsState(
                    targetValue = if (isSelected)
                        Theme.color.primary.primary
                    else
                        Theme.color.secondary.shadeSecondary,
                    label = ""
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(76.dp)
                        .clickable(
                            enabled = !isSelected,
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            onItemSelected(index)
                        },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Icon(
                        painter = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title,
                        modifier = Modifier.size(24.dp),
                        tint = iconTint
                    )

                    if (isSelected) {
                        Text(
                            text = item.title,
                            style = Theme.textStyle.label.medium,
                            color = Theme.color.primary.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            softWrap = false
                        )
                    }

                }
            }
        }

        Box(
            Modifier
                .padding(horizontal = 20.dp)
                .offset(x = indicatorOffset)
                .clip(
                    RoundedCornerShape(
                        bottomStart = 12.dp,
                        bottomEnd = 12.dp
                    )
                )
                .background(color = Theme.color.primary.primary)
                .size(indicatorWidth, 4.dp)
        )
    }
}

data class NavItem(
    val title: String,
    val selectedIcon: Painter,
    val unselectedIcon: Painter
)

@Composable
private fun PreviewBottomNavigationBar() {
    MehrabTheme(isDarkTheme = false) {

        val items = listOf(
            NavItem(
                title = "Home",
                selectedIcon = painterResource(Res.drawable.ic_home_selected),
                unselectedIcon = painterResource(Res.drawable.ic_home_not_selected)
            ),
            NavItem(
                title = "Azan",
                selectedIcon = painterResource(Res.drawable.ic_azan_selected),
                unselectedIcon = painterResource(Res.drawable.ic_azan_not_selected)
            ),
            NavItem(
                title = "Radio",
                selectedIcon = painterResource(Res.drawable.ic_radio_selected),
                unselectedIcon = painterResource(Res.drawable.ic_radio_not_selected)
            )
        )

        var selectedIndex by remember { mutableIntStateOf(0) }

        BottomNavigationBar(
            items = items,
            selectedIndex = selectedIndex,
            onItemSelected = { selectedIndex = it },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        )
    }
}
