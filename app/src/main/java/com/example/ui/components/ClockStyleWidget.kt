package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ClockStyleWidget(
    style: String, 
    fontFamily: FontFamily, 
    primaryColor: Color, 
    isLocationGranted: Boolean,
    use24HourFormat: Boolean,
    useFahrenheit: Boolean,
    contentColor: Color = Color.White   // adaptive: dark on light wallpaper, light on dark
) {
    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }
    
    LaunchedEffect(use24HourFormat) {
        while (true) {
            val cal = Calendar.getInstance()
            currentTime = if (use24HourFormat) {
                String.format(Locale.getDefault(), "%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
            } else {
                val hr12 = cal.get(Calendar.HOUR)
                val hourActual = if (hr12 == 0) 12 else hr12
                val ampm = if (cal.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"
                String.format(Locale.getDefault(), "%d:%02d %s", hourActual, cal.get(Calendar.MINUTE), ampm)
            }
            val dayAbbr = when (cal.get(Calendar.DAY_OF_WEEK)) {
                Calendar.SUNDAY -> "Sun"
                Calendar.MONDAY -> "Mon"
                Calendar.TUESDAY -> "Tue"
                Calendar.WEDNESDAY -> "Wed"
                Calendar.THURSDAY -> "Thu"
                Calendar.FRIDAY -> "Fri"
                Calendar.SATURDAY -> "Sat"
                else -> ""
            }
            val monthAbbr = when (cal.get(Calendar.MONTH)) {
                Calendar.JANUARY -> "Jan"
                Calendar.FEBRUARY -> "Feb"
                Calendar.MARCH -> "Mar"
                Calendar.APRIL -> "Apr"
                Calendar.MAY -> "May"
                Calendar.JUNE -> "Jun"
                Calendar.JULY -> "Jul"
                Calendar.AUGUST -> "Aug"
                Calendar.SEPTEMBER -> "Sep"
                Calendar.OCTOBER -> "Oct"
                Calendar.NOVEMBER -> "Nov"
                Calendar.DECEMBER -> "Dec"
                else -> ""
            }
            currentDate = "$dayAbbr, $monthAbbr ${cal.get(Calendar.DAY_OF_MONTH)}"
            delay(1000)
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (style) {
            "Dextera Date" -> {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = currentDate,
                            fontSize = 16.sp,
                            fontFamily = fontFamily,
                            color = contentColor,
                            style = TextStyle(shadow = Shadow(Color.Black.copy(alpha = 0.6f), Offset(1f, 2f), 4f))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("☀️", fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (useFahrenheit) {
                             if (isLocationGranted) "70°F | Sunny" else "75°F | Sunny"
                        } else {
                             if (isLocationGranted) "21°C | Sunny" else "24°C | Sunny"
                        },
                        fontSize = 14.sp,
                        fontFamily = fontFamily,
                        color = contentColor.copy(alpha = 0.7f),
                        style = TextStyle(shadow = Shadow(Color.Black.copy(alpha = 0.6f), Offset(1f, 2f), 4f))
                    )
                }
            }
            "Minimal Digital" -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = currentTime,
                        fontSize = 54.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        color = contentColor,
                        textAlign = TextAlign.Center,
                        style = TextStyle(shadow = Shadow(Color.Black.copy(alpha = 0.6f), Offset(1f, 2f), 4f))
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = currentDate,
                            fontSize = 15.sp,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Medium,
                            color = contentColor.copy(alpha = 0.9f),
                            style = TextStyle(shadow = Shadow(Color.Black.copy(alpha = 0.6f), Offset(1f, 2f), 4f))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(if (isLocationGranted) Color(0xFFFFB74D) else Color(0xFFFBC02D), RoundedCornerShape(6.dp))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (useFahrenheit) {
                                if (isLocationGranted) "Local · 70°F" else "75°F"
                            } else {
                                if (isLocationGranted) "Local · 21°C" else "24°C"
                            },
                            fontSize = 15.sp,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Medium,
                            color = contentColor.copy(alpha = 0.9f),
                            style = TextStyle(shadow = Shadow(Color.Black.copy(alpha = 0.6f), Offset(1f, 2f), 4f))
                        )
                    }
                }
            }
            "Bold Accent" -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val parts = currentTime.split(" ")
                    val mainTime = parts.getOrNull(0) ?: currentTime
                    val ampmSuffix = parts.getOrNull(1) ?: ""
                    
                    val tParts = mainTime.split(":")
                    val hr = tParts.getOrNull(0) ?: "00"
                    val min = tParts.getOrNull(1) ?: "00"
                    
                    val shadow = Shadow(Color.Black.copy(alpha = 0.6f), Offset(1f, 2f), 4f)

                    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.Center) {
                        Text(hr, fontSize = 48.sp, fontFamily = fontFamily, fontWeight = FontWeight.ExtraBold, color = primaryColor, style = TextStyle(shadow = shadow))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(min, fontSize = 32.sp, fontFamily = fontFamily, fontWeight = FontWeight.Bold, color = contentColor, style = TextStyle(shadow = shadow))
                        if (ampmSuffix.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(ampmSuffix, fontSize = 16.sp, fontFamily = fontFamily, fontWeight = FontWeight.SemiBold, color = contentColor.copy(alpha = 0.7f), style = TextStyle(shadow = shadow))
                        }
                    }
                    Text(currentDate, fontSize = 11.sp, fontFamily = fontFamily, fontWeight = FontWeight.Light, color = contentColor.copy(alpha = 0.5f), textAlign = TextAlign.Center, style = TextStyle(shadow = shadow))
                    Text(
                        text = if (useFahrenheit) {
                            if (isLocationGranted) "Local Weather · 70°F · Sunny" else "New York · 72°F · Partly Cloudy"
                        } else {
                            if (isLocationGranted) "Local Weather · 21°C · Sunny" else "New York · 22°C · Partly Cloudy"
                        },
                        fontSize = 11.sp,
                        fontFamily = fontFamily,
                        color = contentColor.copy(alpha = 0.45f),
                        modifier = Modifier.padding(top = 2.dp),
                        textAlign = TextAlign.Center,
                        style = TextStyle(shadow = shadow)
                    )
                }
            }
            "Classic Analog" -> {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    var angleSec by remember { mutableIntStateOf(0) }
                    var angleMin by remember { mutableIntStateOf(0) }
                    var angleHr by remember { mutableStateOf(0f) }
                    LaunchedEffect(Unit) {
                        while (true) {
                            val cal = Calendar.getInstance()
                            angleSec = cal.get(Calendar.SECOND) * 6
                            angleMin = cal.get(Calendar.MINUTE) * 6
                            angleHr = cal.get(Calendar.HOUR) * 30f + cal.get(Calendar.MINUTE) * 0.5f
                            delay(1000)
                        }
                    }
                    Canvas(modifier = Modifier.size(54.dp)) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val radius = size.width / 2f
                        drawCircle(Color.White.copy(alpha = 0.12f), radius = radius, center = center)
                        drawCircle(primaryColor, radius = radius, center = center, style = Stroke(width = 1.5.dp.toPx()))
                        drawCircle(Color.White, radius = 2.dp.toPx(), center = center)
                        val hrLen = radius * 0.5f
                        val hrRad = Math.toRadians(angleHr.toDouble() - 90)
                        drawLine(Color.White, start = center, end = Offset((center.x + hrLen * cos(hrRad)).toFloat(), (center.y + hrLen * sin(hrRad)).toFloat()), strokeWidth = 3.dp.toPx())
                        val minLen = radius * 0.75f
                        val minRad = Math.toRadians(angleMin.toDouble() - 90)
                        drawLine(Color.White, start = center, end = Offset((center.x + minLen * cos(minRad)).toFloat(), (center.y + minLen * sin(minRad)).toFloat()), strokeWidth = 1.5.dp.toPx())
                    }
                    val shadow = Shadow(Color.Black.copy(alpha = 0.6f), Offset(1f, 2f), 4f)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(currentTime, fontSize = 20.sp, fontFamily = fontFamily, fontWeight = FontWeight.Bold, color = contentColor, style = TextStyle(shadow = shadow))
                        Text(currentDate, fontSize = 10.sp, fontFamily = fontFamily, color = Color.White.copy(alpha = 0.6f), style = TextStyle(shadow = shadow))
                        Text(
                            text = if (useFahrenheit) "New York · 72°F · Partly Cloudy" else "New York · 22°C · Partly Cloudy",
                            fontSize = 10.sp,
                            fontFamily = fontFamily,
                            color = primaryColor.copy(alpha = 0.85f),
                            style = TextStyle(shadow = shadow)
                        )
                    }
                }
            }
            "Typographic Word" -> {
                val cal = Calendar.getInstance()
                val hourNames = listOf("twelve", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven")
                val tens = listOf("", "ten", "twenty", "thirty", "forty", "fifty")
                val hr = cal.get(Calendar.HOUR)
                val min = cal.get(Calendar.MINUTE)
                val ampm = if (cal.get(Calendar.AM_PM) == Calendar.AM) "morning" else "afternoon"
                
                val hrWord = hourNames[hr % 12]
                val minWordText = if (min == 0) "o'clock" else if (min in 1..9) "oh ${hourNames[min]}" else if (min in 10..19) {
                    val teens = listOf("ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen")
                    teens[min - 10]
                } else {
                    val t = tens[min / 10]
                    val rem = min % 10
                    val remWord = if (rem > 0) " " + hourNames[rem] else ""
                    "$t$remWord"
                }
                
                val shadow = Shadow(Color.Black.copy(alpha = 0.6f), Offset(1f, 2f), 4f)

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("It's $hrWord $minWordText", fontSize = 16.sp, fontFamily = fontFamily, fontWeight = FontWeight.Bold, color = contentColor, textAlign = TextAlign.Center, style = TextStyle(shadow = shadow))
                    Text("in the $ampm", fontSize = 13.sp, fontFamily = fontFamily, color = Color.White.copy(alpha = 0.6f), textAlign = TextAlign.Center, style = TextStyle(shadow = shadow))
                    Text(currentDate, fontSize = 10.sp, fontFamily = fontFamily, color = primaryColor, letterSpacing = 1.sp, modifier = Modifier.padding(top = 1.dp), textAlign = TextAlign.Center, style = TextStyle(shadow = shadow))
                    Text(
                        text = if (useFahrenheit) "New York · 72°F · Partly Cloudy" else "New York · 22°C · Partly Cloudy",
                        fontSize = 10.sp,
                        fontFamily = fontFamily,
                        color = contentColor.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center,
                        style = TextStyle(shadow = shadow)
                    )
                }
            }
        }
    }
}
