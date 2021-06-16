package cc.ryujingames.security.utils.message;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.logging.Level;

public enum TL {
	ACCOUNT_ALREADY_REGISTERED("ACCOUNT_ALREADY_REGISTERED", "This account is already registered if you believe this is an error contact Longeh."),
	ACCOUNT_SUCCESSFULLY_REGISTERED("ACCOUNT_SUCCESSFULLY_REGISTERED", "Your account has been successfully registered."),
	PLAYER_ONLY_COMMAND("PLAYER_ONLY_COMMAND", "&c&l(!) &cThis is a command cannot be executed via console."),
	NO_PERMISSION("NO_PERMISSION", "&c&l(!) &cYou do not have permission to execute that command."),
	NO_PASSWORD_SPECIFIED("NO_PASSWORD_SPECIFIED", "You did not specify the password you wish to use to login to your account."),
	NO_PASSWORD_SPECIFIED_LOGIN("NO_PASSWORD_SPECIFIED_LOGIN", "You need to specify your password in order to login to your account."),
	SUCCESSFULLY_LOGGED_IN("SUCCESSFULLY_LOGGED_IN", "You have successfully logged in!"),
	LOGGED_IN_NO_IP_CHANGE("LOGGED_IN_NO_IP_CHANGE","You have been automatically logged in as your IP Address has not changed."),
	WRONG_PASSWORD("WRONG_PASSWORD", "The password you've specified is not the correct password. If you believe this is an error contact Longeh"),
	MUST_BE_LOGGED_IN("MUST_BE_LOGGED_IN", "You must be logged in to do that."),
	YOU_HAVE_ALREADY_LOGGED_IN("YOU_HAVE_ALREADY_LOGGED_IN", "You have already logged in."),
	MUST_REGISTER_ACCOUNT("MUST_REGISTER_ACCOUNT", "You must register your account using the &b/register&f command.");

	private String path, def;

	TL(String path, String start) {
		this.path = path;
		this.def = start;
	}

	public String getDefault() {
		return this.def;
	}

	public String getPath() {
		return this.path;
	}

	public void setDefault(String message) {
		this.def = message;
	}

	public void send(CommandSender sender) {
		if (getDefault().contains("\\n")) {
			for (String string : getDefault().split("\n")) {
				sender.sendMessage(sender instanceof Player ? C.color(string) : C.strip(string));
			}
			return;
		}
		sender.sendMessage(sender instanceof Player ? C.color(getDefault()) : C.strip(getDefault()));
	}

	public void sendPrefixed(CommandSender sender) {
		if (getDefault().contains("\\n")) {
			for (String string : getDefault().split("\n")) {
				sender.sendMessage(sender instanceof Player ? C.color(string) : C.strip(string));
			}
			return;
		}
		sender.sendMessage(sender instanceof Player ? C.color(getDefault()) : C.strip(getDefault()));
	}

	public void send(CommandSender sender, PlaceHolder... placeHolders) {
		if (getDefault().contains("\\n")) {
			for (String string : getDefault().split("\n")) {
				sender.sendMessage(sender instanceof Player ? C.color(string) : C.strip(string));
			}
			return;
		}
		sender.sendMessage(sender instanceof Player ? C.color(getDefault(), placeHolders) : C.strip(getDefault(), placeHolders));
	}

	public static void message(CommandSender sender, String message) {
		sender.sendMessage(C.color(message));
	}

	public static void message(CommandSender sender, String message, PlaceHolder... placeHolders) {
		sender.sendMessage(C.color(message, placeHolders));
	}

	public static void message(CommandSender sender, List<String> message) {
		message.forEach(m -> sender.sendMessage(C.color(m)));
	}

	public static void message(CommandSender sender, List<String> message, PlaceHolder... placeHolders) {
		message.forEach(m -> sender.sendMessage(C.color(m, placeHolders)));
	}

	public static void log(Level lvl, String message) {
		Bukkit.getLogger().log(lvl, message);
	}
}
