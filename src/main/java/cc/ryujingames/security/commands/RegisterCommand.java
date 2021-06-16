package cc.ryujingames.security.commands;

import cc.ryujingames.security.RyuSecure;
import cc.ryujingames.security.objects.Authentication;
import cc.ryujingames.security.utils.AuthenticationUtils;
import cc.ryujingames.security.utils.message.TL;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegisterCommand implements CommandExecutor {
    private RyuSecure ryuSecure;

    public RegisterCommand(RyuSecure ryuSecure) {
        this.ryuSecure = ryuSecure;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player)sender;
            if(player.hasPermission(ryuSecure.getConfig().getString("securityPermission"))) {
                if(ryuSecure.getAuthenticationHashMap().containsKey(player.getUniqueId())) {
                    TL.ACCOUNT_ALREADY_REGISTERED.send(player);
                    return false;
                }
                if(args.length == 1) {
                    String rawPassword = args[0];
                    String rawIPAddress = player.getAddress().getAddress().getHostAddress();
                    ryuSecure.cacheAuthenticationObject(new Authentication(player.getUniqueId(), AuthenticationUtils.createHashedPassword(rawPassword), AuthenticationUtils.createHashedIPAddress(rawIPAddress)));
                    TL.ACCOUNT_SUCCESSFULLY_REGISTERED.send(player);
                    return true;
                }
                TL.NO_PASSWORD_SPECIFIED.send(player);
                return false;
            }
            TL.NO_PERMISSION.send(player);
            return false;
        }
        TL.PLAYER_ONLY_COMMAND.send(sender);
        return false;
    }
}
