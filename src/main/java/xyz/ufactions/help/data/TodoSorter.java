package xyz.ufactions.help.data;

import java.util.Comparator;

public class TodoSorter implements Comparator<TodoTask> {

	@Override
	public int compare(TodoTask a, TodoTask b) {
		if (a.getPriority() > b.getPriority())
			return -1;
		if (a.getStatus() == TodoStatus.DONE)
			return -1;
		return 0;
	}
}