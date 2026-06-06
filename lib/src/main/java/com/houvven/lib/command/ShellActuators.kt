package com.houvven.lib.command

import java.io.DataOutputStream

/**
 * A singleton utility object for executing shell commands on Android devices.
 *
 * Provides methods to run commands either with root (`su`) or unprivileged (`sh`) access.
 * Commands are executed via [Runtime.exec] and piped through a [DataOutputStream] to the
 * spawned process. Results are captured from stdout on success or from stderr on failure.
 *
 * This object is designed for use within Xposed modules where shell execution is needed
 * to interact with system-level resources.
 */
object ShellActuators {

    /** Shell command to invoke a root shell. */
    private const val COMMAND_SU = "su"

    /** Shell command to invoke a standard unprivileged shell. */
    private const val COMMAND_SH = "sh"

    /** Command string used to gracefully terminate the shell process. */
    private const val COMMAND_EXIT = "exit\n"

    /** Line separator written after each command to simulate pressing Enter. */
    private const val COMMAND_LINE_END = "\n"


    /**
     * Checks whether root access is available on the current device.
     *
     * Attempts to execute an empty command with root privileges. If the process exits
     * with code 0, root access is considered available.
     *
     * @return `true` if a root shell can be successfully opened, `false` otherwise.
     */
    @JvmStatic
    fun checkRoot(): Boolean = exec(arrayOf(""), true).isSuccess

    /**
     * Executes a single shell command.
     *
     * Convenience overload that wraps the single [command] string into an array and
     * delegates to [exec] with an array of commands.
     *
     * @param command the shell command string to execute.
     * @param uesRoot whether to execute via `su` (root) or `sh` (unprivileged). Note:
     *                 parameter name preserves the original typo for API compatibility.
     * @return a [Result] wrapping the stdout output on success, or the exception on failure.
     */
    @JvmStatic
    fun exec(command: String, uesRoot: Boolean) = exec(arrayOf(command), uesRoot)

    /**
     * Executes a sequence of shell commands in a single shell session.
     *
     * Opens a shell process (root or unprivileged), writes each non-blank command to the
     * process's stdin followed by a newline, and then sends an `exit` command to close
     * the session. The process exit code determines success or failure:
     * - Exit code 0: stdout content is returned as the success value.
     * - Non-zero exit code: stderr content is wrapped in a [RuntimeException] and returned
     *   as a failure.
     *
     * @param commands an array of shell command strings to execute sequentially. Must not
     *                 be empty; blank entries are silently skipped.
     * @param useRoot  `true` to execute via `su` (root shell), `false` to use `sh`.
     * @return a [Result] wrapping the combined stdout output on success, or the
     *         [RuntimeException] containing stderr on failure.
     * @throws IllegalArgumentException if [commands] is empty.
     */
    @JvmStatic
    fun exec(
        commands: Array<String>,
        useRoot: Boolean
    ) = runCatching {
        if (commands.isEmpty()) {
            throw IllegalArgumentException("commands is empty.")
        }

        // Spawn the appropriate shell process based on privilege level
        val process = Runtime.getRuntime().exec(if (useRoot) COMMAND_SU else COMMAND_SH)

        // Write each command to the process stdin, then terminate the shell
        DataOutputStream(process.outputStream).use { os ->
            for (it in commands) {
                if (it.isBlank()) continue
                os.run {
                    write(it.toByteArray()); writeBytes(COMMAND_LINE_END); flush()
                }
            }
            os.writeBytes(COMMAND_EXIT)
            os.flush()
        }

        // Evaluate the process exit code: 0 means success, anything else is an error
        when (process.waitFor()) {
            0 -> process.inputStream.bufferedReader().readText()
            else -> throw RuntimeException(process.errorStream.bufferedReader().readText())
        }
    }
}
