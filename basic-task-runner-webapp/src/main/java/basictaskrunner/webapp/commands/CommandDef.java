package basictaskrunner.webapp.commands;

import java.util.ArrayList;
import java.util.List;

public class CommandDef {
	public String command;
	public String displayName;
	public List<CommandArgDef> args = new ArrayList<>();
}
