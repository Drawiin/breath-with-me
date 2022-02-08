package com.drawiin.breathewithme

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimatable
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.resetToBeginning
import com.drawiin.breathewithme.ui.theme.BreatheWithMeTheme
import com.drawiin.breathewithme.ui.theme.SoftBlue

@Composable
fun BreathingScreen(viewModel: BreatheViewModel = viewModel()) {
    val state = viewModel.state.collectAsState().value
    BreathingScreenBody(
        state = state,
        onButtonClicked = {
            viewModel.acceptEvent(BreathingEvents.Button)
        },
        onRepetitionEnded = {
            viewModel.acceptEvent(BreathingEvents.FinishRepetition)
        }
    )
}

@Composable
fun BreathingScreenBody(
    state: BreathState,
    onButtonClicked: () -> Unit,
    onRepetitionEnded: () -> Unit
) {
    Scaffold {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize()
        ) {
            val composition by LottieCompositionSpec.Url(
                "https://assets9.lottiefiles.com/packages/lf20_otjpnzz9.json"
            ).let { rememberLottieComposition(it) }
            val animate = rememberLottieAnimatable()

            when (state) {
                is BreathState.Breathing -> {
                    LaunchedEffect(composition, state) {
                        composition ?: return@LaunchedEffect
                        animate.animate(
                            composition,
                            continueFromPreviousAnimate = false,
                        )
                    }
                    RepetitionText(state)
                    Animation(animate, composition, onRepetitionEnded)
                    ActionButton("Pausar", Icons.Default.Close, onClick = onButtonClicked)
                }
                is BreathState.Finished -> {
                    LaunchedEffect(composition, state) {
                        animate.resetToBeginning()
                    }
                    RepetitionText(state)
                    Animation(animate, composition, onRepetitionEnded)
                    ActionButton("Recomeçar", Icons.Default.Refresh, onClick = onButtonClicked)
                }
                is BreathState.Paused -> {
                    LaunchedEffect(composition, state) {
                        composition ?: return@LaunchedEffect
                        animate.resetToBeginning()
                    }
                    RepetitionText(state)
                    Animation(animate, composition, onRepetitionEnded)
                    ActionButton("Continuar", Icons.Default.PlayArrow, onClick = onButtonClicked)
                }
            }

        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun RepetitionText(state: BreathState) {
    val text = when(state){
        is BreathState.Finished -> "Seção completa"
        else -> "${state.current}"
    }
    AnimatedContent(targetState = text) { visibleText ->
        Text(
            text = visibleText,
            style = MaterialTheme.typography.h3.copy(color = SoftBlue, fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ActionButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = ButtonDefaults.elevation(
            defaultElevation = 0.dp
        )
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedContent(targetState = text) { visibleText ->
                Text(text = visibleText)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(imageVector = icon, contentDescription = null)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Animation(
    animatable: LottieAnimatable,
    composition: LottieComposition?,
    onAnimationEnd: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(300.dp),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = animatable.progress,
            modifier = Modifier
                .size(300.dp)
        )
        val text = if (animatable.progress >= 0.5f) "Expire" else "Inspire"
        val textScale =
            if (animatable.progress >= 0.5f) (1f + (1f - animatable.progress)) else (1f + animatable.progress)
        val fontSize = MaterialTheme.typography.h6.fontSize * textScale
        AnimatedContent(targetState = text) { visibleText ->
            Text(
                text = visibleText,
                style = MaterialTheme.typography.h6.copy(
                    color = Color.White,
                    fontSize = fontSize,
                    fontWeight = FontWeight.Normal
                )
            )
        }
    }

    if (animatable.progress == 1f) LaunchedEffect(Unit) {
        onAnimationEnd()
    }
}

@Preview
@Composable
fun BreathingPreview() {
    BreatheWithMeTheme {
        BreathingScreenBody(state = BreathState.Finished(total = 2, current = 1), {}, {})
    }

}
