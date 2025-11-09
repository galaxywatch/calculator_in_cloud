package app;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;//It comes in a separate package called java.util.concurrent, 
import java.util.concurrent.Executors;//so it needs to be imported separately(use GPT) 
import java.io.PrintWriter;
import java.util.Scanner; 

public class TCPServer1 {//calc method uses a calculation result or error code Return to String
    public static String calc(String exp) {
        StringTokenizer st = new StringTokenizer(exp, " ");
        int tokenCount = st.countTokens();

        if (tokenCount < 3) { //[PROTOCOL]Error in number of arguments
            return "ERR_TOO_FEW_ARGS";
        } else if (tokenCount > 3) {
            return "ERR_TOO_MANY_ARGS";
        }

        String res = "";
        try { //Split input string [command op1 op2]
            String opcode = st.nextToken();
            int op1 = Integer.parseInt(st.nextToken());
            int op2 = Integer.parseInt(st.nextToken());

            switch (opcode) { //calculate
                case "ADD":
                    res = Integer.toString(op1 + op2);
                    break;
                case "MIN": 
                    res = Integer.toString(op1 - op2);
                    break;
                case "MTP":
                    res = Integer.toString(op1 * op2);
                    break;
                case "DIV":
                    if (op2 == 0) { //[PROTOCOL]Error when divided by 0
                        res = "ERR_DIV_ZERO";
                    } else {
                        res = Integer.toString(op1 / op2);
                    }
                    break;
                default: //[PROTOCOL]Error when input invalid operator
                    res = "ERR_INVALID_OPERATOR";
            }
        } catch (NumberFormatException e) {
            // [PROTOCOL] Error entering non-numerical values
            res = "ERR_NOT_A_NUMBER";
        }
        return res;
    }

    public static void main(String[] args) throws Exception {
        ServerSocket listener = new ServerSocket(9999);// Create a server socket with port 9999 (door)
        System.out.println("SERVER IS RUNNING...");
        ExecutorService pool = Executors.newFixedThreadPool(20);//[Requirements] Create Thread Pool (up to 20 threads)
        
        while (true) { // The main thread goes around an infinite loop and waits for the client's connection
            Socket sock = listener.accept();
            pool.execute(new CapitalizeClient(sock));// [Requirements] Assign client processing tasks to thread pools
        }
    }
    /**
     * What Threads in Thread Pool Do (Implement Runable Interface)
     * Handles communications exclusively to one client
     */
    private static class CapitalizeClient implements Runnable {
        private Socket socket;// Client socket for this thread to take charge of
        CapitalizeClient(Socket socket) { this.socket = socket; } 

        
        public void run() { 
            System.out.println("Connected: " + socket);
            try (
                var in = new Scanner(socket.getInputStream()); 
                var out = new PrintWriter(socket.getOutputStream(), true) 
            ) {
                while (in.hasNextLine()) { // Repeat until client disconnects (until next line)
                    String inputMessage = in.nextLine();
                    if (inputMessage.equalsIgnoreCase("bye")) {
                        break;
                    }
                 // 1. Call the calc method to get results (or error codes)
                    String res = calc(inputMessage); 
                 // [protocol] 2. server sends the results to the client in a single line
                    out.println(res);
                }
            } catch (Exception e) {// Exception during communication (e.g. client forced shutdown)
                System.out.println("Error:" + socket);
            } finally {
                try { socket.close();}catch (IOException e) {} 
                System.out.println("Closed: " + socket);
            }
        }
    }
}