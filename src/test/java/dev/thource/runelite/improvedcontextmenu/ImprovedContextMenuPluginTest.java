package dev.thource.runelite.improvedcontextmenu;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ImprovedContextMenuPluginTest {

  @SuppressWarnings("unchecked")
  public static void main(String[] args) throws Exception {
    ExternalPluginManager.loadBuiltin(ImprovedContextMenuPlugin.class);
    RuneLite.main(args);
  }
}
