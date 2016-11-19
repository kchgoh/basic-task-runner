package basictaskrunner.webapp.commands;

public class CommandArgDef {
	public final String name;
	public final String type;
	public final String defaultValue;
	
	public CommandArgDef(String name, String type) {
		this(name, type, null);
	}
	
	public CommandArgDef(String name, String type, String defaultValue) {
		this.name = name;
		this.type = type;
		this.defaultValue = defaultValue;
	}
}
