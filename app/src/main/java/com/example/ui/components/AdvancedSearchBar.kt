package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.utils.evaluateMath

@Composable
fun AdvancedSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    fontFamily: FontFamily,
    onSearchWeb: (String) -> Unit,
    isSearchFocused: Boolean,
    onFocusChanged: (Boolean) -> Unit,
    onSearchExecute: () -> Unit = {}
) {
    val _onSearchWeb = onSearchWeb // Suppress unused warning
    val focusManager = LocalFocusManager.current
    val mathResult = remember(query) { if (query.isNotEmpty()) evaluateMath(query) else "" }
    val isMath = mathResult != "Error" && mathResult != "Enter simple math (e.g. 15 * 6)" && query.any { it in "+-*/" }
    
    val density = LocalDensity.current
    val isImeVisible = WindowInsets.ime.getBottom(density) > 0
    val targetBottomPadding = if (isImeVisible) 20.dp else 12.dp

    // Design states based on whether search is focused or query is non-empty
    val isSearchActive = isSearchFocused || query.isNotEmpty()

    // Margins and corner radius animations
    val horizontalMargin by animateDpAsState(
        targetValue = if (isSearchActive) 12.dp else 24.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
    )
    val cornerRadius by animateDpAsState(
        targetValue = if (isSearchActive) 16.dp else 28.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
    )

    // Unified Styling per instructions
    val containerBackground = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
    val activeTextColor = MaterialTheme.colorScheme.onSurface
    val placeholderHintColor = MaterialTheme.colorScheme.onSurfaceVariant
    val borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .imePadding()
            .padding(start = horizontalMargin, end = horizontalMargin, top = 6.dp, bottom = targetBottomPadding)
    ) {
        if (query.isNotEmpty() && isMath) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh), 
                shape = RoundedCornerShape(16.dp), 
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Calculator Result: ", fontSize = 11.sp, color = placeholderHintColor)
                        Text("$query = $mathResult", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(elevation = if (isSearchActive) 12.dp else 0.dp, shape = RoundedCornerShape(cornerRadius))
                .border(1.dp, borderColor, RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            color = containerBackground
        ) {
            androidx.compose.material3.TextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxSize()
                    .onFocusChanged { onFocusChanged(it.isFocused) },
                textStyle = TextStyle(fontFamily = fontFamily, fontSize = 16.sp, color = activeTextColor),
                placeholder = {
                    Text(
                        text = "Search contacts, apps, web...",
                        fontSize = 15.sp,
                        fontFamily = fontFamily,
                        color = placeholderHintColor.copy(alpha = 0.6f)
                    )
                },
                leadingIcon = {
                    if (isSearchActive) {
                        IconButton(onClick = { onQueryChange(""); focusManager.clearFocus(); onFocusChanged(false) }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = placeholderHintColor, modifier = Modifier.size(20.dp))
                        }
                    } else {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = placeholderHintColor, modifier = Modifier.size(20.dp))
                    }
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = placeholderHintColor, modifier = Modifier.size(20.dp))
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(32.dp)
                                .background(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), shape = CircleShape)
                                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "MT", color = MaterialTheme.colorScheme.primary, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, fontFamily = fontFamily)
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearchExecute() }),
                colors = androidx.compose.material3.TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )
        }
    }
}
