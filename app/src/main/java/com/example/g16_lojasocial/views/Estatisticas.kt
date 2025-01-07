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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.nativeCanvas


@Composable
fun Estatisticas(
    modifier: Modifier = Modifier,
    viewsViewModel: ViewsViewModel = viewModel()
) {
    // Collect the LiveData state as Compose State
    val artigosLevadosCount by viewsViewModel.artigosLevadosCount.observeAsState(initial = 0)
    val nacionalidadeCounts by viewsViewModel.nacionalidadeCounts.observeAsState(emptyMap())

    // Trigger data loading when the composable is launched
    LaunchedEffect(Unit) {
        viewsViewModel.loadArtigosLevadosCount()
        viewsViewModel.loadNacionalidadeCounts()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display the count of artigos levados
        Text(
            text = "Número de Artigos Levados: $artigosLevadosCount",
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Pie chart das nacionalidades",
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
        // Display the pie chart if nacionalidade data is available
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

            // Calculate label position for the nacionalidade name
            val labelAngle = startAngle + sweepAngle / 2
            val labelRadius = radius * 0.7f
            val labelX = (canvasWidth / 2) + labelRadius * cos(labelAngle * PI / 180).toFloat()
            val labelY = (canvasHeight / 2) + labelRadius * sin(labelAngle * PI / 180).toFloat()

            drawIntoCanvas { canvas ->
                val paint = android.graphics.Paint().apply {
                    color = Color.Black // Correct color usage
                    textSize = 14.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                canvas.nativeCanvas.drawText(
                    entry.key,
                    labelX,
                    labelY,
                    paint
                )
            }

            // Draw the count next to the nacionalidade
            val countX = (canvasWidth / 2) + labelRadius * cos(labelAngle * PI / 180).toFloat()
            val countY = (canvasHeight / 2) + labelRadius * sin(labelAngle * PI / 180).toFloat() + 20.dp.toPx()

            drawIntoCanvas { canvas ->
                val paint = android.graphics.Paint().apply {
                    color = Color.Black
                    textSize = 12.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                canvas.nativeCanvas.drawText(
                    entry.value.toString(),
                    countX,
                    countY,
                    paint
                )
            }

            startAngle += sweepAngle
        }
    }
}