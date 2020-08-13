package me.hsgamer.topin.command;

import static me.hsgamer.hscore.bukkit.utils.MessageUtils.sendMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import me.hsgamer.topin.Permissions;
import me.hsgamer.topin.TopIn;
import me.hsgamer.topin.config.MainConfig;
import me.hsgamer.topin.config.MessageConfig;
import me.hsgamer.topin.data.list.DataList;
import me.hsgamer.topin.data.value.PairDecimal;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

public class GetTopCommand extends BukkitCommand {

  public GetTopCommand() {
    super("gettop", "Get Top List", "/gettop <data_list> [<from_index> <to_index>]",
        Arrays.asList("toplist", "gettoplist"));
  }

  @Override
  public boolean execute(CommandSender sender, String commandLabel, String[] args) {
    if (!sender.hasPermission(Permissions.TOP)) {
      sendMessage(sender, MessageConfig.NO_PERMISSION.getValue());
      return false;
    }
    if (args.length < 1) {
      sendMessage(sender, "&c" + getUsage());
      return false;
    }
    Optional<DataList> optional = TopIn.getInstance().getDataListManager().getDataList(args[0]);
    if (!optional.isPresent()) {
      sendMessage(sender, MessageConfig.DATA_LIST_NOT_FOUND.getValue());
      return false;
    }
    DataList dataList = optional.get();

    int fromIndex = 0;
    int toIndex = 10;
    if (args.length == 2) {
      try {
        toIndex = Integer.parseInt(args[1]);
      } catch (NumberFormatException e) {
        sendMessage(sender, MessageConfig.NUMBER_REQUIRED.getValue());
        return false;
      }
    } else if (args.length > 2) {
      try {
        fromIndex = Integer.parseInt(args[1]);
        toIndex = Integer.parseInt(args[2]);
      } catch (NumberFormatException e) {
        sendMessage(sender, MessageConfig.NUMBER_REQUIRED.getValue());
        return false;
      }
    }
    if (fromIndex >= toIndex) {
      sendMessage(sender, MessageConfig.ILLEGAL_FROM_TO_NUMBER.getValue());
      return false;
    }
    if (fromIndex >= dataList.getSize()) {
      sendMessage(sender, MessageConfig.OUT_OF_BOUND.getValue());
      return false;
    }

    int displayIndex = MainConfig.DISPLAY_TOP_START_INDEX.getValue() + fromIndex;
    List<String> list = new ArrayList<>(MessageConfig.TOP_LIST_HEADER.getValue());
    List<String> body = MessageConfig.TOP_LIST_BODY.getValue();
    for (PairDecimal pairDecimal : dataList.getTopRange(fromIndex, toIndex)) {
      for (String s : body) {
        list.add(s
            .replace("<name>", Bukkit.getOfflinePlayer(pairDecimal.getUniqueId()).getName())
            .replace("<value>", pairDecimal.getValue().toPlainString())
            .replace("<index>", String.valueOf(displayIndex++))
        );
      }
    }
    list.addAll(MessageConfig.TOP_LIST_FOOTER.getValue());
    list.replaceAll(s -> s
        .replace("<suffix>", dataList.getSuffix())
        .replace("<data_list>", dataList.getDisplayName()));
    list.forEach(s -> sendMessage(sender, s, false));

    return true;
  }

  @Override
  public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
    List<String> list = new ArrayList<>();
    if (args.length == 1) {
      list.addAll(TopIn.getInstance().getDataListManager().getSuggestedDataListNames(args[0]));
    }
    return list;
  }
}