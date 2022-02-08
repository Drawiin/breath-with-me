package com.drawiin.breathewithme

import androidx.lifecycle.ViewModel
import com.drawiin.breathewithme.BreathState.Breathing
import com.drawiin.breathewithme.BreathState.Finished
import com.drawiin.breathewithme.BreathState.Paused
import com.drawiin.breathewithme.BreathingEvents.Button
import com.drawiin.breathewithme.BreathingEvents.FinishRepetition
import kotlinx.coroutines.flow.MutableStateFlow

class BreatheViewModel(initialState: BreathState = Finished(total = 5, current = 5)) :
    ViewModel() {
    val state: MutableStateFlow<BreathState> = MutableStateFlow(initialState)

    private fun setState(setState: BreathState.() -> BreathState) {
        state.value = state.value.setState()
    }

    fun acceptEvent(event: BreathingEvents) = setState {
        when (event) {
            is Button -> when (this) {
                is Breathing -> Paused(total, current)
                is Paused -> Breathing(total, current)
                is Finished -> Breathing(total, INITIAL_REPETITION)
            }
            is FinishRepetition -> when (this) {
                is Breathing -> if (current >= total)
                    Finished(total, current)
                else
                    Breathing(total, current + INITIAL_REPETITION)
                else -> this
            }
        }
    }


    companion object {
        private const val INITIAL_REPETITION = 1
    }
}
