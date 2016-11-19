package basictaskrunner.webapp.commands;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * To add a new type of command, create a class that can be passed into the
 *  {{@link #addCommand(CommandDef, Supplier)} argument signature, then add a
 *  call to the method in the registry's static constructor.
 *
 */
public class CommandRegistry implements ICommandRegistry {
	public final static CommandDef[] Defs;
	
	private final static Map<String, CommandRegistryItem> Commands;
	
	static {
		Commands = new LinkedHashMap<>();
		addCommand(PatternMatchCommand.Def, PatternMatchCommand::new);
		addCommand(DummyMessageListenCommand.Def, DummyMessageListenCommand::new);
		// add other commands
		
		Defs = Commands.values().stream().map(i -> i.Def).collect(Collectors.toList()).toArray(new CommandDef[] {});
	}
	
	private static void addCommand(CommandDef def, Supplier<? extends ICommand> ctor) {
		CommandRegistryItem item = new CommandRegistryItem();
		item.Ctor = ctor;
		item.Def = def;
		Commands.put(def.command, item);
	}
	
	@Override
	public boolean isValidCommand(String command) {
		return Commands.containsKey(command);
	}
	
	@Override
	public final ICommand createCommandInstance(String command) {
		return Commands.get(command).Ctor.get();
	}
	
	private static class CommandRegistryItem {
		public CommandDef Def;
		public Supplier<? extends ICommand> Ctor;
	}
}

/**
 * Allow creating a statement block that has a return value that can be assigned directly.
 * 
 * e.g. int i = InlineSupplier.get(() -> { for(x : foos) if(x > 0) return x; return -1 } );
 *
 */
class InlineSupplier {
	public static <T> T get(Supplier<T> s) {
		return s.get();
	}
}
