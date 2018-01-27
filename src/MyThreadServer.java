import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class MyThreadServer implements Runnable {

    private final static int PORT = 8080;
    private final Socket csocket;
    private static int clientCount = 0;

    private MyThreadServer(Socket csocket) {
        this.csocket = csocket;
    }

    /**
     * Main method
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {
        ServerSocket ssock = new ServerSocket(PORT);
        System.out.println("Listening on port "+String.valueOf(PORT));

        while (true) {
            Socket sock = ssock.accept();
            System.out.println("Client Connected");
            increaseClient();

            new Thread(new MyThreadServer(sock)).start();
        }
    }

    /**
     * Multithread execution
     */
    public void run() {

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(csocket.getInputStream()));
            OutputStream out = csocket.getOutputStream();

            while (true) {
                // Get input
                String clientCommand = in.readLine();

                // Decide action depending on input
                if (clientCommand == null) {
                    System.out.println("Client closed");

                    // Close Streams
                    in.close();
                    out.close();

                    // Decrease Client
                    decreaseClient();
                    break;
                } else if (clientCommand.equalsIgnoreCase("count")) {
                    out.write((String.valueOf(clientCount) + "\n").getBytes());
                } else {
                    // Compute calculation
                    String response = computeServerResponse(clientCommand);

                    // Write output to socket
                    if (response == null) {
                        out.write(("404BadRequest\n").getBytes());
                    } else {
                        out.write(("200OK - " + response + "\n").getBytes());
                    }
                }

                // Log Request
                System.out.println("Request was successfully processed");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param ops String operation to compute. Ex: 2+5
     * @return String with resul. Ex: 2+5=7
     */
    private String computeServerResponse(String ops) {
        int n1 = 0, n2 = 0;
        String sym = "";

        for (int i = 0; i < ops.length(); i++) {
            char a = ops.charAt(i);
            if (Character.isDigit(a)){
                n2 = 10 * n2 + Character.getNumericValue(a);
            } else {
                if (sym.isEmpty()) {
                    n1 = n2;
                    n2 = 0;
                }
                sym = sym + Character.toString(a);
            }
        }

        if (sym.equalsIgnoreCase("//")){
            return String.format("%s=%d", ops, (int) Math.floor(n1 / n2));
        } else if (sym.equalsIgnoreCase("/")){
            return String.format("%s=%.2f", ops, (double) n1 / n2);
        } else if (sym.equalsIgnoreCase("%")){
            return String.format("%s=%d", ops, n1 % n2);
        } else if (sym.equalsIgnoreCase("*")){
            return String.format("%s=%d", ops, n1 * n2);
        } else if (sym.equalsIgnoreCase("+")){
            return String.format("%s=%d", ops, n1 + n2);
        } else if (sym.equalsIgnoreCase("-")){
            return String.format("%s=%d", ops, n1 - n2);
        } else {
            return null;
        }
    }

    private static void increaseClient(){
        clientCount++;
    }

    private static void decreaseClient(){
        clientCount--;
    }

}
