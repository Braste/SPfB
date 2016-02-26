package de.braste.SPfB.configuration;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;

import java.util.Map;

public class SPfBSection extends MemorySection implements ConfigurationSection {
    public Map<String, Object> getMap() {
        return this.map;
    }
}
