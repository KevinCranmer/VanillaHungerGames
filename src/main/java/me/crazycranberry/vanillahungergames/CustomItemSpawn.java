package me.crazycranberry.vanillahungergames;

import org.bukkit.Material;

import java.util.LinkedHashMap;
import java.util.List;

import static me.crazycranberry.vanillahungergames.VanillaHungerGames.logger;

public class CustomItemSpawn {
    private final Material material;
    private final Double chance;
    private final Integer min;
    private final Integer max;

    public static CustomItemSpawn fromYaml(LinkedHashMap<String, ?> input) {
        boolean anyFieldsMissing = reportAnyMissingFields(input);
        if (anyFieldsMissing) {
            return null;
        }
        Material material = validateMaterial(input.get("name"));
        Double chance = validateChance(input.get("chance"));
        Integer min = validateMin(input.get("min"));
        Integer max = validateMax(input.get("min"));
        if (material == null || chance == null || min == null || max == null) {
            return null;
        }
        if (max < min) {
            logger().warning("The max was less than the min. Defaulting both to the min value.");
            max = min;
        }
        return new CustomItemSpawn(material, chance, min, max);
    }

    private static <T> Integer validateMin(T inputMin) {
        if (!(inputMin instanceof Integer)) {
            logger().warning("A custom_item_spawns min was not an Integer.");
            return null;
        }
        return (Integer) inputMin;
    }

    private static <T> Integer validateMax(T inputMax) {
        if (!(inputMax instanceof Integer)) {
            logger().warning("A custom_item_spawns max was not an Integer.");
            return null;
        }
        return (Integer) inputMax;
    }

    private static <T> Double validateChance(T inputChance) {
        if (!(inputChance instanceof Double)) {
            logger().warning("A custom_item_spawns chance was not a Double.");
            return null;
        }
        return (Double) inputChance;
    }

    private static <T> Material validateMaterial(T name) {
        if (!(name instanceof String)) {
            logger().warning("A custom_item_spawns name was not a String.");
            return null;
        }
        Material material = Material.matchMaterial((String) name);
        if (material == null) {
            logger().warning("custom_item_spawns item '" + name + "' is not a valid Material name.");
            return null;
        }
        return material;
    }

    private static boolean reportAnyMissingFields(LinkedHashMap<String, ?> input) {
        boolean anyMissing = false;
        for (String fieldName : List.of("name", "chance", "min", "max")) {
            if (!input.containsKey(fieldName)) {
                logger().warning("The '" + fieldName + "' field for a CustomItemSpawn item is missing.");
                anyMissing = true;
            }
        }
        return anyMissing;
    }

    public CustomItemSpawn(Material material, Double chance, Integer min, Integer max) {
        this.material = material;
        this.chance = chance;
        this.min = min;
        this.max = max;
    }

    public Material material() {
        return material;
    }

    public Double chance() {
        return chance;
    }

    public Integer min() {
        return min;
    }

    public Integer max() {
        return max;
    }

    @Override
    public String toString() {
        return String.format("Material: %s, chance: %s, min: %s, max: %s", material, chance, min, max);
    }
}