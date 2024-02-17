package dev.thource.runelite.improvedcontextmenu;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

/** ImprovedContextMenuConfig manages the config for the plugin. */
@SuppressWarnings("SameReturnValue")
@ConfigGroup("improvedContextMenu")
public interface ImprovedContextMenuConfig extends Config {

  String CONFIG_GROUP = "improvedContextMenu";

  @ConfigItem(
      keyName = "condensePlayerOptions",
      name = "Condense player options",
      description = "Whether player options should be placed in submenus (like how it works in "
          + "rs3).")
  default boolean condensePlayerOptions() {
    return true;
  }

  @Range(min = 5)
  @ConfigItem(
      keyName = "maxMenuEntries",
      name = "Max menu entries",
      description = "The max amount of entries to show in the context menu.")
  default int maxMenuEntries() {
    return 999;
  }
}
