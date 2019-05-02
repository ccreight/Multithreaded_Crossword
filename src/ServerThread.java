import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class ServerThread extends Thread {

//	private BufferedReader br;
//	private PrintWriter pw;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private ChatRoom cr;
	private Lock lock;
	private Condition cond;
	boolean first;
	private boolean firstEntrance = true;
	public int ID;
	private InetAddress IP;
	private boolean waiting = true;
	private Crossword cross;
	
	public ServerThread(Socket s, ChatRoom cr, Lock l, Condition c, boolean isFirst, int ID, Crossword cross) {
		
		this.cr = cr;
		lock = l;
		cond = c;
		first = isFirst;
		this.cross = cross;
		this.ID = ID;
		IP = s.getInetAddress();
		
		try {
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			this.start();
			
		} catch(IOException ioe) {
			System.out.println("ioe in ServerThread: " + ioe.getMessage());
		}
		
	}
	
	public void sendMessage(ChatMessage cm) {
		try {
			oos.writeObject(cm);
			oos.flush();
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
	}
	
	public String getInput() {
		String response = "";
		try {
			ChatMessage cm = (ChatMessage)ois.readObject();
			response = cm.getMessage().toLowerCase();
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}
	
	public void run() {
		
		lock.lock();
		
		while(firstEntrance) {
		
			firstEntrance = false;
			
			try {
				if(!first) {
					ChatMessage cm = new ChatMessage("There is a game waiting for you");
					sendMessage(cm);
					for(int i = 1; i < ID-1; i++) {
						ChatMessage c = new ChatMessage("Player " + i + " has already joined.");
						sendMessage(c);
					}
					
					cond.await();
				}
				else {
					
					ChatMessage cm = new ChatMessage("How many players will there be? ");
					sendMessage(cm);
					String response = "";
					
					try {
//						response = br.readLine();
						cm = (ChatMessage)ois.readObject();
						response = cm.getMessage();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					
					ChatMessage c = new ChatMessage("Number of players: " + response);
					cr.broadcast(c, this);
//					ChatRoom.playerCount = Integer.valueOf(response);
					ChatRoom.setCount(Integer.valueOf(response));
					int numPlayers = Integer.valueOf(response);
					
					for(int i = 2; i <= numPlayers; i++) {
						c = new ChatMessage("Waiting for Player " + i + ".");
						this.sendMessage(c);
						System.out.println("Waiting for Player " + i + ".");
					}
					
				}
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			String line = "";
		
		}
			
			if(!firstEntrance && waiting) {
				
				waiting = false;
				int index = ID;
				int currentCount = ChatRoom.counter;
				
				while(ChatRoom.serverThreads.size() != ChatRoom.playerCount) {
					if(currentCount < ChatRoom.counter) {
						ChatMessage c = new ChatMessage("Player " + ChatRoom.counter + " has joined from " + IP);
						sendMessage(c);
						currentCount = ChatRoom.counter;
					}
				}
				
			}
		
		cr.signal();
		lock.unlock();
		
	}
	
}
