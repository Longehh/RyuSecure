package cc.ryujingames.security.listeners;

import cc.ryujingames.security.RyuSecure;
import cc.ryujingames.security.utils.AuthenticationUtils;
import cc.ryujingames.security.utils.message.TL;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;

public class SecurityListener implements Listener {
    private RyuSecure ryuSecure;

    public SecurityListener(RyuSecure ryuSecure) {
        this.ryuSecure = ryuSecure;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if(e.getPlayer().hasPermission(ryuSecure.getConfig().getString("securityPermission"))) {
            if(!ryuSecure.getAuthenticationHashMap().containsKey(e.getPlayer().getUniqueId())) {
                TL.MUST_REGISTER_ACCOUNT.send(e.getPlayer());
                return;
            }
            if(AuthenticationUtils.isCorrectIP(e.getPlayer().getUniqueId(), e.getPlayer().getAddress().getAddress().getHostAddress())) {
                ryuSecure.getAuthenticatedStaffMembers().add(e.getPlayer().getUniqueId());
                TL.LOGGED_IN_NO_IP_CHANGE.send(e.getPlayer());
            }
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if(AuthenticationUtils.isStaffMember(e.getPlayer())) {
            if(!ryuSecure.getAuthenticatedStaffMembers().contains(e.getPlayer().getUniqueId())) {
                if(!ryuSecure.getConfig().getBoolean("preventions.commandBlocker")) {
                    return;
                }
                if(!e.getMessage().toLowerCase().startsWith("/register") && !e.getMessage().toLowerCase().startsWith("/login")) {
                    e.setCancelled(true);
                    TL.MUST_BE_LOGGED_IN.send(e.getPlayer());
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if(ryuSecure.getAuthenticatedStaffMembers().contains(e.getPlayer().getUniqueId())) {
            ryuSecure.getAuthenticatedStaffMembers().remove(e.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if(AuthenticationUtils.isStaffMember(e.getPlayer())) {
            if(!ryuSecure.getAuthenticatedStaffMembers().contains(e.getPlayer().getUniqueId())) {
                if(!ryuSecure.getConfig().getBoolean("preventions.playerMoveEvent")) {
                    return;
                }
                e.setTo(e.getFrom());
                TL.MUST_BE_LOGGED_IN.send(e.getPlayer());
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if(AuthenticationUtils.isStaffMember(e.getPlayer())) {
            if(!ryuSecure.getAuthenticatedStaffMembers().contains(e.getPlayer().getUniqueId())) {
                if(!ryuSecure.getConfig().getBoolean("preventions.blockBreakEvent")) {
                    return;
                }
                e.setCancelled(true);
                TL.MUST_BE_LOGGED_IN.send(e.getPlayer());
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if(AuthenticationUtils.isStaffMember(e.getPlayer())) {
            if(!ryuSecure.getAuthenticatedStaffMembers().contains(e.getPlayer().getUniqueId())) {
                if(!ryuSecure.getConfig().getBoolean("preventions.blockPlaceEvent")) {
                    return;
                }
                e.setCancelled(true);
                TL.MUST_BE_LOGGED_IN.send(e.getPlayer());
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if(AuthenticationUtils.isStaffMember(e.getPlayer())) {
            if(!ryuSecure.getAuthenticatedStaffMembers().contains(e.getPlayer().getUniqueId())) {
                if(!ryuSecure.getConfig().getBoolean("preventions.playerChatEvent")) {
                    return;
                }
                e.setCancelled(true);
                TL.MUST_BE_LOGGED_IN.send(e.getPlayer());
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if(AuthenticationUtils.isStaffMember(e.getPlayer())) {
            if(!ryuSecure.getAuthenticatedStaffMembers().contains(e.getPlayer().getUniqueId())) {
                if(!ryuSecure.getConfig().getBoolean("preventions.itemDropEvent")) {
                    return;
                }
                e.setCancelled(true);
                TL.MUST_BE_LOGGED_IN.send(e.getPlayer());
            }
        }
    }
}
