package me.crazycranberry.vanillahungergames.playerclasses;

import me.crazycranberry.vanillahungergames.Participant;
import me.crazycranberry.vanillahungergames.events.TournamentStartedEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static me.crazycranberry.vanillahungergames.VanillaHungerGames.getPlugin;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesParticipantManager.tournamentParticipants;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesWorldManager.hungerGamesWorld;
import static org.bukkit.Sound.ITEM_SHIELD_BLOCK;

public class Ninja implements PlayerClass {
    private final String DASH_ATTACK_TOOL_TIP_1 = "Use this to Dash Attack";
    private final String DASH_ATTACK_TOOL_TIP_2 = "Damage is calculated based on the strongest weapon in your hotbar";
    private final String DOUBLE_JUMP_TOOL_TIP_1 = "Use this to Double Jump";
    private final String DOUBLE_JUMP_TOOL_TIP_2 = "This class also takes less fall damage";
    private final int DOUBLE_JUMP_COOLDOWN_SECONDS = 30; //kind of meaningless since it refreshes when they hit the ground
    private final int DASH_ATTACK_COOLDOWN_SECONDS = 15;
    private final int DASH_ATTACK_DURATION_TICKS = 6;
    private final double FALL_DAMAGE_CUSHION = 4;
    private final List<Integer> DASH_ATTACK_POTENTIAL_WEAPON_INDEXES = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 40);
    private final Map<Material, Double> WEAPON_DAMAGE = Map.ofEntries(
            Map.entry(Material.WOODEN_AXE, 7.0),
            Map.entry(Material.WOODEN_PICKAXE, 2.0),
            Map.entry(Material.WOODEN_SHOVEL, 2.5),
            Map.entry(Material.WOODEN_SWORD, 4.0),
            Map.entry(Material.GOLDEN_AXE, 7.0),
            Map.entry(Material.GOLDEN_PICKAXE, 2.0),
            Map.entry(Material.GOLDEN_SHOVEL, 2.5),
            Map.entry(Material.GOLDEN_SWORD, 4.0),
            Map.entry(Material.STONE_AXE, 9.0),
            Map.entry(Material.STONE_PICKAXE, 3.0),
            Map.entry(Material.STONE_SHOVEL, 3.5),
            Map.entry(Material.STONE_SWORD, 5.0),
            Map.entry(Material.IRON_AXE, 9.0),
            Map.entry(Material.IRON_PICKAXE, 4.0),
            Map.entry(Material.IRON_SHOVEL, 4.5),
            Map.entry(Material.IRON_SWORD, 6.0),
            Map.entry(Material.DIAMOND_AXE, 9.0),
            Map.entry(Material.DIAMOND_PICKAXE, 5.0),
            Map.entry(Material.DIAMOND_SHOVEL, 5.5),
            Map.entry(Material.DIAMOND_SWORD, 7.0),
            Map.entry(Material.NETHERITE_AXE, 10.0),
            Map.entry(Material.NETHERITE_PICKAXE, 6.0),
            Map.entry(Material.NETHERITE_SHOVEL, 6.5),
            Map.entry(Material.NETHERITE_SWORD, 8.0)
    );
    private static Map<Player, Integer> dashAttackTaskIdsByPlayer = new HashMap<>();
    private static Map<Player, Location> dashAttackPreviousTickLocationForPlayer = new HashMap<>();
    private static Map<Player, Integer> dashAttackTicksRemainingByPlayer = new HashMap<>();
    private static Map<Player, List<LivingEntity>> dashAttackTargetsAlreadyHit = new HashMap<>();

    @Override
    public String getName() {
        return "Ninja";
    }

    @Override
    public String getInfo() {
        return "Ninja's get usable items that allow them to double jump and dash attack. Note: The dash attack is blockable with a shield.";
    }

    @EventHandler
    private void tournamentStarted(TournamentStartedEvent event) {
        for (Participant p : tournamentParticipants()) {
            if (isCorrectClass(p.getPlayer())) {
                ItemStack rabbitsFoot = new ItemStack(Material.RABBIT_FOOT, 1);
                ItemMeta rabbitsFootMeta = rabbitsFoot.getItemMeta();
                rabbitsFootMeta.setLore(List.of(String.format("%s%s%s", ChatColor.GOLD, DOUBLE_JUMP_TOOL_TIP_1, ChatColor.RESET), String.format("%s%s%s", ChatColor.GRAY, DOUBLE_JUMP_TOOL_TIP_2, ChatColor.RESET)));
                rabbitsFoot.setItemMeta(rabbitsFootMeta);
                ItemStack blaze = new ItemStack(Material.BLAZE_POWDER, 1);
                ItemMeta blazeMeta = blaze.getItemMeta();
                blazeMeta.setLore(List.of(String.format("%s%s%s", ChatColor.GOLD, DASH_ATTACK_TOOL_TIP_1, ChatColor.RESET), String.format("%s%s%s", ChatColor.GRAY, DASH_ATTACK_TOOL_TIP_2, ChatColor.RESET)));
                blaze.setItemMeta(blazeMeta);
                p.getPlayer().getInventory().addItem(rabbitsFoot);
                p.getPlayer().getInventory().addItem(blaze);
            }
        }
    }

    @EventHandler
    public void onItemUse(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();
        if (isCorrectClass(player) && item != null) {
            doubleJump(item, player);
            dashAttack(item, player);
        }
    }

    private void doubleJump(ItemStack item, Player player) {
        if (item.getType() == Material.RABBIT_FOOT && !player.hasCooldown(Material.RABBIT_FOOT)) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.getLore() != null && meta.getLore().get(0).contains(DOUBLE_JUMP_TOOL_TIP_1)) {
                player.setCooldown(Material.RABBIT_FOOT, 20 * DOUBLE_JUMP_COOLDOWN_SECONDS);
                player.setVelocity(player.getVelocity().setX(player.getVelocity().getX() * 1.2).setY(0.5).setZ(player.getVelocity().getZ() * 1.2));
            }
        }
    }

    private void dashAttack(ItemStack item, Player player) {
        if (item.getType() == Material.BLAZE_POWDER && !player.hasCooldown(Material.BLAZE_POWDER)) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.getLore() != null && meta.getLore().get(0).contains(DASH_ATTACK_TOOL_TIP_1)) {
                player.setCooldown(Material.BLAZE_POWDER, 20 * DASH_ATTACK_COOLDOWN_SECONDS);
                Vector velocity = player.getLocation().getDirection().multiply(2.2);
                boolean playerWasAllowedFlight = player.getAllowFlight();
                player.setAllowFlight(true);
                player.setFlying(true);
                Location startingLocation = player.getLocation().clone();
                startingLocation.setY(startingLocation.getY() + 0.1);
                player.teleport(startingLocation);
                player.setVelocity(velocity);
                double dashDamage = findStrongestWeaponDamage(player) / 2;
                dashAttackTicksRemainingByPlayer.put(player, DASH_ATTACK_DURATION_TICKS);
                dashAttackTargetsAlreadyHit.put(player, new ArrayList<>());
                dashAttackPreviousTickLocationForPlayer.put(player, startingLocation);
                BukkitScheduler scheduler = Bukkit.getScheduler();
                int taskId = scheduler.runTaskTimer(getPlugin(), () -> {
                    Integer ticksRemaining = dashAttackTicksRemainingByPlayer.get(player);
                    if (ticksRemaining <= 0) {
                        Integer expiredTasksId = dashAttackTaskIdsByPlayer.get(player);
                        player.setVelocity(new Vector(0, 0, 0));
                        player.setFlying(false);
                        player.setAllowFlight(playerWasAllowedFlight);
                        Bukkit.getServer().getScheduler().cancelTask(expiredTasksId);
                    }
                    Stream<LivingEntity> collidedEntities = player.getNearbyEntities(0.9, 0.9, 0.9).stream().filter(e -> e instanceof LivingEntity).map(e -> (LivingEntity) e);
                    collidedEntities.filter(e -> !dashAttackTargetsAlreadyHit.get(player).contains(e))
                        .forEach(e -> {
                            boolean blocked = false;
                            if (e instanceof Player) {
                                Player playerTarget = (Player) e;
                                if (playerTarget.getInventory().getItemInOffHand().getType().equals(Material.SHIELD) && playerTarget.isHandRaised() && isFacingDashAttacker(playerTarget, dashAttackPreviousTickLocationForPlayer.get(player))) {
                                    ItemStack shield = playerTarget.getInventory().getItemInOffHand();
                                    Damageable shieldMeta = (Damageable) shield.getItemMeta();
                                    if (shieldMeta != null) {
                                        shieldMeta.setDamage(shieldMeta.getDamage() + 1);
                                        shield.setItemMeta(shieldMeta);
                                        hungerGamesWorld().playSound(playerTarget.getLocation(), ITEM_SHIELD_BLOCK, 1, 1);
                                    }
                                    blocked = true;
                                }
                            }
                            if (!blocked) {
                                e.damage(dashDamage, player);
                            }
                            dashAttackTargetsAlreadyHit.get(player).add(e);
                        });
                    dashAttackTicksRemainingByPlayer.put(player, ticksRemaining - 1);
                    dashAttackPreviousTickLocationForPlayer.put(player, player.getLocation());
                }, 0 /*<-- the initial delay */, 1L /*<-- the interval */).getTaskId();
                dashAttackTaskIdsByPlayer.put(player, taskId);
            }
        }
    }

    private boolean isFacingDashAttacker(Player playerTarget, Location attackerLocation) {
        Vector locationDifference = attackerLocation.toVector().clone().subtract(playerTarget.getLocation().toVector());
        Vector targetDirection = playerTarget.getLocation().getDirection();
        locationDifference.normalize();
        Vector difference = locationDifference.subtract(targetDirection);
        return difference.lengthSquared() <= 0.5;
    }

    @EventHandler
    private void onFallDamage(EntityDamageEvent event) {
        if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL) && event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (isCorrectClass(player) && player.getWorld().equals(hungerGamesWorld())) {
                double damage = Math.max(event.getDamage() - FALL_DAMAGE_CUSHION, 0);
                if (damage <= 0) {
                    event.setCancelled(true);
                }
                event.setDamage(damage);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (isCorrectClass(event.getPlayer()) && event.getPlayer().isOnGround()) {
            event.getPlayer().setCooldown(Material.RABBIT_FOOT, 0);
        }
    }

    private Double findStrongestWeaponDamage(Player player) {
        Double damage = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getBaseValue();
        for(int i : DASH_ATTACK_POTENTIAL_WEAPON_INDEXES) {
            ItemStack item = player.getInventory().getItem(i);

            if (item != null && WEAPON_DAMAGE.containsKey(item.getType())) {
                if (WEAPON_DAMAGE.get(item.getType()) > damage) {
                    damage = WEAPON_DAMAGE.get(item.getType());
                }
            }
        }
        return damage;
    }
}
