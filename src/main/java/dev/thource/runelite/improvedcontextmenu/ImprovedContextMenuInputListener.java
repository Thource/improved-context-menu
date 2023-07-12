package dev.thource.runelite.improvedcontextmenu;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.MenuEntry;
import net.runelite.api.Point;
import net.runelite.client.input.MouseListener;
import net.runelite.client.input.MouseWheelListener;

public class ImprovedContextMenuInputListener implements MouseWheelListener, MouseListener {

  private final ImprovedContextMenuPlugin plugin;
  private final ImprovedContextMenuConfig improvedContextMenuConfig;
  private final Client client;

  @Inject
  private ImprovedContextMenuInputListener(ImprovedContextMenuPlugin plugin,
      ImprovedContextMenuConfig improvedContextMenuConfig,
      Client client) {
    this.plugin = plugin;
    this.improvedContextMenuConfig = improvedContextMenuConfig;
    this.client = client;
  }

  @Override
  public MouseWheelEvent mouseWheelMoved(MouseWheelEvent event) {
    if (improvedContextMenuConfig.enableScrolling() && client.isMenuOpen() && isMouseOverMenu()) {
      plugin.scrollMenu(event.getWheelRotation());
      event.consume();
    }

    return event;
  }

  @Override
  public MouseEvent mouseClicked(MouseEvent mouseEvent) {
    return mouseEvent;
  }

  private boolean isMouseOverMenu() {
    int menuHeight = client.getMenuHeight();
    int menuWidth = client.getMenuWidth();
    int menuX = client.getMenuX();
    int menuY = client.getMenuY();
    Point mousePosition = client.getMouseCanvasPosition();
    return mousePosition.getX() > menuX && mousePosition.getX() < menuX + menuWidth
        && mousePosition.getY() > menuY && mousePosition.getY() < menuY + menuHeight;
  }

  private MenuEntry getHoveredMenuEntry() {
    if (!client.isMenuOpen() || !isMouseOverMenu()) {
      return null;
    }

    int menuY = client.getMenuY();
    Point mousePosition = client.getMouseCanvasPosition();
    MenuEntry[] entries = client.getMenuEntries();
    int entryIndex = entries.length - (
        (mousePosition.getY() - menuY - ImprovedContextMenuPlugin.MENU_HEADER_HEIGHT)
            / ImprovedContextMenuPlugin.MENU_ROW_HEIGHT) - 1;
    if (entryIndex < 0 || entryIndex >= entries.length) {
      return null;
    }

    return entries[entryIndex];
  }

  @Override
  public MouseEvent mousePressed(MouseEvent mouseEvent) {
    MenuEntry hoveredEntry = getHoveredMenuEntry();
    if (hoveredEntry != null) {
      if (hoveredEntry == plugin.getScrollUpIndicator()) {
        plugin.scrollMenu(-1);
        mouseEvent.consume();
      } else if (hoveredEntry == plugin.getScrollDownIndicator()) {
        plugin.scrollMenu(1);
        mouseEvent.consume();
      }
    }

    return mouseEvent;
  }

  @Override
  public MouseEvent mouseReleased(MouseEvent mouseEvent) {
    return mouseEvent;
  }

  @Override
  public MouseEvent mouseEntered(MouseEvent mouseEvent) {
    return mouseEvent;
  }

  @Override
  public MouseEvent mouseExited(MouseEvent mouseEvent) {
    return mouseEvent;
  }

  @Override
  public MouseEvent mouseDragged(MouseEvent mouseEvent) {
    return mouseEvent;
  }

  @Override
  public MouseEvent mouseMoved(MouseEvent mouseEvent) {
    return mouseEvent;
  }
}
