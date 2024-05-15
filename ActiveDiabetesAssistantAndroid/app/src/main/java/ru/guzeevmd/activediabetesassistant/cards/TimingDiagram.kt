package ru.guzeevmd.activediabetesassistant.cards
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import ru.guzeevmd.activediabetesassistant.data.models.GlucoseInfoViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.max




@Composable
fun TimingDiagram(glucoseInfoList: List<GlucoseInfoViewModel>, labelCount: Int) {
    val padding = 16.dp
    val strokeWidth = 2.dp
    val glucoseColor = Color.Red
    val stepsColor = Color.Green
    val textColor = MaterialTheme.colorScheme.primary

    Column(modifier = Modifier.padding(padding)) {
        Text("Диаграмма", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(glucoseColor)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Уровень глюкозы", color = glucoseColor, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.width(16.dp))

            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(stepsColor)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Количество шагов", color = stepsColor, style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(top = padding)
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Define the max values for scaling purposes
            val maxGlucose = (glucoseInfoList.maxOfOrNull { it.glucoseData } ?: 0).toFloat()
            val maxSteps = (glucoseInfoList.maxOfOrNull { it.stepsCount ?: 0 } ?: 0).toFloat()
            val maxY = max(maxGlucose, maxSteps)

            val spacing = canvasWidth / (glucoseInfoList.size + 1)

            // Draw grid lines and labels
            for (i in 0..labelCount) {
                val y = canvasHeight - (canvasHeight / labelCount) * i
                drawLine(
                    color = Color.LightGray,
                    start = Offset(0f, y),
                    end = Offset(canvasWidth, y),
                    strokeWidth = strokeWidth.toPx()
                )
                val label = (maxY / labelCount * i).toInt().toString()
                drawContext.canvas.nativeCanvas.drawText(label, 0f, y, Paint().asFrameworkPaint().apply {
                    color = textColor.hashCode()
                    textSize = 24f
                })
            }

            val pathGlucose = Path()
            val pathSteps = Path()

            for (i in glucoseInfoList.indices) {
                val glucoseInfo = glucoseInfoList[i]
                val glucoseHeight = (glucoseInfo.glucoseData.toFloat() / maxY) * canvasHeight
                val stepsHeight = ((glucoseInfo.stepsCount ?: 0).toFloat() / maxY) * canvasHeight

                val x = spacing * (i + 1)

                if (i == 0) {
                    pathGlucose.moveTo(x, canvasHeight - glucoseHeight)
                    pathSteps.moveTo(x, canvasHeight - stepsHeight)
                } else {
                    pathGlucose.lineTo(x, canvasHeight - glucoseHeight)
                    pathSteps.lineTo(x, canvasHeight - stepsHeight)
                }

                // Draw points
                drawCircle(glucoseColor, 4.dp.toPx(), Offset(x, canvasHeight - glucoseHeight))
                drawCircle(stepsColor, 4.dp.toPx(), Offset(x, canvasHeight - stepsHeight))

                // Draw data labels
                drawContext.canvas.nativeCanvas.drawText(glucoseInfo.glucoseData.toString(), x + 8f, canvasHeight - glucoseHeight, Paint().asFrameworkPaint().apply {
                    color = glucoseColor.hashCode()
                    textSize = 24f
                })

                glucoseInfo.stepsCount?.let {
                    drawContext.canvas.nativeCanvas.drawText(it.toString(), x + 8f, canvasHeight - stepsHeight, Paint().asFrameworkPaint().apply {
                        color = stepsColor.hashCode()
                        textSize = 24f
                    })
                }

                // Draw time labels
                val time = LocalDateTime.parse(glucoseInfo.createdAt, DateTimeFormatter.ISO_DATE_TIME)
                    .format(DateTimeFormatter.ofPattern("HH:mm"))
                drawContext.canvas.nativeCanvas.drawText(time, x - 24f, canvasHeight + 24f, Paint().asFrameworkPaint().apply {
                    color = textColor.hashCode()
                    textSize = 24f
                })
            }

            // Draw lines
            drawPath(pathGlucose, glucoseColor, style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round))
            drawPath(pathSteps, stepsColor, style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round))
        }
    }
}