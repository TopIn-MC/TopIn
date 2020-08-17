package me.hsgamer.topin.command;

import static me.hsgamer.topin.TopIn.getInstance;

import java.util.Collections;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.topin.Permissions;
import me.hsgamer.topin.config.MessageConfig;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

public final class ReloadCommand extends BukkitCommand {

  public ReloadCommand() {
    super("reloadplugin", "Reload the plugin", "/reloadplugin",
        Collections.singletonList("rlplugin"));
  }

  @Override
  public boolean execute(CommandSender sender, String commandLabel, String[] args) {
    if (!sender.hasPermission(Permissions.RELOAD)) {
      MessageUtils.sendMessage(sender, MessageConfig.NO_PERMISSION.getValue());
      return false;
    }

    getInstance().getDataListManager().saveAll();
    getInstance().getMainConfig().reloadConfig();
    getInstance().getMessageConfig().reloadConfig();
    getInstance().getAddonManager().callReload();
    getInstance().getCommandManager().syncCommand();
    getInstance().startNewSaveTask();
    MessageUtils.sendMessage(sender, MessageConfig.SUCCESS.getValue());
    return true;
  }
}
