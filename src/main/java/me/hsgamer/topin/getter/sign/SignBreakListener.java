package me.hsgamer.topin.getter.sign;

import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.topin.Permissions;
import me.hsgamer.topin.config.MessageConfig;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;

public final class SignBreakListener implements Listener {

  private final SignGetter signGetter;

  public SignBreakListener(SignGetter getter) {
    this.signGetter = getter;
  }

  @EventHandler
  public void onBreak(BlockBreakEvent event) {
    Location location = event.getBlock().getLocation();
    Player player = event.getPlayer();
    if (signGetter.containsSign(location)) {
      if (!player.hasPermission(Permissions.SIGN_BREAK) || !player.isSneaking()) {
        event.setCancelled(true);
        return;
      }
      signGetter.removeSign(location);
      MessageUtils.sendMessage(player, MessageConfig.SIGN_REMOVED.getValue());
    }
  }

  @EventHandler
  public void onExplode(BlockExplodeEvent event) {
    event.blockList().removeIf(block -> signGetter.containsSign(block.getLocation()));
  }
}
