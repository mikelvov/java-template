package edu.spbu.client_server;

import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.net.Socket;

public class Server {
  public Socket clientSocket;
  private InputStream inputStream;
  private OutputStream outputStream;
  private String fileName;

  public Server(Socket clientSocket) throws IOException {
    this.clientSocket = clientSocket;
    this.inputStream = clientSocket.getInputStream();
    this.outputStream = clientSocket.getOutputStream();
    this.fileName = "C:\\Users\\Михаил\\IdeaProjects\\java-template\\src\\main\\java\\edu\\spbu\\client_server\\index.txt";
  }

  public void readInputStream() throws IOException {
    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
    String rez = null;
    while (true) {
      String s = in.readLine();
      if (s == null || s.trim().length() == 0) {
        break;
      }
      rez = rez + s + "\n";
    }
  }

  public void writeOutputStream() throws IOException {
    File file = new File(fileName);

    if (file.exists()) {
      String s = new String(Files.readAllBytes(Paths.get(fileName)));
      String response = "HTTP/1.1 200 OK\n" +
              "Content-Type: text/html\n" +
              "Content-Length: " + s.length() + "\n" +
              "Connection: close\n\n" + s;
      outputStream.write(response.getBytes());
      outputStream.flush();
    }
    else {
        outputStream.write("<html><h2>404</h2></html>".getBytes());
        outputStream.flush();
    }

  }
}

//

/*String response = "HTTP/1.1 200 OK\r\n" +
            "Content-Type:text/html\r\n\r\n" +
            "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<body>\n" +
            "<p style=\"color:red;\">Hello</p>\n" +
            "<p style=\"color:blue;\">World</p>\n" +
            "</body>\n" +
            "</html>\n\n";
    outputStream.write(response.getBytes());*/