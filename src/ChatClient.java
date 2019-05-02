import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient extends Thread {

//	private BufferedReader br;
//	private PrintWriter pw;
	public static int count;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	//private Lock playerLock;
	//private Condition theirTurn;
	
	public ChatClient(String hostname, int port) {
	//public ChatClient() {
		Scanner scan = new Scanner(System.in);

		try {
			Socket s = new Socket(hostname, port);
			//br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			//pw = new PrintWriter(s.getOutputStream());
			
			ois = new ObjectInputStream(s.getInputStream());
			oos = new ObjectOutputStream(s.getOutputStream());
			
			this.start();
			
			while(true) {
				String line = scan.nextLine();
				ChatMessage cm = new ChatMessage(line);
				oos.writeObject(cm);
				oos.flush();
//				pw.println(line);
//				pw.flush();
			}
			
		} catch (IOException ioe) {
			System.out.println("ioe in ChatClient constructor: " + ioe.getMessage());
		}
		
	}
	
	
	
	
	public void run() {
		try {
			while(true) {
//				String line = br.readLine();
//				System.out.println(line);
				ChatMessage cm = (ChatMessage)ois.readObject();
				System.out.println(cm.getMessage());
			}
			
		} catch(IOException ioe) {
			System.out.println("ioe reading from server: " + ioe.getMessage());
		} catch (ClassNotFoundException cnfe) {
			System.out.println("cnfe: " + cnfe.getMessage());
		}
		
	}
	
	public static void main(String[] args) {
		
		
		System.out.println("Welcome to 201 Crossword!");
		System.out.print("Enter the server hostname: ");
		Scanner sc = new Scanner(System.in);
		String hostname = sc.nextLine();
		System.out.print("Enter the server port: ");
		int port = Integer.valueOf(sc.nextLine());
		new ChatClient(hostname, port);
		
	}
	
}
