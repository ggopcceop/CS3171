import java.util.LinkedList;
import java.util.List;

/**
 * class of a room
 */
public class Room {

    private String roomName;  //room name
    private LinkedList<Client> members;  //members in this room
    private final int MAX_CLIENT;   //max number of members allow in this room

    public Room(String name, int maxClient) {
        this.roomName = name;
        this.MAX_CLIENT = maxClient;
        members = new LinkedList<Client>();
    }

    /**
     * boardcast message to all clients except the sender
     *
     * @param message
     * @param sender
     */
    public void boardcast(String message, Client sender) {
        for (Client client : members) {
            if (!client.equals(sender)) {
                client.write(message);
            }
        }
    }

    /**
     * boardcast message to all clients
     *
     * @param message
     */
    public void boardcast(String message) {
        for (Client client : members) {
            client.write(message);
        }
    }

    /**
     * get room name
     *
     * @return
     */
    public String getRoomName() {
        return roomName;
    }

    /**
     * get members in the room
     *
     * @return
     */
    public List<Client> getMembers() {
        return new LinkedList(members);
    }

    /**
     * add a member into this room
     *
     * @param member
     */
    public void addMember(Client member) {
        if (members.size() < MAX_CLIENT) {
            this.boardcast(member.getName() + " enter the chat room!", member);
            members.add(member);
            member.setRoom(this);
        }

    }

    /**
     * remove the member out of room
     *
     * @param member
     */
    public void removeMember(Client member) {
        this.boardcast(member.getName() + " leave the chat room!", member);
        members.remove(member);
        member.setRoom(null);
    }

    /**
     * get remaining space of this room
     *
     * @return
     */
    public int getRemainingSpace() {
        return MAX_CLIENT - members.size();
    }
}
