import java.io.Serializable;
import java.util.List;

public class Message implements Serializable {
    static final long serialVersionUID = 42L;
    //private MessageType type;
    private String content;
    private String sender;
    private String recipient;
    private String username;

    //private String groupName;
    private List<String> groupName;
    public List<String> userNames;

    public Message(String content, String sender, String recipient, String groupName){
        this.content=content;
        this.sender=sender;
        this.recipient=recipient;
        //this.groupName=groupName;
    }

    // getters and setters
    public String getContent() {
        return content;
    }

    public Message(String username) {
        this.username = username;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getGroup() {
        return groupName;
    }

    public void setGroup(List<String> groupName) {
        this.groupName = groupName;
    }

    public List<String> getUserNames() {
        return userNames;
    }

    public void setUser(List<String> userNames) {
        this.userNames = userNames;
    }
}

