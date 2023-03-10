package com.rs.game.npc.drops.droptable;

import com.rs.game.npc.drops.MobRewardNodeBuilder;
import com.rs.game.player.Player;
import com.rs.game.player.content.pet.Pets;

public final class BlueDragon extends MobRewardNodeBuilder {

	public BlueDragon() {
		super(new Object[] { "Blue dragon", -1 });
	}

	@Override
	public void populate(Player player) {
		switch (rarityNode(player)) {
		case -1:
		case COMMON:
			createNodeBatch(1, node(STEEL_PLATELEGS, 1), 
					node(WATER_RUNE, 75), 
					node(GRIMY_GUAM, 1), 
					node(GRIMY_MARRENTILL, 1), 
					node(GRIMY_TARROMIN, 1),  
					node(GRIMY_HARRALANDER, 1), 
					node(COINS, 11, 454));
			break;
		case VERY_RARE:
			createNodeBatch(1, node(DEATH_RUNE, 5));
			break;
		case RARE:
			createNodeBatch(1, node(RUNE_DAGGER, 1),
					node(LAW_RUNE, 3),
					node(GRIMY_DWARF_WEED, 1));
			break;
		case UNCOMMON:
			createNodeBatch(1, node(STEEL_BATTLEAXE, 1),  
					node(MITHRIL_AXE, 1), 
					node(MITHRIL_SPEAR, 1), 
					node(ADAMANT_DAGGER, 1), 
					node(MITHRIL_KITESHIELD, 1), 
					node(ADAMANT_FULL_HELM, 1), 
					node(NATURE_RUNE, 15),
					node(FIRE_RUNE, 37),
					node(GRIMY_RANARR, 1), 
					node(GRIMY_IRIT, 1), 
					node(GRIMY_AVANTOE, 1), 
					node(GRIMY_KWUARM, 1), 
					node(GRIMY_CADANTINE, 1), 
					node(GRIMY_LANTADYME, 1), 
					node(ADAMANTITE_ORE, 1), 
					node(BASS, 1));
			break;
		}
		addObj(DRAGON_BONES, 1);// bones
		addObj(BLUE_DRAGONHIDE, 1);// hide
		shakeTreasureTrail(player, HARD_CLUE);
		shakeSummoningCharm(1, 11, 27.5, 11, 2);

	}
}
