package com.github.grishberg.test.launcher

class App {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val launcher = Launcher(args)
            launcher.launch()
        }
    }
}
