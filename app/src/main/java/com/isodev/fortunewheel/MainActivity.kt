package com.isodev.fortunewheel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.example.compose.AppTheme
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FortuneWheelScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


@Composable
fun FortuneWheelScreen(modifier: Modifier = Modifier) {
    var targetRotation by remember { mutableStateOf(0f) }
    var isSpinning by remember { mutableStateOf(false) }

    val rotationAnimation = animateFloatAsState(
        targetValue = targetRotation,
        animationSpec = tween(durationMillis = 3000, easing = FastOutSlowInEasing),
        finishedListener = {
            isSpinning = false
        }
    )

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(modifier = Modifier.size(300.dp)) {
            FortuneWheel(modifier = Modifier.rotate(rotationAnimation.value))
            DrawCircleWithTriangle(modifier = Modifier.align(Alignment.Center))
        }

        Spacer(modifier = Modifier.height(96.dp))

        Button(onClick = {
            if (!isSpinning) {
                val rotationAmount =
                    (360 * 3) + Random.nextInt(0, 360)
                targetRotation += rotationAmount
                isSpinning = true
            }
        }) {
            Text("Turn")
        }
    }
}

@Composable
fun FortuneWheel(modifier: Modifier = Modifier) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val wheelSize = min(screenWidth, screenHeight) * 0.8f

    val pinkColor = colorResource(id = R.color.fortune_wheel_pink)
    val whiteColor = colorResource(id = R.color.fortune_wheel_white)
    val blackColor = colorResource(id = R.color.fortune_wheel_black)
    val yellowColor = colorResource(id = R.color.fortune_wheel_light_yellow)
    val lightGlowColor = whiteColor.copy(alpha = 0.5f)

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(
            modifier = modifier
                .size(wheelSize)
                .align(Alignment.TopCenter)
        ) {
            val radius = size.minDimension / 2
            val centerX = size.width / 2
            val centerY = size.height / 2
            val anglePerSegment = 360f / 12

            drawCircle(
                color = blackColor,
                radius = radius,
                center = Offset(centerX, centerY),
                style = Stroke(width = 125f)
            )

            for (i in 0 until 12) {
                val startAngle = i * anglePerSegment + 15
                drawArc(
                    color = if (i % 2 == 0) pinkColor else whiteColor,
                    startAngle = startAngle,
                    sweepAngle = anglePerSegment,
                    useCenter = true,
                    topLeft = Offset(centerX - radius, centerY - radius),
                    size = Size(radius * 2, radius * 2)
                )


            }

            val buttonRadius = radius + 30f
            val buttonCount = 12
            val anglePerButton = 360f / buttonCount

            for (i in 0 until buttonCount) {
                val angleRad = Math.toRadians((i * anglePerButton).toDouble()) + 16.5
                val buttonCenterX = centerX + buttonRadius * cos(angleRad).toFloat()
                val buttonCenterY = centerY + buttonRadius * sin(angleRad).toFloat()

                drawCircle(
                    color = lightGlowColor,
                    radius = 28f,
                    center = Offset(buttonCenterX, buttonCenterY)
                )

                drawCircle(
                    color = yellowColor,
                    radius = 20f,
                    center = Offset(buttonCenterX, buttonCenterY)
                )
            }
        }

    }
}

@Composable
fun DrawCircleWithTriangle(modifier: Modifier) {
    val whiteColor = colorResource(id = R.color.fortune_wheel_white)
    val blackColor = colorResource(id = R.color.fortune_wheel_black)
    Canvas(modifier = modifier.fillMaxSize()) {
        val radius = 15.dp.toPx()
        val centerX = size.width / 2
        val centerY = size.height / 2

        val triangleHeight = 35.dp.toPx()

        val trianglePath = Path().apply {
            moveTo(centerX, centerY - radius - triangleHeight)
            lineTo(
                centerX - triangleHeight / 2f,
                centerY - radius + 15.dp.toPx()
            )
            lineTo(
                centerX + triangleHeight / 2f,
                centerY - radius + 15.dp.toPx()
            )
            close()
        }

        drawPath(
            path = trianglePath,
            color = blackColor
        )

        withTransform({
        }) {
            drawIntoCanvas { canvas ->
                val shadowPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.WHITE
                    setShadowLayer(
                        45f,
                        0f,
                        10f,
                        blackColor.toArgb()
                    )
                }
                canvas.nativeCanvas.drawCircle(
                    centerX,
                    centerY,
                    radius + 5.dp.toPx(),
                    shadowPaint
                )
            }
        }

        drawCircle(
            color = whiteColor,
            radius = radius,
            center = Offset(centerX, centerY)
        )
    }
}