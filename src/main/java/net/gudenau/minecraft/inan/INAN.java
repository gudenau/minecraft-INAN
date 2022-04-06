package net.gudenau.minecraft.inan;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Environment(EnvType.CLIENT)
public final class INAN implements ClientModInitializer {
    public static final boolean CONFIG_DISABLE_MP_NAG;
    public static final boolean CONFIG_DISABLE_AUTO_AUTO_JUMP;
    public static final boolean CONFIG_DISABLE_INTERACTION_NAG;
    public static final boolean CONFIG_DISABLE_TUTORIAL;
    
    static{
        Map<String, Boolean> config = new HashMap<>();
        config.put("disable_mp_nag", true);
        config.put("disable_auto_auto_jump", true);
        config.put("disable_interaction_nag", true);
        config.put("disable_tutorial", true);
        
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("gud").resolve("inan.txt");
        
        Set<String> requiredKeys = new HashSet<>(config.keySet());
        boolean missingKeys = load(configPath, config);
        if(missingKeys){
            Set<String> badKeys = new HashSet<>(config.keySet());
            badKeys.removeAll(requiredKeys);
            badKeys.forEach(config::remove);
            save(configPath, config);
        }
    
        CONFIG_DISABLE_MP_NAG = config.get("disable_mp_nag");
        CONFIG_DISABLE_AUTO_AUTO_JUMP = config.get("disable_auto_auto_jump");
        CONFIG_DISABLE_INTERACTION_NAG = config.get("disable_interaction_nag");
        CONFIG_DISABLE_TUTORIAL = config.get("disable_tutorial");
    }
    
    private static boolean load(Path configPath, Map<String, Boolean> config) {
        if(!Files.isRegularFile(configPath)){
            return true;
        }
        
        Set<String> missingKeys = new HashSet<>(config.keySet());
        try(var reader = Files.newBufferedReader(configPath, StandardCharsets.UTF_8)){
            reader.lines()
                .map((line)->{
                    var index = line.indexOf('#');
                    if(index != -1){
                        return line.substring(0, index);
                    }else{
                        return line;
                    }
                })
                .filter((line) -> !line.isBlank())
                .forEachOrdered((line)->{
                    var split = line.split("=", 2);
                    if(split.length != 2){
                        return;
                    }
                    
                    var key = split[0].toLowerCase(Locale.ROOT);
                    missingKeys.remove(key);
                    config.put(key, Boolean.valueOf(split[1]));
                });
        }catch (IOException ignored){}
        return !missingKeys.isEmpty();
    }
    
    private static void save(Path configPath, Map<String, Boolean> config) {
        try {
            Files.createDirectories(configPath.getParent());
            try(var writer = Files.newBufferedWriter(configPath, StandardCharsets.UTF_8)){
                config.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEachOrdered((entry)->{
                        try {
                            writer.write(entry.getKey() + '=' + (entry.getValue() ? "true" : "false") + '\n');
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
            }catch (UncheckedIOException e) {
                throw e.getCause();
            }
        } catch (IOException ignored) {
            try {
                Files.deleteIfExists(configPath);
            } catch (IOException ignored2) {}
        }
    }
    
    @Override
    public void onInitializeClient() {}
}
