package com.cd.todoarch.util

import com.cd.todoarch.core.framework.FluxKore
import com.cd.todoarch.core.framework.Kommand
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo

fun <STATE, EVENT> fluxTest(
    fluxKore: FluxKore<STATE, EVENT>,
    kommand: Kommand<STATE, EVENT>,
    initialEvent: EVENT,
    expectedFinalState: STATE,
) = runBlocking {
    fluxKore.execute(kommand)
    fluxKore.state.value shouldBeEqualTo expectedFinalState
}