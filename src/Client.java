import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private static final String SERVER_NAME = "127.0.0.1";
    private static final int SERVER_PORT = 8080;

    public static void main(String [] agrs) throws IOException {

        // Open socket
        Socket client = new Socket(SERVER_NAME, SERVER_PORT);
        DataInputStream in = new DataInputStream(client.getInputStream());
        DataOutputStream out = new DataOutputStream(client.getOutputStream());

        try {
            while (true) {
                System.out.println("Enter your operation: ");

                // Read from user input
                Scanner scanner = new Scanner(System.in);
                String operation = scanner.nextLine();

                // End Client on empty string
                if (operation.isEmpty()) {
                    break;
                }

                // Validate user input
                if (!operation.equalsIgnoreCase("count") &&
                        !operation.matches("\\d+(\\+|-|\\*|/|%|//)\\d+")) {
                    System.out.println("Invalid input: " + operation);
                    continue;
                }

                // Write Request to out stream in open socket
                out.writeBytes(operation + "\n");

                // Read input stream in open socker (Response)
                System.out.println("Server says: " + in.readLine());
            }

        } catch (ConnectException e) {
            System.out.println("Server is not running");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            // Close streams
            in.close();
            out.close();

            // Close socket
            client.close();
        }

    }
}
