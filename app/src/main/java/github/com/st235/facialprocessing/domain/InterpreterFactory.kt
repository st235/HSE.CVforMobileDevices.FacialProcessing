package github.com.st235.facialprocessing.domain

import android.content.Context
import androidx.annotation.RawRes
import github.com.st235.facialprocessing.utils.loadModelFromRawResources
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.gpu.GpuDelegateFactory

class InterpreterFactory(
    private val context: Context
) {
    private companion object {
        const val DEFAULT_THREADS_NUMBER = 4
    }

    fun create(@RawRes modelRes: Int): Interpreter {
        val options = Interpreter.Options().setNumThreads(DEFAULT_THREADS_NUMBER)

        val compatList = CompatibilityList()
        val canUseGPU = compatList.isDelegateSupportedOnThisDevice
        if (canUseGPU) {
            val gpuDelegateOptions = GpuDelegateFactory.Options()
            gpuDelegateOptions.setInferencePreference(GpuDelegateFactory.Options.INFERENCE_PREFERENCE_SUSTAINED_SPEED)
            options.addDelegate(GpuDelegate(gpuDelegateOptions))
        }

        val model = loadModelFromRawResources(context, modelRes)
        val interpreter = Interpreter(model, options)
        interpreter.allocateTensors()

        return interpreter
    }
}