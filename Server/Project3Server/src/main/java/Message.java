
import java.io.Serializable;
import java.util.List;

public class Message implements Serializable {
    static final long serialVersionUID = 42L;
    //private MessageType type;
    private String content;
    private String sender;
    private String recipient;

    //private String groupName;
    private List<String> groupName;
    private List<String> userNames;

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


