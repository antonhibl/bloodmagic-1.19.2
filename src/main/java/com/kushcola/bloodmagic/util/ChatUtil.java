package com.kushcola.bloodmagic.util;

import java.text.DecimalFormat;
import java.util.function.Supplier;

import com.kushcola.bloodmagic.BloodMagic;
import com.kushcola.bloodmagic.util.helper.TextHelper;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent.Context;

public class ChatUtil
{
	private static final int DELETION_ID = 2525277;
	private static int lastAdded;
	public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###,###.##");

	private static void sendNoSpamMessages(Component[] messages)
	{
		ChatComponent chat = Minecraft.getInstance().gui.getChat();
		for (Component msg : messages)
			chat.addMessage(msg);
		lastAdded = DELETION_ID + messages.length - 1;
	}

	public static Component wrap(String s)
	{
		return Component.literal(s);
	}

	public static Component[] wrap(String... s)
	{
		Component[] ret = new Component[s.length];
		for (int i = 0; i < s.length; i++)
			ret[i] = wrap(s[i]);
		return ret;
	}

	public static Component wrapFormatted(String key, Object... args)
	{
		return Component.translatable(key, args);
	}

	/* Generic sendChat: on the server, cast to ServerPlayer; on the client, display in GUI */
	public static void sendChat(Player player, Component... lines)
	{
		for (Component c : lines)
		{
			if (player instanceof ServerPlayer sp)
			{
				sp.sendSystemMessage(c);
			}
			else if (Minecraft.getInstance().player == player)
			{
				// client‐side
				Minecraft.getInstance().player.displayClientMessage(c, false);
			}
		}
	}

	public static void sendChat(Player player, String... lines)
	{
		sendChat(player, wrap(lines));
	}

	public static void sendChatUnloc(Player player, String... unlocLines)
	{
		sendChat(player, TextHelper.localizeAll(unlocLines));
	}

	public static void sendNoSpamClientUnloc(String... unlocLines)
	{
		sendNoSpamClient(TextHelper.localizeAll(unlocLines));
	}

	public static void sendNoSpamClient(String... lines)
	{
		sendNoSpamClient(wrap(lines));
	}

	public static void sendNoSpamClient(Component... lines)
	{
		sendNoSpamMessages(lines);
	}

	public static void sendNoSpamUnloc(Player player, String... unlocLines)
	{
		sendNoSpam(player, TextHelper.localizeAll(unlocLines));
	}

	public static void sendNoSpam(Player player, String... lines)
	{
		sendNoSpam(player, wrap(lines));
	}

	public static void sendNoSpam(Player player, Component... lines)
	{
		if (player instanceof ServerPlayer sp)
			sendNoSpam(sp, lines);
	}

	public static void sendNoSpamUnloc(ServerPlayer player, String... unlocLines)
	{
		sendNoSpam(player, TextHelper.localizeAll(unlocLines));
	}

	public static void sendNoSpam(ServerPlayer player, String... lines)
	{
		sendNoSpam(player, wrap(lines));
	}

	public static void sendNoSpam(ServerPlayer player, Component... lines)
	{
		if (lines.length > 0)
			BloodMagic.packetHandler.sendTo(new PacketNoSpamChat(lines), player);
	}

	public static class PacketNoSpamChat
	{
		private Component[] chatLines;

		public PacketNoSpamChat()
		{
			chatLines = new Component[0];
		}

		private PacketNoSpamChat(Component... lines)
		{
			chatLines = lines;
		}

		public static void encode(PacketNoSpamChat pkt, FriendlyByteBuf buf)
		{
			buf.writeInt(pkt.chatLines.length);
			for (Component c : pkt.chatLines)
				buf.writeUtf(Component.Serializer.toJson(c));
		}

		public static PacketNoSpamChat decode(FriendlyByteBuf buf)
		{
			PacketNoSpamChat pkt = new PacketNoSpamChat(new Component[buf.readInt()]);
			for (int i = 0; i < pkt.chatLines.length; i++)
				pkt.chatLines[i] = Component.Serializer.fromJsonLenient(buf.readUtf());
			return pkt;
		}

		public static void handle(PacketNoSpamChat message, Supplier<Context> context)
		{
			context.get().enqueueWork(() -> sendNoSpamMessages(message.chatLines));
			context.get().setPacketHandled(true);
		}
	}
}