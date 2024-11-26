package se.tristanfarkas.forza.api.forsete.ssh;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import se.tristanfarkas.forza.api.forsete.ForseteException;

import java.io.*;

/**
 * Connects over SSH in-order to get the output from the submission result from Forsete.
 */
public class ForseteOutputChecker {
    private Session jSchSession;


    public ForseteOutputChecker(String host, String username, String password) {
        try {
            jSchSession = new JSch().getSession(username, host, 22);
            jSchSession.setPassword(password);
            jSchSession.setConfig("StrictHostKeyChecking", "no");
        } catch (JSchException e) {
            throw new ForseteException(e);
        }
    }

    public void consumeAndPrintOutput() {
        try {
            jSchSession.connect();
            ChannelShell channel = (ChannelShell) jSchSession.openChannel("shell");
            boolean hasReadResult = false;


            OutputStream outputStream = channel.getOutputStream();
            InputStream inputStream = channel.getInputStream();

            channel.connect();
            var reader = new BufferedReader(new InputStreamReader(inputStream));

            channel.setInputStream(null);
            while(!hasReadResult) {
                Thread.sleep(2000);
                String command = "while [ ! -f \"forsete.output\" ]; do sleep 1; done; cat \"forsete.output\"\n";
                outputStream.write((command + "\n").getBytes());
                outputStream.flush();
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!hasReadResult) {
                        outputStream.write(("exit" + "\n").getBytes());
                        outputStream.flush();
                    }
                    hasReadResult = true;
                    System.out.println(line);
                }
            }

            channel.disconnect();
            jSchSession.disconnect();
        } catch (InterruptedException | JSchException | IOException e) {
            throw new ForseteException(e);
        }
    }

}
