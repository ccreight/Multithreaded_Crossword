
public class CrosswordObj {

	private int num;
	private String word;
	private String hint;
	private boolean isAcross;
	private boolean placed;
	
	CrosswordObj(int n, String w, String h, boolean a){
		num = n;
		word = w;
		hint = h;
		isAcross = a;
		placed = false;
	}
	
	public void setNum(int n) {
		num = n;
	}
	public int getNum() {
		return num;
	}
	public void setWord(String w) {
		word = w;
	}
	public String getWord() {
		return word;
	}
	public void setHint(String h) {
		hint = h;
	}
	public String getHint() {
		return hint;
	}
	public void setIsAcross(boolean a) {
		isAcross = a;
	}
	public boolean getIsAcross() {
		return isAcross;
	}
	public void setPlaced(boolean p) {
		placed = p;
	}
	public boolean getPlaced() {
		return placed;
	}
	
}
