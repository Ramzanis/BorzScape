package com.rs.game.player.actions.combat.modernspells;

import com.rs.game.Entity;
import com.rs.game.Graphics;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.combat.PlayerCombat;
import com.rs.utils.Utils;

public class RSModernCombatSpells {

	/*
	 * @author -Andreas
	 * 
	 * @2018-06-21
	 */

	public static final int AIR_SPELL = 0, WATER_SPELL = 1, EARTH_SPELL = 2, FIRE_SPELL = 3;

	public static enum ModernCombatSpellsStore {

		// animationid -1 == standard 14221 animationId
		WIND_RUSH(98, 2.5, 10, 1162, null, 2699, 2700, AIR_SPELL),

		AIR_STRIKE(25, 5.5, 20, 1162, new Graphics(90, 0, 100), 91, 92, AIR_SPELL),

		WATER_STRIKE(28, 7.5, 40, 1162, new Graphics(93, 0, 100), 94, 95, WATER_SPELL),

		EARTH_STRIKE(30, 9.5, 60, 1162, new Graphics(96, 0, 100), 97, 98, EARTH_SPELL),

		FIRE_STRIKE(32, 11.5, 80, 1162, new Graphics(99, 0, 100), 100, 101, FIRE_SPELL),

		WIND_BOLT(34, 13.5, 90, 1162, new Graphics(117, 0, 100), 118, 119, AIR_SPELL),

		WATER_BOLT(39, 16.5, 100, 1162, new Graphics(120, 0, 100), 121, 122, WATER_SPELL),

		EARTH_BOLT(42, 19.5, 110, 1162, new Graphics(123, 0, 100), 124, 125, EARTH_SPELL),

		FIRE_BOLT(45, 22.5, 120, 1162, new Graphics(126, 0, 100), 127, 128, FIRE_SPELL),

		CRUMBLE_UNDEAD(47, 24.5, 160, 724, new Graphics(145, 0, 100), 146, 147),

		WIND_BLAST(49, 25.5, 130, 1162, new Graphics(132, 0, 100), 133, 134, AIR_SPELL),

		WATER_BLAST(52, 28.5, 140, 1162, new Graphics(135, 0, 100), 136, 137, WATER_SPELL),

		IBAN_BLAST(54, 30.0, 250, 708, new Graphics(87, 0, 100), 88, 89),

		MAGIC_DART(56, 30.0, 0, 1575, null, 328, 329),

		EARTH_BLAST(58, 31.5, 150, 1162, new Graphics(138, 0, 100), 139, 140, EARTH_SPELL),

		FIRE_BLAST(63, 34.5, 160, 1162, new Graphics(129, 0, 100), 130, 131, FIRE_SPELL),

		SARADOMIN_STRIKE(66, 35.0, 200, 811, null, -1, 76),

		CLAWS_OF_GUTHIX(67, 35.0, 200, 811, null, -1, 77),

		FLAMES_OF_ZAMORAK(68, 35.0, 200, 811, null, -1, 78),

		WIND_WAVE(70, 36.0, 1161, 1167,  new Graphics(158, 0, 100), 159, 160, AIR_SPELL),

		WATER_WAVE(73, 37.5, 1161, 1167, new Graphics(161, 0, 100), 162, 163, WATER_SPELL),

		EARTH_WAVE(77, 40.0, 1161, 1167, new Graphics(164, 0, 100), 165, 166, EARTH_SPELL),

		FIRE_WAVE(80, 42.5, 1161, 1167, new Graphics(155, 0, 100), 156, 157, FIRE_SPELL),

		WIND_SURGE(84, 44.5, 1161, 1167, new Graphics(457, 0, 100), 159, 160, AIR_SPELL),

		WATER_SURGE(87, 46.5, 1161, 1167, new Graphics(2701, 0, 100), 162, 163, WATER_SPELL),

		EARTH_SURGE(89, 48.5, 1161, 1167, new Graphics(2717, 0, 100), 165, 166, EARTH_SPELL),

		FIRE_SURGE(91, 50.5, 1161, 1167, new Graphics(2728, 0, 100), new int[] { 156, 159 }, 157, FIRE_SPELL),

		STORM_OF_ARMADYL(99, 70, 0, 10546, new Graphics(457), 1019, 1019),

		CONFUSE(26, 13.0, 1, 1163, new Graphics(102, 0, 100), 103, 104),

		WEAKEN(31, 21.0, 1, 1164, new Graphics(105, 0, 100), 106, 107),

		CURSE(35, 29.0, 1, 1165, new Graphics(108, 0, 100), 109, 110),

		VULNERABILITY(75, 76.0, 1, 1163, new Graphics(167, 0, 100), 168, 169),

		ENFEEBLE(78, 83.0, 1, 1164, new Graphics(170, 0, 100), 171, 172),

		STUN(82, 90.0, 1, 1165, new Graphics(173, 0, 100), 174, 107),

		BIND(36, 30.0, 20, 1161, new Graphics(177, 0, 100), 178, 179),

		SNARE(55, 60.0, 30, 1161, new Graphics(177, 0, 100), 178, 180),

		ENTANGLE(81, 90.0, 50, 1161, new Graphics(177, 0, 100), 178, 181),

		TELEPORT_BLOCK(86, 80, 30, 10503, new Graphics(1841), 1842, 1843);

		private int id;
		private double xp;
		private int baseDamage;
		private int animation;
		private Graphics startGfx;
		private int projectileId;
		private int[] projectiles;
		private int endGfx;
		private int spell_type;

		public static ModernCombatSpellsStore getSpell(int i) {
			for (ModernCombatSpellsStore s : ModernCombatSpellsStore.values()) {
				if (s.getId() == i)
					return s;
			}
			return null;
		}

		private ModernCombatSpellsStore(int id, double xp, int baseDamage, int animation, Graphics startGfx,
				int[] projectiles, int endGfx) {
			this.setId(id);
			this.setXp(xp);
			this.setBaseDamage(baseDamage);
			this.setAnimation(animation);
			this.setStartGfx(startGfx);
			this.setProjectiles(projectiles);
			this.setEndGfx(endGfx);
		}

		private ModernCombatSpellsStore(int id, double xp, int baseDamage, int animation, Graphics startGfx,
				int[] projectiles, int endGfx, int spell_type) {
			this.setId(id);
			this.setXp(xp);
			this.setBaseDamage(baseDamage);
			this.setAnimation(animation);
			this.setStartGfx(startGfx);
			this.setProjectiles(projectiles);
			this.setEndGfx(endGfx);
			this.setSpellType(spell_type);
		}

		private ModernCombatSpellsStore(int id, double xp, int baseDamage, int animation, Graphics startGfx,
				int projectileId, int endGfx, int spell_type) {
			this.setId(id);
			this.setXp(xp);
			this.setBaseDamage(baseDamage);
			this.setAnimation(animation);
			this.setStartGfx(startGfx);
			this.setProjectileId(projectileId);
			this.setEndGfx(endGfx);
			this.setSpellType(spell_type);
		}

		private ModernCombatSpellsStore(int id, double xp, int baseDamage, int animation, Graphics startGfx,
				int projectileId, int endGfx) {
			this.setId(id);
			this.setXp(xp);
			this.setBaseDamage(baseDamage);
			this.setAnimation(animation);
			this.setStartGfx(startGfx);
			this.setProjectileId(projectileId);
			this.setEndGfx(endGfx);
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public double getXp() {
			return xp;
		}

		public void setXp(double xp) {
			this.xp = xp;
		}

		public int getBaseDamage() {
			return baseDamage;
		}

		public void setBaseDamage(int baseDamage) {
			this.baseDamage = baseDamage;
		}

		public Graphics getStartGfx() {
			return startGfx;
		}

		public void setStartGfx(Graphics startGfx) {
			this.startGfx = startGfx;
		}

		public int getProjectileId() {
			return projectileId;
		}

		public void setProjectileId(int projectileId) {
			this.projectileId = projectileId;
		}

		public int getEndGfx() {
			return endGfx;
		}

		public void setEndGfx(int endGfx) {
			this.endGfx = endGfx;
		}

		public int getAnimation() {
			return animation;
		}

		public void setAnimation(int animation) {
			this.animation = animation;
		}

		public int[] getProjectiles() {
			return projectiles;
		}

		public void setProjectiles(int[] projectiles) {
			this.projectiles = projectiles;
		}

		public int getSpellType() {
			return spell_type;
		}

		public void setSpellType(int spell_type) {
			this.spell_type = spell_type;
		}
	}

	public static int getDamageBoost(Player player, int spellId, int baseDamage) {
		ModernCombatSpellsStore spellStore = ModernCombatSpellsStore.getSpell(spellId);
		if (spellStore == null)
			return baseDamage;
		switch (spellStore) {
			case STORM_OF_ARMADYL:
				int minDamage = 160 + (player.getSkills().getLevelForXp(Skills.MAGIC) - 77) * 4;
				int boost = (player.getSkills().getLevelForXp(Skills.MAGIC) - 77) * 4;
				int hit = minDamage + boost;
				if (hit > 0 && hit < boost)
					hit += boost;
				return hit;
			case WIND_BOLT:
			case WATER_BOLT:
			case EARTH_BOLT:
			case FIRE_BOLT:
				if (player.getEquipment().getGlovesId() == 777)
					return baseDamage + 30;
			case MAGIC_DART:
				return (player.getSkills().getLevel(Skills.MAGIC) / 10) * 10 + 100;
		}
		return baseDamage;
	}

	public static void instantSpellEffect(Player player, Entity target, int spellId, boolean successHit) {
		ModernCombatSpellsStore spellStore = ModernCombatSpellsStore.getSpell(spellId);
		if (spellStore == null)
			return;
		switch (spellStore) {
			case TELEPORT_BLOCK:
				if (target instanceof Player && successHit) {
					Player p2 = (Player) target;
					PlayerCombat.teleBlockTime = (p2.getPrayer().usingPrayer(0, 17) || p2.getPrayer().usingPrayer(1, 7)
							? 150000
							: 300000);
				}
				break;
			case BIND:
			case SNARE:
			case ENTANGLE:
				if (successHit)
					target.addFreezeDelay(spellStore == ModernCombatSpellsStore.BIND ? 5000 : spellStore == ModernCombatSpellsStore.SNARE ? 10000 : 15000, true);
				break;
		}
	}

	public static void handleSpellEffect(Player player, Entity target, int spellId) {
		ModernCombatSpellsStore spellStore = ModernCombatSpellsStore.getSpell(spellId);
		if (spellStore == null)
			return;
		if (!(target instanceof Player))//TODO NPC WEAKNESSES
			return;
		Player p2 = (Player) target;
		String spellName = Utils.formatString(spellStore.name());
		switch (spellStore) {
			case CONFUSE:
					p2.sm("Your attack level was drained by enemies " + spellName + " spell.");
					p2.getSkills().drainLevel(Skills.ATTACK, (int) (p2.getSkills().getLevel(Skills.ATTACK) * 0.05));
				break;
			case WEAKEN:
					p2.sm("Your strength level was drained by enemies " + spellName + " spell.");
					p2.getSkills().drainLevel(Skills.STRENGTH, (int) (p2.getSkills().getLevel(Skills.STRENGTH) * 0.05));
				break;
			case CURSE:
					p2.sm("Your defence level was drained by enemies " + spellName + " spell.");
					p2.getSkills().drainLevel(Skills.DEFENCE, (int) (p2.getSkills().getLevel(Skills.DEFENCE) * 0.05));
				break;
			case STUN:
				p2.sm("Your attack level was drained by enemies " + spellName + " spell.");
				p2.getSkills().drainLevel(Skills.ATTACK, (int) (p2.getSkills().getLevel(Skills.ATTACK) * 0.10));
				break;
			case ENFEEBLE:
				p2.sm("Your strength level was drained by enemies " + spellName + " spell.");
				p2.getSkills().drainLevel(Skills.STRENGTH, (int) (p2.getSkills().getLevel(Skills.STRENGTH) * 0.10));
				break;
			case VULNERABILITY:
				p2.sm("Your defence level was drained by enemies " + spellName + " spell.");
				p2.getSkills().drainLevel(Skills.DEFENCE, (int) (p2.getSkills().getLevel(Skills.DEFENCE) * 0.10));
				break;
			case SARADOMIN_STRIKE:
				p2.getPrayer().drainPrayer(10);
				break;
			case CLAWS_OF_GUTHIX:
				p2.getSkills().drainLevel(Skills.DEFENCE, (int) (p2.getSkills().getLevel(Skills.DEFENCE) * 0.05));
				break;
			case FLAMES_OF_ZAMORAK:
				p2.getSkills().drainLevel(Skills.MAGIC, (int) (p2.getSkills().getLevel(Skills.MAGIC) * 0.05));
				break;
		}
	}
}
