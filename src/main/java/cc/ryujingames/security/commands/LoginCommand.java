package cc.ryujingames.security.commands;

import cc.ryujingames.security.RyuSecure;
import cc.ryujingames.security.utils.AuthenticationUtils;
import cc.ryujingames.security.utils.message.TL;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class LoginCommand implements CommandExecutor, Listener {
    private RyuSecure ryuSecure;

    public LoginCommand(RyuSecure ryuSecure) {
        this.ryuSecure = ryuSecure;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player)sender;
            if(player.hasPermission(ryuSecure.getConfig().getString("securityPermission"))) {
                if(!ryuSecure.getAuthenticationHashMap().containsKey(player.getUniqueId())) {
                    TL.MUST_REGISTER_ACCOUNT.send(player);
                    return false;
                }
                if(ryuSecure.getAuthenticatedStaffMembers().contains(player.getUniqueId())) {
                    TL.YOU_HAVE_ALREADY_LOGGED_IN.send(player);
                    return false;
                }
                if(args.length == 1) {
                    if(AuthenticationUtils.isCorrectPassword(player.getUniqueId(), args[0])) {
                        if(!AuthenticationUtils.isCorrectIP(player.getUniqueId(), player.getAddress().getAddress().getHostAddress())) {
                            ryuSecure.updateIPAddress(player.getUniqueId(), AuthenticationUtils.createHashedIPAddress(player.getAddress().getAddress().getHostAddress()));
                        }
                        ryuSecure.getAuthenticatedStaffMembers().add(player.getUniqueId());
                        TL.SUCCESSFULLY_LOGGED_IN.send(player);
                        return true;
                    }
                    TL.WRONG_PASSWORD.send(player);
                    return false;
                }
                TL.NO_PASSWORD_SPECIFIED_LOGIN.send(player);
                return false;
            }
            TL.NO_PERMISSION.send(player);
            return false;
        }
        TL.PLAYER_ONLY_COMMAND.send(sender);
        return false;
    }
}
