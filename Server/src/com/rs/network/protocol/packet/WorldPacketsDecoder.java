package com.rs.network.protocol.packet;

import com.rs.Settings;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.item.FloorItem;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.npc.familiar.Familiar.SpecialAttack;
import com.rs.game.player.ChatMessage;
import com.rs.game.player.Inventory;
import com.rs.game.player.LogicPacket;
import com.rs.game.player.Player;
import com.rs.game.player.PublicChatMessage;
import com.rs.game.player.QuickChatMessage;
import com.rs.game.player.RouteEvent;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.PlayerFollow;
import com.rs.game.player.actions.combat.Magic;
import com.rs.game.player.actions.combat.PlayerCombat;
import com.rs.game.player.content.Commands;
import com.rs.game.player.content.ForumAuthManager;
import com.rs.game.player.content.FriendChatsManager;
import com.rs.game.player.content.Shop;
import com.rs.game.player.content.SkillsDialogue;
import com.rs.game.player.content.pet.Pets;
import com.rs.game.route.RouteFinder;
import com.rs.game.route.strategy.FixedTileStrategy;
import com.rs.io.InputStream;
import com.rs.network.protocol.packet.impl.ButtonHandler;
import com.rs.network.protocol.packet.impl.InventoryOptionsHandler;
import com.rs.network.protocol.packet.impl.NPCHandler;
import com.rs.network.protocol.packet.impl.ObjectHandler;
import com.rs.utils.DisplayNames;
import com.rs.utils.Encrypt;
import com.rs.utils.ItemExamines;
import com.rs.utils.Logger;
import com.rs.utils.ReportAbuse;
import com.rs.utils.Utils;
import com.rs.utils.huffman.Huffman;

public final class WorldPacketsDecoder {

	public static final byte[] PACKET_SIZES = new byte[100];

	// Converted
	private final static int COMMANDS_PACKET = 78;
	private final static int WALKING_PACKET = 5;
	private final static int MINI_WALKING_PACKET = 59;
	private final static int CLOSE_INTERFACE_PACKET = 69;
	private final static int PING_PACKET = 74;
	private final static int MOVE_CAMERA_PACKET = 51;
	private final static int AFK_PACKET = 19;
	private final static int IN_OUT_SCREEN_PACKET = 48;
	private final static int CLICK_PACKET = 49;
	private final static int KEY_TYPED_PACKET = 21;
	public final static int ACTION_BUTTON1_PACKET = 6;
	public final static int ACTION_BUTTON2_PACKET = 38;
	public final static int ACTION_BUTTON3_PACKET = 62;
	public final static int ACTION_BUTTON4_PACKET = 46;
	public final static int ACTION_BUTTON5_PACKET = 64;
	public final static int ACTION_BUTTON6_PACKET = 8;
	public final static int ACTION_BUTTON7_PACKET = 28;
	public final static int ACTION_BUTTON8_PACKET = 70;
	public final static int ACTION_BUTTON9_PACKET = 66;
	public final static int ACTION_BUTTON10_PACKET = 20;
	private final static int SCREEN_PACKET = 58;
	private final static int MOUVE_MOUSE_PACKET = 42;
	private final static int DIALOGUE_CONTINUE_PACKET = 18;
	private final static int WORLD_MAP_CLICK = 57;
	private final static int ENTER_INTEGER_PACKET = 81;
	private final static int SWITCH_INTERFACE_COMPONENTS_PACKET = 75;
	private final static int ATTACK_NPC = 35;
	private final static int CHAT_PACKET = 79;
	private final static int NPC_CLICK1_PACKET = 13;
	private final static int NPC_CLICK2_PACKET = 30;
	private final static int OBJECT_CLICK1_PACKET = 77;
	private final static int OBJECT_EXAMINE_PACKET = 73;
	private static final int NPC_EXAMINE_PACKET = 43;
	private final static int DONE_LOADING_REGION_PACKET = 17;
	private final static int ADD_FRIEND_PACKET = 50;
	private final static int REMOVE_FRIEND_PACKET = 27;
	private final static int ADD_IGNORE_PACKET = 9;
	private final static int REMOVE_IGNORE_PACKET = 0;
	private final static int ITEM_TAKE_PACKET = 80;
	private final static int ITEM_GROUND_EXAMINE_PACKET = 1;
	private final static int ENTER_LONG_TEXT_PACKET = 72;
	private final static int PLAYER_OPTION_2_PACKET = 41;
	private final static int SEND_FRIEND_MESSAGE_PACKET = 44;
//	private final static int PUBLIC_QUICK_CHAT_PACKET = 3;
	private final static int NPC_CLICK3_PACKET = 31;
	private final static int GRAND_EXCHANGE_ITEM_SELECT_PACKET = 61;
	private final static int REPORT_ABUSE_PACKET = 52;
	private final static int JOIN_FRIEND_CHAT_PACKET = 15;
	private final static int ENTER_NAME_PACKET = 53;
	private final static int OBJECT_CLICK2_PACKET = 14;
	private final static int OBJECT_CLICK3_PACKET = 10;
	private final static int KICK_FRIEND_CHAT_PACKET = 63;
	private final static int PLAYER_OPTION_4_PACKET = 65;
	private final static int PLAYER_OPTION_1_PACKET = 40;
	private final static int CHANGE_FRIEND_CHAT_PACKET = 24;
	private final static int INTERFACE_ON_INTERFACE = 55;
	private final static int INTERFACE_ON_OBJECT = 16;
	private final static int SEND_FRIEND_QUICK_CHAT_PACKET = 2;
	private final static int RECEIVE_PACKET_COUNT_PACKET = 71;
	
	// Not converted
	private final static int OBJECT_CLICK4_PACKET = 923;
	private final static int NPC_CLICK4_PACKET = 7230;
	private final static int PLAYER_OPTION_3_PACKET = -1;
	private final static int PLAYER_OPTION_6_PACKET = -1;
	private final static int PUBLIC_QUICK_CHAT_PACKET = -1;
	private final static int OBJECT_CLICK5_PACKET = -1;
	private final static int PLAYER_OPTION_9_PACKET = -1;
	private final static int INTERFACE_ON_PLAYER = -1;
	private final static int INTERFACE_ON_NPC = -1;

	private Player player;

	public WorldPacketsDecoder(Player player) {
		this.player = player;
	}

	static {
		loadPacketSizes();
	}

	public static void loadPacketSizes() {
		for (int i = 0; i < PACKET_SIZES.length; i++) {
			PACKET_SIZES[i] = -3;
		}

		PACKET_SIZES[25] = 4;
		PACKET_SIZES[19] = 2;
		PACKET_SIZES[14] = 7;
		PACKET_SIZES[66] = 8;
		PACKET_SIZES[38] = 8;
		PACKET_SIZES[50] = -1;
		PACKET_SIZES[21] = -1;
		PACKET_SIZES[1] = 2;
		PACKET_SIZES[28] = 8;
		PACKET_SIZES[49] = 6;
		PACKET_SIZES[54] = 12;
		PACKET_SIZES[5] = 5;
		PACKET_SIZES[40] = 3;
		PACKET_SIZES[81] = 4;
		PACKET_SIZES[53] = -1;
		PACKET_SIZES[72] = -1;
		PACKET_SIZES[26] = 7;
		PACKET_SIZES[68] = 3;
		PACKET_SIZES[4] = -1;
		PACKET_SIZES[75] = 16;
		PACKET_SIZES[47] = 3;
		PACKET_SIZES[8] = 8;
		PACKET_SIZES[44] = -1;
		PACKET_SIZES[6] = 8;
		PACKET_SIZES[15] = -1;
		PACKET_SIZES[39] = 7;
		PACKET_SIZES[56] = -1;
		PACKET_SIZES[23] = 3;
		PACKET_SIZES[64] = 8;
		PACKET_SIZES[80] = 7;
		PACKET_SIZES[71] = 2;
		PACKET_SIZES[13] = 3;
		PACKET_SIZES[76] = 3;
		PACKET_SIZES[18] = 6;
		PACKET_SIZES[55] = 16;
		PACKET_SIZES[52] = -1;
		PACKET_SIZES[41] = 3;
		PACKET_SIZES[61] = 2;
		PACKET_SIZES[20] = 8;
		PACKET_SIZES[70] = 8;
		PACKET_SIZES[31] = 3;
		PACKET_SIZES[69] = 0;
		PACKET_SIZES[9] = -1;
		PACKET_SIZES[73] = 2;
		PACKET_SIZES[34] = 11;
		PACKET_SIZES[59] = 18;
		PACKET_SIZES[3] = -1;
		PACKET_SIZES[65] = 3;
		PACKET_SIZES[30] = 3;
		PACKET_SIZES[42] = -1;
		PACKET_SIZES[32] = -1;
		PACKET_SIZES[45] = 3;
		PACKET_SIZES[51] = 4;
		PACKET_SIZES[33] = 11;
		PACKET_SIZES[43] = 2;
		PACKET_SIZES[12] = 4;
		PACKET_SIZES[0] = -1;
		PACKET_SIZES[77] = 7;
		PACKET_SIZES[37] = 15;
		PACKET_SIZES[24] = -1;
		PACKET_SIZES[48] = 1;
		PACKET_SIZES[79] = -1;
		PACKET_SIZES[63] = -1;
		PACKET_SIZES[62] = 8;
		PACKET_SIZES[7] = 7;
		PACKET_SIZES[10] = 7;
		PACKET_SIZES[2] = -1;
		PACKET_SIZES[11] = 7;
		PACKET_SIZES[78] = -1;
		PACKET_SIZES[60] = 3;
		PACKET_SIZES[29] = 7;
		PACKET_SIZES[35] = 3;
		PACKET_SIZES[27] = -1;
		PACKET_SIZES[74] = 0;
		PACKET_SIZES[67] = 7;
		PACKET_SIZES[22] = 4;
		PACKET_SIZES[36] = 3;
		PACKET_SIZES[17] = 0;
		PACKET_SIZES[58] = 6;
		PACKET_SIZES[57] = 4;
		PACKET_SIZES[46] = 8;
		PACKET_SIZES[16] = 15;

	}

	public static void decodeLogicPacket(final Player player, LogicPacket packet) {
		int packetId = packet.getId();
		InputStream stream = new InputStream(packet.getData());
		if (packetId == WALKING_PACKET || packetId == MINI_WALKING_PACKET) {
			if (!player.hasStarted() || !player.clientHasLoadedMapRegion() || player.isDead())
				return;
			if (player.isLocked())
				return;
			if (player.getFreezeDelay() >= Utils.currentTimeMillis()) {
				player.getSocialManager().sendGameMessage("A magical force prevents you from moving.");
				return;
			}
			boolean forceRun = stream.readUnsignedByte() == 1;
			int baseX = stream.readUnsignedShort();
			int baseY = stream.readUnsignedShort();
			player.stopAll();
			if (forceRun)
				player.setRun(forceRun);
			int steps = RouteFinder.findRoute(RouteFinder.WALK_ROUTEFINDER, player.getX(), player.getY(), player.getPlane(), player.getSize(), new FixedTileStrategy(baseX, baseY), true);
			int[] bufferX = RouteFinder.getLastPathBufferX();
			int[] bufferY = RouteFinder.getLastPathBufferY();
			int last = -1;
			for (int i = steps - 1; i >= 0; i--) {
				if (!player.addWalkSteps(bufferX[i], bufferY[i], 25, true))
					break;
				last = i;
			}
			if (last != -1) {
				WorldTile tile = new WorldTile(bufferX[last], bufferY[last], player.getPlane());
				player.getPackets().sendMinimapFlag(tile.getLocalX(player.getLastLoadedMapRegionTile(), player.getMapSize()), tile.getLocalY(player.getLastLoadedMapRegionTile(), player.getMapSize()));
			} else {
				player.getPackets().sendResetMinimapFlag();
			}
		} else if (packetId == INTERFACE_ON_PLAYER) {
			if (!player.hasStarted() || !player.clientHasLoadedMapRegion() || player.isDead())
				return;
			if (player.isLocked() || player.getEmotesManager().isDoingEmote())
				return;
			final int itemId = stream.readUnsignedShort();
			int playerIndex = stream.readUnsignedShort();
			int interfaceHash = stream.readIntV2();
			int interfaceSlot = stream.readUnsignedShortLE128();
			final boolean forceRun = stream.read128Byte() == 1;
			int interfaceId = interfaceHash >> 16;
			int componentId = interfaceHash - (interfaceId << 16);
			if (Utils.getInterfaceDefinitionsSize() <= interfaceId)
				return;
			if (!player.getInterfaceManager().containsInterface(interfaceId))
				return;
			if (componentId == 65535)
				componentId = -1;
			if (componentId != -1 && Utils.getInterfaceDefinitionsComponentsSize(interfaceId) <= componentId)
				return;
			final Player p2 = World.getPlayers().get(playerIndex);
			if (p2 == null || p2 == player || p2.isDead() || p2.hasFinished() || !player.getMapRegionsIds().contains(p2.getRegionId()))
				return;
			player.stopAll();
			if (forceRun)
				player.setRun(forceRun);
			switch (interfaceId) {
			case Inventory.INVENTORY_INTERFACE:
				final Item item = player.getInventory().getItem(interfaceSlot);
				if (item == null || item.getId() != itemId)
					return;
				player.setRouteEvent(new RouteEvent(p2, new Runnable() {
					@Override
					public void run() {
						if (!player.getControlerManager().processItemOnPlayer(p2, item))
							return;
					}
				}));
				break;
			case 662:
			case 747:
				if (player.getFamiliar() == null)
					return;
				player.resetWalkSteps();
				if ((interfaceId == 747 && componentId == 15) || (interfaceId == 662 && componentId == 65) || (interfaceId == 662 && componentId == 74) || interfaceId == 747 && componentId == 18) {
					if ((interfaceId == 662 && componentId == 74 || interfaceId == 747 && componentId == 24 || interfaceId == 747 && componentId == 18)) {
						if (player.getFamiliar().getSpecialAttack() != SpecialAttack.ENTITY)
							return;
					}
					if (!player.isCanPvp() || !p2.isCanPvp()) {
						player.getSocialManager().sendGameMessage("You can only attack players in a player-vs-player area.");
						return;
					}
					if (!player.getFamiliar().canAttack(p2)) {
						player.getSocialManager().sendGameMessage("You can only use your familiar in a multi-zone area.");
						return;
					} else {
						player.getFamiliar().setSpecial(interfaceId == 662 && componentId == 74 || interfaceId == 747 && componentId == 18);
						player.getFamiliar().setTarget(p2);
					}
				}
				break;
			case 193:
				switch (componentId) {
				case 28:
				case 32:
				case 24:
				case 20:
				case 30:
				case 34:
				case 26:
				case 22:
				case 29:
				case 33:
				case 25:
				case 21:
				case 31:
				case 35:
				case 27:
				case 23:
					if (Magic.checkCombatSpell(player, componentId, 1, false)) {
						player.setNextFaceWorldTile(new WorldTile(p2.getCoordFaceX(p2.getSize()), p2.getCoordFaceY(p2.getSize()), p2.getPlane()));
						if (!player.getControlerManager().canAttack(p2))
							return;
						if (!player.isCanPvp() || !p2.isCanPvp()) {
							player.getSocialManager().sendGameMessage("You can only attack players in a player-vs-player area.");
							return;
						}
						if (!p2.isAtMultiArea() || !player.isAtMultiArea()) {
							if (player.getAttackedBy() != p2 && player.getAttackedByDelay() > Utils.currentTimeMillis()) {
								player.getSocialManager().sendGameMessage("That " + (player.getAttackedBy() instanceof Player ? "player" : "npc") + " is already in combat.");
								return;
							}
							if (p2.getAttackedBy() != player && p2.getAttackedByDelay() > Utils.currentTimeMillis()) {
								if (p2.getAttackedBy() instanceof NPC) {
									p2.setAttackedBy(player); // changes
									// enemy
									// to player,
									// player has
									// priority over
									// npc on single
									// areas
								} else {
									player.getSocialManager().sendGameMessage("That player is already in combat.");
									return;
								}
							}
						}
						player.getActionManager().setAction(new PlayerCombat(p2));
					}
					break;
				}
			case 192:
				switch (componentId) {
				case 25: // air strike
				case 28: // water strike
				case 30: // earth strike
				case 32: // fire strike
				case 34: // air bolt
				case 39: // water bolt
				case 42: // earth bolt
				case 45: // fire bolt
				case 49: // air blast
				case 52: // water blast
				case 58: // earth blast
				case 63: // fire blast
				case 70: // air wave
				case 73: // water wave
				case 77: // earth wave
				case 80: // fire wave
				case 86: // teleblock
				case 84: // air surge
				case 87: // water surge
				case 89: // earth surge
				case 91: // fire surge
				case 99: // storm of armadyl
				case 36: // bind
				case 66: // Sara Strike
				case 67: // Guthix Claws
				case 68: // Flame of Zammy
				case 55: // snare
				case 81: // entangle
					if (Magic.checkCombatSpell(player, componentId, 1, false)) {
						player.setNextFaceWorldTile(new WorldTile(p2.getCoordFaceX(p2.getSize()), p2.getCoordFaceY(p2.getSize()), p2.getPlane()));
						if (!player.getControlerManager().canAttack(p2))
							return;
						if (!player.isCanPvp() || !p2.isCanPvp()) {
							player.getSocialManager().sendGameMessage("You can only attack players in a player-vs-player area.");
							return;
						}
						if (!p2.isAtMultiArea() || !player.isAtMultiArea()) {
							if (player.getAttackedBy() != p2 && player.getAttackedByDelay() > Utils.currentTimeMillis()) {
								player.getSocialManager().sendGameMessage("That " + (player.getAttackedBy() instanceof Player ? "player" : "npc") + " is already in combat.");
								return;
							}
							if (p2.getAttackedBy() != player && p2.getAttackedByDelay() > Utils.currentTimeMillis()) {
								if (p2.getAttackedBy() instanceof NPC) {
									p2.setAttackedBy(player); // changes
									// enemy
									// to player,
									// player has
									// priority over
									// npc on single
									// areas
								} else {
									player.getSocialManager().sendGameMessage("That player is already in combat.");
									return;
								}
							}
						}
						player.getActionManager().setAction(new PlayerCombat(p2));
					}
					break;
				}
				break;
			}
			if (Settings.DEBUG)
				System.out.println("Spell:" + componentId);
		} else if (packetId == INTERFACE_ON_NPC) {
			if (!player.hasStarted() || !player.clientHasLoadedMapRegion() || player.isDead())
				return;
			if (player.isLocked() || player.getEmotesManager().isDoingEmote())
				return;
			boolean forceRun = stream.readByte() == 1;
			int interfaceHash = stream.readInt();
			int npcIndex = stream.readUnsignedShortLE();
			int interfaceSlot = stream.readUnsignedShortLE128();
			@SuppressWarnings("unused")
			int junk2 = stream.readUnsignedShortLE();
			int interfaceId = interfaceHash >> 16;
			int componentId = interfaceHash - (interfaceId << 16);
			if (Utils.getInterfaceDefinitionsSize() <= interfaceId)
				return;
			if (!player.getInterfaceManager().containsInterface(interfaceId))
				return;
			if (componentId == 65535)
				componentId = -1;
			if (componentId != -1 && Utils.getInterfaceDefinitionsComponentsSize(interfaceId) <= componentId)
				return;
			NPC npc = World.getNPCs().get(npcIndex);
			if (npc == null || npc.isDead() || npc.hasFinished() || !player.getMapRegionsIds().contains(npc.getRegionId()))
				return;
			player.stopAll();
			if (forceRun)
				player.setRun(forceRun);
			if (interfaceId != Inventory.INVENTORY_INTERFACE) {
				if (!npc.getDefinitions().hasAttackOption()) {
					player.getSocialManager().sendGameMessage("You can't attack this npc.");
					return;
				}
			}
			switch (interfaceId) {
			case Inventory.INVENTORY_INTERFACE:
				Item item = player.getInventory().getItem(interfaceSlot);
				if (item == null || !player.getControlerManager().processItemOnNPC(npc, item))
					return;
				else if (npc instanceof Familiar) {
					Familiar familiar = (Familiar) npc;
					if (familiar != player.getFamiliar()) {
						player.getSocialManager().sendGameMessage("This is not your familiar!");
						return;
					}
				}
				NPCHandler.handleItemOnNPC(player, npc, item);
				break;
			case 662:
			case 747:
				if (player.getFamiliar() == null)
					return;
				player.resetWalkSteps();
				if ((interfaceId == 747 && componentId == 15) || (interfaceId == 662 && componentId == 65) || (interfaceId == 662 && componentId == 74) || interfaceId == 747 && componentId == 18 || interfaceId == 747 && componentId == 24) {
					if ((interfaceId == 662 && componentId == 74 || interfaceId == 747 && componentId == 18)) {
						if (player.getFamiliar().getSpecialAttack() != SpecialAttack.ENTITY)
							return;
					}
					if (npc instanceof Familiar) {
						Familiar familiar = (Familiar) npc;
						if (familiar == player.getFamiliar()) {
							player.getSocialManager().sendGameMessage("You can't attack your own familiar.");
							return;
						}
						if (!player.getFamiliar().canAttack(familiar.getOwner())) {
							player.getSocialManager().sendGameMessage("You can only attack players in a player-vs-player area.");
							return;
						}
					}
					if (!player.getFamiliar().canAttack(npc)) {
						player.getSocialManager().sendGameMessage("You can only use your familiar in a multi-zone area.");
						return;
					} else {
						player.getFamiliar().setSpecial(interfaceId == 662 && componentId == 74 || interfaceId == 747 && componentId == 18);
						player.getFamiliar().setTarget(npc);
					}
				}
				break;
			case 193:
				switch (componentId) {
				case 28:
				case 32:
				case 24:
				case 20:
				case 30:
				case 34:
				case 26:
				case 22:
				case 29:
				case 33:
				case 25:
				case 21:
				case 31:
				case 35:
				case 27:
				case 23:
					if (Magic.checkCombatSpell(player, componentId, 1, false)) {
						player.setNextFaceWorldTile(new WorldTile(npc.getCoordFaceX(npc.getSize()), npc.getCoordFaceY(npc.getSize()), npc.getPlane()));
						if (!player.getControlerManager().canAttack(npc))
							return;
						if (npc instanceof Familiar) {
							Familiar familiar = (Familiar) npc;
							if (familiar == player.getFamiliar()) {
								player.getSocialManager().sendGameMessage("You can't attack your own familiar.");
								return;
							}
							if (!familiar.canAttack(player)) {
								player.getSocialManager().sendGameMessage("You can't attack this npc.");
								return;
							}
						} else if (!npc.isForceMultiAttacked()) {
							if (!npc.isAtMultiArea() || !player.isAtMultiArea()) {
								if (player.getAttackedBy() != npc && player.getAttackedByDelay() > Utils.currentTimeMillis()) {
									player.getSocialManager().sendGameMessage("You are already in combat.");
									return;
								}
								if (npc.getAttackedBy() != player && npc.getAttackedByDelay() > Utils.currentTimeMillis()) {
									player.getSocialManager().sendGameMessage("This npc is already in combat.");
									return;
								}
							}
						}
						player.getActionManager().setAction(new PlayerCombat(npc));
					}
					break;
				}
			case 192:
				switch (componentId) {
				case 25: // air strike
				case 28: // water strike
				case 30: // earth strike
				case 32: // fire strike
				case 34: // air bolt
				case 39: // water bolt
				case 42: // earth bolt
				case 45: // fire bolt
				case 49: // air blast
				case 52: // water blast
				case 58: // earth blast
				case 63: // fire blast
				case 70: // air wave
				case 73: // water wave
				case 77: // earth wave
				case 80: // fire wave
				case 84: // air surge
				case 87: // water surge
				case 89: // earth surge
				case 66: // Sara Strike
				case 67: // Guthix Claws
				case 68: // Flame of Zammy
				case 93:
				case 91: // fire surge
				case 99: // storm of Armadyl
				case 36: // bind
				case 55: // snare
				case 81: // entangle
					if (Magic.checkCombatSpell(player, componentId, 1, false)) {
						player.setNextFaceWorldTile(new WorldTile(npc.getCoordFaceX(npc.getSize()), npc.getCoordFaceY(npc.getSize()), npc.getPlane()));
						if (!player.getControlerManager().canAttack(npc))
							return;
						if (npc instanceof Familiar) {
							Familiar familiar = (Familiar) npc;
							if (familiar == player.getFamiliar()) {
								player.getSocialManager().sendGameMessage("You can't attack your own familiar.");
								return;
							}
							if (!familiar.canAttack(player)) {
								player.getSocialManager().sendGameMessage("You can't attack this npc.");
								return;
							}
						} else if (!npc.isForceMultiAttacked()) {
							if (!npc.isAtMultiArea() || !player.isAtMultiArea()) {
								if (player.getAttackedBy() != npc && player.getAttackedByDelay() > Utils.currentTimeMillis()) {
									player.getSocialManager().sendGameMessage("You are already in combat.");
									return;
								}
								if (npc.getAttackedBy() != player && npc.getAttackedByDelay() > Utils.currentTimeMillis()) {
									player.getSocialManager().sendGameMessage("This npc is already in combat.");
									return;
								}
							}
						}
						player.getActionManager().setAction(new PlayerCombat(npc));
					}
					break;
				}
				break;
			}
			if (Settings.DEBUG)
				System.out.println("Spell:" + componentId);
		} else if (packetId == INTERFACE_ON_OBJECT) {
			int x = stream.readShort128();
			int interfaceHash = stream.readIntV2();
			final int interfaceId = interfaceHash >> 16;
			int slot = stream.readShort128();
			int itemId = stream.readShort128();
			boolean forceRun = stream.readByteC() == 1;
			int y = stream.readShort();
			int objectId = stream.readShortLE128();
			System.out.println(interfaceId + ", " + itemId + ", " + slot + ", " + objectId);
			if (!player.hasStarted() || !player.clientHasLoadedMapRegion() || player.isDead())
				return;
			if (player.isLocked() || player.getEmotesManager().isDoingEmote())
				return;
			final WorldTile tile = new WorldTile(x, y, player.getPlane());
			int regionId = tile.getRegionId();
			if (!player.getMapRegionsIds().contains(regionId))
				return;
			WorldObject mapObject = World.getObjectWithId(tile, objectId);
			if (mapObject == null || mapObject.getId() != objectId)
				return;
			final WorldObject object = mapObject;
			final Item item = player.getInventory().getItem(slot);
			if (player.isDead() || Utils.getInterfaceDefinitionsSize() <= interfaceId)
				return;
			if (player.isLocked())
				return;
			if (!player.getInterfaceManager().containsInterface(interfaceId))
				return;
			if (item == null || item.getId() != itemId)
				return;
			player.stopAll();
			if (forceRun)
				player.setRun(forceRun);
			switch (interfaceId) {
			case Inventory.INVENTORY_INTERFACE: // inventory
				ObjectHandler.handleItemOnObject(player, object, interfaceId, item);
				break;
			}
		} else if (packetId == PLAYER_OPTION_1_PACKET) {
			if (!player.hasStarted() || !player.clientHasLoadedMapRegion() || player.isDead())
				return;
			int playerIndex = stream.readUnsignedShort128();
			boolean forceRun = stream.readUnsignedByte() == 1;
			Player p2 = World.getPlayers().get(playerIndex);
			if (forceRun)
				player.setRun(forceRun);
			player.stopAll();

			if (p2 == null || p2 == player || p2.isDead() || p2.hasFinished() || !player.getMapRegionsIds().contains(p2.getRegionId()))
				return;
			if (player.isLocked() || player.getEmotesManager().isDoingEmote() || !player.getControlerManager().canPlayerOption1(p2))
				return;
			if (!player.isCanPvp())
				return;
			if (!player.getControlerManager().canAttack(p2))
				return;
			if (!player.isCanPvp() || !p2.isCanPvp()) {
				player.getSocialManager().sendGameMessage("You can only attack players in a player-vs-player area.");
				return;
			}
			if (!p2.isAtMultiArea() || !player.isAtMultiArea()) {
				if (player.getAttackedBy() != p2 && player.getAttackedByDelay() > Utils.currentTimeMillis()) {
					player.getSocialManager().sendGameMessage("You are already in combat.");
					return;
				}
				if (p2.getAttackedBy() != player && p2.getAttackedByDelay() > Utils.currentTimeMillis()) {
					if (p2.getAttackedBy() instanceof NPC) {
						p2.setAttackedBy(player); // changes enemy to player,
						// player has priority over
						// npc on single areas
					} else {
						player.getSocialManager().sendGameMessage("That player is already in combat.");
						return;
					}
				}
			}

			player.getActionManager().setAction(new PlayerCombat(p2));
		} else if (packetId == PLAYER_OPTION_2_PACKET) {
			if (!player.hasStarted() || !player.clientHasLoadedMapRegion() || player.isDead())
				return;
			int playerIndex = stream.readUnsignedShort128();
			boolean forceRun = stream.readUnsignedByte() == 1;
			Player p2 = World.getPlayers().get(playerIndex);
			if (p2 == null || p2 == player || p2.isDead() || p2.hasFinished() || !player.getMapRegionsIds().contains(p2.getRegionId()))
				return;
			if (player.isLocked())
				return;
			if (!player.getControlerManager().canPlayerOption2(p2))
				return;
			if (forceRun)
				player.setRun(forceRun);
			player.stopAll();
			player.getActionManager().setAction(new PlayerFollow(p2));
		} else if (packetId == PLAYER_OPTION_3_PACKET) {
			final boolean forceRun = stream.readUnsignedByte() == 1;
			int playerIndex = stream.readUnsignedShortLE128();
			final Player p2 = World.getPlayers().get(playerIndex);
			if (p2 == null || p2 == player || p2.isDead() || p2.hasFinished() || !player.getMapRegionsIds().contains(p2.getRegionId()))
				return;
			if (player.isLocked())
				return;
			if (forceRun)
				player.setRun(forceRun);
			player.stopAll();
			player.setRouteEvent(new RouteEvent(p2, new Runnable() {
				@Override
				public void run() {
					if (!player.getControlerManager().canPlayerOption3(p2))
						return;
				}
			}));
		} else if (packetId == PLAYER_OPTION_4_PACKET) {
			boolean forceRun = stream.readUnsigned128Byte() == 1;
			int playerIndex = stream.readUnsignedShortLE128();
			final Player p2 = World.getPlayers().get(playerIndex);
			if (p2 == null || p2 == player || p2.isDead() || p2.hasFinished() || !player.getMapRegionsIds().contains(p2.getRegionId()))
				return;
			if (player.isLocked())
				return;
			if (forceRun)
				player.setRun(forceRun);
			player.setRouteEvent(new RouteEvent(p2, new Runnable() {
				@Override
				public void run() {
					if (!player.getControlerManager().canPlayerOption4(p2))
						return;
					player.stopAll();
					if (player.isStarter()) {
						player.getSocialManager().sendGameMessage("You can't trade for the first half hour after creating account.");
						return;
					}
					if (player.isCantTrade() || player.getControlerManager().getControler() != null) {
						player.getSocialManager().sendGameMessage("You are busy.");
						return;
					}
					if (p2.getInterfaceManager().containsScreenInter() || p2.isCantTrade() || p2.getControlerManager().getControler() != null

							|| p2.isLocked()) {
						player.getSocialManager().sendGameMessage("The other player is busy.");
						return;
					}
					if (!p2.withinDistance(player, 14)) {
						player.getSocialManager().sendGameMessage("Unable to find target " + p2.getDisplayName());
						return;
					}
					if (p2.getTemporaryAttributtes().get("TradeTarget") == player) {
						p2.getTemporaryAttributtes().remove("TradeTarget");
						player.getTrade().openTrade(p2);
						p2.getTrade().openTrade(player);
						return;
					}
					player.getTemporaryAttributtes().put("TradeTarget", p2);
					player.getSocialManager().sendGameMessage("Sending " + p2.getDisplayName() + " a request...");
					p2.getSocialManager().sendTradeRequestMessage(player);
				}
			}));
		} else if (packetId == PLAYER_OPTION_6_PACKET) {
			if (!player.hasStarted() || !player.clientHasLoadedMapRegion() || player.isDead())
				return;
			boolean forceRun = stream.readUnsignedByte() == 1;
			int playerIndex = stream.readUnsignedShortLE128();
			Player p2 = World.getPlayers().get(playerIndex);
			if (p2 == null || p2 == player || p2.isDead() || p2.hasFinished() || !player.getMapRegionsIds().contains(p2.getRegionId()))
				return;
			if (player.isLocked())
				return;
			if (forceRun)
				player.setRun(forceRun);
			player.stopAll();
			if (player.getRights() >= 1)
				player.getDialogueManager().startDialogue("ModReportD", p2);
			else
				ReportAbuse.report(player, p2.getDisplayName());
		} else if (packetId == PLAYER_OPTION_9_PACKET) {
			boolean forceRun = stream.readUnsignedByte() == 1;
			int playerIndex = stream.readUnsignedShortLE128();
			Player p2 = World.getPlayers().get(playerIndex);
			if (p2 == null || p2 == player || p2.isDead() || p2.hasFinished() || !player.getMapRegionsIds().contains(p2.getRegionId()))
				return;
			if (player.isLocked())
				return;
			if (forceRun)
				player.setRun(forceRun);
			player.stopAll();
		} else if (packetId == ATTACK_NPC) {
			if (!player.hasStarted() || !player.clientHasLoadedMapRegion() || player.isDead())
				return;
			boolean forceRun = stream.read128Byte() == 1;
			int npcIndex = stream.readUnsignedShort();

			NPC npc = World.getNPCs().get(npcIndex);
			if (npc == null || npc.isDead() || npc.hasFinished() || !player.getMapRegionsIds().contains(npc.getRegionId()) || !npc.getDefinitions().hasAttackOption())
				return;
			if (player.isLocked() || player.getEmotesManager().isDoingEmote())
				return;
			if (!player.getControlerManager().canAttack(npc))
				return;
			if (forceRun) // you scrwed up cutscenes
				player.setRun(forceRun);
			player.stopAll();
			if (npc instanceof Familiar) {
				Familiar familiar = (Familiar) npc;
				if (familiar == player.getFamiliar()) {
					player.getSocialManager().sendGameMessage("You can't attack your own familiar.");
					return;
				}
				if (!familiar.canAttack(player)) {
					player.getSocialManager().sendGameMessage("You can't attack this npc.");
					return;
				}
			} else if (!npc.isForceMultiAttacked()) {
				if (!npc.isAtMultiArea() || !player.isAtMultiArea()) {
					if (player.getAttackedBy() != npc && player.getAttackedByDelay() > Utils.currentTimeMillis()) {
						player.getSocialManager().sendGameMessage("You are already in combat.");
						return;
					}
					if (npc.getAttackedBy() != player && npc.getAttackedByDelay() > Utils.currentTimeMillis()) {
						player.getSocialManager().sendGameMessage("This npc is already in combat.");
						return;
					}
				}
			}
			player.getActionManager().setAction(new PlayerCombat(npc));
		} else if (packetId == NPC_CLICK1_PACKET)
			NPCHandler.handleOption1(player, stream);
		else if (packetId == NPC_CLICK2_PACKET)
			NPCHandler.handleOption2(player, stream);
		else if (packetId == NPC_CLICK3_PACKET)
			NPCHandler.handleOption3(player, stream);
		else if (packetId == NPC_CLICK4_PACKET)
			NPCHandler.handleOption4(player, stream);
		else if (packetId == OBJECT_CLICK1_PACKET)
			ObjectHandler.handleOption(player, stream, 1);
		else if (packetId == OBJECT_CLICK2_PACKET)
			ObjectHandler.handleOption(player, stream, 2);
		else if (packetId == OBJECT_CLICK3_PACKET)
			ObjectHandler.handleOption(player, stream, 3);
		else if (packetId == OBJECT_CLICK4_PACKET)
			ObjectHandler.handleOption(player, stream, 4);
		else if (packetId == OBJECT_CLICK5_PACKET)
			ObjectHandler.handleOption(player, stream, 5);
		else if (packetId == ITEM_TAKE_PACKET) {
			if (!player.hasStarted() || !player.clientHasLoadedMapRegion() || player.isDead())
				return;
			if (player.isLocked())
				return;
			final int id = stream.readUnsignedShort128();
			boolean forceRun = stream.readByteC() == 1;
			int x = stream.readUnsignedShort();
			int y = stream.readUnsignedShortLE128();

			final WorldTile tile = new WorldTile(x, y, player.getPlane());
			final int regionId = tile.getRegionId();
			if (!player.getMapRegionsIds().contains(regionId))
				return;
			final FloorItem item = World.getRegion(regionId).getGroundItem(id, tile, player);
			if (item == null || !player.getControlerManager().canTakeItem(item))
				return;
			if (forceRun)
				player.setRun(forceRun);
			player.stopAll();
			player.setRouteEvent(new RouteEvent(item, new Runnable() {
				@Override
				public void run() {
					final FloorItem item = World.getRegion(regionId).getGroundItem(id, tile, player);
					if (item == null)
						return;
					// player.setNextFaceWorldTile(tile);
					if (World.removeGroundItem(player, item))
						Logger.globalLog(player.getUsername(), player.getIP(), new String(" has picked up item [ id: " + item.getId() + ", amount: " + item.getAmount() + " ] originally owned to " + (item.getOwner() == null ? "no owner" : item.getOwner()) + "."));
				}
			}));
		}
	}

	public void processPackets(final int packetId, InputStream stream, int length) {
		if (packetId == PING_PACKET) {
			// kk we ping :)
		} else if (packetId == MOUVE_MOUSE_PACKET) {
			// USELESS PACKET
		} else if (packetId == KEY_TYPED_PACKET) {
			// USELESS PACKET
		} else if (packetId == RECEIVE_PACKET_COUNT_PACKET) {
			// interface packets
			stream.readShort();
		} else if (packetId == INTERFACE_ON_INTERFACE) {
			InventoryOptionsHandler.handleInterfaceOnInterface(player, stream);
		} else if (packetId == AFK_PACKET) {
			stream.readUnsignedShort(); // idle time
			// System.out.println(idle);
			// player.getSession().getChannel().close();
		} else if (packetId == CLOSE_INTERFACE_PACKET) {
			if (player.hasStarted() && !player.hasFinished() && !player.isRunning()) { // used
				// for
				// old
				// welcome
				// screen
				player.run();
				return;
			}
			player.stopAll();
		} else if (packetId == MOVE_CAMERA_PACKET) {
			// not using it atm
			stream.readUnsignedShort();
			stream.readUnsignedShort();
		} else if (packetId == IN_OUT_SCREEN_PACKET) {
			// not using this check because not 100% efficient
			@SuppressWarnings("unused")
			boolean inScreen = stream.readByte() == 1;
		} else if (packetId == SCREEN_PACKET) {
			int displayMode = stream.readUnsignedByte();
			int width = stream.readUnsignedShort();
			int height = stream.readUnsignedShort();
			@SuppressWarnings("unused")
			boolean switchScreenMode = stream.readUnsignedByte() == 1;
			player.setScreenWidth(width);
			player.setScreenHeight(height);

			if (!player.hasStarted() || player.hasFinished() || displayMode == player.getDisplayMode() || !player.getInterfaceManager().containsInterface(742))
				return;
			player.setDisplayMode(displayMode);
			player.getInterfaceManager().removeAll();
			player.getInterfaceManager().sendInterfaces();
			player.getInterfaceManager().sendInterface(742);
		} else if (packetId == CLICK_PACKET) {
			int mouseHash = stream.readShortLE128();
			int mouseButton = mouseHash >> 15;
			int time = mouseHash - (mouseButton << 15); // time
			int positionHash = stream.readIntV1();
			int y = positionHash >> 16; // y;
			int x = positionHash - (y << 16); // x
			@SuppressWarnings("unused")
			boolean clicked;
			// mass click or stupid autoclicker, lets stop lagg
			if (time <= 1 || x < 0 || x > player.getScreenWidth() || y < 0 || y > player.getScreenHeight()) {
				// player.getSession().getChannel().close();
				clicked = false;
				return;
			}
			clicked = true;
		} else if (packetId == DIALOGUE_CONTINUE_PACKET) {
			int junk = stream.readShort128();
			int interfaceHash = stream.readInt();
			int interfaceId = interfaceHash >> 16;
			int buttonId = (interfaceHash & 0xFF);
			if (Utils.getInterfaceDefinitionsSize() <= interfaceId) {
				// hack, or server error or client error
				// player.getSession().getChannel().close();
				return;
			}
			if (!player.isRunning() || !player.getInterfaceManager().containsInterface(interfaceId))
				return;
			if (Settings.DEBUG)
				Logger.log(this, "Dialogue: " + interfaceId + ", " + buttonId + ", " + junk);
			int componentId = interfaceHash - (interfaceId << 16);
			if (interfaceId == 513)
				SkillsDialogue.handleSetQuantityButtons(player, componentId);
			
			player.getDialogueManager().continueDialogue(interfaceId, componentId);
		} else if (packetId == WORLD_MAP_CLICK) {
			int coordinateHash = stream.readIntV1();
			int x = coordinateHash >> 14;
			int y = coordinateHash & 0x3fff;
			int plane = coordinateHash >> 28;
			Integer hash = (Integer) player.getTemporaryAttributtes().get("worldHash");
			if (hash == null || coordinateHash != hash)
				player.getTemporaryAttributtes().put("worldHash", coordinateHash);
			else {
				player.getTemporaryAttributtes().remove("worldHash");
				player.getHintIconsManager().addHintIcon(x, y, plane, 20, 0, 2, -1, true);
				player.getVarsManager().sendVar(1159, coordinateHash);
			}
		} else if (packetId == ACTION_BUTTON1_PACKET || packetId == ACTION_BUTTON2_PACKET || packetId == ACTION_BUTTON4_PACKET || packetId == ACTION_BUTTON5_PACKET || packetId == ACTION_BUTTON6_PACKET || packetId == ACTION_BUTTON7_PACKET || packetId == ACTION_BUTTON8_PACKET || packetId == ACTION_BUTTON3_PACKET || packetId == ACTION_BUTTON9_PACKET || packetId == ACTION_BUTTON10_PACKET) {
			ButtonHandler.handleButtons(player, stream, packetId);
		} else if (packetId == ENTER_NAME_PACKET) {
			if (!player.isRunning() || player.isDead())
				return;
			String value = stream.readString();
			if (value.equals(""))
				return;
			if (player.getInterfaceManager().containsInterface(1108))
				player.getFriendsIgnores().setChatPrefix(value);
			else if (player.getTemporaryAttributtes().remove("forum_authuserinput") == Boolean.TRUE) {
				player.getTemporaryAttributtes().put("forum_authuser", value);
				player.getTemporaryAttributtes().put("forum_authpasswordinput", true);
				player.getPackets().sendInputNameScript("Enter your forum password:");
			} else if (player.getTemporaryAttributtes().remove("forum_authpasswordinput") == Boolean.TRUE) {
				String authuser = (String) player.getTemporaryAttributtes().get("forum_authuser");
				String authpassword = value;
				if (authuser == null || authpassword == null)
					return;
				player.getTemporaryAttributtes().remove("forum_authuser");
				ForumAuthManager.registerAuth(player, authuser, authpassword);
			}
		} else if (packetId == ENTER_LONG_TEXT_PACKET) {
			if (!player.isRunning() || player.isDead())
				return;
			String value = stream.readString();
			if (value.equals(""))
				return;
			if (player.getTemporaryAttributtes().remove("entering_note") == Boolean.TRUE) {
				player.getNotes().add(value);
			} else if (player.getTemporaryAttributtes().remove("editing_note") == Boolean.TRUE)
				player.getNotes().edit(value);
			else if (player.getTemporaryAttributtes().remove("change_pass") == Boolean.TRUE) {
				if (value.length() < 5 || value.length() > 15) {
					player.getSocialManager().sendGameMessage("Password length is limited to 5-15 characters.");
					return;
				}
				player.setPassword(Encrypt.encryptSHA1(value));
				player.getSocialManager().sendGameMessage("You have changed your password! Your new password is \"" + value + "\".");
			} else if (player.getTemporaryAttributtes().remove("change_troll_name") == Boolean.TRUE) {
				value = Utils.formatPlayerNameForDisplay(value);
				if (value.length() < 3 || value.length() > 14) {
					player.getSocialManager().sendGameMessage("You can't use a name shorter than 3 or longer than 14 characters.");
					return;
				}
				if (value.equalsIgnoreCase("none")) {
					player.getPetManager().setTrollBabyName(null);
				} else {
					player.getPetManager().setTrollBabyName(value);
					if (player.getPet() != null && player.getPet().getId() == Pets.TROLL_BABY.getBabyNpcId()) {
						player.getPet().setName(value);
					}
				}
			} else if (player.getTemporaryAttributtes().remove("yellcolor") == Boolean.TRUE) {
				if (value.length() != 6) {
					player.getSocialManager().sendGameMessage("The HEX yell color you wanted to pick cannot be longer and shorter then 6.");
				} else if (Utils.containsInvalidCharacter(value) || value.contains("_")) {
					player.getSocialManager().sendGameMessage("The requested yell color can only contain numeric and regular characters.");
				} else {
					player.setYellColor(value);
					player.getSocialManager().sendGameMessage("Your yell color has been changed to <col=" + player.getYellColor() + ">" + player.getYellColor() + "</col>.");
				}
			} else if (player.getTemporaryAttributtes().remove("setdisplay") == Boolean.TRUE) {
				if (Utils.invalidAccountName(Utils.formatPlayerNameForProtocol(value))) {
					player.getSocialManager().sendGameMessage("Name contains invalid characters or is too short/long.");
					return;
				}
				if (!DisplayNames.setDisplayName(player, value)) {
					player.getSocialManager().sendGameMessage("This name is already in use.");
					return;
				}
				player.getSocialManager().sendGameMessage("Your display name was successfully changed.");
			}
		} else if (packetId == ENTER_INTEGER_PACKET) {
			if (!player.isRunning() || player.isDead())
				return;
			int value = stream.readInt();
			if (value < 0)
				return;
			if ((player.getInterfaceManager().containsInterface(762) && player.getInterfaceManager().containsInterface(763)) || player.getInterfaceManager().containsInterface(11)) {
				Integer bank_item_X_Slot = (Integer) player.getTemporaryAttributtes().remove("bank_item_X_Slot");
				if (bank_item_X_Slot == null)
					return;
				player.getBank().setLastX(value);
				player.getBank().refreshLastX();
				if (player.getTemporaryAttributtes().remove("bank_isWithdraw") != null)
					player.getBank().withdrawItem(bank_item_X_Slot, value);
				else
					player.getBank().depositItem(bank_item_X_Slot, value, player.getInterfaceManager().containsInterface(11) ? false : true);
			} else if (player.getInterfaceManager().containsInterface(206) && player.getInterfaceManager().containsInterface(207)) {
				Integer pc_item_X_Slot = (Integer) player.getTemporaryAttributtes().remove("pc_item_X_Slot");
				if (pc_item_X_Slot == null)
					return;
				if (player.getTemporaryAttributtes().remove("pc_isRemove") != null)
					player.getPriceCheckManager().removeItem(pc_item_X_Slot, value);
				else
					player.getPriceCheckManager().addItem(pc_item_X_Slot, value);
			} else if (player.getInterfaceManager().containsInterface(671) && player.getInterfaceManager().containsInterface(665)) {
				if (player.getFamiliar() == null || player.getFamiliar().getBob() == null)
					return;
				Integer bob_item_X_Slot = (Integer) player.getTemporaryAttributtes().remove("bob_item_X_Slot");
				if (bob_item_X_Slot == null)
					return;
				if (player.getTemporaryAttributtes().remove("bob_isRemove") != null)
					player.getFamiliar().getBob().removeItem(bob_item_X_Slot, value);
				else
					player.getFamiliar().getBob().addItem(bob_item_X_Slot, value);
			} else if (player.getInterfaceManager().containsInterface(335) && player.getInterfaceManager().containsInterface(336)) {
				Integer trade_item_X_Slot = (Integer) player.getTemporaryAttributtes().remove("trade_item_X_Slot");
				if (trade_item_X_Slot == null)
					return;
				if (player.getTemporaryAttributtes().remove("trade_isRemove") != null)
					player.getTrade().removeItem(trade_item_X_Slot, value);
				else
					player.getTrade().addItem(trade_item_X_Slot, value);
			} else if (player.getInterfaceManager().containsInterface(105) && player.getTemporaryAttributtes().remove("GEPRICESET") != null) {
				player.getGeManager().modifyPricePerItem(value);
			} else if (player.getInterfaceManager().containsInterface(105) && player.getTemporaryAttributtes().remove("GEQUANTITYSET") != null) {
				player.getGeManager().modifyAmount(value);
			} else if (player.getTemporaryAttributtes().remove("xformring") == Boolean.TRUE)
				player.getAppearence().transformIntoNPC(value);
			else if (player.getTemporaryAttributtes().get("skillId") != null) {
				if (player.getEquipment().wearingArmour()) {
					player.getDialogueManager().finishDialogue();
					player.getDialogueManager().startDialogue("SimpleMessage", "You cannot do this while having armour on!");
					return;
				}
				int skillId = (Integer) player.getTemporaryAttributtes().remove("skillId");
				if (skillId == Skills.HITPOINTS && value <= 9)
					value = 10;
				else if (value < 1)
					value = 1;
				else if (value > 99)
					value = 99;
				player.getSkills().set(skillId, value);
				player.getSkills().setXp(skillId, Skills.getXPForLevel(value));
				player.getAppearence().generateAppearenceData();
				player.getDialogueManager().finishDialogue();

			}
		} else if (packetId == SWITCH_INTERFACE_COMPONENTS_PACKET) {
			int fromInterfaceHash = stream.readInt();
			int fromSlot = stream.readUnsignedShortLE128();
			int toInterfaceHash = stream.readIntV1();
			int toSlot = stream.readUnsignedShortLE128();
			stream.readUnsignedShortLE(); // from ItemId
			stream.readUnsignedShortLE(); // to ItemId
			int toInterfaceId = toInterfaceHash >> 16;
			int toComponentId = toInterfaceHash - (toInterfaceId << 16);
			int fromInterfaceId = fromInterfaceHash >> 16;
			int fromComponentId = fromInterfaceHash - (fromInterfaceId << 16);
			if (Utils.getInterfaceDefinitionsSize() <= fromInterfaceId || Utils.getInterfaceDefinitionsSize() <= toInterfaceId)
				return;
			if (!player.getInterfaceManager().containsInterface(fromInterfaceId) || !player.getInterfaceManager().containsInterface(toInterfaceId))
				return;
			if (fromComponentId != -1 && Utils.getInterfaceDefinitionsComponentsSize(fromInterfaceId) <= fromComponentId)
				return;
			if (toComponentId != -1 && Utils.getInterfaceDefinitionsComponentsSize(toInterfaceId) <= toComponentId)
				return;
			if (fromInterfaceId == Inventory.INVENTORY_INTERFACE && fromComponentId == 0 && toInterfaceId == Inventory.INVENTORY_INTERFACE && toComponentId == 0) {
				toSlot -= 28;
				if (toSlot < 0 || toSlot >= player.getInventory().getItemsContainerSize() || fromSlot >= player.getInventory().getItemsContainerSize())
					return;
				player.getInventory().switchItem(fromSlot, toSlot);
			} else if (fromInterfaceId == 763 && fromComponentId == 0 && toInterfaceId == 763 && toComponentId == 0) {
				if (toSlot >= player.getInventory().getItemsContainerSize() || fromSlot >= player.getInventory().getItemsContainerSize())
					return;
				player.getInventory().switchItem(fromSlot, toSlot);

			} else if (fromInterfaceId == 762 && toInterfaceId == 762) {
				player.getBank().switchItem(fromSlot, toSlot, fromComponentId, toComponentId);
			} else if (fromInterfaceId == 1265 && toInterfaceId == 1266 && player.getTemporaryAttributtes().get("is_buying") != null) {
				if ((boolean) player.getTemporaryAttributtes().get("is_buying") == true) {
					Shop shop = (Shop) player.getTemporaryAttributtes().get("shop_instance");
					if (shop == null)
						return;
					// shop.buyItem(player, fromSlot, 1);
				}
			} else if (fromInterfaceId == 34 && toInterfaceId == 34)
				player.getNotes().switchNotes(fromSlot, toSlot);
			if (Settings.DEBUG)
				System.out.println("Switch item " + fromInterfaceId + ", " + fromSlot + ", " + toSlot + " fromCom: " + fromComponentId + " toCom: " + toComponentId);
		} else if (packetId == DONE_LOADING_REGION_PACKET) {
			/*
			 * if(!player.clientHasLoadedMapRegion()) { //load objects and items here player.setClientHasLoadedMapRegion(); }
			 * //player.refreshSpawnedObjects(); //player.refreshSpawnedItems();
			 */
		} else if (packetId == WALKING_PACKET || packetId == MINI_WALKING_PACKET || packetId == ITEM_TAKE_PACKET || packetId == PLAYER_OPTION_2_PACKET || packetId == PLAYER_OPTION_3_PACKET || packetId == PLAYER_OPTION_4_PACKET || packetId == PLAYER_OPTION_6_PACKET || packetId == PLAYER_OPTION_9_PACKET || packetId == PLAYER_OPTION_1_PACKET || packetId == ATTACK_NPC || packetId == INTERFACE_ON_PLAYER || packetId == INTERFACE_ON_NPC || packetId == NPC_CLICK1_PACKET || packetId == NPC_CLICK2_PACKET || packetId == NPC_CLICK3_PACKET || packetId == NPC_CLICK4_PACKET || packetId == OBJECT_CLICK1_PACKET || packetId == SWITCH_INTERFACE_COMPONENTS_PACKET || packetId == OBJECT_CLICK2_PACKET || packetId == OBJECT_CLICK3_PACKET || packetId == OBJECT_CLICK4_PACKET || packetId == OBJECT_CLICK5_PACKET || packetId == INTERFACE_ON_OBJECT)
			player.addLogicPacketToQueue(new LogicPacket(packetId, length, stream));
		else if (packetId == OBJECT_EXAMINE_PACKET) {
			ObjectHandler.handleOption(player, stream, -1);
		} else if (packetId == NPC_EXAMINE_PACKET) {
			NPCHandler.handleExamine(player, stream);
		} else if (packetId == ITEM_GROUND_EXAMINE_PACKET) {
			int itemId = stream.readUnsignedShort();
			player.getSocialManager().sendGameMessage(ItemExamines.getExamine(new Item(itemId)));
		} else if (packetId == JOIN_FRIEND_CHAT_PACKET) {
			if (!player.hasStarted())
				return;
			FriendChatsManager.joinChat(stream.readString(), player);
		} else if (packetId == KICK_FRIEND_CHAT_PACKET) {
			if (!player.hasStarted())
				return;
			player.setLastPublicMessage(Utils.currentTimeMillis() + 1000); // avoids
			// message
			// appearing
			player.kickPlayerFromFriendsChannel(stream.readString());
		} else if (packetId == CHANGE_FRIEND_CHAT_PACKET) {
			if (!player.hasStarted() || !player.getInterfaceManager().containsInterface(1108))
				return;
			int rank = stream.readUnsigned128Byte();
			player.getFriendsIgnores().changeRank(stream.readString(), rank);
		} else if (packetId == ADD_FRIEND_PACKET) {
			if (!player.hasStarted())
				return;
			player.getFriendsIgnores().addFriend(stream.readString());
		} else if (packetId == REMOVE_FRIEND_PACKET) {
			if (!player.hasStarted())
				return;
			player.getFriendsIgnores().removeFriend(stream.readString());
		} else if (packetId == ADD_IGNORE_PACKET) {
			if (!player.hasStarted())
				return;
			player.getFriendsIgnores().addIgnore(stream.readString(), stream.readUnsignedByte() == 1);
		} else if (packetId == REMOVE_IGNORE_PACKET) {
			if (!player.hasStarted())
				return;
			player.getFriendsIgnores().removeIgnore(stream.readString());
		} else if (packetId == SEND_FRIEND_MESSAGE_PACKET) {
			if (!player.hasStarted())
				return;
			if (player.getMuted() > Utils.currentTimeMillis()) {
				player.getSocialManager().sendGameMessage("You temporary muted. Recheck in 48 hours.");
				return;
			}
			String username = stream.readString();
			Player p2 = World.getPlayerByDisplayName(username);
			if (p2 == null)
				return;

			player.getFriendsIgnores().sendMessage(p2, new ChatMessage(Huffman.readEncryptedMessage(150, stream)));
		} else if (packetId == SEND_FRIEND_QUICK_CHAT_PACKET) {
			if (!player.hasStarted())
				return;
			String username = stream.readString();
			int fileId = stream.readUnsignedShort();
			if (!Utils.isQCValid(fileId))
				return;
			byte[] data = null;
			if (length > 3 + username.length()) {
				data = new byte[length - (3 + username.length())];
				stream.readBytes(data);
			}
			data = Utils.completeQuickMessage(player, fileId, data);
			Player p2 = World.getPlayerByDisplayName(username);
			if (p2 == null)
				return;
			player.getFriendsIgnores().sendQuickChatMessage(p2, new QuickChatMessage(fileId, data));
		} else if (packetId == PUBLIC_QUICK_CHAT_PACKET) {
			if (!player.hasStarted())
				return;
			if (player.getLastPublicMessage() > Utils.currentTimeMillis())
				return;
			player.setLastPublicMessage(Utils.currentTimeMillis() + 300);
			// just tells you which client script created packet
			@SuppressWarnings("unused")
			boolean secondClientScript = stream.readByte() == 1;// script 5059
			// or 5061
			int fileId = stream.readUnsignedShort();
			if (!Utils.isQCValid(fileId))
				return;
			byte[] data = null;
			if (length > 3) {
				data = new byte[length - 3];
				stream.readBytes(data);
			}
			data = Utils.completeQuickMessage(player, fileId, data);
			player.sendPublicMessage(new QuickChatMessage(fileId, data));
		} else if (packetId == CHAT_PACKET) {
			if (!player.hasStarted())
				return;
			if (player.getLastPublicMessage() > Utils.currentTimeMillis())
				return;
			player.setLastPublicMessage(Utils.currentTimeMillis() + 300);
			stream.readUnsignedByte();
			int colors = stream.readUnsignedByte();
			int effects = stream.readUnsignedByte();
			String message = Huffman.readEncryptedMessage(76, stream);
			if (message == null || message.replaceAll(" ", "").equals(""))
				return;

			if (message.startsWith("::") || message.startsWith(";;")) {
				// if command exists and processed wont send message as public
				// message
				Commands.processCommand(player, message.replace("::", "").replace(";;", ""), false, false);
				return;
			}
			if (player.getMuted() > Utils.currentTimeMillis()) {
				player.getSocialManager().sendGameMessage("You temporary muted. Recheck in 48 hours.");
				return;
			}
			if (message.substring(0, 1).equalsIgnoreCase("/") && !message.substring(1, 2).equalsIgnoreCase("/"))
				player.sendFriendsChannelMessage(new ChatMessage(message.replaceFirst("/", "")));
			else
				player.sendPublicMessage(new PublicChatMessage(message, effects, colors));
		} else if (packetId == COMMANDS_PACKET) {
			if (!player.isRunning())
				return;
			boolean clientCommand = stream.readUnsignedByte() == 1;
			String command = stream.readString();
			if (!Commands.processCommand(player, command, true, clientCommand) && Settings.DEBUG)
				Logger.log(this, "Command: " + command);
		} else if (packetId == REPORT_ABUSE_PACKET) {
			if (!player.hasStarted())
				return;
			String displayName = stream.readString();
			int type = stream.readUnsignedByte();
			boolean mute = stream.readUnsignedByte() == 1;
			@SuppressWarnings("unused")
			String unknown2 = stream.readString();
			ReportAbuse.report(player, displayName, type, mute);
		} else if (packetId == GRAND_EXCHANGE_ITEM_SELECT_PACKET) {
			int itemId = stream.readUnsignedShort();
			player.getGeManager().chooseItem(itemId);
		} else {
			if (Settings.DEBUG)
				Logger.log(this, "Missing packet " + packetId + ", expected size: " + length + ", actual size: " + PACKET_SIZES[packetId]);
		}
	}
}
