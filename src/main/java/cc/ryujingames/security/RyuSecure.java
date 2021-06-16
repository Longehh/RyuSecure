package cc.ryujingames.security;

import cc.ryujingames.security.commands.LoginCommand;
import cc.ryujingames.security.commands.RegisterCommand;
import cc.ryujingames.security.listeners.SecurityListener;
import cc.ryujingames.security.objects.Authentication;
import cc.ryujingames.security.utils.GsonFactory;
import cc.ryujingames.security.utils.message.TL;
import com.google.common.reflect.TypeToken;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import javax.xml.transform.Result;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class RyuSecure extends JavaPlugin {

    public void onEnable() {
        instance = this;
        if (!getDataFolder().exists()) {
            saveDefaultConfig();
            return;
        }
        loadMessages();
        initializeSecurityFile();
        Bukkit.getPluginManager().registerEvents(new SecurityListener(this), this);
        getCommand("register").setExecutor(new RegisterCommand(this));
        getCommand("login").setExecutor(new LoginCommand(this));

        ((Logger) LogManager.getRootLogger()).addFilter(new Filter(){
            @Override
            public Result filter(LogEvent l) {
                if(l.getMessage().toString().contains("logged in with entity id") || l.getMessage().toString().toLowerCase().contains("login") || l.getMessage().toString().toLowerCase().contains("register"))return Filter.Result.DENY;
                return Filter.Result.NEUTRAL;
            }
            @Override
            public Result filter(Logger arg0, Level arg1, Marker arg2, String arg3, Object... arg4) {
                return Filter.Result.NEUTRAL;
            }
            @Override
            public Result filter(Logger arg0, Level arg1, Marker arg2, Object arg3, Throwable arg4) {
                return Filter.Result.NEUTRAL;
            }
            @Override
            public Result filter(Logger arg0, Level arg1, Marker arg2, Message arg3, Throwable arg4) {
                return Filter.Result.NEUTRAL;
            }
            @Override
            public Result getOnMatch() {
                return Filter.Result.NEUTRAL;
            }
            @Override
            public Result getOnMismatch() {
                return Filter.Result.NEUTRAL;
            }
        });
    }

    public void onDisable() {
        saveSecurityFile();
    }

    public void initializeSecurityFile() {
        this.securityDataFile = new File(getDataFolder(), "securityData.json");
        try {
            if (!securityDataFile.exists()) {
                securityDataFile.getParentFile().mkdirs();
                securityDataFile.createNewFile();
                authenticationArrayList = new ArrayList<>();
            } else try (FileReader reader = new FileReader(securityDataFile)) {
                ArrayList<Authentication> arrayList = gsonFactory.getCompactGson().fromJson(reader, new TypeToken<ArrayList<Authentication>>() {
                }.getType());
                if (arrayList == null) {
                    authenticationArrayList = new ArrayList<>();
                } else {
                    authenticationArrayList = arrayList;
                    for(Authentication authentication : authenticationArrayList) {
                        authenticationHashMap.put(authentication.getUuid(), authentication);
                    }
                    System.out.println("Successfully Cached " + authenticationArrayList.size() + " Staff Accounts.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cacheAuthenticationObject(Authentication authentication) {
        authenticationArrayList.add(authentication);
        authenticationHashMap.put(authentication.getUuid(), authentication);

        saveSecurityFile();
    }

    public void updateIPAddress(UUID uuid, String newHashedIP) {
        authenticationHashMap.get(uuid).setHashedIPAddress(newHashedIP);
        for(Authentication authentication : authenticationArrayList) {
            if(authentication.getUuid().equals(uuid)) {
                authentication.setHashedIPAddress(newHashedIP);
            }
        }
        saveSecurityFile();
    }

    public void saveSecurityFile() {
        if (!authenticationArrayList.isEmpty()) {
            try (FileWriter fileWriter = new FileWriter(securityDataFile)) {
                fileWriter.write(gsonFactory.getPrettyGson().toJson(authenticationArrayList));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                securityDataFile.delete();
                securityDataFile.createNewFile();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static RyuSecure getInstance() {
        return instance;
    }

    public HashMap<UUID, Authentication> getAuthenticationHashMap() {
        return authenticationHashMap;
    }

    public CopyOnWriteArrayList<UUID> getAuthenticatedStaffMembers() {
        return authenticatedStaffMembers;
    }

    public Argon2 getArgon2() {
        return argon2;
    }

    private static RyuSecure instance;
    private GsonFactory gsonFactory = new GsonFactory();
    private File securityDataFile;
    private Argon2 argon2 = Argon2Factory.create();
    private ArrayList<Authentication> authenticationArrayList = new ArrayList<>();
    private HashMap<UUID, Authentication> authenticationHashMap = new HashMap<>();

    public void loadMessages() {
        FileConfiguration config = getConfig();
        for (TL message : TL.values()) {
            if (config.getString("messages." + message.getPath()) != null) {
                message.setDefault(config.getString("messages." + message.getPath()));
            } else {
                config.set("messages." + message.getPath(), message.getDefault());
            }
        }
        saveConfig();
    }

    // Authenticated Staff Members
    private CopyOnWriteArrayList<UUID> authenticatedStaffMembers = new CopyOnWriteArrayList<>();
}
