package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.db.SensoryLogEntity
import com.example.ui.theme.BatteryHigh
import com.example.ui.theme.BatteryLow
import com.example.ui.theme.BatteryMedium

@Composable
fun SensoryHistoryChartCard(sensoryLogs: List<SensoryLogEntity>) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant

    // Sort logs chronologically for the chart (oldest to newest)
    val sortedLogs = remember(sensoryLogs) {
        sensoryLogs.sortedBy { it.timestamp }.takeLast(10)
    }

    val avgEnergy = if (sortedLogs.isNotEmpty()) sortedLogs.map { it.energyPercent }.average().toInt() else 0
    val mostCommonState = if (sortedLogs.isNotEmpty()) {
        sortedLogs.groupingBy { it.sensoryState }.eachCount().maxByOrNull { it.value }?.key ?: "Balanced"
    } else "No Data"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("sensory_history_chart_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Timeline,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(26.dp)
                    )
                    Text(
                        text = "Energy Trend & Patterns",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (sortedLogs.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = primaryColor.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "Avg: $avgEnergy%",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            if (sortedLogs.size < 2) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Log at least 2 check-ins to view weekly energy trend chart.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // Canvas Line Chart
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .testTag("sensory_line_chart_canvas")
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val width = size.width
                        val height = size.height
                        val paddingBottom = 24.dp.toPx()
                        val paddingTop = 16.dp.toPx()
                        val chartHeight = height - paddingBottom - paddingTop

                        val points = sortedLogs.mapIndexed { index, log ->
                            val x = if (sortedLogs.size == 1) width / 2f else index * (width / (sortedLogs.size - 1))
                            val normalizedEnergy = (log.energyPercent - 10f) / 90f // 10 to 100
                            val y = paddingTop + chartHeight * (1f - normalizedEnergy.coerceIn(0f, 1f))
                            Offset(x, y)
                        }

                        // Draw horizontal grid lines (25%, 50%, 75%, 100%)
                        val gridColor = surfaceVariant.copy(alpha = 0.6f)
                        for (i in 0..3) {
                            val fraction = i / 3f
                            val y = paddingTop + chartHeight * (1f - fraction)
                            drawLine(
                                color = gridColor,
                                start = Offset(0f, y),
                                end = Offset(width, y),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        // Build path for gradient fill and line
                        val path = Path().apply {
                            if (points.isNotEmpty()) {
                                moveTo(points.first().x, points.first().y)
                                for (i in 0 until points.size - 1) {
                                    val p1 = points[i]
                                    val p2 = points[i + 1]
                                    val controlPoint1 = Offset((p1.x + p2.x) / 2f, p1.y)
                                    val controlPoint2 = Offset((p1.x + p2.x) / 2f, p2.y)
                                    cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, p2.x, p2.y)
                                }
                            }
                        }

                        // Draw gradient fill under curve
                        val fillPath = Path().apply {
                            addPath(path)
                            if (points.isNotEmpty()) {
                                lineTo(points.last().x, height - paddingBottom)
                                lineTo(points.first().x, height - paddingBottom)
                                close()
                            }
                        }

                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    primaryColor.copy(alpha = 0.35f),
                                    primaryColor.copy(alpha = 0.02f)
                                ),
                                startY = paddingTop,
                                endY = height - paddingBottom
                            )
                        )

                        // Draw main line
                        drawPath(
                            path = path,
                            color = primaryColor,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Draw data points
                        points.forEachIndexed { index, point ->
                            val log = sortedLogs[index]
                            val dotColor = when {
                                log.energyPercent >= 70 -> BatteryHigh
                                log.energyPercent >= 40 -> BatteryMedium
                                else -> BatteryLow
                            }
                            drawCircle(
                                color = Color.White,
                                radius = 6.dp.toPx(),
                                center = point
                            )
                            drawCircle(
                                color = dotColor,
                                radius = 4.dp.toPx(),
                                center = point
                            )
                        }
                    }
                }

                // Insights Footer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Most Frequent State",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = mostCommonState,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Recorded Check-ins",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${sortedLogs.size} entries",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
