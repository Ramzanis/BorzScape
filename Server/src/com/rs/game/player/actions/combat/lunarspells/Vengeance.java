package com.rs.game.player.actions.combat.lunarspells;

import com.rs.game.Animation;
import com.rs.game.Graphics;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.utils.Utils;

public class Vengeance {
	
	public static boolean cast(Player player, double xp) {
		if (player.getVengeanceDelay() >= Utils.currentTimeMillis()) {
			player.sm("You can only cast vengeance every 30 seconds.");
			return false;
		}
		player.getSkills().addXp(Skills.MAGIC, xp);
		player.gfx(new Graphics(726, 0, 100));
		player.animate(new Animation(4410));
		player.setVengeance(true);
		player.setVengeanceDelay(30000);
		player.sm("You cast vengeance.");
		return true;
	}

}
