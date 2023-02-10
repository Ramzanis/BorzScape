package com.rs.game.player.content;

import com.rs.game.WorldTile;
import com.rs.game.player.Player;
import com.rs.game.player.actions.combat.Magic;

public class SpiritTree {

	private static final int TREE_INTERFACE = 864;
	private static final WorldTile[] TELEPORTS = { new WorldTile(2554, 3255, 0), new WorldTile(3187, 3507, 0), new WorldTile(2416, 2852, 0), new WorldTile(2339, 3108, 0), new WorldTile(2541, 3170, 0), new WorldTile(2462, 3445, 0) };

	public static void openInterface(Player player, boolean isMini) {
		player.getVarsManager().sendVarBit(3959, 3);
		player.getInterfaceManager().sendInterface(TREE_INTERFACE);
		player.getPackets().sendIComponentSettings(TREE_INTERFACE, 6, 0, 7, 0);
		if (player.getRegionId() == 10033)
			player.getVarsManager().sendVar(1469, 0x27b8c61);
		else if (player.getRegionId() == 9781)
			player.getVarsManager().sendVar(1469, 0x2678d74);
		else {
			sendTeleport(player, TELEPORTS[4]);
		}
	}

	private static void sendTeleport(Player player, WorldTile tile) {
		player.getSocialManager().sendGameMessage("You place your hands on the dry tough bark of the spirit tree, and feel a surge of energy run through your veins.");
		Magic.sendTeleportSpell(player, 7082, 7084, 1229, 1229, 1, 0, tile, 4, true, Magic.OBJECT_TELEPORT);
	}

	public static void handleSpiritTree(Player player, int slot) {
		if (slot == 0) {
			if (player.getRegionId() == 10033)
				slot = 6;
			else
				slot = 5;
		}
		sendTeleport(player, TELEPORTS[slot - 1]);
	}
}
