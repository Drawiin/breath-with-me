package com.drawiin.breathewithme

import junit.framework.TestCase.assertEquals
import org.junit.Test

class BreatheViewModelTest {
    @Test
    fun `Should start on initial state`() {
        val initialState = BreathState.Breathing(total = 1, current = 1)
        val viewModel = BreatheViewModel(initialState = initialState)

        assertEquals(initialState, viewModel.state.value)
    }

    @Test
    fun `Should pause when a button event is emmit on a Breathing state`() {
        val initialState = BreathState.Breathing(total = 1, current = 1)
        val viewModel = BreatheViewModel(initialState = initialState)

        viewModel.acceptEvent(BreathingEvents.Button)

        assertEquals(BreathState.Paused(total = 1, current =  1), viewModel.state.value)
    }

    @Test
    fun `Finish breathing on last repetition should emit the finished state`() {
        val initialState = BreathState.Breathing(total = 1, current = 1)
        val viewModel = BreatheViewModel(initialState = initialState)

        viewModel.acceptEvent(BreathingEvents.FinishRepetition)

        assertEquals(BreathState.Finished(total = 1, current =  1), viewModel.state.value)
    }

    @Test
    fun `When finishing a repetition should increment total repetition`() {
        val initialState = BreathState.Breathing(total = 2, current = 1)
        val viewModel = BreatheViewModel(initialState = initialState)

        viewModel.acceptEvent(BreathingEvents.FinishRepetition)

        assertEquals(BreathState.Breathing(total = 2, current =  2), viewModel.state.value)
    }

    @Test
    fun `Should unpause when button ins pressed on the paused state`() {
        val initialState = BreathState.Paused(total = 1, current = 1)
        val viewModel = BreatheViewModel(initialState = initialState)

        viewModel.acceptEvent(BreathingEvents.Button)

        assertEquals(BreathState.Breathing(total = 1, current =  1), viewModel.state.value)
    }

    @Test
    fun `When on finished state press the button should restart`() {
        val initialState = BreathState.Finished(total = 2, current = 2)
        val viewModel = BreatheViewModel(initialState = initialState)

        viewModel.acceptEvent(BreathingEvents.Button)

        assertEquals(BreathState.Breathing(total = 2, current =  1), viewModel.state.value)
    }
}
