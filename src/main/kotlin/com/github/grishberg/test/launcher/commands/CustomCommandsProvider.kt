package com.github.grishberg.test.launcher.commands

import com.android.ddmlib.testrunner.TestIdentifier
import com.github.grishberg.tests.ConnectedDeviceWrapper
import com.github.grishberg.tests.Environment
import com.github.grishberg.tests.InstrumentationArgsProvider
import com.github.grishberg.tests.XmlReportGeneratorDelegate
import com.github.grishberg.tests.commands.DeviceRunnerCommand
import com.github.grishberg.tests.commands.DeviceRunnerCommandProvider
import com.github.grishberg.tests.commands.SingleInstrumentalTestCommand
import com.github.grishberg.tests.common.RunnerLogger
import com.github.grishberg.tests.planner.TestPlanElement
import java.util.ArrayList
import java.util.HashMap

class CustomCommandsProvider(
    private val projectName: String,
    private val argsProvider: InstrumentationArgsProvider,
    private val logger: RunnerLogger
) : DeviceRunnerCommandProvider {
    override fun provideCommandsForDevice(
        device: ConnectedDeviceWrapper,
        testPlanElements: MutableList<TestPlanElement>,
        environment: Environment
    ): MutableList<DeviceRunnerCommand> {
        val deviceRunnerCommands = ArrayList<DeviceRunnerCommand>()
        val args: Map<String, String> = argsProvider.provideInstrumentationArgs(device)
        logger.i(
            "provideCommandsForDevice %s, density is: %d",
            device.toString(),
            device.getDensity()
        )

        val command = SingleInstrumentalTestCommand(
            projectName, "pref", args, testPlanElements, XmlReportDelegate(),
            SingleInstrumentalTestCommand.RetryHandler.NOOP
        )
        deviceRunnerCommands.add(command)
        return deviceRunnerCommands
    }

    private class XmlReportDelegate : XmlReportGeneratorDelegate {

        override fun provideProperties(): Map<String, String> {
            val properties = HashMap<String, String>()
            properties["enabledFeatures"] = "someEmptyFeature"
            return properties
        }


        override fun provideAdditionalAttributesForTest(testIdentifier: TestIdentifier): Map<String, String> {
            val additionalParams: MutableMap<String, String> = HashMap()
            additionalParams["customAttribute"] = "testName=" + testIdentifier.testName
            return additionalParams
        }
    }
}
