import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class Client extends Thread {

    private static final int port = 1194;    //default port to connect
    private static Socket socket;

    public static void main(String[] args) {
        //format the time
        SimpleDateFormat date = new SimpleDateFormat("HH:mm:ss");

        //get input from keyboard
        Scanner keyboard = new Scanner(System.in);
        System.out.print("Enter hostname: ");
        String host = keyboard.nextLine();
        try {
            //connect to server
            System.out.println("connecting " + host);
            socket = new Socket(host, port);
            new Client().start();   //thread recive message from server

            //keep reading message and sent to server
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            while (true) {
                String line = keyboard.nextLine();
                if (!line.startsWith("/")) {
                    line = "(" + date.format(System.currentTimeMillis())
                            + "): " + line;
                }
                out.println(line);
            }
        } catch (UnknownHostException ex) {
            System.out.println("Unknow Host: " + host);
            System.exit(1);
        } catch (IOException ex) {
            System.out.println("Unable to open connection!");
            System.exit(1);
        }


    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException ex) {
        }
        System.out.println("Connection closed");

        System.exit(0);
    }
}
