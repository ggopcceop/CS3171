
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPPinger {

    private static final String host = "localhost";

    public static void main(String[] args) {
        DatagramSocket clientSocket = null;
        InetAddress IPAddress = null;
        try {
            clientSocket = new DatagramSocket();
            
            clientSocket.setSoTimeout(1000);
            IPAddress = InetAddress.getByName(host);
        } catch (SocketException ex) {
            System.out.println("Can not create socket!");
            System.exit(1);
        } catch (UnknownHostException ex) {
            System.out.println("Unknown host");
            System.exit(1);
        }
        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 12000);
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

        for (int i = 1; i <= 10; i++) {
            long startTime = System.currentTimeMillis();
            try {
                clientSocket.send(sendPacket);
                clientSocket.receive(receivePacket);
                long endTime = System.currentTimeMillis();
                System.out.println("Ping " + i + " takes " + (endTime - startTime)
                        + " ms");
            } catch (IOException ex) {
                System.out.println("Pint " + i + " lost!");
            }
        }
    }
}
