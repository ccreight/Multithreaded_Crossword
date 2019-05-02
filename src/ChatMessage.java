import java.io.Serializable;

public class ChatMessage implements Serializable {
	public static final long serialVersionUID = 1;
	private String message;
	
	public ChatMessage(String message) {
		this.message = message;
	}
	public String getMessage() {
		return message;
	}
	
}
