package com.github.grishberg.test.launcher

import com.android.build.gradle.internal.test.report.ReportType
import com.android.build.gradle.internal.test.report.TestReportExt
import com.github.grishberg.tests.DeviceCommandsRunnerFabric
import com.github.grishberg.tests.InstrumentalExtension
import com.github.grishberg.tests.InstrumentationTestLauncher
import com.github.grishberg.tests.adb.AdbWrapperImpl
import com.github.grishberg.tests.common.RunnerLogger
import com.github.grishberg.tests.planner.InstrumentalTestPlanProvider
import com.github.grishberg.tests.planner.PackageTreeGenerator
import org.apache.commons.cli.*
import java.io.File

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
        options.addRequiredOption("t", "testPackage", true, "Instrumental test package")
        options.addOption("i", "instrumentalRunner", true, "Instrumental runner")
        options.addOption("o", "outDir", true, "output report dir")
        options.addOption("f", "flavor", true, "flavor")

        val parser = DefaultParser()
        val formatter = HelpFormatter()
        try {
            val cmd = parser.parse(options, args)
            initAndLaunch(cmd)
        } catch (e: ParseException) {
            System.out.println(e.message)
            formatter.printHelp("Instrumental tes launcher:", options)

            System.exit(1)
        }
    }

    private fun initAndLaunch(cmd: CommandLine) {
        val projectName = "project"
        val amInstrumentParams = HashMap<String, String>()

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

        val testPackage = cmd.getOptionValue("t")
        if (testPackage != null) {
            extension.instrumentalPackage = testPackage
        }

        val logger: RunnerLogger = Log4JLogger()
        val instrumentalTestPlanProvider = provideInstrumentalTestPlanProvider(amInstrumentParams,
                extension, logger)
        val instrumentalLauncher = InstrumentationTestLauncher(projectName,
                reportDir,
                extension,
                AdbWrapperImpl(),
                DeviceCommandsRunnerFabric(instrumentalTestPlanProvider),
                logger

        )
        var success = false
        try {
            instrumentalLauncher.launchTests()
            success = true
        } finally {
            generateHtmlReport(success,
                    instrumentalLauncher.resultsDir,
                    instrumentalLauncher.reportsDir,
                    instrumentalLauncher.screenshotRelations
            )
        }
    }

    private fun generateHtmlReport(success: Boolean,
                                   resultDir: File,
                                   reportsDir: File,
                                   screenshotMap: Map<String, String>) {
        val report = TestReportExt(ReportType.SINGLE_FLAVOR, resultDir, reportsDir, screenshotMap)
        report.generateReport()
        if (!success) {
            val reportUrl = File(reportsDir, "index.html").path
            val message = String.format("There were failing tests. See the report at: %s",
                    reportUrl)
            throw RuntimeException(message)
        }
    }

    private fun provideInstrumentalTestPlanProvider(amInstrumentParams: HashMap<String, String>,
                                                    extension: InstrumentalExtension,
                                                    logger: RunnerLogger): InstrumentalTestPlanProvider {
        val packageTreeGenerator = PackageTreeGenerator()
        return InstrumentalTestPlanProvider(amInstrumentParams, extension,
                packageTreeGenerator, logger)
    }
}
