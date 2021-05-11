package com.github.grishberg.test.launcher.commands

import com.github.grishberg.tests.ConnectedDeviceWrapper
import com.github.grishberg.tests.InstrumentationArgsProvider
import com.github.grishberg.tests.common.RunnerLogger
import java.util.HashMap


private const val TABLET_ANNOTATION: String =
    "com.github.grishberg.instrumentaltestsample.TabletOnly"

class ArgsProvider(
    private val logger: RunnerLogger
) : InstrumentationArgsProvider {
    override fun provideInstrumentationArgs(device: ConnectedDeviceWrapper): MutableMap<String, String> {
        logger.i(
            "provideCommandsForDevice  %s, density is: %d, width = %d, width in dp = %d",
            device.toString(),
            device.density,
            device.width,
            device.widthInDp
        )

        val argsMap = HashMap<String, String>()

        if (device.widthInDp >= 600) {
            argsMap["annotation"] = TABLET_ANNOTATION
        } else {
            argsMap["notAnnotation"] = TABLET_ANNOTATION
        }
        return argsMap
    }
}
