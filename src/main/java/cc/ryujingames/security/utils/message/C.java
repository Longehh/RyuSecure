package cc.ryujingames.security.utils.message;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class C {
	
	public static String color(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}

	public static String color(String s, PlaceHolder... placeHolders) {
		String message = s;
		for (PlaceHolder placeHolder : placeHolders) {
			message = color(message.replace(placeHolder.getPlaceHolder(), placeHolder.getReplace()));
		}
		return message;
	}

	public static List<String> color(List<String> s) {
		List<String> toReturn = new ArrayList<>();
		for (String str : s) {
			toReturn.add(color(str));
		}
		return toReturn;
	}

	public static List<String> color(List<String> messages, PlaceHolder... placeholders) {
		List<String> colored = new ArrayList<>();
		for (String line : messages) {
			String coloredLine = line;
			for (PlaceHolder placeholder : placeholders) {
				coloredLine = color(coloredLine.replace(placeholder.getPlaceHolder(), placeholder.getReplace()));
			}
			colored.add(coloredLine);
		}
		return colored;
	}
	
	public static String strip(String s) {
		return ChatColor.stripColor(s);
	}

	public static String strip(String s, PlaceHolder... placeHolders) {
		String message = s;
		for (PlaceHolder placeHolder : placeHolders) {
			message = message.replace(placeHolder.getPlaceHolder(), placeHolder.getReplace());
		}
		return ChatColor.stripColor(message);
	}

	public static String capitalizeFirstLetter(String original) {
		if (original == null || original.length() == 0) {
			return original;
		}
		return original.substring(0, 1).toUpperCase() + original.substring(1);
	}

	public static String getProgressBar(int current, int max, int totalBars, String symbol, String notSymbol){

		float percent = (float) current / max;

		int progressBars = (int) ((int) totalBars * percent);

		int leftOver = (totalBars - progressBars);

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < progressBars; i++) {
			sb.append(symbol);
		}
		for (int i = 0; i < leftOver; i++) {
			sb.append(notSymbol);
		}
		return sb.toString();

	}

	public static String getDurationBreakdown(long millis) {
		if (millis < 0) return "0";

		long days = TimeUnit.MILLISECONDS.toDays(millis);
		millis -= TimeUnit.DAYS.toMillis(days);
		long hours = TimeUnit.MILLISECONDS.toHours(millis);
		millis -= TimeUnit.HOURS.toMillis(hours);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
		millis -= TimeUnit.MINUTES.toMillis(minutes);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

		StringBuilder sb = new StringBuilder(64);

		if (days != 0) {
			sb.append(days);
			sb.append(" D ");
		}

		if (hours != 0) {
			sb.append(hours);
			sb.append(" Hr ");
		}

		if (minutes != 0) {
			sb.append(minutes);
			sb.append(" Min ");
		}

		if (seconds != 0) {
			sb.append(seconds);
			sb.append(" Sec");
		} else {
			sb.append(millis);
			sb.append(" Ms");
		}



		return sb.toString();
	}

	public static void sendMessage(Player p, String message) {
		PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + message.replace("&", "§") + "\"}"), (byte) 2);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
	}
}
