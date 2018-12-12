package edu.spbu.client_server;

import java.net.*;
import java.io.*;
import java.util.*;

public class Client {

  private Socket socket;
  private InputStream inputStream;
  private OutputStream outputStream;

  public Client(Socket socket) throws IOException {
    this.socket = socket;
    this.inputStream = socket.getInputStream();
    this.outputStream = socket.getOutputStream();
  }

  public void writeOutputStream(String host) throws IOException {
    //St// str =ring str = "GET / HTTP/1.1\r\n\r\n";
    String str= "GET / HTTP/1.1\n" +"Host: "+ host +":"+"\n\n";
    outputStream.write(str.getBytes());
    outputStream.write("".getBytes());
    outputStream.flush();

  }

  public void readInputStream() throws IOException {
    Scanner scan = new Scanner(inputStream);
    String str = new String();
    while (scan.hasNextLine()){
      str = scan.nextLine();
      System.out.println(str);
    }

  }
}