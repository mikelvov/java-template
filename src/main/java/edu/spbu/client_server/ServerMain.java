package edu.spbu.client_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
  public static void main(String[] args) throws IOException {
    ServerSocket serverSocket = new ServerSocket(8080);
    while (true) {
      System.out.println("Waiting for client connection...");
      Socket clientSocket = serverSocket.accept();
      Server server = new Server(clientSocket);
      System.out.println("Client connected!");
      server.readInputStream();
      server.writeOutputStream();
      server.clientSocket.close();
    }
  }
}