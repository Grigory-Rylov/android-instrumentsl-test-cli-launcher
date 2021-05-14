package com.github.grishberg.test.launcher

import com.github.grishberg.test.launcher.commands.ArgsProvider
import com.github.grishberg.test.launcher.commands.CustomCommandsProvider
import com.github.grishberg.tests.DeviceCommandsRunnerFactory
import com.github.grishberg.tests.InstrumentalExtension
import com.github.grishberg.tests.InstrumentationTestLauncher
import com.github.grishberg.tests.adb.AdbWrapper
import com.github.grishberg.tests.common.RunnerLogger
import io.github.grigoryrylov.android.test.TestReportExt
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException
import java.io.File
import kotlin.system.exitProcess

/**
 * Android instrumental test launcher.
 */
class Launcher(private val args: Array<String>) {
    /**
     * Parse command line arguments and launch instrumental tests.
     */
    fun launch() {
        val options = Options()
        options.addRequiredOption("a", "appId", true, "applicationId")
        options.addRequiredOption("p", "testPackage", true, "Instrumental test package")
        options.addRequiredOption("t", "testApk", true, "apk with tests")
        options.addOption("i", "instrumentalRunner", true, "Instrumental runner")
        options.addOption("o", "outDir", true, "output report dir")
        options.addOption("f", "flavor", true, "flavor")

        val parser = DefaultParser()
        val formatter = HelpFormatter()
        try {
            val cmd = parser.parse(options, args)
            initAndLaunch(cmd)
            exitProcess(0)
        } catch (e: ParseException) {
            println(e.message)
            formatter.printHelp("Instrumental tes launcher:", options)
            exitProcess(1)
        } catch (e: TestsFailedException) {
            exitProcess(2)
        }
    }

    private fun initAndLaunch(cmd: CommandLine) {
        val projectName = "project"

        var reportDir = cmd.getOptionValue("o")
        if (reportDir == null) {
            val currentDir = File("report")
            reportDir = currentDir.absolutePath
        }

        val extension = InstrumentalExtension()
        var instrumentalTestRunner = cmd.getOptionValue("i")
        if (instrumentalTestRunner == null) {
            instrumentalTestRunner = "android.support.test.runner.AndroidJUnitRunner"
        }
        extension.instrumentalRunner = instrumentalTestRunner

        val appId = cmd.getOptionValue("a")
        if (appId != null) {
            extension.applicationId = appId
        }

        var flavor = cmd.getOptionValue("f")
        if (flavor == null) {
            flavor = "debug"
        }
        extension.flavorName = flavor

        val testPackage = cmd.getOptionValue("p")
        if (testPackage != null) {
            extension.instrumentalPackage = testPackage
        }

        val testApk = cmd.getOptionValue("t")
        if (testApk != null) {
            extension.testApkPath = testApk
        }

        val logger: RunnerLogger = Log4JLogger()

        val instrumentalLauncher = InstrumentationTestLauncher(
            projectName,
            reportDir,
            extension,
            AdbWrapper(),
            DeviceCommandsRunnerFactory(extension),
            logger
        )

        val argsProvider = ArgsProvider(logger)
        val commandsProvider = CustomCommandsProvider(projectName, argsProvider, logger)

        instrumentalLauncher.setInstrumentationArgsProvider(argsProvider)
        instrumentalLauncher.setCommandProvider(commandsProvider)

        var success = false
        try {
            success = instrumentalLauncher.launchTests()
            if (!success) {
                val reportUrl = File(instrumentalLauncher.reportsDir, "index.html").path
                val message = String.format(
                    "There were failing tests. See the report at: %s",
                    reportUrl
                )
                throw TestsFailedException()
            }
        } finally {
            generateHtmlReport(
                instrumentalLauncher.resultsDir,
                instrumentalLauncher.reportsDir,
                instrumentalLauncher.screenshotRelations
            )
        }
    }

    private fun generateHtmlReport(
        resultDir: File,
        reportsDir: File,
        screenshotMap: Map<String, String>
    ) {
        val report = TestReportExt(resultDir, reportsDir, screenshotMap)
        report.generateReport()
    }
}
