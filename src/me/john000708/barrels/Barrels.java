package me.john000708.barrels;

import java.util.List;

import org.bstats.bukkit.Metrics;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.thebusybiscuit.cscorelib2.updater.BukkitUpdater;
import io.github.thebusybiscuit.cscorelib2.updater.GitHubBuildsUpdater;
import io.github.thebusybiscuit.cscorelib2.updater.Updater;
import me.john000708.barrels.listeners.DisplayListener;
import me.john000708.barrels.listeners.WorldListener;
import me.mrCookieSlime.CSCoreLibPlugin.PluginUtils;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.events.ItemUseEvent;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Lists.SlimefunItems;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.handlers.ItemInteractionHandler;
import me.mrCookieSlime.Slimefun.Setup.SlimefunManager;
import me.mrCookieSlime.Slimefun.api.BlockStorage;

/**
 * Created by John on 06.05.2016.
 */
public class Barrels extends JavaPlugin {

    public static boolean displayItem;
    public static JavaPlugin plugin;
    public static Config config;

    //Can be private.
    private boolean plastic;

    public void onEnable() {
        plugin = this;

        PluginUtils utils = new PluginUtils(this);
        utils.setupConfig();
        config = utils.getConfig();
        
        // Setting up bStats
        new Metrics(this);

		// Setting up the Auto-Updater
		Updater updater;

		if (!getDescription().getVersion().startsWith("DEV - ")) {
			// We are using an official build, use the BukkitDev Updater
			updater = new BukkitUpdater(this, getFile(), 99947);
		}
		else {
			// If we are using a development build, we want to switch to our custom 
			updater = new GitHubBuildsUpdater(this, getFile(), "John000708/Barrels/master");
		}

		if (config.getBoolean("options.auto-update")) updater.start();

        new DisplayListener();
        new WorldListener();

        displayItem = config.getBoolean("options.displayItem");
        plastic = config.getBoolean("options.plastic-recipe");
        
        setup();
        getLogger().info("Barrels v" + getDescription().getVersion() + " has been enabled!");
    }

    public void onDisable() {
        plugin = null;
    }
    
    private void setup() {
        Category barrelCat = new Category(new CustomItem(new ItemStack(Material.OAK_LOG), "&aБочки для хранения предметов", "", "&a> Нажмите, чтобы открыть"), 2);

        ItemStack SMALL_BARREL = new CustomItem(new ItemStack(Material.OAK_LOG), "&eМаленькая бочка", "", "&8\u21E8 &7Вместимость: 64 стака");
        ItemStack MEDIUM_BARREL = new CustomItem(Material.SPRUCE_LOG, "&eСредняя бочка", "", "&8\u21E8 &7Вместимость: 128 стаков");
        ItemStack BIG_BARREL = new CustomItem(Material.DARK_OAK_LOG, "&eБольшая бочка", "", "&8\u21E8 &7Вместимость: 256 стаков");
        ItemStack LARGE_BARREL = new CustomItem(new ItemStack(Material.ACACIA_LOG), "&eОгромная бочка", "", "&8\u21E8 &7Вместимость: 512 стаков");
        ItemStack DSU = new CustomItem(new ItemStack(Material.DIAMOND_BLOCK), "&3Глубокий блок хранения", "", "&4Хранилище предметов последней разработки", "", "&8\u21E8 &7Вместимость: 1048576 стаков");

        //Upgrades
        final ItemStack EXPLOSION_MODULE = new CustomItem(new ItemStack(Material.ITEM_FRAME), "&9Взрывоустойчивость", "", "&rПредотвращает уничтожение", "&rбочки при помощи взрывов");
        final ItemStack BIOMETRIC_PROTECTION = new CustomItem(new ItemStack(Material.ITEM_FRAME), "&9Биометрическая защита", "", "&rЗапрещает другим игрокам", "&rоткрывать бочку");
        final ItemStack ID_CARD = new CustomItem(new ItemStack(Material.PAPER), "&rИдентификационная карта", "", "&eПравый клик&r, чтобы вписать свой ник", "&eПравый клик по бочке&r, чтобы дать", "&rигроку доступ к ней");
        final ItemStack STRUCT_UPGRADE_1 = new CustomItem(new ItemStack(Material.ITEM_FRAME), "&9Структурная реконструкция &7– &eI", "&bМаленькая &8\u21E8 &bсредняя");
        final ItemStack STRUCT_UPGRADE_2 = new CustomItem(new ItemStack(Material.ITEM_FRAME), "&9Структурная реконструкция &7– &eII", "&bСредняя &8\u21E8 &bбольшая");
        final ItemStack STRUCT_UPGRADE_3 = new CustomItem(new ItemStack(Material.ITEM_FRAME), "&9Структурная реконструкция &7– &eIII", "&bБольшая &8\u21E8 &bогромная");

        new Barrel(barrelCat, SMALL_BARREL, "BARREL_SMALL", RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{new ItemStack(Material.OAK_SLAB), plastic ? SlimefunItems.PLASTIC_SHEET : new ItemStack(Material.CAULDRON), new ItemStack(Material.OAK_SLAB), new ItemStack(Material.OAK_SLAB), new ItemStack(Material.CHEST), new ItemStack(Material.OAK_SLAB), new ItemStack(Material.OAK_SLAB), SlimefunItems.GILDED_IRON, new ItemStack(Material.OAK_SLAB)}, 4096) {

            @Override
            public String getInventoryTitle() {
                return "&eМаленькая &9бочка";
            }

        }.register();

        new Barrel(barrelCat, MEDIUM_BARREL, "BARREL_MEDIUM", RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{new ItemStack(Material.OAK_SLAB), plastic ? SlimefunItems.PLASTIC_SHEET : new ItemStack(Material.CAULDRON), new ItemStack(Material.OAK_SLAB), new ItemStack(Material.OAK_SLAB), SMALL_BARREL, new ItemStack(Material.OAK_SLAB), new ItemStack(Material.OAK_SLAB), SlimefunItems.GILDED_IRON, new ItemStack(Material.OAK_SLAB)}, 8192) {

            @Override
            public String getInventoryTitle() {
                return "&eСредняя &9бочка";
            }

        }.register();

        new Barrel(barrelCat, BIG_BARREL, "BARREL_BIG", RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{new ItemStack(Material.OAK_SLAB), plastic ? SlimefunItems.PLASTIC_SHEET : new ItemStack(Material.CAULDRON), new ItemStack(Material.OAK_SLAB), new ItemStack(Material.OAK_SLAB), MEDIUM_BARREL, new ItemStack(Material.OAK_SLAB), new ItemStack(Material.OAK_SLAB), SlimefunItems.GILDED_IRON, new ItemStack(Material.OAK_SLAB)}, 16384) {

            @Override
            public String getInventoryTitle() {
                return "&eБольшая &9бочка";
            }

        }.register();

        new Barrel(barrelCat, LARGE_BARREL, "BARREL_LARGE", RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{new ItemStack(Material.OAK_SLAB), plastic ? SlimefunItems.PLASTIC_SHEET : new ItemStack(Material.CAULDRON), new ItemStack(Material.OAK_SLAB), new ItemStack(Material.OAK_SLAB), BIG_BARREL, new ItemStack(Material.OAK_SLAB), new ItemStack(Material.OAK_SLAB), SlimefunItems.GILDED_IRON, new ItemStack(Material.OAK_SLAB)}, 32768) {

            @Override
            public String getInventoryTitle() {
                return "&eОгромная &9бочка";
            }

        }.register();

        new Barrel(barrelCat, DSU, "BARREL_GIGANTIC", RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[]{SlimefunItems.REINFORCED_PLATE, new ItemStack(Material.ENDER_CHEST), SlimefunItems.REINFORCED_PLATE, SlimefunItems.PLASTIC_SHEET, LARGE_BARREL, SlimefunItems.PLASTIC_SHEET, SlimefunItems.REINFORCED_PLATE, SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.REINFORCED_PLATE}, 1048576) {

            @Override
            public String getInventoryTitle() {
                return "&3Глубокий блок хранения";
            }

        }.register();

        //This line is too long to be readable easily.
        new SlimefunItem(barrelCat, EXPLOSION_MODULE, "EXPLOSION_MODULE", RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[]{new ItemStack(Material.TNT), new ItemStack(Material.GOLD_INGOT), new ItemStack(Material.TNT), new ItemStack(Material.GOLD_INGOT), new ItemStack(Material.REDSTONE), new ItemStack(Material.GOLD_INGOT), new ItemStack(Material.TNT), new ItemStack(Material.GOLD_INGOT), new ItemStack(Material.TNT)})
        .register(false, new ItemInteractionHandler() {

            @Override
            public boolean onRightClick(ItemUseEvent itemUseEvent, Player player, ItemStack itemStack) {
                if (!SlimefunManager.isItemSimiliar(itemStack, EXPLOSION_MODULE, true)) return false;
                if (itemUseEvent.getClickedBlock() != null && BlockStorage.hasBlockInfo(itemUseEvent.getClickedBlock()) && BlockStorage.checkID(itemUseEvent.getClickedBlock()).startsWith("BARREL_")) {
                    Block clickedBlock = itemUseEvent.getClickedBlock();
                    if (BlockStorage.getLocationInfo(clickedBlock.getLocation(), "explosion") == null) {
                        BlockStorage.addBlockInfo(clickedBlock, "explosion", "true");
                        // Fixes issue #6.
                        //player.getInventory().setItem(player.getInventory().getHeldItemSlot(), InvUtils.decreaseItem(itemStack, 1));
                        int amount = itemStack.getAmount();
                        if (amount <= 1) {
                            itemStack.setAmount(0);
                        }
                        else itemStack.setAmount(amount - 1);
                        player.sendMessage(ChatColor.GREEN + "Модуль успешно применён!");
                    }
                }
                return true;
            }
        });

        new SlimefunItem(barrelCat, STRUCT_UPGRADE_1, "STRUCT_UPGRADE_1", RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[]{SlimefunItems.LEAD_INGOT, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.LEAD_INGOT, SlimefunItems.DAMASCUS_STEEL_INGOT, MEDIUM_BARREL, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.LEAD_INGOT, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.LEAD_INGOT})
        .register(false, new ItemInteractionHandler() {

            @Override
            public boolean onRightClick(ItemUseEvent itemUseEvent, Player player, ItemStack itemStack) {
                if (!SlimefunManager.isItemSimiliar(itemStack, STRUCT_UPGRADE_1, true)) return false;
                if (itemUseEvent.getClickedBlock() != null && BlockStorage.hasBlockInfo(itemUseEvent.getClickedBlock()) && BlockStorage.checkID(itemUseEvent.getClickedBlock()).startsWith("BARREL_") && BlockStorage.getLocationInfo(itemUseEvent.getClickedBlock().getLocation(), "STRUCT_1") == null) {
                    Block clickedBlock = itemUseEvent.getClickedBlock();

                    BlockStorage.addBlockInfo(clickedBlock, "STRUCT_1", "true");
                    //There's no need to box the integer.
                    BlockStorage.addBlockInfo(clickedBlock, "capacity", String.valueOf(Integer.parseInt(BlockStorage.getLocationInfo(clickedBlock.getLocation(), "capacity")) + 8192));
                    // Fixes issue #6.
                    //player.getInventory().setItem(player.getInventory().getHeldItemSlot(), InvUtils.decreaseItem(itemStack, 1));
                    int amount = itemStack.getAmount();
                    if (amount <= 1) {
                        itemStack.setAmount(0);
                    }
                    else itemStack.setAmount(amount - 1);
                    player.sendMessage(ChatColor.GREEN + "Модуль успешно применён!");
                }
                return true;
            }
        });

        new SlimefunItem(barrelCat, STRUCT_UPGRADE_2, "STRUCT_UPGRADE_2", RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[]{SlimefunItems.LEAD_INGOT, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.LEAD_INGOT, SlimefunItems.DAMASCUS_STEEL_INGOT, BIG_BARREL, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.LEAD_INGOT, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.LEAD_INGOT})
        .register(false, new ItemInteractionHandler() {

            @Override
            public boolean onRightClick(ItemUseEvent itemUseEvent, Player player, ItemStack itemStack) {
                if (!SlimefunManager.isItemSimiliar(itemStack, STRUCT_UPGRADE_2, true)) return false;
                if (itemUseEvent.getClickedBlock() != null && BlockStorage.hasBlockInfo(itemUseEvent.getClickedBlock()) && BlockStorage.checkID(itemUseEvent.getClickedBlock()).startsWith("BARREL_") && BlockStorage.getLocationInfo(itemUseEvent.getClickedBlock().getLocation(), "STRUCT_2") == null) {
                    Block clickedBlock = itemUseEvent.getClickedBlock();

                    BlockStorage.addBlockInfo(clickedBlock, "STRUCT_2", "true");
                    //There's no need to box the integer.
                    BlockStorage.addBlockInfo(clickedBlock, "capacity", String.valueOf(Integer.parseInt(BlockStorage.getLocationInfo(clickedBlock.getLocation(), "capacity")) + 16384));
                    // Fixes issue #6.
                    //player.getInventory().setItem(player.getInventory().getHeldItemSlot(), InvUtils.decreaseItem(itemStack, 1));
                    int amount = itemStack.getAmount();
                    if (amount <= 1) {
                        itemStack.setAmount(0);
                    }
                    else itemStack.setAmount(amount - 1);
                    player.sendMessage(ChatColor.GREEN + "Модуль успешно применён!");
                }
                return true;
            }
        });

        new SlimefunItem(barrelCat, STRUCT_UPGRADE_3, "STRUCT_UPGRADE_3", RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[]{SlimefunItems.LEAD_INGOT, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.LEAD_INGOT, SlimefunItems.DAMASCUS_STEEL_INGOT, LARGE_BARREL, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.LEAD_INGOT, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.LEAD_INGOT})
        .register(false, new ItemInteractionHandler() {

            @Override
            public boolean onRightClick(ItemUseEvent itemUseEvent, Player player, ItemStack itemStack) {
                if (!SlimefunManager.isItemSimiliar(itemStack, STRUCT_UPGRADE_3, true)) return false;
                if (itemUseEvent.getClickedBlock() != null && BlockStorage.hasBlockInfo(itemUseEvent.getClickedBlock()) && BlockStorage.checkID(itemUseEvent.getClickedBlock()).startsWith("BARREL_") && BlockStorage.getLocationInfo(itemUseEvent.getClickedBlock().getLocation(), "STRUCT_3") == null) {
                    Block clickedBlock = itemUseEvent.getClickedBlock();

                    BlockStorage.addBlockInfo(clickedBlock, "STRUCT_3", "true");
                    //There's no need to box the integer.
                    BlockStorage.addBlockInfo(clickedBlock, "capacity", String.valueOf(Integer.parseInt(BlockStorage.getLocationInfo(clickedBlock.getLocation(), "capacity")) + 32768));
                    // Fixes issue #6.
                    //player.getInventory().setItem(player.getInventory().getHeldItemSlot(), InvUtils.decreaseItem(itemStack, 1));
                    int amount = itemStack.getAmount();
                    if (amount <= 1) {
                        itemStack.setAmount(0);
                    }
                    else itemStack.setAmount(amount - 1);
                    player.sendMessage(ChatColor.GREEN + "Модуль успешно применён!");
                }
                return true;
            }
        });

        new SlimefunItem(barrelCat, BIOMETRIC_PROTECTION, "BIO_PROTECTION", RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[]{new ItemStack(Material.REDSTONE), new ItemStack(Material.DIAMOND), new ItemStack(Material.REDSTONE), new ItemStack(Material.DIAMOND), new ItemStack(Material.PAPER), new ItemStack(Material.DIAMOND), new ItemStack(Material.REDSTONE), new ItemStack(Material.DIAMOND), new ItemStack(Material.REDSTONE)})
        .register(false, new ItemInteractionHandler() {
            @Override
            public boolean onRightClick(ItemUseEvent itemUseEvent, Player player, ItemStack itemStack) {
                if (!SlimefunManager.isItemSimiliar(itemStack, BIOMETRIC_PROTECTION, true)) return false;
                if (itemUseEvent.getClickedBlock() != null && BlockStorage.hasBlockInfo(itemUseEvent.getClickedBlock()) && BlockStorage.checkID(itemUseEvent.getClickedBlock()).startsWith("BARREL_") && BlockStorage.getLocationInfo(itemUseEvent.getClickedBlock().getLocation(), "BIO_PROT") == null) {
                    Block clickedBlock = itemUseEvent.getClickedBlock();

                    BlockStorage.addBlockInfo(clickedBlock, "protected", "true");
                    // Fixes issue #6.
                    //player.getInventory().setItem(player.getInventory().getHeldItemSlot(), InvUtils.decreaseItem(itemStack, 1));
                    int amount = itemStack.getAmount();
                    if (amount <= 1) {
                        itemStack.setAmount(0);
                    }
                    else itemStack.setAmount(amount - 1);
                    player.sendMessage(ChatColor.GREEN + "Модуль успешно применён!");
                }
                return true;
            }
        });

        new SlimefunItem(barrelCat, ID_CARD, "BARREL_ID_CARD", RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[]{new ItemStack(Material.REDSTONE), new ItemStack(Material.GOLD_NUGGET), new ItemStack(Material.REDSTONE), new ItemStack(Material.GOLD_NUGGET), new ItemStack(Material.PAPER), new ItemStack(Material.GOLD_NUGGET), new ItemStack(Material.REDSTONE), new ItemStack(Material.GOLD_NUGGET), new ItemStack(Material.REDSTONE)})
        .register(false, new ItemInteractionHandler() {
            @Override
            public boolean onRightClick(ItemUseEvent itemUseEvent, Player player, ItemStack itemStack) {
                if (!SlimefunManager.isItemSimiliar(itemStack, ID_CARD, false)) return false;
                Block clickedBlock = itemUseEvent.getClickedBlock();
                // No need to reference itemStack again in a new variable.
                //ItemStack idCard = itemStack;
                ItemMeta meta = itemStack.getItemMeta();
                if (!meta.hasLore()) return false;
                List<String> lore = itemStack.getItemMeta().getLore();
                if (lore.size() != 3) return false;

                if (lore.get(0).equals("")) {
                    lore.set(0, ChatColor.translateAlternateColorCodes('&', "&0" + player.getUniqueId().toString()));
                    lore.set(1, ChatColor.translateAlternateColorCodes('&', "&rПривязана к: " + player.getName()));
                    meta.setLore(lore);
                    itemStack.setItemMeta(meta);
                    player.sendMessage(ChatColor.GREEN + "Идентификационная карта успешно привязана.");
                } else if (clickedBlock != null && BlockStorage.hasBlockInfo(clickedBlock) && BlockStorage.checkID(clickedBlock).startsWith("BARREL_") && BlockStorage.getLocationInfo(clickedBlock.getLocation(), "whitelist") != null && BlockStorage.getLocationInfo(clickedBlock.getLocation(), "owner").equals(player.getUniqueId().toString())) {
                    String whitelistedPlayers = BlockStorage.getLocationInfo(clickedBlock.getLocation(), "whitelist");
                    String name = ChatColor.stripColor(lore.get(0));
                    if (!whitelistedPlayers.contains(name)) {
                        BlockStorage.addBlockInfo(clickedBlock, "whitelist", whitelistedPlayers + name + ";");
                        // Fixes issue #6.
                        //player.getInventory().setItem(player.getInventory().getHeldItemSlot(), InvUtils.decreaseItem(itemStack, 1));
                        int amount = itemStack.getAmount();
                        if (amount <= 1) {
                            itemStack.setAmount(0);
                        }
                        else itemStack.setAmount(amount - 1);
                        player.sendMessage(ChatColor.GREEN + "Игрок " + name + " добавлен в белый список!");
                    } 
                    else {
                        player.sendMessage(ChatColor.RED + "Этот игрок уже в белом списке.");
                    }
                }
                return true;
            }
        });
    }
}
