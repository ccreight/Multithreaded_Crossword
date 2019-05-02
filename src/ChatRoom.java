import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ChatRoom {

	public static Vector<ServerThread> serverThreads;
	public static Crossword c;
	public static int playerCount = 0;
	public String fileReadingReport;
	public static boolean firstMove = true;
	public static int counter = 0;
	private Vector<Lock> locks;
	private Vector<Condition> conditions;	
	public static Vector<Integer> scores;
	public static Vector<Boolean> inGame;
	public static int turn;
	public static int wordsGuessed = 0;
	
	@SuppressWarnings("deprecation")
	public ChatRoom(int port) {
		
		while(true) {
		
			ServerSocket ss = null;
			locks = new Vector<Lock>();
			conditions = new Vector<Condition>();
			boolean first = true;
			scores = new Vector<Integer>();
			playerCount = 0;
			counter = 0;
			wordsGuessed = 0;
			
			try {
				
				ss = new ServerSocket(port);
				System.out.println("Listening on port " + port);
				System.out.println("Waiting for players...");
				serverThreads = new Vector<ServerThread>();
				
				boolean fileRead = false;
				c = new Crossword();
				String error = c.readFiles();
				
				while(playerCount == 0 || serverThreads.size() < playerCount || !error.contentEquals("File read succesfully.")) {	
				
					Socket s = ss.accept();
					Lock lock = new ReentrantLock();
					Condition cond = lock.newCondition();
					locks.add(lock);
					conditions.add(cond);
	
					counter++;
					
					if(error.equals("No files read successfully.")) {
						c = new Crossword();
						error = c.readFiles();
					}
					else {
						c.initializeBoard();
					}
					
					System.out.println("Connection from " + s.getInetAddress());
					ServerThread st = new ServerThread(s, this, lock, cond, first, counter+1, c);
					
					if(first) {
						System.out.println("Reading random game file.");
						System.out.println(error);
					}
					
					first = false;
					serverThreads.add(st);
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
				}
				
			} catch(IOException ioe) {
				System.out.println("ioe: " + ioe.getMessage());
				
			} finally {
				try {
					if(ss != null) ss.close();
				} catch(IOException ioe) {
					System.out.println("ioe closing ss: " + ioe.getMessage());
				}
			}
			
			for(int x = 0; x < serverThreads.size(); x++) {
				scores.add(0);
			}
			
			int playerIndex = 0;
			ChatMessage c1 = new ChatMessage("The game is beginning");
			System.out.println("The game is beginning");
			broadcastToAll(c1);
			
			while(wordsGuessed != c.getNumWords()) {
				
				if(playerIndex == serverThreads.size()) {
					playerIndex = 0;
				}
				
				ServerThread player = serverThreads.elementAt(playerIndex);
				
				ChatMessage cm = new ChatMessage(c.printBoard());
				broadcastToAll(cm);
	//			player.sendMessage(cm);
				System.out.println("Sending game board.");
				
				cm = new ChatMessage(c.printHints());
				broadcastToAll(cm);
	//			player.sendMessage(cm);
				
				cm = new ChatMessage("Player " + (playerIndex+1) + "'s turn");
				broadcast(cm, player);
				
//				System.out.println("Player " + (playerIndex+1) + "'s turn");
				
				cm = new ChatMessage("Would you like to answer a question across (a) or down (d)? ");
				player.sendMessage(cm);
				String response = "";
				boolean firstLoop = true;
				String direction = ""; 
				
				while(!direction.equals("a") && !direction.equals("d")) {
					if(!firstLoop) {
						cm = new ChatMessage("That is not a valid option.");
						player.sendMessage(cm);
						cm = new ChatMessage("Would you like to answer a question across (a) or down (d)? ");
						player.sendMessage(cm);
					}
					firstLoop = false;
					
					direction = player.getInput();
				}
				
				cm = new ChatMessage("Which number? ");
				player.sendMessage(cm);
				String number = player.getInput();
				
				while(!c.hasWord(Integer.valueOf(number), direction)) {
					cm = new ChatMessage("That is not a valid option.");
					player.sendMessage(cm);
					cm = new ChatMessage("Which number? ");
					player.sendMessage(cm);
					
					number = player.getInput();
					
				}
				
				cm = new ChatMessage("What is your guess for " + number + " " + direction + "?");
	//			broadcast(cm, player);
				player.sendMessage(cm);
				
				response = player.getInput();
				
				cm = new ChatMessage("Player " + (playerIndex+1) + " guessed \"" + response + "\" for " + number + " " + direction);
				broadcast(cm, player);
	//			player.sendMessage(cm);
				
				boolean correctGuess = c.guess(response, direction, number);
				
				if(!correctGuess) {
					cm = new ChatMessage("That is incorrect!");
					broadcastToAll(cm);
					playerIndex++;
					continue;
				}
				
				else {
					cm = new ChatMessage("That is correct!");
					broadcastToAll(cm);
//					c.removeHint(response);
					scores.set(playerIndex, scores.elementAt(playerIndex)+1);
					wordsGuessed++;
					continue;
				}
				
			}
			
			//print scores here
			ChatMessage cm = new ChatMessage(c.printBoard());
			broadcastToAll(cm);
			System.out.println("Sending game board.");
			cm = new ChatMessage(c.printHints());
			broadcastToAll(cm);
			
			System.out.println("The game has concluded.\nSending scores.");
			
			String finalScore = "Final Score\n";
			int winner = 0;
			
			for(int x = 0; x < playerCount; x++) {
				finalScore += "Player " + (x+1) + " - " + scores.elementAt(x) + " correct answers.\n";
				if(scores.elementAt(x) > scores.elementAt(winner)) {
					winner = x;
				}
			}
			
			boolean tie = false;
			int score = 0;
			
			for(int x = 0; x < playerCount-1; x++) {
				for(int y = x; y < playerCount; y++) {
					if(scores.elementAt(x) == scores.elementAt(y)) {
						tie = true;
						score = scores.elementAt(x);
					}
				}
			}
			
			if(!tie) {finalScore += "\nPlayer " + (winner+1) + " is the winner.";}
			else { finalScore += "There was a tie of " + score + " points!";}
			finalScore += "\n\n\nDisconnect to leave the game.";
			
			cm = new ChatMessage(finalScore);
			broadcastToAll(cm);
			System.out.println("Waiting for players...");
		
		
			
		}
		
	}
	
	
//	public void broadcast(String message, ServerThread currentST) {
	public void broadcast(ChatMessage cm, ServerThread st) {
//		if(message != null) {
		if(cm != null) {
			System.out.println(cm.getMessage());
			
			for(ServerThread s : serverThreads) {
				if(s != st) { //don't want to send message to same client again
					s.sendMessage(cm);
				}
			}
		}
	}
	
	public static void setCount(int c) {
		playerCount = c;
	}
	
	public void broadcastToAll(ChatMessage cm) {
		
		if(cm != null) {
			for(ServerThread s : serverThreads) {
				s.sendMessage(cm);
			}
		}
	}
	
	public void broadcastToPlayers(ChatMessage cm, ServerThread st) {
		if(cm != null) {
			for(ServerThread s : serverThreads){
				if(s != st) {
					s.sendMessage(cm);
				}
			}
		}
	}
	
	public void signal() {
		locks.elementAt(counter%serverThreads.size()).lock();
		conditions.elementAt(counter%serverThreads.size()).signal();
		locks.elementAt(counter%serverThreads.size()).unlock();
		
	}
	
	public static void main(String[] args) {
		ChatRoom cr = new ChatRoom(3456);
	}
	
}
