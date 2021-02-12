package vip.onetool.pass.card.rewards;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import vip.onetool.pass.card.seasons.SeasonPlayer;
import vip.onetool.pass.card.util.LanguageUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mcard
 */
public class ItemReward implements RewardInterface {

    /**
     * 获取用于描述的物品
     *
     * @param config 当前配置
     * @return 物品
     */
    @Override
    public ItemStack getDetailItemStack(Object config) {
        String defaultName = "reward.item.detail-item-stack.default-name";
        ItemStack defItemStack = new ItemStack(Material.GRASS);
        ItemMeta defItemMeta = defItemStack.getItemMeta();
        defItemMeta.setDisplayName(LanguageUtils.get(defaultName, null));
        defItemStack.setItemMeta(defItemMeta);
        if (!(config instanceof ConfigurationSection)) {
            // 配置无效或无配置
            return defItemStack;
        }
        ConfigurationSection configSection = ((ConfigurationSection) config);
        String typePath = "type";
        String amountPath = "amount";
        String damagePath = "damage";
        String namePath = "name";
        String lorePath = "lore";
        String enchantmentsPath = "enchantments";
        String unbreakablePath = "unbreakable";
        if (configSection.isSet(typePath)) {
            // 单物品
            return getItemStack(defItemStack, typePath, amountPath, damagePath,
                    namePath, lorePath, enchantmentsPath, unbreakablePath, configSection, 1);
        } else {
            // 多物品
            for (String key : configSection.getKeys(false)) {
                ConfigurationSection cfg = configSection.getConfigurationSection(key);
                return getItemStack(defItemStack, typePath, amountPath, damagePath,
                        namePath, lorePath, enchantmentsPath, unbreakablePath, cfg, configSection.getKeys(false).size());
            }
        }
        return defItemStack;
    }

    private ItemStack getItemStack(ItemStack defItemStack, String typePath, String amountPath,
                                   String damagePath, String namePath, String lorePath,
                                   String enchantmentsPath, String unbreakablePath,
                                   ConfigurationSection cfg, int count) {
        String type = cfg.getString(typePath);
        int amount = cfg.getInt(amountPath, 1);
        int damage = cfg.getInt(damagePath, 0);
        String name = cfg.getString(namePath);
        List<String> lore = cfg.getStringList(lorePath);
        List<String> enchantments = cfg.getStringList(enchantmentsPath);
        boolean unbreakable = cfg.getBoolean(unbreakablePath);
        Material typeMaterial;
        try {
            typeMaterial = Material.valueOf(type);
        } catch (Exception e) {
            String itemTypeError = "reward.item.item-type-error";
            Bukkit.getConsoleSender().sendMessage(LanguageUtils.get(itemTypeError, null, type));
            return defItemStack;
        }
        ItemStack itemStack = new ItemStack(typeMaterial);
        itemStack.setAmount(amount);
        itemStack.setDurability((short) damage);
        ItemMeta itemMeta = itemStack.getItemMeta();
        String moreItemPath = "reward.item.more-item";
        if (count >= 1) {
            name = name + LanguageUtils.get(moreItemPath, null, count);
        }
        itemMeta.setDisplayName(name);
        itemMeta.setLore(lore);
        for (String enchantmentStr : enchantments) {
            String[] enchantTarget = enchantmentStr.split("\\|", 2);
            Enchantment enchantment;
            try {
                enchantment = Enchantment.getByName(enchantTarget[0]);
            } catch (Exception e) {
                // 附魔类型未知
                String itemTypeError = "reward.item.enchantment-type-error";
                Bukkit.getConsoleSender().sendMessage(LanguageUtils.get(itemTypeError, null, enchantTarget[0]));
                continue;
            }
            int level = 1;
            if (enchantTarget.length == 2) {
                try {
                    level = Integer.parseInt(enchantTarget[1]);
                } catch (NumberFormatException ignored) {
                }
                if (level <= 0) {
                    level = 1;
                }
            }
            itemMeta.addEnchant(enchantment, level, true);
        }
        itemMeta.setUnbreakable(unbreakable);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    /**
     * 获取编辑模板
     *
     * @return 模板类型
     */
    @Override
    public RewardTypeEnum getRewardEditType() {
        return RewardTypeEnum.ITEM_STACK_MULTIPLE;
    }

    /**
     * 获取编辑帮助
     * <p>只有<b>{@link RewardTypeEnum#ITEM_STACK_SINGLE}</b>
     * <b>{@link RewardTypeEnum#ITEM_STACK_MULTIPLE}</b>、
     * <b>{@link RewardTypeEnum#CHAT_SINGLE}</b>、
     * <b>{@link RewardTypeEnum#CHAT_MULTIPLE}</b>会调用此方法</p>
     *
     * @param player 谁获取这个帮助
     * @return 帮助内容
     */
    @Override
    public List<String> getEditHelp(Player player) {
        String help = "reward.item.help";
        return LanguageUtils.getList(help, player);
    }

    /**
     * 检查编辑状态
     * <p>只有<b>{@link RewardTypeEnum#ITEM_STACK_SINGLE}</b>
     * <b>{@link RewardTypeEnum#ITEM_STACK_MULTIPLE}</b>、
     * <b>{@link RewardTypeEnum#CHAT_SINGLE}</b>、
     * <b>{@link RewardTypeEnum#CHAT_MULTIPLE}</b>会调用此方法</p>
     *
     * @param player 谁在编辑
     * @param config 当前的配置（可能是{@link String}、{@link List<String>}、{@link ItemStack}和{@link List<ItemStack>}）
     * @return 返回null代表编辑无效，其他情况请返回格式化后的配置内容（如String转换为int）
     */
    @Override
    public Object editDone(Player player, Object config) {
        ConfigurationSection cfg = new MemoryConfiguration();
        String typePath = "type";
        String amountPath = "amount";
        String damagePath = "damage";
        String namePath = "name";
        String lorePath = "lore";
        String enchantmentsPath = "enchantments";
        String unbreakablePath = "unbreakable";
        if (config instanceof ItemStack) {
            // 单物品
            ItemStack itemStack = (ItemStack) config;
            cfg.set(typePath, itemStack.getType().name());
            cfg.set(amountPath, itemStack.getAmount());
            cfg.set(damagePath, itemStack.getDurability());
            if (itemStack.hasItemMeta()) {
                cfg.set(namePath, itemStack.getItemMeta().getDisplayName());
                cfg.set(lorePath, itemStack.getItemMeta().getLore());
                cfg.set(unbreakablePath, itemStack.getItemMeta().isUnbreakable());
                List<String> enchantmentList = new ArrayList<>();
                for (Enchantment enchantment : itemStack.getItemMeta().getEnchants().keySet()) {
                    enchantmentList.add(enchantment.getName() + "|" + itemStack.getItemMeta().getEnchants().get(enchantment));
                }
                if (enchantmentList.size() != 0) {
                    cfg.set(enchantmentsPath, enchantmentList);
                }
            }
        } else if (config instanceof ArrayList<?>) {
            // 多物品
            try {
                List<ItemStack> itemStacks = new ArrayList<>();
                for (Object o : (List<?>) config) {
                    itemStacks.add(((ItemStack) o));
                }
                for (int i = 1; i <= itemStacks.size(); i++) {
                    ConfigurationSection temp = new MemoryConfiguration();
                    ItemStack itemStack = itemStacks.get(i);
                    temp.set(typePath, itemStack.getType().name());
                    temp.set(amountPath, itemStack.getAmount());
                    temp.set(damagePath, itemStack.getDurability());
                    if (itemStack.hasItemMeta()) {
                        temp.set(namePath, itemStack.getItemMeta().getDisplayName());
                        temp.set(lorePath, itemStack.getItemMeta().getLore());
                        temp.set(unbreakablePath, itemStack.getItemMeta().isUnbreakable());
                        List<String> enchantmentList = new ArrayList<>();
                        for (Enchantment enchantment : itemStack.getItemMeta().getEnchants().keySet()) {
                            enchantmentList.add(enchantment.getName() + "|" + itemStack.getItemMeta().getEnchants().get(enchantment));
                        }
                        if (enchantmentList.size() != 0) {
                            temp.set(enchantmentsPath, enchantmentList);
                        }
                    }
                    cfg.set(String.valueOf(i), temp);
                }
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
        return cfg;
    }

    /**
     * 开始编辑
     * <p>只有<b>{@link RewardTypeEnum#CUSTOM}</b>类型的才会调用此方法</p>
     *
     * @param player    谁来编辑
     * @param nowConfig 旧的配置
     * @param callback  回调，<b>务必</b>把新的config和修改结果传递到这里
     */
    @Override
    public void editFor(Player player, Object nowConfig, RewardEditFinishInterface callback) {
        callback.finish(true, nowConfig);
    }

    /**
     * 注册的类型名称
     *
     * @return 名称
     */
    @Override
    public String getTypeName() {
        return "item";
    }

    /**
     * 执行奖励方案
     * <p>
     * 此时<b>一定</b>可以获得{@link Player}对象
     * </p>
     *
     * @param player 玩家
     * @param config 根据自身情况可能是{@link Integer}、{@link String}或者{@link ConfigurationSection}等等
     */
    @Override
    public void run(SeasonPlayer player, Object config) {
        if (!(config instanceof ConfigurationSection)) {
            return;
        }
        ConfigurationSection cfg = (ConfigurationSection) config;
        String typePath = "type";
        String amountPath = "amount";
        String damagePath = "damage";
        String namePath = "name";
        String lorePath = "lore";
        String enchantmentsPath = "enchantments";
        String unbreakablePath = "unbreakable";
        if (cfg.contains("type")) {
            // 单物品
            ItemStack item = getItemStack(null, typePath, amountPath, damagePath,
                    namePath, lorePath, enchantmentsPath, unbreakablePath, cfg, 0);
            if (player.getPlayer().getInventory().firstEmpty() == -1) {
                // 无空余格子
                player.getPlayer().getWorld().dropItem(player.getPlayer().getLocation(), item);
            } else {
                player.getPlayer().getInventory().addItem(item);
            }
        } else {
            // 多物品
            for (String key : cfg.getKeys(false)) {
                ConfigurationSection temp = cfg.getConfigurationSection(key);
                ItemStack item = getItemStack(null, typePath, amountPath, damagePath,
                        namePath, lorePath, enchantmentsPath, unbreakablePath, temp, 0);
                if (player.getPlayer().getInventory().firstEmpty() == -1) {
                    // 无空余格子
                    player.getPlayer().getWorld().dropItem(player.getPlayer().getLocation(), item);
                } else {
                    player.getPlayer().getInventory().addItem(item);
                }
            }
        }


    }
}
