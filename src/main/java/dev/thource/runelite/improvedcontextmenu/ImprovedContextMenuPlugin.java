package dev.thource.runelite.improvedcontextmenu;

import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.MenuOpened;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

/**
 * ImprovedContextMenuPlugin is a RuneLite plugin designed to improve the functionality of the
 * in-game right click menu.
 */
@Slf4j
@PluginDescriptor(
    name = "Improved Context Menu",
    description = "Improves the context (right click) menu by adding scrolling indicators and menu entry limit",
    tags = {"right click", "context menu"}
)
public class ImprovedContextMenuPlugin extends Plugin {

  public static final int MENU_HEADER_HEIGHT = 22;
  public static final int MENU_ROW_HEIGHT = 15;
  public static final int MENU_SIDE_PADDING = 4;
  private static final int CHARACTER_WIDTH_SPACE = 4;
  private static final int CHARACTER_WIDTH_UP_CHEVRON = 9;
  private static final int CHARACTER_WIDTH_LOWERCASE_V = 8;
  private final Map<MenuEntry, List<MenuEntry>> submenuEntryMap = new HashMap<>();
  @Getter @Inject private Client client;
  @Getter @Inject private ClientThread clientThread;
  @Getter @Inject private ImprovedContextMenuConfig config;
  @Inject private MouseManager mouseManager;
  @Inject private ImprovedContextMenuInputListener improvedContextMenuInputListener;
  private MenuEntry[] menuEntries;
  private int scrolledAmount = 0;
  private boolean isMenuScrollable = false;
  @Getter private MenuEntry scrollUpIndicator;
  @Getter private MenuEntry scrollDownIndicator;

  @Override
  protected void startUp() {
    mouseManager.registerMouseListener(improvedContextMenuInputListener);
    mouseManager.registerMouseWheelListener(improvedContextMenuInputListener);
  }


  @Override
  protected void shutDown() {
    mouseManager.unregisterMouseListener(improvedContextMenuInputListener);
    mouseManager.unregisterMouseWheelListener(improvedContextMenuInputListener);
  }

  // Use a high priority so that this is called way after any other plugins add menu entries
  @Subscribe(priority = 999)
  public void onMenuOpened(MenuOpened event) {
    menuEntries = event.getMenuEntries();
    populateSubmenuEntryMap();

    scrolledAmount = 0;
    isMenuScrollable = menuEntries.length > getMaxMenuEntries();

    if (isMenuScrollable) {
      scrollUpIndicator = client.createMenuEntry(getMaxMenuEntries() - 1);
      scrollDownIndicator = client.createMenuEntry(0);

      clientThread.invokeLater(this::generateIndicatorOptions);
    }

    reorderMenu();
  }

  private void generateIndicatorOptions() {
    if (scrollUpIndicator == null || scrollDownIndicator == null) {
      return;
    }

    int menuWidth = client.getMenuWidth();
    int textWidth = menuWidth - MENU_SIDE_PADDING * 2;
    int upSpaces = (textWidth - CHARACTER_WIDTH_UP_CHEVRON * 3) / CHARACTER_WIDTH_SPACE;
    int downSpaces = (textWidth - CHARACTER_WIDTH_LOWERCASE_V * 3) / CHARACTER_WIDTH_SPACE;

    StringBuilder upOption = new StringBuilder();
    // This looks weird, but if upSpaces is 5, this will get us 3 on the left and 2 on the right
    for (int i = 0; i < upSpaces - upSpaces / 2; i++) {
      upOption.append(" ");
    }
    upOption.append("^");

    StringBuilder downOption = new StringBuilder();
    for (int i = 0; i < downSpaces - downSpaces / 2; i++) {
      downOption.append(" ");
    }
    downOption.append("v");

    scrollUpIndicator.setOption(upOption.toString());
    scrollDownIndicator.setOption(downOption.toString());
  }

  private void populateSubmenuEntryMap() {
    submenuEntryMap.clear();
    List<MenuEntry> menuEntryList = new ArrayList<>();
    for (int i = menuEntries.length - 1; i >= 0; i--) {
      MenuEntry menuEntry = menuEntries[i];

      if (menuEntry.getParent() != null) {
        submenuEntryMap.computeIfAbsent(menuEntry.getParent(), a -> new ArrayList<>()).add(0, menuEntry);
        continue;
      }

      menuEntryList.add(0, menuEntry);
    }

    menuEntries = menuEntryList.toArray(new MenuEntry[]{});
  }

  private int getMaxMenuEntries() {
    return Math.min((client.getCanvasHeight() - MENU_HEADER_HEIGHT) / MENU_ROW_HEIGHT,
        config.maxMenuEntries());
  }

  private void reorderMenu() {
    int menuEntryCount = Math.min(menuEntries.length, getMaxMenuEntries());
    List<MenuEntry> adjustedMenuEntries = new ArrayList<>();

    if (isMenuScrollable) {
      // Fill the list with nulls, so that .set can be used below
      for (int i = 0; i < menuEntryCount; i++) {
        adjustedMenuEntries.add(null);
      }

      for (int index = 0; index < menuEntryCount; index++) {
        int rawIndex = menuEntries.length - 1 - index - scrolledAmount;
        int adjustedIndex = menuEntryCount - 1 - index;
        adjustedMenuEntries.set(adjustedIndex, menuEntries[rawIndex]);
      }

      if (scrolledAmount > 0) {
        adjustedMenuEntries.set(menuEntryCount - 1, scrollUpIndicator);
      }

      if (scrolledAmount < menuEntries.length - menuEntryCount) {
        adjustedMenuEntries.set(0, scrollDownIndicator);
      }
    } else {
      Collections.addAll(adjustedMenuEntries, menuEntries);
    }

    ListIterator<MenuEntry> listIterator = adjustedMenuEntries.listIterator();
    while (listIterator.hasNext()) {
      MenuEntry menuEntry = listIterator.next();

      List<MenuEntry> entries = submenuEntryMap.get(menuEntry);
      if (entries != null) {
        entries.forEach(listIterator::add);
      }
    }

    client.setMenuEntries(adjustedMenuEntries.toArray(new MenuEntry[]{}));
  }

  void scrollMenu(int scrollDirection) {
    if (!isMenuScrollable) {
      return;
    }

    clientThread.invoke(() -> {
      scrolledAmount = Math.min(Math.max(scrolledAmount + scrollDirection, 0),
          menuEntries.length - getMaxMenuEntries());
      reorderMenu();
    });
  }

  @Provides
  ImprovedContextMenuConfig provideConfig(ConfigManager configManager) {
    return configManager.getConfig(ImprovedContextMenuConfig.class);
  }
}
