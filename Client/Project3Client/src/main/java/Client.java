import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class Client extends Thread{


	Socket socketClient;

	ObjectOutputStream out;
	ObjectInputStream in;

	private Consumer<Serializable> callback;

	ArrayList<String> clients= new ArrayList<>();
	private boolean usernameSubmitted = false;
	Client(Consumer<Serializable> call){

		callback = call;
	}

	public void run() {

		try {
			socketClient= new Socket("127.0.0.1",5555);
			out = new ObjectOutputStream(socketClient.getOutputStream());
			in = new ObjectInputStream(socketClient.getInputStream());
			socketClient.setTcpNoDelay(true);
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		while (true) {
			try {
				Message data=(Message)in.readObject();
				callback.accept((Message)data);
				for(int i=0; i<data.userNames.size(); i++){
					clients.add(data.userNames.get(i));
				}
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public void send(String data) {

		try {
			out.writeObject(data);
			out.flush();
			out.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void validateUsername(String username) {
		if (usernameSubmitted) {
			Alert alert = new Alert(Alert.AlertType.ERROR, "Username already submitted.", ButtonType.OK);
			alert.showAndWait();
		}
		else {
			send(username);
			usernameSubmitted = true;
		}
	}

	public void start() {
		try {
			socketClient = new Socket("127.0.0.1", 5555);
			out = new ObjectOutputStream(socketClient.getOutputStream());
			in = new ObjectInputStream(socketClient.getInputStream());
			socketClient.setTcpNoDelay(true);
			String username = "";
			out.writeObject(username); // sends username to server
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void requestConnectedUsers() {
		// Send a request to the server to get the list of connected users
		send("GET_CONNECTED_USERS");
	}
}
