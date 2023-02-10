package com.rs.game.player.actions.combat.lunarspells;

import com.rs.game.World;
import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.content.Pots;
import com.rs.utils.Utils;

public class StatRestoreShare {

	public static boolean cast(Player player, double xp, int itemId, int slotId) {
		Item item = new Item(itemId);
		if (!item.getName().toLowerCase().contains("restore") && !item.getName().toLowerCase().contains("sanfew")) {
			player.sm("You can only use this spell on restore potions.");
			return false;
		}
		if (player.getPotDelay() > Utils.currentTimeMillis())
			return false;
		player.getSkills().addXp(Skills.MAGIC, xp);
		Pots.pot(player, item, slotId);
		for (Player other : World.getPlayers()) {
			if (other == null || other == player)
				continue;
			if (other.withinDistance(player, 4) && other.isAcceptingAid() && other.isAtMultiArea()) {
				Pots.sharedPot(other, item, slotId);
				other.sm(player.getDisplayName() + " shared a " + item.getName().replace("(6)", "").replace("(5)", "")
						.replace("(4)", "").replace("(3)", "").replace("(2)", "").replace("(1)", "")
						+ "dose with you.");
			}
		}
		return true;
	}
}
