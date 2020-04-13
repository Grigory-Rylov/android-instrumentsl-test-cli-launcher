package com.github.grishberg.test.launcher

import com.github.grishberg.tests.common.RunnerLogger
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.Logger

class Log4JLogger : RunnerLogger {
    var log: Logger = LogManager.getLogger("runner") as Logger

    init {
        log.setLevel(Level.DEBUG);
    }

    override fun i(tag: String, msg: String) {
        log.info("$tag: $msg")
    }

    override fun i(tag: String, format: String, vararg args: Any?) {

        when (args.size) {
            1 -> log.info("$tag: $format", args[0])
            2 -> log.info("$tag: $format", args[0], args[1])
            3 -> log.info("$tag: $format", args[0], args[1], args[2])
            4 -> log.info("$tag: $format", args[0], args[1], args[2], args[3])
            5 -> log.info("$tag: $format", args[0], args[1], args[2], args[3], args[4])
            else -> log.info("$tag: $format", args)
        }
    }

    override fun w(tag: String, msg: String) {
        log.warn("$tag: $msg")
    }

    override fun w(tag: String, format: String, vararg args: Any?) {
        when (args.size) {
            1 -> log.warn("$tag: $format", args[0])
            2 -> log.warn("$tag: $format", args[0], args[1])
            3 -> log.warn("$tag: $format", args[0], args[1], args[2])
            4 -> log.warn("$tag: $format", args[0], args[1], args[2], args[3])
            5 -> log.warn("$tag: $format", args[0], args[1], args[2], args[3], args[4])
            else -> log.warn("$tag: $format", args)
        }
    }

    override fun e(tag: String, msg: String) {
        log.error("$tag: $msg")
    }

    override fun e(tag: String, msg: String, t: Throwable?) {
        log.error("$tag: $msg", t)
    }

    override fun d(tag: String, msg: String) {
        log.debug("$tag: $msg")
    }

    override fun d(tag: String, format: String, vararg args: Any?) {
        when (args.size) {
            1 -> log.debug("$tag: $format", args[0])
            2 -> log.debug("$tag: $format", args[0], args[1])
            3 -> log.debug("$tag: $format", args[0], args[1], args[2])
            4 -> log.debug("$tag: $format", args[0], args[1], args[2], args[3])
            5 -> log.debug("$tag: $format", args[0], args[1], args[2], args[3], args[4])
            else -> log.debug("$tag: $format", args)
        }
    }
}