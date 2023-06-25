package me.demonis.warpscreator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{

	String line = ChatColor.BLUE + "[Warp]";
	List<String> warpList = new ArrayList<String>();

	public void onEnable() {

		saveDefaultConfig();
		Bukkit.getPluginManager().registerEvents(this, this);

		loadWarp();

	}


	public void loadWarp() {
		reloadConfig();  	
		warpList.clear();

		for (String warp : getConfig().getKeys(false)) {

			if (!warp.equals("icon")) {
				warpList.add(warp);
			}
		}
	}

	public void reload() {
		loadWarp();
	}


	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			List<String> warpsDispo = new ArrayList<String>();

			for (String slot : warpList) {
				if (sender.hasPermission("warpscreator.warp." + slot)) {
					warpsDispo.add(slot);
				}
			}

			if (sender instanceof Player) {
				int size = warpsDispo.size();
				if (size % 9 > 0) {
					size += 9 - size % 9;
				}

				Inventory inv = Bukkit.createInventory(null, size, ChatColor.GRAY + "Warps");

				for (String warp : warpsDispo) {
					ItemStack icon = getConfig().getItemStack("icon." + warp.toLowerCase());
					if (icon == null) {
						icon = new ItemStack(Material.GRASS);
						ItemMeta meta = icon.getItemMeta();
						meta.setDisplayName(ChatColor.YELLOW + warp);
						List<String> lore = new ArrayList<String>();
						lore.add("");
						lore.add(ChatColor.YELLOW + "Left Click" + ChatColor.GRAY + " to use");
						lore.add(ChatColor.YELLOW + "Right Click" + ChatColor.GRAY + " to delete");
						meta.setLore(lore);
						icon.setItemMeta(meta);
						inv.setItem(warpsDispo.indexOf(warp), icon);
					}
					else if (icon.hasItemMeta() && icon.getItemMeta().hasLore()) {
						ItemMeta meta = icon.getItemMeta();
						meta.setDisplayName(icon.getItemMeta().getDisplayName());
					}
					inv.setItem(warpsDispo.indexOf(warp), icon);
				}	
				((Player) sender).openInventory(inv);
			} else {
				sender.sendMessage("" + ChatColor.GOLD + ChatColor.UNDERLINE + "Warps (" + warpsDispo.size() + "): " + ChatColor.WHITE + StringUtils.join(warpsDispo, ", "));
			}
		} else {
			if (args[0].equalsIgnoreCase("create")) {
				if (sender instanceof Player) {
					if (sender.hasPermission("warpscreator.create")) {
						if (args.length >= 2) {
							if (!warpList.contains(args[1].toLowerCase())) {
								Location location = ((Player) sender).getLocation();

								getConfig().set(args[1].toLowerCase() + ".world", location.getWorld().getName());
								getConfig().set(args[1].toLowerCase() + ".X", location.getX());
								getConfig().set(args[1].toLowerCase() + ".Y", location.getY());
								getConfig().set(args[1].toLowerCase() + ".Z", location.getZ());
								getConfig().set(args[1].toLowerCase() + ".yaw", location.getYaw());
								getConfig().set(args[1].toLowerCase() + ".pitch", location.getPitch());

								saveConfig();
								warpList.add(args[1].toLowerCase());
								sender.sendMessage(ChatColor.GREEN + "Warp " + args[1].toLowerCase() + " has been created!");
							} else {
								sender.sendMessage(ChatColor.RED + "This warp already exist!");
							}
						}
					}
				}
			} else if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
				if (sender.hasPermission("warpscreator.reload")) {
					reload();

					sender.sendMessage(ChatColor.GREEN + "The config has been reloaded!");
				}
			} else if (args[0].equalsIgnoreCase("delete")) {
				if (sender.hasPermission("warpscreator.delete")) {
					if (args.length >= 2) {
						if (warpList.contains(args[1].toLowerCase())) {
							getConfig().set(args[1].toLowerCase(), null);
							saveConfig();
							warpList.remove(args[1].toLowerCase());
						}
					}
				}
			} else if (warpList.contains(args[0].toLowerCase())) {
				if (args.length >= 2) {
					if (sender.hasPermission("warpscreator.tp." + args[0].toLowerCase())) {
						Player player = Bukkit.getPlayer(args[1]);
						String name = args[0].toLowerCase();
						if (player != null && player.isOnline()) {

							World world = Bukkit.getWorld(getConfig().getString(name + ".world"));
							double x = getConfig().getDouble(name + ".X");
							double y = getConfig().getDouble(name + ".Y");
							double z = getConfig().getDouble(name + ".Z");
							float yaw = (float) getConfig().getDouble(name + ".yaw");
							float pitch = (float) getConfig().getDouble(name + ".pitch");

							Location location = new Location(world, x, y, z, yaw, pitch);

							player.teleport(location);

							player.sendMessage(ChatColor.GREEN + "You have been warped to warp " + name + "!");
							sender.sendMessage(ChatColor.GREEN + "" + player.getName() + " has been warped to warp " + name + "!");
						}
					}
				} else if (sender instanceof Player) {
					if (sender.hasPermission("warpscreator.warp." + args[0].toLowerCase())) {

						Player player = (Player) sender;
						String name = args[0].toLowerCase();

						World world = Bukkit.getWorld(getConfig().getString(name + ".world"));
						double x = getConfig().getDouble(name + ".X");
						double y = getConfig().getDouble(name + ".Y");
						double z = getConfig().getDouble(name + ".Z");
						float yaw = (float) getConfig().getDouble(name + ".yaw");
						float pitch = (float) getConfig().getDouble(name + ".pitch");

						Location location = new Location(world, x, y, z, yaw, pitch);

						player.teleport(location);

						player.sendMessage(ChatColor.GREEN + "You have been warped to warp " + name + "!");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "You must be a player!");
				}
			}
		}
		return true;
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getInventory().getTitle().equals(ChatColor.GRAY + "Warps")) {
			event.setCancelled(true);
			if (event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName()) {
				String name = event.getCurrentItem().getItemMeta().getDisplayName();
				name = ChatColor.stripColor(name);
				name = name.toLowerCase();
				if (warpList.contains(name)) {
					if (event.getAction().equals(InventoryAction.PICKUP_ALL)) {
						if (event.getWhoClicked().hasPermission("warpscreator.warp." + name.toLowerCase())) {

							World world = Bukkit.getWorld(getConfig().getString(name + ".world"));
							double x = getConfig().getDouble(name + ".X");
							double y = getConfig().getDouble(name + ".Y");
							double z = getConfig().getDouble(name + ".Z");
							float yaw = (float) getConfig().getDouble(name + ".yaw");
							float pitch = (float) getConfig().getDouble(name + ".pitch");

							Location location = new Location(world, x, y, z, yaw, pitch);

							event.getWhoClicked().teleport(location);

							event.getWhoClicked().sendMessage(ChatColor.GREEN + "You have been warped to warp " + name + "!");
						}
					} else if (event.getAction().equals(InventoryAction.PICKUP_HALF)) {
						if (event.getWhoClicked().hasPermission("warpscreator.delete")) {
							getConfig().set(name.toLowerCase(), null);
							saveConfig();
							warpList.remove(name);
							event.getWhoClicked().sendMessage(ChatColor.GREEN + "The warp " + name + " has been removed!");
							event.getWhoClicked().closeInventory();
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getClickedBlock() != null && event.getClickedBlock().getState() instanceof Sign) {
			Sign sign = (Sign) event.getClickedBlock().getState();
			sign.setLine(0, sign.getLine(0).replaceAll("[^ -~§]", ""));
			sign.setLine(1, sign.getLine(1).replaceAll("[^ -~§]", ""));
			sign.setLine(2, sign.getLine(2).replaceAll("[^ -~§]", ""));
			sign.setLine(3, sign.getLine(3).replaceAll("[^ -~§]", ""));
			sign.update();
			if (sign.getLine(0).equals(line)) {
				if (warpList.contains(sign.getLine(1).toLowerCase())) {
					if (event.getPlayer().hasPermission("warpscreator.warp." + sign.getLine(1).toLowerCase())) {

						String name = sign.getLine(1).toLowerCase();
						Player player = event.getPlayer();

						World world = Bukkit.getWorld(getConfig().getString(name + ".world"));
						double x = getConfig().getDouble(name + ".X");
						double y = getConfig().getDouble(name + ".Y");
						double z = getConfig().getDouble(name + ".Z");
						float yaw = (float) getConfig().getDouble(name + ".yaw");
						float pitch = (float) getConfig().getDouble(name + ".pitch");

						Location location = new Location(world, x, y, z, yaw, pitch);

						player.teleport(location);

						player.sendMessage(ChatColor.GREEN + "You have been warped to warp " + name + "!");

					} else {
						event.getPlayer().sendMessage(ChatColor.RED + "You don't have permission!");
					}
				} else {
					event.getPlayer().sendMessage(ChatColor.RED + "This warp does not exist!");
				}
			}
		}

	}

	@EventHandler
	public void onSignUpdate(SignChangeEvent event) {
		event.setLine(0, event.getLine(0).replaceAll("[^ -~§]", ""));
		event.setLine(1, event.getLine(1).replaceAll("[^ -~§]", ""));
		event.setLine(2, event.getLine(2).replaceAll("[^ -~§]", ""));
		event.setLine(3, event.getLine(3).replaceAll("[^ -~§]", ""));
		if (event.getLine(0).equalsIgnoreCase(ChatColor.stripColor(line))) {
			event.setLine(0, line);
			event.setLine(1, event.getLine(1));
			event.setLine(2, "");
			event.setLine(3, "");
		}

	}
}
