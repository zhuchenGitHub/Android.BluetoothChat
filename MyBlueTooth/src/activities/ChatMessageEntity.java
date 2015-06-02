package activities;

public class ChatMessageEntity {
	private String date;
	private String message;
	private String name;
	private boolean isGet;

	public ChatMessageEntity(String date, String message, String name,
			boolean isGet) {
		this.date = date;
		this.message = message;
		this.name = name;
		this.isGet = isGet;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDate() {
		return date;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setIsGet(boolean isGet) {
		this.isGet = isGet;
	}

	public boolean getIsGet() {
		return isGet;
	}
}
