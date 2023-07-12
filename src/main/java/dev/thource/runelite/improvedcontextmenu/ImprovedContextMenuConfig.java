package dev.thource.runelite.improvedcontextmenu;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

/** ImprovedContextMenuConfig manages the config for the plugin. */
@SuppressWarnings("SameReturnValue")
@ConfigGroup("improvedContextMenu")
public interface ImprovedContextMenuConfig extends Config {

  String CONFIG_GROUP = "improvedContextMenu";

  @ConfigItem(
      keyName = "enableScrolling",
      name = "Enable scrolling",
      description = "Whether scrolling the mouse wheel while hovering the context menu will swap"
          + "the menu entries.")
  default boolean enableScrolling() {
    return true;
  }

  @ConfigItem(
      keyName = "condensePlayerOptions",
      name = "Condense player options",
      description = "Whether player options should be placed in submenus (like how it works in rs3).")
  default boolean condensePlayerOptions() {
    return true;
  }
}
