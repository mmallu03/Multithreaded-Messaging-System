import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.control.ListView;
/*
 * Clicker: A: I really get it    B: No idea what you are talking about
 * C: kind of following
 */

public class Server{

	int count = 1;
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	//Map<ClientThread, String> clientUsernames = new HashMap<>();
	TheServer server;
	private Consumer<Serializable> callback;


	Server(Consumer<Serializable> call){

		callback = call;
		server = new TheServer();
		server.start();
	}


	public class TheServer extends Thread{

		public void run() {

			try(ServerSocket mysocket = new ServerSocket(5555);){
				System.out.println("Server is waiting for a client!");


				while(true) {
					ClientThread c = new ClientThread(mysocket.accept(), count, "username");
					callback.accept(c.getUsername() + " has connected to server");
//					callback.accept("client has connected to server: " + "client #" + count);
					clients.add(c);
					c.start();

					count++;

				}

			}//end of try
			catch(Exception e) {
				callback.accept("Server socket did not launch");
			}
		}//end of while
	}


	class ClientThread extends Thread{


		Socket connection;
		int count;
		ObjectInputStream in;
		ObjectOutputStream out;
		String username;

		ClientThread(Socket s, int count, String username) {
			this.connection = s;
			this.count = count;
			this.username = username;
		}

		public String getUsername() {
			return username;
		}

		public void updateClients(String message) {
			for(int i = 0; i < clients.size(); i++) {
				ClientThread t = clients.get(i);
				try {
					t.out.writeObject(message);
				}
				catch(Exception e) {}
			}
		}

		public void run(){

			try {
				in = new ObjectInputStream(connection.getInputStream());
				out = new ObjectOutputStream(connection.getOutputStream());
				connection.setTcpNoDelay(true);
			}
			catch(Exception e) {
				System.out.println("Streams not open");
			}

			updateClients("new client on server: client #"+count);

			while(true) {
				try {
					String data = in.readObject().toString();
					callback.accept("client: " + count + " sent: " + data);
					updateClients("client #"+count+" said: "+data);
					//I added
					if ("GET_CONNECTED_USERS".equals(data)) {
						sendConnectedUsers();
					}
				}
				catch(Exception e) {
					callback.accept("OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
					updateClients("Client #"+count+" has left the server!");
					clients.remove(this);
					break;
				}
			}
		}//end of run
		//I added
		private void sendConnectedUsers() {
			ArrayList<String> connectedUsers = new ArrayList<>();
			for (ClientThread client : clients) {
				connectedUsers.add("Client #" + client.count);
			}
			try {
				out.writeObject(connectedUsers);
			} catch (IOException e) {
				//e.printStackTrace();
			}
		}




	}//end of client thread
}