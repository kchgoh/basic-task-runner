package basictaskrunner.webapp.commands;

public interface ICommandRegistry {
	boolean isValidCommand(String command);
	ICommand createCommandInstance(String command);
	
}
