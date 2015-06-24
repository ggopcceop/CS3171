import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class ChatServer {

    private static final int port = 1194; //default port to start server
    private static LinkedList<Room> rooms; //list of rooms
    private static LinkedList<Client> clients; //list of clients
    private static int MAX_CLIENT; //max clients allow in the server

    /**
     * Initialize server data
     */
    public static void init() {
        rooms = new LinkedList<Room>();
        clients = new LinkedList<Client>();
        readConfig();
    }

    /**
     * add a room to this server
     *
     * @param name name of the room
     * @param max max size of the room
     */
    private static void addRoom(String name, int max) {
        Room room = new Room(name, max);
        rooms.addLast(room);
    }

    /**
     * add a client to the server
     *
     * @param client the client
     */
    public static void addClient(Client client) {
        if (clients.size() < MAX_CLIENT) {
            clients.add(client);
        }

    }

    /**
     * remove the client from server
     *
     * @param client the client
     */
    public static void removeClient(Client client) {
        clients.remove(client);
    }

    /**
     * get clients
     *
     * @return clients
     */
    public static List<Client> getClients() {
        return new LinkedList(clients);
    }

    /**
     * get rooms
     *
     * @return rooms
     */
    public static List<Room> getRooms() {
        return new LinkedList(rooms);
    }

    /**
     * read config file
     */
    private static void readConfig() {
        Scanner config = new Scanner(ChatServer.class.
                getResourceAsStream("config.txt"));
        MAX_CLIENT = config.nextInt();
        String roomName;
        int maxClient;
        while (config.hasNextLine()) {
            roomName = config.next();
            maxClient = config.nextInt();
            addRoom(roomName, maxClient);
        }
    }

    /**
     * main the start the server
     *
     * @param args
     */
    public static void main(String[] args) {
        init();
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server listening on port " + port);
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    Client client = new Client(socket);
                    ChatServer.addClient(client);
                    client.write("Hello! " + client.getName());
                    System.out.println("Connect a client: " + client.getName()
                            + " form " + socket.getLocalAddress());
                } catch (IOException ex) {
                }
            }
        } catch (IOException ex) {
            System.out.println("Can not create socket");
            System.exit(1);
        }
    }

    /**
     * change the name of a client
     *
     * @param client client that change name
     * @param newName the new name
     */
    public static void changeName(Client client, String newName) {
        for (Client c : ChatServer.getClients()) {
            if (c.getName().equals(newName) && !c.equals(client)) {
                client.write("duplicate");
                return;
            }
        }
        if (client.getRoom() != null) {
            client.getRoom().boardcast(client.getName() + " changed name to "
                    + newName);
        }
        System.out.println(client.getName() + " changed name to " + newName);

        client.setName(newName);
        client.write("ok");

    }

    /**
     * send the client list of rooms
     *
     * @param client
     */
    public static void showRooms(Client client) {
        StringBuilder sb = new StringBuilder();
        for (Room r : rooms) {
            sb.append(r.getRoomName());
            sb.append(" ");
            sb.append(r.getRemainingSpace());
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length() - 1);
        client.write(sb.toString());
    }

    /**
     * send the client members in room
     *
     * @param client
     */
    public static void showMembers(Client client) {
        if (client.getRoom() != null) {
            StringBuilder sb = new StringBuilder();
            for (Client c : client.getRoom().getMembers()) {
                sb.append(c.getName());
                sb.append(", ");
            }
            sb.delete(sb.length() - 2, sb.length() - 1);
            client.write(sb.toString());
        }
    }

    /**
     * let a client to join to a room
     *
     * @param client the client
     * @param room room get into
     */
    public static void joinRoom(Client client, String room) {
        for (Room r : ChatServer.getRooms()) {
            if (r.getRoomName().equalsIgnoreCase(room)) {
                if (room != null) {
                    client.leaveRoom();
                }
                r.addMember(client);
                client.write("ok");
                return;
            }
        }
    }

    /**
     * send a private message from sender to a reicer
     *
     * @param sender sender
     * @param revicer revicer
     * @param Message message
     */
    public static void sendPrivateMessage(Client sender, String revicer,
            String Message) {
        for (Client r : clients) {
            if (r.getName().equalsIgnoreCase(revicer)) {
                r.write(sender.getName() + "->you: " + Message);
                sender.write("sent");
                return;
            }
        }
    }
}
