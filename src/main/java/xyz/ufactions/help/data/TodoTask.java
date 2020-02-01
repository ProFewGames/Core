package xyz.ufactions.help.data;

public class TodoTask {

	private int id;
	private TodoStatus status;
	private String task;
	private int priority;
	private String submitter;

	public TodoTask(int id, String task, String submitter, int priority, TodoStatus status) {
		this.task = task;
		this.id = id;
		this.submitter = submitter;
		this.priority = priority;
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public String getSubmitter() {
		return submitter;
	}

	public int getPriority() {
		return priority;
	}

	public TodoStatus getStatus() {
		return status;
	}

	public String getTask() {
		return task;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public void setStatus(TodoStatus status) {
		this.status = status;
	}
}