package basictaskrunner.webapp.commands;

import java.util.Map;

import basictaskrunner.DummyMessageListener;
import basictaskrunner.OutputSink;

public class DummyMessageListenCommand implements ICommand {
	public static final String Name = "dummylisten";

	public static final CommandDef Def = InlineSupplier.get(() -> {
		CommandDef def = new CommandDef();
		def.command = Name;
		def.displayName = "Dummy Message Listen";
		def.args.add(new CommandArgDef("echo", "string", "Echo text"));
		return def;
	});
	
	private DummyMessageListener listener;
	
	@Override
	public void start(Map<String, String> args, OutputSink status) {
		listener = new DummyMessageListener();
		listener.run(status, args.get("echo"));
	}

	@Override
	public void stop() {
		listener.stop();
	}

}
