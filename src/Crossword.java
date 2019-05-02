import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;

public class Crossword {

	private char[][] board;
	private char[][] solution;
//	private Vector<String> acrossHints;
//	private Vector<String> downHints;
//	private Vector<String> acrossWords;
//	private Vector<String> downWords;
	private Vector<CrosswordObj> clues;
	private boolean valid = false;
	private int totalSize;
	private int minX = Integer.MAX_VALUE, maxX = 0;
	private int minY = Integer.MAX_VALUE, maxY = 0;
	 
	public Crossword() {
//		acrossHints = new Vector<String>();
//		downHints = new Vector<String>();
//		acrossWords = new Vector<String>();
//		downWords = new Vector<String>();
		clues = new Vector<CrosswordObj>();
	}
	
	public String readFiles() {
		
		File folder = new File("gamedata");
		File[] listOfFiles = folder.listFiles();
		
		boolean success = false;
		
		for(File f : listOfFiles) {

			try {
				
				URL url = getClass().getResource(f.getPath());
				BufferedReader br = new BufferedReader(new FileReader(f.getPath()));
				
				String line = br.readLine();

				if(line == null || line.isEmpty() || (!line.trim().toLowerCase().equals("across")&&!line.trim().toLowerCase().equals("down"))) {
					continue;
				}
				
				boolean isAcross = false;
				
				if(line.trim().toLowerCase().equals("across")) {
					isAcross = true;
				}
				
//				boolean hasWordDown = false;
				boolean error = false;
				
				line = br.readLine();
				
				while(line != null) {
					
					if(line.trim().toLowerCase().equals("across") && isAcross) {
						error = true;
						break;
					}
					
					if(line.trim().toLowerCase().equals("down") && !isAcross) {
						error = true;
						break;
					}
					
					if(line.trim().toLowerCase().equals("across")) {
						isAcross = true;
						line = br.readLine();
						continue;
					}
					
					if(line.trim().toLowerCase().equals("down")) {
						isAcross = false;
						line = br.readLine();
						continue;
					}
					
//					if(line.trim().toLowerCase().equals("down")) {
//						hasWordDown = true;
//						line = br.readLine();
//						continue;
//					}
					
					StringTokenizer st = new StringTokenizer(line, "|");
					
					if(st.countTokens() != 3) {
						error = true;
						break;
					}
					
					int num = 0;
					String word = "", hint = "";
					
					try { //number of hint
						String str = (String)st.nextElement();
						str = str.trim();
						if(str == "") {
							error = true;
							break;
						}
						num = Integer.valueOf(str);
					} catch(Exception e) {
						error = true;
						break;
					}
					try { //word in puzzle
						word = (String)st.nextElement();
						word = word.trim();
						word = word.toLowerCase();
						
						if(word == "") {
							error = true;
							break;
						}
//						if(hasWordDown) {
//							downWords.add(word);
//						}
//						else {
//							acrossWords.add(word);
//						}
						
					} catch(Exception e) {
						error = true;
						break;
					}
					try { //hint
						hint = (String)st.nextElement();
						hint = hint.trim();
						if(hint == "") {
							error = true;
							break;
						}
					} catch(Exception e) {
						error = true;
						break;
					}
//					
//					if(hasWordDown) {
//						downHints.add(num + " " + hint);
//					}
//					else {
//						acrossHints.add(num + " " + hint);
//					}
					
					CrosswordObj c = new CrosswordObj(num, word, hint, isAcross);
					clues.add(c);
					
					line = br.readLine();
					
				}
				
				//if(!hasWordDown || error) {
				if(error) {
//					downHints = new Vector<String>();
//					acrossHints = new Vector<String>();
					clues = new Vector<CrosswordObj>();
					continue;
				}
				else {
					success = true;
					break;
				}
				
			} catch(FileNotFoundException fnfe) {
				break;
			} catch (IOException e) {
				break;
			}
			
		}
		
		if(success) {
			
			valid = true;
			totalSize = 0;
			
			for(int x = 0; x < clues.size(); x++) {
				totalSize += clues.get(x).getWord().length();
			}
			
//			for(int x = 0; x < acrossWords.size(); x++) {
//				totalSize += acrossWords.elementAt(x).length();
//			}
//			
//			for(int x = 0; x < downWords.size(); x++) {
//				totalSize += downWords.elementAt(x).length();
//			}
			
			totalSize *= 2;
			
			board = new char[totalSize][totalSize];
			solution = new char[totalSize][totalSize];
			
			for(int x = 0; x < totalSize; x++) {
				for(int y = 0; y < totalSize; y++) {
					board[x][y] = ' ';
					solution[x][y] = ' ';
				}
			}
			
		/*	for(int x = 0; x < clues.size()-1; x++) {
				for(int y = x+1; y < clues.size(); y++) {
					if(clues.get(x).getWord().length() < clues.get(y).getWord().length()) {
						CrosswordObj c = clues.get(x);
						clues.set(x, clues.get(y));
						clues.set(y, c);
					}
				}
			}*/
			
			/*
			for(int x = 0; x < acrossWords.size(); x++) {
				for(int y = x+1; y < acrossWords.size(); y++) {
					if(acrossWords.get(x).length() < acrossWords.get(y).length()) {
						String s = acrossWords.get(x);
						acrossWords.set(x, acrossWords.get(y));
						acrossWords.set(y, s);
					}
				}
			}
			
			for(int x = 0; x < downWords.size(); x++) {
				for(int y = x+1; y < downWords.size(); y++) {
					if(downWords.get(x).length() < downWords.get(y).length()) {
						String s = downWords.get(x);
						downWords.set(x, downWords.get(y));
						downWords.set(y, s);
					}
				}
			}
			*/
			
			return "File read succesfully.";
		}
		else {
			valid = false;
			return "No files read successfully.";
		}
	}
	
	public void removeHint(String word) {
		for(int x = 0; x < clues.size(); x++) {
			if(clues.get(x).getWord().equals(word)){
				clues.remove(x);
			}
		}
	}
	
	public boolean helper(int coordX, int coordY, int wordIndex) {
		
		boolean isValid = true;
		int letterIndex = clues.get(wordIndex).getWord().indexOf(solution[coordX][coordY]);
		CrosswordObj intersection = null;
		int intersectionIndex = 0;
		
		for(int x = 0; x < clues.size(); x++) {
			if(clues.get(x).getNum() == clues.get(wordIndex).getNum() && clues.get(x).getWord() != clues.get(wordIndex).getWord()) {
				intersection = clues.get(x);
				intersectionIndex = x;
				break;
			}
		}
		
		if(intersection != null && intersection.getPlaced()) {
			intersection = null;
		}
		
		/*if(board[coordX][coordY-1] != ' ' && board[coordX][coordY-1] > 47 && board[coordX][coordY-1] < 58 && board[coordX][coordY-1] != clues.get(wordIndex).getNum()) {
			isValid = false;
		}
		
		if(board[coordX-1][coordY] != ' ' && board[coordX-1][coordY] > 47 && board[coordX-1][coordY] < 58 && board[coordX-1][coordY] != clues.get(wordIndex).getNum()) {
			isValid = false;
		}*/
		
		if(clues.get(wordIndex).getIsAcross()) {
			int index = 0;
			if(solution[coordX][coordY - letterIndex - 1] != ' ' || solution[coordX][coordY + clues.get(wordIndex).getWord().length() - letterIndex] != ' ') {
				isValid = false;
			}
			for(int y = coordY - letterIndex; y < coordY + clues.get(wordIndex).getWord().length() - letterIndex; y++) {
				if(solution[coordX][y] != ' ' && solution[coordX][y] != clues.get(wordIndex).getWord().charAt(index)) {
					isValid = false;
					break;
				}
				if((solution[coordX-1][y] != ' ' || solution[coordX+1][y] != ' ') && solution[coordX][y] != clues.get(wordIndex).getWord().charAt(index)){
					isValid = false;
					break;
				}
				index++;
			}
			if(intersection != null) {
				index = 1;
				for(int x = 1; x < intersection.getWord().length(); x++) {
					if(solution[coordX + x][coordY] != ' ' && solution[coordX + x][coordY] != intersection.getWord().charAt(index)) {
						isValid = false;
						break;
					}
					if((solution[coordX + x][coordY-1] != ' ' || solution[coordX + x][coordY+1] != ' ') && solution[coordX + x][coordY] != intersection.getWord().charAt(index)) {
						isValid = false;
						break;
					}
					index++;
				}
				
			}
		}
		else {
			int index = 0;
			if(solution[coordX - letterIndex - 1][coordY] != ' ' || solution[coordX + clues.get(wordIndex).getWord().length() - letterIndex][coordY] != ' ') {
				isValid = false;
			}
			for(int x = coordX - letterIndex; x < coordX + clues.get(wordIndex).getWord().length() - letterIndex; x++) {
				if(solution[x][coordY] != ' ' && solution[x][coordY] != clues.get(wordIndex).getWord().charAt(index)) {
					isValid = false;
					break;
				}
				if((solution[x][coordY-1] != ' ' || solution[x][coordY+1] != ' ') && solution[x][coordY] != clues.get(wordIndex).getWord().charAt(index)) {
					isValid = false;
					break;
				}
				index++;
			}
			if(intersection != null) {
				index = 1;
				//int intersectionLetterIndex = clues.get(wordIndex).getWord().charAt(0);
				for(int x = 1; x < intersection.getWord().length(); x++) {
					if(solution[coordX][coordY+x] != ' ' && solution[coordX][coordY+x] != intersection.getWord().charAt(index)) {
						isValid = false;
						break;
					}
					if((solution[coordX+1][coordY+x] != ' ' || solution[coordX-1][coordY+x] != ' ') && solution[coordX][coordY+x] != intersection.getWord().charAt(index)) {
						isValid = false;
						break;
					}
					index++;
				}
				
			}
		}
		
		if(isValid) {
			if(clues.get(wordIndex).getIsAcross()) {
				int index = 0;
				minY = Math.min(minY, coordY - letterIndex - 1);
				maxY = Math.max(maxY, coordY + clues.get(wordIndex).getWord().length() - letterIndex);
				solution[coordX][coordY - letterIndex - 1] = (char)(clues.get(wordIndex).getNum()+48);
				board[coordX][coordY - letterIndex - 1] = (char)(clues.get(wordIndex).getNum()+48);
//				System.out.print("Placing: ");
				for(int y = coordY - letterIndex; y < coordY + clues.get(wordIndex).getWord().length() - letterIndex; y++) {
//					System.out.print(clues.get(wordIndex).getWord().charAt(index) + " on " + solution[coordX][y]);
					solution[coordX][y] = clues.get(wordIndex).getWord().charAt(index);
					board[coordX][y] = '_';
					index++;
				}
				if(intersection != null) {
					minX = Math.min(minX, coordX - intersection.getWord().length() - 3);
					maxX = Math.max(maxX, coordX + clues.get(wordIndex).getWord().length());
					for(int x = 1; x < intersection.getWord().length(); x++) {
						solution[coordX+x][coordY-letterIndex] = intersection.getWord().charAt(x);
						board[coordX+x][coordY-letterIndex] = '_';
					}
				}
			}
			else {
				int index = 0;
				minX = Math.min(minX, coordX - letterIndex - 2);
				maxX = Math.max(maxX, coordX + clues.get(wordIndex).getWord().length());
				solution[coordX - letterIndex - 1][coordY] = (char)(clues.get(wordIndex).getNum()+48);
				board[coordX - letterIndex - 1][coordY] = (char)(clues.get(wordIndex).getNum()+48);
				for(int x = coordX - letterIndex; x < coordX + clues.get(wordIndex).getWord().length() - letterIndex; x++) {
					solution[x][coordY] = clues.get(wordIndex).getWord().charAt(index);
					board[x][coordY] = '_';
					index++;
				}
				if(intersection != null) {
					minY = Math.min(minX, coordY - intersection.getWord().length() - 1);
					maxY = Math.max(maxY, coordY + intersection.getWord().length());
					for(int x = 1; x < intersection.getWord().length(); x++) {
						solution[coordX-letterIndex][coordY+x] = intersection.getWord().charAt(x);
						board[coordX-letterIndex][coordY+x] = '_';
					}
				}
			}
			
			clues.get(wordIndex).setPlaced(true);
			if(intersection != null) {
				clues.get(intersectionIndex).setPlaced(true);
			}
			
//			printSolution();
			return true;
		}
		else {
			return false;
		}
	}
	
	public void initializeBoard() {
		
		if(!valid) {
			return;
		}
		
		int firstWordIndex = 0;
		boolean success = false;
		int wordsPlaced = 0;
		
		Vector<CrosswordObj> holder = new Vector<CrosswordObj>();
		
		for(int x = 0; x < clues.size(); x++) {
			CrosswordObj obj = clues.get(x);
			holder.add(obj);
		}
		
		while(wordsPlaced != clues.size()) {
			
			if(firstWordIndex == clues.size()) {
				firstWordIndex = 0;
			}
			
			minX = totalSize/2-1;
			maxX = totalSize/2+1;
			minY = totalSize/2-1;
			maxY = totalSize/2+1;
			//placing first word onto the board
			if(clues.get(firstWordIndex).getIsAcross()) {
				board[totalSize/2][totalSize/2-1] = (char)(clues.get(firstWordIndex).getNum()+48);
				solution[totalSize/2][totalSize/2-1] = (char)(clues.get(firstWordIndex).getNum()+48);
				maxY = Math.max(maxY, maxY + clues.get(firstWordIndex).getWord().length());
				for(int y = 0; y < clues.get(firstWordIndex).getWord().length(); y++) {
					board[totalSize/2][totalSize/2 + y] = '_';
					solution[totalSize/2][totalSize/2 + y] = clues.get(firstWordIndex).getWord().charAt(y);
				}
				for(int x = 0; x < clues.size(); x++) {
					if(clues.get(x).getNum() == clues.get(firstWordIndex).getNum() && clues.get(x).getWord() != clues.get(firstWordIndex).getWord()) {
						for(int y = 0; y < clues.get(x).getWord().length(); y++) {
							board[totalSize/2 + y][totalSize/2] = '_';
							solution[totalSize/2+y][totalSize/2] = clues.get(x).getWord().charAt(y);
						}
						break;
					}
				}
			}
			else if(!clues.get(firstWordIndex).getIsAcross()) {
				board[totalSize/2-1][totalSize/2] = (char)(clues.get(firstWordIndex).getNum()+48);
				solution[totalSize/2-1][totalSize/2] = (char)(clues.get(firstWordIndex).getNum()+48);
				maxX = Math.max(maxX, maxX + clues.get(firstWordIndex).getWord().length());
				for(int y = 0; y < clues.get(firstWordIndex).getWord().length(); y++) {
					board[totalSize/2 + y][totalSize/2] = '_';
					solution[totalSize/2 + y][totalSize/2] = clues.get(firstWordIndex).getWord().charAt(y);
				}
				for(int x = 0; x < clues.size(); x++) {
					if(clues.get(x).getNum() == clues.get(firstWordIndex).getNum() && clues.get(x).getWord() != clues.get(firstWordIndex).getWord()) {
						for(int y = 0; y < clues.get(x).getWord().length(); y++) {
							board[totalSize/2][totalSize/2 + y] = '_';
							solution[totalSize/2][totalSize/2 + y] = clues.get(x).getWord().charAt(y);
						}
						break;
					}
				}
			}
			
			clues.get(firstWordIndex).setPlaced(true);
			wordsPlaced = 1;
			
			Collections.shuffle(clues);
			
			for(int w = 0; w < clues.size(); w++) {
				for(int x = 0; x < totalSize; x++) {
					for(int y = 0; y < totalSize; y++) {
						if(board[x][y] != ' ' && solution[x][y] > 64 && clues.get((w+firstWordIndex)%clues.size()).getWord().indexOf(solution[x][y]) != -1) {
							
							if((w+firstWordIndex)%clues.size() != firstWordIndex && !clues.get((w+firstWordIndex)%clues.size()).getPlaced()) {
								success = helper(x,y,(w+firstWordIndex)%clues.size());
								if(success) {
									for(int i = 0; i < clues.size(); i++) {
										if(clues.get((w+firstWordIndex)%clues.size()).getNum() == clues.get(i).getNum() && (w+firstWordIndex)%clues.size() != i) {
											wordsPlaced++;
//											clues.get(i).setPlaced(true);
										}
									}
									wordsPlaced++;
									break; //couldn't place another word here
								}
								
							}
							
						}
						
					}
				}
			}
			
			if(wordsPlaced != clues.size()) {
				for(int x = 0; x < totalSize; x++) {
					for(int y = 0; y < totalSize; y++) {
						board[x][y] = ' ';
						solution[x][y] = ' ';
					}
				}
				for(int x = 0; x < clues.size(); x++) {
					clues.get(x).setPlaced(false);
				}
				firstWordIndex++;
				wordsPlaced = 0;
			}
			
			else {
				break;
			}
			
		}
		
		clues = new Vector<CrosswordObj>();
		
		for(int x = 0; x < holder.size(); x++) {
			CrosswordObj obj = holder.get(x);
			clues.add(obj);
		}
		
	}
	
	public boolean guess(String word, String direction, String number) {
		
		word = word.toLowerCase();
		boolean found = false;
		int indexOfGuess = 0;
		boolean isAcross = (direction.contentEquals("a"));
		
		for(int x = 0; x < clues.size(); x++) {
			if(clues.get(x).getWord().equals(word) && clues.get(x).getIsAcross() == isAcross && clues.get(x).getNum() == Integer.valueOf(number)) {
				found = true;
				indexOfGuess = x;
				break;
			}
		}
		
		if(!found) {
			return false;
		}
		
		for(int x = 0; x < board.length; x++) {
			for(int y = 0; y < board.length; y++) {
				if(solution[x][y] == word.charAt(0)) {
					if(clues.get(indexOfGuess).getIsAcross() && y+1 < board.length && solution[x][y+1] == word.charAt(1)) {
						int index = 0;
						boolean valid = true;
						for(int z = 0; z < word.length(); z++) {
							if(solution[x][y+z] == word.charAt(index)) {
								index++;
							}
							else {
								valid = false;
								break;
							}
						}
						if(valid) {
							for(int z = 0; z < word.length(); z++) {
								board[x][y+z] = solution[x][y+z];
							}
						}
						return true;
					}
					else if(!clues.get(indexOfGuess).getIsAcross() && x+1 < board.length && solution[x+1][y] == word.charAt(1)) {
						int index = 0;
						boolean valid = true;
						for(int z = 0; z < word.length(); z++) {
							if(solution[x+z][y] == word.charAt(index)) {
								index++;
							}
							else {
								valid = false;
								break;
							}
						}
						if(valid) {
							for(int z = 0; z < word.length(); z++) {
								board[x+z][y] = solution[x+z][y];
							}
						}
						return true;
					}
				}
			}
		}
		
		return false;
		
	}
	
	public String printHints() {
		
		String hints = "";
		
		if(clues.get(0).getIsAcross()) { hints += "Across\n"; }
		else { hints += "Down\n"; }
		
		for(int x = 0; x < clues.size(); x++) {
			if(x != 0 && clues.get(x-1).getIsAcross() && !clues.get(x).getIsAcross()) {
//				System.out.println("Down");
				hints += "Down\n";
			}
			else if(x != 0 && !clues.get(x-1).getIsAcross() && clues.get(x).getIsAcross()) {
//				System.out.println("Across");
				hints += "Across\n";
			}
//			System.out.println(clues.get(x).getNum() + " " + clues.get(x).getHint());
			hints += clues.get(x).getNum() + " " + clues.get(x).getHint() + "\n";
		}
		return hints;
	}
	
	public String printBoard() {
		
		String result = "";
		
		for(int x = minX; x <= maxX; x++) {
			for(int y = minY; y <= maxY; y++) {
//				System.out.print(board[x][y] + " ");
				result += board[x][y] + " ";
			}
//			System.out.println();
			result += "\n";
		}
		return result;
	}
	
	public void printSolution() {
		
		for(int x = minX; x <= maxX; x++) {
			for(int y = minY; y <= maxY; y++) {
				System.out.print(solution[x][y] + " ");
			}
			System.out.println();
		}
	}
	
	public boolean hasWord(int index, String direction) {
		
		boolean isAcross = true;
		if(direction.equals("d")){
			isAcross = false;
		}
		
		for(int x = 0; x < clues.size(); x++) {
			if(clues.get(x).getIsAcross() == isAcross && clues.get(x).getNum() == index) {
				return true;
			}
		}
		
		return false;
	}

	public int getNumWords() {
		return clues.size();
	}
	
	/*public static void main(String[] args) {
		Crossword c = new Crossword();
		c.readFiles();
//		c.printAcrossHints();
//		c.printDownHints();
		c.printHints();
		c.initializeBoard();

		System.out.println(c.printBoard());
		c.printSolution();
		
		for(int x = 0; x < 5; x++) {
			System.out.println("Guess?");
			Scanner s = new Scanner(System.in);
			String str = s.nextLine();
			c.guess(str);
			c.printBoard();
		}	
		
	}*/
	
}

