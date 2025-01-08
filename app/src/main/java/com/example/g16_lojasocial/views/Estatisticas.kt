package com.example.g16_lojasocial.views



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.nativeCanvas


@Composable
fun Estatisticas(
    modifier: Modifier = Modifier,
    viewsViewModel: ViewsViewModel = viewModel()
) {
    val artigosLevadosCount by viewsViewModel.artigosLevadosCount.observeAsState(initial = 0)
    val nacionalidadeCounts by viewsViewModel.nacionalidadeCounts.observeAsState(emptyMap())
    val artigosByHour by viewsViewModel.artigosByHour.observeAsState(initial = emptyMap())

    LaunchedEffect(Unit) {
        viewsViewModel.loadArtigosLevadosCount()
        viewsViewModel.loadNacionalidadeCounts()
        viewsViewModel.loadArtigosByHour()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF)),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .shadow(
                    elevation = 20.dp,
                    spotColor = Color(0xFF000000).copy(alpha = 0.35f),
                    shape = RoundedCornerShape(5.dp),
                )
                .clip(RoundedCornerShape(5.dp))
                .background(Color.White),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Text(
                text = "Artigos Levados (Total): $artigosLevadosCount",
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(16.dp)
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .shadow(
                    elevation = 20.dp,
                    spotColor = Color(0xFF000000).copy(alpha = 0.35f),
                    shape = RoundedCornerShape(5.dp),
                )
                .clip(RoundedCornerShape(5.dp))
                .background(Color.White),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Horas mais ativas",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                if (artigosByHour.isNotEmpty()) {
                    LineChart(data = artigosByHour)
                } else {
                    Text(
                        text = "Carregando dados...",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .shadow(
                    elevation = 20.dp,
                    spotColor = Color(0xFF000000).copy(alpha = 0.35f),
                    shape = RoundedCornerShape(5.dp),
                )
                .clip(RoundedCornerShape(5.dp))
                .background(Color.White),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Beneficiarios (Nacionalidades)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                if (nacionalidadeCounts.isNotEmpty()) {
                    PieChart(data = nacionalidadeCounts)
                } else {
                    Text(
                        text = "Carregando dados...",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}


@Composable
fun PieChart(
    data: Map<String, Int>,
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(
        Color(0xFF1F77B4),
        Color(0xFFFF7F0E),
        Color(0xFF2CA02C),
        Color(0xFFD62728),
        Color(0xFF9467BD),
        Color(0xFF8C564B),
        Color(0xFFE377C2),
        Color(0xFF7F7F7F),
        Color(0xFFBCBD22),
        Color(0xFF17BECF)
    )
) {
    val total = data.values.sum()
    var startAngle = 0f

    Canvas(modifier = modifier.size(300.dp)) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val radius = size.minDimension / 2

        data.entries.forEachIndexed { index, entry ->
            val sweepAngle = 360f * (entry.value / total.toFloat())
            var color = colors[index % colors.size]

            // Draw pie slice
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(
                    (canvasWidth - 2 * radius) / 2,
                    (canvasHeight - 2 * radius) / 2
                ),
                size = Size(2 * radius, 2 * radius)
            )

            // Draw labels and counts
            val labelAngle = startAngle + sweepAngle / 2
            val labelRadius = radius * 0.7f
            val labelX = (canvasWidth / 2) + labelRadius * cos(labelAngle * PI / 180).toFloat()
            val labelY = (canvasHeight / 2) + labelRadius * sin(labelAngle * PI / 180).toFloat()

            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    entry.key,
                    labelX,
                    labelY,
                    android.graphics.Paint().apply {
                        color = Color.Black
                        textSize = 14.sp.toPx()
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
                drawText(
                    entry.value.toString(),
                    labelX,
                    labelY + 20.dp.toPx(),
                    android.graphics.Paint().apply {
                        color = Color.Black
                        textSize = 12.sp.toPx()
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
            }

            startAngle += sweepAngle
        }
    }
}

@Composable
fun LineChart(data: Map<Int, Int>) {
    val sortedData = data.toSortedMap() // Sort by hour
    val maxCount = sortedData.values.maxOrNull() ?: 1

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val barWidth = canvasWidth / 9f // Divide canvas width into 24 hours

        // Draw axes
        drawLine(
            color = Color.Black,
            start = Offset(0f, canvasHeight),
            end = Offset(canvasWidth, canvasHeight),
            strokeWidth = 2f
        )
        drawLine(
            color = Color.Black,
            start = Offset(0f, 0f),
            end = Offset(0f, canvasHeight),
            strokeWidth = 2f
        )

        // Calculate points for the line chart
        val points = sortedData.entries.mapIndexed { index, entry ->
            val x = index * barWidth
            val y = canvasHeight - (entry.value / maxCount.toFloat() * canvasHeight)
            Offset(x, y)
        }

        // Draw the lines between points
        points.zipWithNext { start, end ->
            drawLine(
                color = Color.Blue,
                start = start,
                end = end,
                strokeWidth = 4f
            )
        }

        // Draw points and labels
        sortedData.entries.forEachIndexed { index, (hour, count) ->
            val x = index * barWidth
            val y = canvasHeight - (count / maxCount.toFloat() * canvasHeight)

            // Draw circle for each point
            drawCircle(
                color = Color.Red,
                center = Offset(x, y),
                radius = 5f
            )

            // Draw hour label below each point
            drawContext.canvas.nativeCanvas.drawText(
                hour.toString(),
                x,
                canvasHeight + 20,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 20f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }
    }
}