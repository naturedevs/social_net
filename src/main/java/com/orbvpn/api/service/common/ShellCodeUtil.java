package com.orbvpn.api.service.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
public class ShellCodeUtil {

    /**
     * @param args examples for args:
     *             Run a shell command on Linux:    "bash", "-c", "ls /home/"
     *             Run a shell script:               path/to/hello.sh
     *             on Windows:                       "cmd.exe", "/c", "dir C:\\Users"
     *             Run a bat file on windows:        "C:\\hello.bat"
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static ShellCodeExecutionResult runShellCode(String[] args) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(args);

        Process process = processBuilder.start();
        StringBuilder output = new StringBuilder();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line + "\n");
        }
        int exitVal = process.waitFor();
        if (exitVal == 0) {
            log.debug("sell code execution terminated successfully. output: " + output);
            return new ShellCodeExecutionResult(output.toString(), true);
        } else {
            return new ShellCodeExecutionResult(output.toString(), false);
        }
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public static class ShellCodeExecutionResult {
        private final String output;
        private final Boolean result;
    }
}
