package com.github.florent37.camerafragment.sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class CommandSocket {

    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    public void connect(String serverIP, int serverPort) throws IOException {
        socket = new Socket(serverIP, serverPort);
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new PrintWriter(socket.getOutputStream(), true);
    }

    public void disconnect() throws IOException {
        input.close();
        output.close();
        socket.close();
    }

    public void sendMessage(String message) {
        output.println(message);
    }

    public String receiveMessage() throws IOException {
        return input.readLine();
    }
}
