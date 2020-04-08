import com.github.grishberg.test.launcher.Launcher
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val launcher = Launcher(args)
    exitProcess(launcher.launch())
}