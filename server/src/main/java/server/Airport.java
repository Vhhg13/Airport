package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Airport {
    public static void main(String[] args) {
        Socket clientSocket = null;
        ServerSocket server = null;
        BufferedReader in = null;
        BufferedWriter out = null;
        for (; ; ) {
            try {
                try {
                    server = new ServerSocket(42000);
                    System.out.printf("Socket opened [%s]\n", new Date());
                    for (; ; ) {
                        System.out.printf("! waiting for input ! [%s]\n", new Date());
                        clientSocket = server.accept();
                        out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        String command = in.readLine();
                        System.out.printf("Received command: `%s` [%s]\n", command, new Date());
//                    String answer = processCommand(command);
                        String answer = command;
                        out.write(answer + "\n");
                        System.out.println("Sent to client: " + answer);
                        out.flush();
                    }
                } finally {
                    if (clientSocket != null) clientSocket.close();
                    if (in != null) in.close();
                    if (out != null) out.close();
                    System.out.printf("Server closed [%s]\n", new Date());
                    if (server != null) server.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
