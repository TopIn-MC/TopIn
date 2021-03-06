package me.hsgamer.topin.getter.skull;

import io.papermc.lib.PaperLib;
import me.hsgamer.topin.TopIn;
import me.hsgamer.topin.data.list.DataList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

public final class TopSkull {

    private static Method setOwningPlayerMethod;
    private static Method setOwnerMethod;

    static {
        try {
            setOwnerMethod = Skull.class.getDeclaredMethod("setOwner", String.class);
            setOwningPlayerMethod = Skull.class.getDeclaredMethod("setOwningPlayer", OfflinePlayer.class);
        } catch (NoSuchMethodException e) {
            // IGNORED
        }
    }

    private final Location location;
    private final String dataListName;
    private final int index;

    public TopSkull(Location location, String dataListName, int index) {
        this.location = location;
        this.dataListName = dataListName;
        this.index = index;
    }

    public static TopSkull deserialize(Map<String, Object> args) {
        return new TopSkull(Location.deserialize(args), (String) args.get("data-list"),
                (int) args.get("index"));
    }

    public void update() {
        Optional<DataList> optionalDataList = TopIn.getInstance().getDataListManager()
                .getDataList(dataListName);
        if (!optionalDataList.isPresent()) {
            return;
        }

        PaperLib.getChunkAtAsync(location, false).thenAccept(chunk -> {
            if (chunk == null) {
                return;
            }

            DataList dataList = optionalDataList.get();
            if (index < 0 || index >= dataList.getSize()) {
                return;
            }
            OfflinePlayer topPlayer = Bukkit.getOfflinePlayer(dataList.getPair(index).getUniqueId());
            BlockState blockState = location.getBlock().getState();
            if (blockState instanceof Skull) {
                Skull skull = (Skull) blockState;
                setOwner(skull, topPlayer);
                skull.update(false, false);
            }
        });
    }

    private void setOwner(Skull skull, OfflinePlayer owner) {
        if (setOwningPlayerMethod != null) {
            try {
                setOwningPlayerMethod.invoke(skull, owner);
            } catch (IllegalAccessException | InvocationTargetException e) {
                TopIn.getInstance().getLogger()
                        .log(Level.WARNING, "Error when setting owner for skulls", e);
            }
        } else {
            try {
                setOwnerMethod.invoke(skull, owner.getName());
            } catch (IllegalAccessException | InvocationTargetException e) {
                TopIn.getInstance().getLogger()
                        .log(Level.WARNING, "Error when setting owner for skulls", e);
            }
        }
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>(location.serialize());
        map.put("data-list", dataListName);
        map.put("index", index);
        return map;
    }

    public Location getLocation() {
        return location;
    }
}
