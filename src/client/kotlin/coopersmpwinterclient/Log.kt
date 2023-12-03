package coopersmpwinterclient

import com.mojang.logging.LogUtils

class Log {
    companion object {
        private const val LOG_PREFIX = "[CooperSMPWinterClient] "
        private val LOGGER = LogUtils.getLogger()

        fun info(message: String) {
            LOGGER.info("$LOG_PREFIX$message")
        }

        fun warn(message: String) {
            LOGGER.warn("$LOG_PREFIX[WARN] $message")
        }

        fun error(message: String) {
            LOGGER.error("$LOG_PREFIX[ERROR] $message")
        }
    }
}