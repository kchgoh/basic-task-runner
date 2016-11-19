package basictaskrunner.webapp.commands;

import java.util.Map;

import basictaskrunner.OutputSink;
import basictaskrunner.PatternMatchService;

public class PatternMatchCommand implements ICommand {
	public static final String Name = "patmatch";
	
	public static final CommandDef Def = InlineSupplier.get(() -> {
		CommandDef def = new CommandDef();
		def.command = Name;
		def.displayName = "Pattern Match";
		def.args.add(new CommandArgDef("regex", "string", "^.*$"));
		def.args.add(new CommandArgDef("text", "string", "test"));
		return def;
	});

	@Override
	public void start(Map<String, String> args, OutputSink status) {
		PatternMatchService.run(status, args.get("regex"), args.get("text"));
	}

	@Override
	public void stop() {
	}

}
