package xyz.ufactions.tags.command;

import org.bukkit.entity.Player;
import xyz.ufactions.commands.CommandBase;
import xyz.ufactions.tags.TitleModule;
import xyz.ufactions.tags.ui.TitleColorMenu;

public class TitleColorCommand extends CommandBase<TitleModule> {

	public TitleColorCommand(TitleModule module) {
		super(module, "titlecolor", "titlecolour");
	}

	@Override
	public void execute(Player player, String[] args) {
		new TitleColorMenu(player, Plugin);
	}
}