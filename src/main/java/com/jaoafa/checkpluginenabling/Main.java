package com.jaoafa.checkpluginenabling;

import com.destroystokyo.paper.event.server.ServerExceptionEvent;
import com.destroystokyo.paper.exception.ServerException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Main extends JavaPlugin implements Listener {
    Pattern pattern = Pattern.compile("^Could not pass event (.+?) to (.+?) v(.+?)$");
    JSONArray exceptions = new JSONArray();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onLoaded(ServerLoadEvent event) {
        getLogger().info("After 1 minute, get the list of plugins and exit. Please wait!");
        new BukkitRunnable() {
            @Override
            public void run() {
                getLogger().info("run()");
                JSONArray array = new JSONArray();
                Arrays
                    .stream(getServer().getPluginManager().getPlugins())
                    .filter(Plugin::isEnabled)
                    .map(Plugin::getName)
                    .forEach(array::add);
                try {
                    Files.write(FileSystems.getDefault().getPath("plugins.json"), Collections.singleton(array.toJSONString()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Files.write(FileSystems.getDefault().getPath("exceptions.json"), Collections.singleton(exceptions.toJSONString()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                getServer().shutdown();
            }
        }.runTaskLater(this, 60 * 20L);
    }

    @EventHandler
    public void onServerExceptionEvent(ServerExceptionEvent event) {
        ServerException e = event.getException();

        Matcher m = pattern.matcher(e.getMessage());
        String errorEvent = null;
        String pluginName = null;
        String pluginVersion = null;
        if (m.matches()) {
            errorEvent = m.group(1);
            pluginName = m.group(2);
            pluginVersion = m.group(3);
        }

        JSONObject exception = new JSONObject();
        exception.put("message", e.getMessage());
        exception.put("event", errorEvent);
        exception.put("pluginName", pluginName);
        exception.put("pluginVersion", pluginVersion);
        exception.put("throwClass", e.getCause().getClass().getName());
        exception.put("throwMessage", e.getCause().getMessage());
        exceptions.add(exception);
    }
}
