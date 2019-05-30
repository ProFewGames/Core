package xyz.ufactions.tags.command;

import org.bukkit.entity.Player;
import xyz.ufactions.commands.CommandBase;
import xyz.ufactions.tags.TitleModule;
import xyz.ufactions.tags.ui.TitleMenu;

public class TitleCommand extends CommandBase<TitleModule> {

	public TitleCommand(TitleModule module) {
		super(module, "title", "tags", "tag", "titles");
	}

	@Override
	public void execute(Player player, String[] args) {
		new TitleMenu(player, Plugin);
	}
}