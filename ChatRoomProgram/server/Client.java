import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

/**
 * class of a client
 */
public class Client {

    private Socket socket;
    private String name;
    private Room room;
    private static Random rand = new Random();
    private PrintWriter out;
    private boolean close;

    public Client(Socket socket) {
        this.socket = socket;
        this.name = rand.nextInt(Integer.MAX_VALUE) + "";
        init();
    }

    /**
     * Initialize client data
     */
    private void init() {
        room = null;
        close = false;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            new ClientThread(this).start();
        } catch (IOException ex) {
        }
    }

    /* getters and setters */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    /**
     * leave the room
     */
    public void leaveRoom() {
        if (room != null) {
            room.removeMember(this);
        }
    }

    /**
     * close the connection of the client
     */
    public void close() {
        if (!close) {
            if (room != null) {
                leaveRoom();
            }
            try {
                socket.close();
            } catch (IOException ex) {
            }
            ChatServer.removeClient(this);
            System.out.println(getName() + " disconnected!");
            close = true;
        }
    }

    /**
     * handle message that the client sent
     *
     * @param message
     */
    private void handleMessage(String message) {
        if (message.startsWith("/")) {
            System.out.println(name + " issue a command: " + message);
            handleCommand(message.toLowerCase().substring(1).split(" "));
        } else if (room != null) {
            message = name + message;
            System.out.println(message);
            room.boardcast(message, this);
        }
    }

    /**
     * handle the meesage if is command
     *
     * @param args
     */
    private void handleCommand(String[] args) {
        if ("name".equalsIgnoreCase(args[0])) {
            if (args.length == 2) {
                ChatServer.changeName(this, args[1]);
            } else {
                write("error");
            }
        } else if ("list".equalsIgnoreCase(args[0])) {
            ChatServer.showRooms(this);
        } else if ("who".equalsIgnoreCase(args[0])) {
            ChatServer.showMembers(this);
        } else if ("join".equalsIgnoreCase(args[0])) {
            if (args.length == 2) {
                ChatServer.joinRoom(this, args[1]);
            } else {
                write("error");
            }
        } else if ("leave".equalsIgnoreCase(args[0])) {
            if (room != null) {
                leaveRoom();
                write("ok");
            } else {
                write("error");
            }
        } else if ("msg".equalsIgnoreCase(args[0])) {
            if (args.length == 3) {
                ChatServer.sendPrivateMessage(this, args[1], args[2]);
            }
            write("error");
        } else if ("disconnect".equalsIgnoreCase(args[0])) {
            close();
        }
    }

    /**
     * send message to client
     *
     * @param message
     */
    public void write(String message) {
        out.println(message);
    }

    /**
     * thread that recive message of socket
     */
    private class ClientThread extends Thread {

        private final Client client;

        private ClientThread(Client client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.length() > 0) {
                        handleMessage(line);
                    }
                }
            } catch (IOException ex) {
            }
            client.close();
        }
    }
}
