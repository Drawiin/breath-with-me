package com.drawiin.breathewithme

sealed class BreathState(open val total: Int, open val current: Int) {
    data class Breathing(override val total: Int, override val  current: Int) : BreathState(total, current)
    data class Paused(override val total: Int, override val  current: Int) : BreathState(total, current)
    data class Finished(override val total: Int, override val  current: Int) : BreathState(total, current)
}
