package io.particle.mesh.setup.flow.setupsteps

import io.particle.mesh.setup.flow.MeshSetupStep
import io.particle.mesh.setup.flow.Scopes
import io.particle.mesh.setup.flow.context.SetupContexts
import io.particle.mesh.setup.flow.FlowUiDelegate
import kotlinx.coroutines.delay


class StepShowTargetPairingSuccessful(private val flowUi: FlowUiDelegate) : MeshSetupStep() {

    override suspend fun doRunStep(ctxs: SetupContexts, scopes: Scopes) {
        val deviceName = ctxs.requireTargetXceiver().bleBroadcastName
        val shouldWaitBeforeAdvancingFlow = flowUi.onTargetPairingSuccessful(deviceName)
        if (shouldWaitBeforeAdvancingFlow) {
            delay(2000)
        }
    }

}