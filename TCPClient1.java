package app;
import java.io.*;
import java.net.*;
import java.util.*;

public class TCPClient1 {
	/**
     * [Protocol] Translates the 'error code' sent by the server into 'human-readable messages'
     * Help Method (Sematic Processing: Meaning)
     */
    private static String translateErrorCode(String errorCode) {
        switch (errorCode) {
            case "ERR_DIV_ZERO":
                return "DIVIDED BY ZERO";
            case "ERR_TOO_FEW_ARGS":
                return "TOO FEW ARGUMENT";
            case "ERR_TOO_MANY_ARGS":
                return "TOO MANY ARGUMENTS";
            case "ERR_NOT_A_NUMBER":
                return "ONLY NUMBER AVAILABLE";
            case "ERR_INVALID_OPERATOR":
                return "INVALID OPERATOR";
            default:
                return "UNKNOWN ERROR" + errorCode;
        }
    }
    
    public static void main(String[] args) throws Exception {

    	//[Requirements] set file read logic
        String serverIP = "localhost"; //default Ip
        int serverPort = 1234;         //default address
        String configFileName = "server_info.dat"; 

        try (BufferedReader br = new BufferedReader(new FileReader(configFileName))) {
            serverIP = br.readLine();// If file exists: read IP and port from file
            serverPort = Integer.parseInt(br.readLine());
            System.out.println("Read file from" + configFileName);
            System.out.println(" -> " + serverIP + ":" + serverPort);
        } catch (FileNotFoundException e) {// [Requirements] If the file does not exist: access by default
            System.out.println("Can't find " + configFileName);
            System.out.println("Connect to default" + serverIP + ":" + serverPort);
        } catch (IOException | NumberFormatException e) {// The file is corrupted or the port is not a number
            System.out.println("incorrect format");
            System.out.println("Connect to default" + serverIP + ":" + serverPort);
        }
     // Attempt to connect to the server with the configured IP and port
        var socket = new Socket(serverIP, serverPort);
        System.out.println("Connected with" + serverIP + ":" + serverPort);

        var scanner = new Scanner(System.in);
        var in = new Scanner(socket.getInputStream());
        var out = new PrintWriter(socket.getOutputStream(), true);

        while (true) {// [Protocol] Send read messages to the server
            System.out.print("INPUT OPERATION (ex.ADD 10 20) : ");
            String outputMessage = scanner.nextLine();
            out.println(outputMessage); 

            if (outputMessage.equalsIgnoreCase("EXIT")) {
                break;
            }

            // [Protocol] 1. Get a 'one line' response from the server.
            String response = in.nextLine(); 
            // [Protocol] 2. Client 'interprets' the response.
            if (response.startsWith("ERR_")) {
            	// When an 'error code' is received, a translation method (translateErrorCode) is called.
                System.out.println("Error message: " + translateErrorCode(response));
            } else {
                System.out.println("Answer: " + response);
            }
        }
     // Return resources at loop termination
        socket.close();
        scanner.close();
        System.out.println("Exit connect");
    }
}