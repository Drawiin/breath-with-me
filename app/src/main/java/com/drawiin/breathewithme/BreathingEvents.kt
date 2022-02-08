package com.drawiin.breathewithme

sealed class BreathingEvents {
    object Button: BreathingEvents()
    object FinishRepetition : BreathingEvents()
}
