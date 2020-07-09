package me.hsgamer.topin.data.list;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import me.hsgamer.topin.config.PluginConfig;
import me.hsgamer.topin.data.value.PairDecimal;
import me.hsgamer.topin.utils.ServerUtils;
import org.bukkit.configuration.file.FileConfiguration;

public abstract class SimpleDataList implements DataList {

  protected final List<PairDecimal> list = new ArrayList<>();

  /**
   * {@inheritDoc}
   */
  @Override
  public void set(UUID uuid, BigDecimal value) {
    PairDecimal pairDecimal = getPair(uuid).orElseGet(() -> {
      PairDecimal newPair = createPairDecimal(uuid);
      list.add(newPair);
      return newPair;
    });

    if (value != null) {
      pairDecimal.setValue(value);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateAll() {
    list.forEach(PairDecimal::update);
    sort();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Optional<PairDecimal> getPair(UUID uuid) {
    return list.parallelStream()
        .filter(pairDecimal -> pairDecimal.getUniqueId().equals(uuid))
        .findFirst();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<PairDecimal> getTopRange(int from, int to) {
    return list.subList(from, Math.min(list.size(), to));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PairDecimal getPair(int index) {
    return list.get(index);
  }

  /**
   * Sort the list from high to low
   */
  protected void sort() {
    list.sort(Comparator.reverseOrder());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void loadData(PluginConfig config) {
    ServerUtils.getAllUniqueIds().forEach(this::add);
    FileConfiguration configuration = config.getConfig();
    configuration.getValues(false)
        .forEach((k, v) -> set(UUID.fromString(k), new BigDecimal(String.valueOf(v))));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void saveData(PluginConfig config) {
    FileConfiguration configuration = config.getConfig();
    for (PairDecimal pair : list) {
      configuration.set(pair.getUniqueId().toString(), pair.getValue().toString());
    }
    config.saveConfig();
  }
}