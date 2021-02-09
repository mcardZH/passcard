package vip.onetool.pass.card.util;

import org.bukkit.Color;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationOptions;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 抽象一个数据库、本地yml文件配置类
 *
 * @author mcard
 */
public class SqlYmlConfigurationUtils implements Configuration {

    private final String connectUrl;
    private final String configTableName;
    private final String selectKey;
    private final String user;
    private final String password;
    private final File configFile;
    private final boolean sqlConfig;
    private String databaseClass = "com.mysql.jdbc.Driver";

    private YamlConfiguration config;

    public SqlYmlConfigurationUtils(String connectUrl, String user, String password, String configTableName, String selectKey) {

        this.sqlConfig = true;
        this.connectUrl = connectUrl;
        this.configTableName = configTableName;
        this.selectKey = selectKey;
        this.user = user;
        this.password = password;
        this.configFile = null;

        this.reload();
    }

    public SqlYmlConfigurationUtils(String databaseClass, String connectUrl, String user, String password, String configTableName, String selectKey) {

        this.sqlConfig = true;
        this.databaseClass = databaseClass;
        this.connectUrl = connectUrl;
        this.configTableName = configTableName;
        this.selectKey = selectKey;
        this.user = user;
        this.password = password;
        this.configFile = null;

        this.reload();
    }

    public SqlYmlConfigurationUtils(File configFile) {
        this.sqlConfig = false;
        this.connectUrl = "";
        this.configTableName = "";
        this.selectKey = "";
        this.user = "";
        this.password = "";
        this.configFile = configFile;

        this.reload();
    }

    /**
     * 重新加载配置
     */
    public void reload() {
        if (this.sqlConfig) {
            //连接数据库并获取最新配置
            try {
                //通过数据库中存储的就是yml内容
                Class.forName(databaseClass);
                Connection connection = DriverManager.getConnection(connectUrl, user, password);
                Statement statement = connection.createStatement();
                String sql = LanguageUtils.replace("SELECT config_value FROM %0 WHERE config_name = '%1'"
                        , configTableName, selectKey);
                ResultSet resultSet = statement.executeQuery(sql);
                String configValue = "";
                while (resultSet.next()) {
                    configValue = resultSet.getString("config_value");
                }
                configValue = configValue.replace("\\n", "\n");
                StringReader stringReader = new StringReader(configValue);
                config = YamlConfiguration.loadConfiguration(stringReader);
                resultSet.close();
                statement.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            config = YamlConfiguration.loadConfiguration(configFile);
        }
    }

    /**
     * 保存配置
     */
    public void save() {
        if (this.sqlConfig) {
            try {
                Class.forName(databaseClass);
                Connection connection = DriverManager.getConnection(connectUrl, user, password);
                Statement statement = connection.createStatement();
                String sql = LanguageUtils.replace("SELECT config_value FROM %0 WHERE config_name = '%1'"
                        , configTableName, selectKey);
                ResultSet resultSet = statement.executeQuery(sql);
                boolean find = false;
                while (resultSet.next()) {
                    find = true;
                }
                if (find) {
                    sql = LanguageUtils.replace("UPDATE %0 SET config_value = '%2' WHERE config_name = '%1'",
                            configTableName,
                            selectKey,
                            config.saveToString()
                                    .replace("\n", "\\n")
                                    .replace("'", "\\'"));
                } else {
                    sql = LanguageUtils.replace("INSERT INTO %0 (config_name, config_value) VALUES ('%2', '%1')",
                            configTableName,
                            selectKey,
                            config.saveToString()
                                    .replace("\n", "\\n")
                                    .replace("'", "\\'"));
                }
                statement.execute(sql);
                resultSet.close();
                statement.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                config.save(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @return 默认是com.mysql.jdbc.Driver
     */
    public String getDatabaseClass() {
        return databaseClass;
    }

    public void setDatabaseClass(String databaseClass) {
        this.databaseClass = databaseClass;
    }

    /**
     * Gets a set containing all keys in this section.
     * <p>
     * If deep is set to true, then this will contain all the keys within any
     * child {@link ConfigurationSection}s (and their children, etc). These
     * will be in a valid path notation for you to use.
     * <p>
     * If deep is set to false, then this will contain only the keys of any
     * direct children, and not their own children.
     *
     * @param deep Whether or not to get a deep list, as opposed to a shallow
     *             list.
     * @return Set of keys contained within this ConfigurationSection.
     */
    @Override
    public Set<String> getKeys(boolean deep) {
        return config.getKeys(deep);
    }

    /**
     * Gets a Map containing all keys and their values for this section.
     * <p>
     * If deep is set to true, then this will contain all the keys and values
     * within any child {@link ConfigurationSection}s (and their children,
     * etc). These keys will be in a valid path notation for you to use.
     * <p>
     * If deep is set to false, then this will contain only the keys and
     * values of any direct children, and not their own children.
     *
     * @param deep Whether or not to get a deep list, as opposed to a shallow
     *             list.
     * @return Map of keys and values of this section.
     */
    @Override
    public Map<String, Object> getValues(boolean deep) {
        return config.getValues(deep);
    }

    /**
     * Checks if this {@link ConfigurationSection} contains the given path.
     * <p>
     * If the value for the requested path does not exist but a default value
     * has been specified, this will return true.
     *
     * @param path Path to check for existence.
     * @return True if this section contains the requested path, either via
     * default or being set.
     * @throws IllegalArgumentException Thrown when path is null.
     */
    @Override
    public boolean contains(String path) {
        return config.contains(path);
    }

    /**
     * Checks if this {@link ConfigurationSection} contains the given path.
     * <p>
     * If the value for the requested path does not exist, the boolean parameter
     * of true has been specified, a default value for the path exists, this
     * will return true.
     * <p>
     * If a boolean parameter of false has been specified, true will only be
     * returned if there is a set value for the specified path.
     *
     * @param path          Path to check for existence.
     * @param ignoreDefault Whether or not to ignore if a default value for the
     *                      specified path exists.
     * @return True if this section contains the requested path, or if a default
     * value exist and the boolean parameter for this method is true.
     * @throws IllegalArgumentException Thrown when path is null.
     */
    @Override
    public boolean contains(String path, boolean ignoreDefault) {
        return config.contains(path, ignoreDefault);
    }

    /**
     * Checks if this {@link ConfigurationSection} has a value set for the
     * given path.
     * <p>
     * If the value for the requested path does not exist but a default value
     * has been specified, this will still return false.
     *
     * @param path Path to check for existence.
     * @return True if this section contains the requested path, regardless of
     * having a default.
     * @throws IllegalArgumentException Thrown when path is null.
     */
    @Override
    public boolean isSet(String path) {
        return config.isSet(path);
    }

    /**
     * Gets the path of this {@link ConfigurationSection} from its root {@link
     * Configuration}
     * <p>
     * For any {@link Configuration} themselves, this will return an empty
     * string.
     * <p>
     * If the section is no longer contained within its root for any reason,
     * such as being replaced with a different value, this may return null.
     * <p>
     * To retrieve the single name of this section, that is, the final part of
     * the path returned by this method, you may use {@link #getName()}.
     *
     * @return Path of this section relative to its root
     */
    @Override
    public String getCurrentPath() {
        return config.getCurrentPath();
    }

    /**
     * Gets the name of this individual {@link ConfigurationSection}, in the
     * path.
     * <p>
     * This will always be the final part of {@link #getCurrentPath()}, unless
     * the section is orphaned.
     *
     * @return Name of this section
     */
    @Override
    public String getName() {
        return config.getName();
    }

    /**
     * Gets the root {@link Configuration} that contains this {@link
     * ConfigurationSection}
     * <p>
     * For any {@link Configuration} themselves, this will return its own
     * object.
     * <p>
     * If the section is no longer contained within its root for any reason,
     * such as being replaced with a different value, this may return null.
     *
     * @return Root configuration containing this section.
     */
    @Override
    public Configuration getRoot() {
        return config.getRoot();
    }

    /**
     * Gets the parent {@link ConfigurationSection} that directly contains
     * this {@link ConfigurationSection}.
     * <p>
     * For any {@link Configuration} themselves, this will return null.
     * <p>
     * If the section is no longer contained within its parent for any reason,
     * such as being replaced with a different value, this may return null.
     *
     * @return Parent section containing this section.
     */
    @Override
    public ConfigurationSection getParent() {
        return config.getParent();
    }

    /**
     * Gets the requested Object by path.
     * <p>
     * If the Object does not exist but a default value has been specified,
     * this will return the default value. If the Object does not exist and no
     * default value was specified, this will return null.
     *
     * @param path Path of the Object to get.
     * @return Requested Object.
     */
    @Override
    public Object get(String path) {
        return config.get(path, path);
    }

    /**
     * Gets the requested Object by path, returning a default value if not
     * found.
     * <p>
     * If the Object does not exist then the specified default value will
     * returned regardless of if a default has been identified in the root
     * {@link Configuration}.
     *
     * @param path Path of the Object to get.
     * @param def  The default value to return if the path is not found.
     * @return Requested Object.
     */
    @Override
    public Object get(String path, Object def) {
        return config.get(path, def);
    }

    /**
     * Sets the specified path to the given value.
     * <p>
     * If value is null, the entry will be removed. Any existing entry will be
     * replaced, regardless of what the new value is.
     * <p>
     * Some implementations may have limitations on what you may store. See
     * their individual javadocs for details. No implementations should allow
     * you to store {@link Configuration}s or {@link ConfigurationSection}s,
     * please use {@link #createSection(String)} for that.
     *
     * @param path  Path of the object to set.
     * @param value New value to set the path to.
     */
    @Override
    public void set(String path, Object value) {
        config.set(path, value);
    }

    /**
     * Creates an empty {@link ConfigurationSection} at the specified path.
     * <p>
     * Any value that was previously set at this path will be overwritten. If
     * the previous value was itself a {@link ConfigurationSection}, it will
     * be orphaned.
     *
     * @param path Path to create the section at.
     * @return Newly created section
     */
    @Override
    public ConfigurationSection createSection(String path) {
        return config.createSection(path);
    }

    /**
     * Creates a {@link ConfigurationSection} at the specified path, with
     * specified values.
     * <p>
     * Any value that was previously set at this path will be overwritten. If
     * the previous value was itself a {@link ConfigurationSection}, it will
     * be orphaned.
     *
     * @param path Path to create the section at.
     * @param map  The values to used.
     * @return Newly created section
     */
    @Override
    public ConfigurationSection createSection(String path, Map<?, ?> map) {
        return config.createSection(path, map);
    }

    /**
     * Gets the requested String by path.
     * <p>
     * If the String does not exist but a default value has been specified,
     * this will return the default value. If the String does not exist and no
     * default value was specified, this will return null.
     *
     * @param path Path of the String to get.
     * @return Requested String.
     */
    @Override
    public String getString(String path) {
        return config.getString(path, path);
    }

    /**
     * Gets the requested String by path, returning a default value if not
     * found.
     * <p>
     * If the String does not exist then the specified default value will
     * returned regardless of if a default has been identified in the root
     * {@link Configuration}.
     *
     * @param path Path of the String to get.
     * @param def  The default value to return if the path is not found or is
     *             not a String.
     * @return Requested String.
     */
    @Override
    public String getString(String path, String def) {
        return config.getString(path, def);
    }

    /**
     * Checks if the specified path is a String.
     * <p>
     * If the path exists but is not a String, this will return false. If the
     * path does not exist, this will return false. If the path does not exist
     * but a default value has been specified, this will check if that default
     * value is a String and return appropriately.
     *
     * @param path Path of the String to check.
     * @return Whether or not the specified path is a String.
     */
    @Override
    public boolean isString(String path) {
        return config.isSet(path);
    }

    /**
     * Gets the requested int by path.
     * <p>
     * If the int does not exist but a default value has been specified, this
     * will return the default value. If the int does not exist and no default
     * value was specified, this will return 0.
     *
     * @param path Path of the int to get.
     * @return Requested int.
     */
    @Override
    public int getInt(String path) {
        return config.getInt(path);
    }

    /**
     * Gets the requested int by path, returning a default value if not found.
     * <p>
     * If the int does not exist then the specified default value will
     * returned regardless of if a default has been identified in the root
     * {@link Configuration}.
     *
     * @param path Path of the int to get.
     * @param def  The default value to return if the path is not found or is
     *             not an int.
     * @return Requested int.
     */
    @Override
    public int getInt(String path, int def) {
        return config.getInt(path, def);
    }

    /**
     * Checks if the specified path is an int.
     * <p>
     * If the path exists but is not a int, this will return false. If the
     * path does not exist, this will return false. If the path does not exist
     * but a default value has been specified, this will check if that default
     * value is a int and return appropriately.
     *
     * @param path Path of the int to check.
     * @return Whether or not the specified path is an int.
     */
    @Override
    public boolean isInt(String path) {
        return config.isInt(path);
    }

    /**
     * Gets the requested boolean by path.
     * <p>
     * If the boolean does not exist but a default value has been specified,
     * this will return the default value. If the boolean does not exist and
     * no default value was specified, this will return false.
     *
     * @param path Path of the boolean to get.
     * @return Requested boolean.
     */
    @Override
    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    /**
     * Gets the requested boolean by path, returning a default value if not
     * found.
     * <p>
     * If the boolean does not exist then the specified default value will
     * returned regardless of if a default has been identified in the root
     * {@link Configuration}.
     *
     * @param path Path of the boolean to get.
     * @param def  The default value to return if the path is not found or is
     *             not a boolean.
     * @return Requested boolean.
     */
    @Override
    public boolean getBoolean(String path, boolean def) {
        return config.getBoolean(path, def);
    }

    /**
     * Checks if the specified path is a boolean.
     * <p>
     * If the path exists but is not a boolean, this will return false. If the
     * path does not exist, this will return false. If the path does not exist
     * but a default value has been specified, this will check if that default
     * value is a boolean and return appropriately.
     *
     * @param path Path of the boolean to check.
     * @return Whether or not the specified path is a boolean.
     */
    @Override
    public boolean isBoolean(String path) {
        return config.isBoolean(path);
    }

    /**
     * Gets the requested double by path.
     * <p>
     * If the double does not exist but a default value has been specified,
     * this will return the default value. If the double does not exist and no
     * default value was specified, this will return 0.
     *
     * @param path Path of the double to get.
     * @return Requested double.
     */
    @Override
    public double getDouble(String path) {
        return config.getDouble(path);
    }

    /**
     * Gets the requested double by path, returning a default value if not
     * found.
     * <p>
     * If the double does not exist then the specified default value will
     * returned regardless of if a default has been identified in the root
     * {@link Configuration}.
     *
     * @param path Path of the double to get.
     * @param def  The default value to return if the path is not found or is
     *             not a double.
     * @return Requested double.
     */
    @Override
    public double getDouble(String path, double def) {
        return config.getDouble(path, def);
    }

    /**
     * Checks if the specified path is a double.
     * <p>
     * If the path exists but is not a double, this will return false. If the
     * path does not exist, this will return false. If the path does not exist
     * but a default value has been specified, this will check if that default
     * value is a double and return appropriately.
     *
     * @param path Path of the double to check.
     * @return Whether or not the specified path is a double.
     */
    @Override
    public boolean isDouble(String path) {
        return config.isDouble(path);
    }

    /**
     * Gets the requested long by path.
     * <p>
     * If the long does not exist but a default value has been specified, this
     * will return the default value. If the long does not exist and no
     * default value was specified, this will return 0.
     *
     * @param path Path of the long to get.
     * @return Requested long.
     */
    @Override
    public long getLong(String path) {
        return config.getLong(path);
    }

    /**
     * Gets the requested long by path, returning a default value if not
     * found.
     * <p>
     * If the long does not exist then the specified default value will
     * returned regardless of if a default has been identified in the root
     * {@link Configuration}.
     *
     * @param path Path of the long to get.
     * @param def  The default value to return if the path is not found or is
     *             not a long.
     * @return Requested long.
     */
    @Override
    public long getLong(String path, long def) {
        return config.getLong(path, def);
    }

    /**
     * Checks if the specified path is a long.
     * <p>
     * If the path exists but is not a long, this will return false. If the
     * path does not exist, this will return false. If the path does not exist
     * but a default value has been specified, this will check if that default
     * value is a long and return appropriately.
     *
     * @param path Path of the long to check.
     * @return Whether or not the specified path is a long.
     */
    @Override
    public boolean isLong(String path) {
        return config.isLong(path);
    }

    /**
     * Gets the requested List by path.
     * <p>
     * If the List does not exist but a default value has been specified, this
     * will return the default value. If the List does not exist and no
     * default value was specified, this will return null.
     *
     * @param path Path of the List to get.
     * @return Requested List.
     */
    @Override
    public List<?> getList(String path) {
        return config.getList(path);
    }

    /**
     * Gets the requested List by path, returning a default value if not
     * found.
     * <p>
     * If the List does not exist then the specified default value will
     * returned regardless of if a default has been identified in the root
     * {@link Configuration}.
     *
     * @param path Path of the List to get.
     * @param def  The default value to return if the path is not found or is
     *             not a List.
     * @return Requested List.
     */
    @Override
    public List<?> getList(String path, List<?> def) {
        return config.getList(path, def);
    }

    /**
     * Checks if the specified path is a List.
     * <p>
     * If the path exists but is not a List, this will return false. If the
     * path does not exist, this will return false. If the path does not exist
     * but a default value has been specified, this will check if that default
     * value is a List and return appropriately.
     *
     * @param path Path of the List to check.
     * @return Whether or not the specified path is a List.
     */
    @Override
    public boolean isList(String path) {
        return config.isList(path);
    }

    /**
     * Gets the requested List of String by path.
     * <p>
     * If the List does not exist but a default value has been specified, this
     * will return the default value. If the List does not exist and no
     * default value was specified, this will return an empty List.
     * <p>
     * This method will attempt to cast any values into a String if possible,
     * but may miss any values out if they are not compatible.
     *
     * @param path Path of the List to get.
     * @return Requested List of String.
     */
    @Override
    public List<String> getStringList(String path) {
        return config.getStringList(path);
    }

    /**
     * Gets the requested List of Integer by path.
     * <p>
     * If the List does not exist but a default value has been specified, this
     * will return the default value. If the List does not exist and no
     * default value was specified, this will return an empty List.
     * <p>
     * This method will attempt to cast any values into a Integer if possible,
     * but may miss any values out if they are not compatible.
     *
     * @param path Path of the List to get.
     * @return Requested List of Integer.
     */
    @Override
    public List<Integer> getIntegerList(String path) {
        return config.getIntegerList(path);
    }

    /**
     * Gets the requested List of Boolean by path.
     * <p>
     * If the List does not exist but a default value has been specified, this
     * will return the default value. If the List does not exist and no
     * default value was specified, this will return an empty List.
     * <p>
     * This method will attempt to cast any values into a Boolean if possible,
     * but may miss any values out if they are not compatible.
     *
     * @param path Path of the List to get.
     * @return Requested List of Boolean.
     */
    @Override
    public List<Boolean> getBooleanList(String path) {
        return config.getBooleanList(path);
    }

    /**
     * Gets the requested List of Double by path.
     * <p>
     * If the List does not exist but a default value has been specified, this
     * will return the default value. If the List does not exist and no
     * default value was specified, this will return an empty List.
     * <p>
     * This method will attempt to cast any values into a Double if possible,
     * but may miss any values out if they are not compatible.
     *
     * @param path Path of the List to get.
     * @return Requested List of Double.
     */
    @Override
    public List<Double> getDoubleList(String path) {
        return config.getDoubleList(path);
    }

    /**
     * Gets the requested List of Float by path.
     * <p>
     * If the List does not exist but a default value has been specified, this
     * will return the default value. If the List does not exist and no
     * default value was specified, this will return an empty List.
     * <p>
     * This method will attempt to cast any values into a Float if possible,
     * but may miss any values out if they are not compatible.
     *
     * @param path Path of the List to get.
     * @return Requested List of Float.
     */
    @Override
    public List<Float> getFloatList(String path) {
        return config.getFloatList(path);
    }

    /**
     * Gets the requested List of Long by path.
     * <p>
     * If the List does not exist but a default value has been specified, this
     * will return the default value. If the List does not exist and no
     * default value was specified, this will return an empty List.
     * <p>
     * This method will attempt to cast any values into a Long if possible,
     * but may miss any values out if they are not compatible.
     *
     * @param path Path of the List to get.
     * @return Requested List of Long.
     */
    @Override
    public List<Long> getLongList(String path) {
        return config.getLongList(path);
    }

    /**
     * Gets the requested List of Byte by path.
     * <p>
     * If the List does not exist but a default value has been specified, this
     * will return the default value. If the List does not exist and no
     * default value was specified, this will return an empty List.
     * <p>
     * This method will attempt to cast any values into a Byte if possible,
     * but may miss any values out if they are not compatible.
     *
     * @param path Path of the List to get.
     * @return Requested List of Byte.
     */
    @Override
    public List<Byte> getByteList(String path) {
        return config.getByteList(path);
    }

    /**
     * Gets the requested List of Character by path.
     * <p>
     * If the List does not exist but a default value has been specified, this
     * will return the default value. If the List does not exist and no
     * default value was specified, this will return an empty List.
     * <p>
     * This method will attempt to cast any values into a Character if
     * possible, but may miss any values out if they are not compatible.
     *
     * @param path Path of the List to get.
     * @return Requested List of Character.
     */
    @Override
    public List<Character> getCharacterList(String path) {
        return config.getCharacterList(path);
    }

    /**
     * Gets the requested List of Short by path.
     * <p>
     * If the List does not exist but a default value has been specified, this
     * will return the default value. If the List does not exist and no
     * default value was specified, this will return an empty List.
     * <p>
     * This method will attempt to cast any values into a Short if possible,
     * but may miss any values out if they are not compatible.
     *
     * @param path Path of the List to get.
     * @return Requested List of Short.
     */
    @Override
    public List<Short> getShortList(String path) {
        return config.getShortList(path);
    }

    /**
     * Gets the requested List of Maps by path.
     * <p>
     * If the List does not exist but a default value has been specified, this
     * will return the default value. If the List does not exist and no
     * default value was specified, this will return an empty List.
     * <p>
     * This method will attempt to cast any values into a Map if possible, but
     * may miss any values out if they are not compatible.
     *
     * @param path Path of the List to get.
     * @return Requested List of Maps.
     */
    @Override
    public List<Map<?, ?>> getMapList(String path) {
        return config.getMapList(path);
    }

    /**
     * Gets the requested {@link ConfigurationSerializable} object at the given
     * path.
     * <p>
     * If the Object does not exist but a default value has been specified, this
     * will return the default value. If the Object does not exist and no
     * default value was specified, this will return null.
     *
     * @param path  the path to the object.
     * @param clazz the type of {@link ConfigurationSerializable}
     * @return Requested {@link ConfigurationSerializable} object
     */
    @Override
    public <T extends ConfigurationSerializable> T getSerializable(String path, Class<T> clazz) {
        return config.getSerializable(path, clazz);
    }

    /**
     * Gets the requested {@link ConfigurationSerializable} object at the given
     * path, returning a default value if not found
     * <p>
     * If the Object does not exist then the specified default value will
     * returned regardless of if a default has been identified in the root
     * {@link Configuration}.
     *
     * @param path  the path to the object.
     * @param clazz the type of {@link ConfigurationSerializable}
     * @param def   the default object to return if the object is not present at
     *              the path
     * @return Requested {@link ConfigurationSerializable} object
     */
    @Override
    public <T extends ConfigurationSerializable> T getSerializable(String path, Class<T> clazz, T def) {
        return config.getSerializable(path, clazz, def);
    }

    /**
     * Gets the requested Vector by path.
     * <p>
     * If the Vector does not exist but a default value has been specified,
     * this will return the default value. If the Vector does not exist and no
     * default value was specified, this will return null.
     *
     * @param path Path of the Vector to get.
     * @return Requested Vector.
     */
    @Override
    public Vector getVector(String path) {
        return config.getVector(path);
    }

    /**
     * Gets the requested {@link Vector} by path, returning a default value if
     * not found.
     * <p>
     * If the Vector does not exist then the specified default value will
     * returned regardless of if a default has been identified in the root
     * {@link Configuration}.
     *
     * @param path Path of the Vector to get.
     * @param def  The default value to return if the path is not found or is
     *             not a Vector.
     * @return Requested Vector.
     */
    @Override
    public Vector getVector(String path, Vector def) {
        return config.getVector(path, def);
    }

    /**
     * Checks if the specified path is a Vector.
     * <p>
     * If the path exists but is not a Vector, this will return false. If the
     * path does not exist, this will return false. If the path does not exist
     * but a default value has been specified, this will check if that default
     * value is a Vector and return appropriately.
     *
     * @param path Path of the Vector to check.
     * @return Whether or not the specified path is a Vector.
     */
    @Override
    public boolean isVector(String path) {
        return config.isVector(path);
    }

    /**
     * Gets the requested OfflinePlayer by path.
     * <p>
     * If the OfflinePlayer does not exist but a default value has been
     * specified, this will return the default value. If the OfflinePlayer
     * does not exist and no default value was specified, this will return
     * null.
     *
     * @param path Path of the OfflinePlayer to get.
     * @return Requested OfflinePlayer.
     */
    @Override
    public OfflinePlayer getOfflinePlayer(String path) {
        return config.getOfflinePlayer(path);
    }

    /**
     * Gets the requested {@link OfflinePlayer} by path, returning a default
     * value if not found.
     * <p>
     * If the OfflinePlayer does not exist then the specified default value
     * will returned regardless of if a default has been identified in the
     * root {@link Configuration}.
     *
     * @param path Path of the OfflinePlayer to get.
     * @param def  The default value to return if the path is not found or is
     *             not an OfflinePlayer.
     * @return Requested OfflinePlayer.
     */
    @Override
    public OfflinePlayer getOfflinePlayer(String path, OfflinePlayer def) {
        return config.getOfflinePlayer(path, def);
    }

    /**
     * Checks if the specified path is an OfflinePlayer.
     * <p>
     * If the path exists but is not a OfflinePlayer, this will return false.
     * If the path does not exist, this will return false. If the path does
     * not exist but a default value has been specified, this will check if
     * that default value is a OfflinePlayer and return appropriately.
     *
     * @param path Path of the OfflinePlayer to check.
     * @return Whether or not the specified path is an OfflinePlayer.
     */
    @Override
    public boolean isOfflinePlayer(String path) {
        return config.isOfflinePlayer(path);
    }

    /**
     * Gets the requested ItemStack by path.
     * <p>
     * If the ItemStack does not exist but a default value has been specified,
     * this will return the default value. If the ItemStack does not exist and
     * no default value was specified, this will return null.
     *
     * @param path Path of the ItemStack to get.
     * @return Requested ItemStack.
     */
    @Override
    public ItemStack getItemStack(String path) {
        return config.getItemStack(path);
    }

    /**
     * Gets the requested {@link ItemStack} by path, returning a default value
     * if not found.
     * <p>
     * If the ItemStack does not exist then the specified default value will
     * returned regardless of if a default has been identified in the root
     * {@link Configuration}.
     *
     * @param path Path of the ItemStack to get.
     * @param def  The default value to return if the path is not found or is
     *             not an ItemStack.
     * @return Requested ItemStack.
     */
    @Override
    public ItemStack getItemStack(String path, ItemStack def) {
        return config.getItemStack(path, def);
    }

    /**
     * Checks if the specified path is an ItemStack.
     * <p>
     * If the path exists but is not a ItemStack, this will return false. If
     * the path does not exist, this will return false. If the path does not
     * exist but a default value has been specified, this will check if that
     * default value is a ItemStack and return appropriately.
     *
     * @param path Path of the ItemStack to check.
     * @return Whether or not the specified path is an ItemStack.
     */
    @Override
    public boolean isItemStack(String path) {
        return config.isItemStack(path);
    }

    /**
     * Gets the requested Color by path.
     * <p>
     * If the Color does not exist but a default value has been specified,
     * this will return the default value. If the Color does not exist and no
     * default value was specified, this will return null.
     *
     * @param path Path of the Color to get.
     * @return Requested Color.
     */
    @Override
    public Color getColor(String path) {
        return config.getColor(path);
    }

    /**
     * Gets the requested {@link Color} by path, returning a default value if
     * not found.
     * <p>
     * If the Color does not exist then the specified default value will
     * returned regardless of if a default has been identified in the root
     * {@link Configuration}.
     *
     * @param path Path of the Color to get.
     * @param def  The default value to return if the path is not found or is
     *             not a Color.
     * @return Requested Color.
     */
    @Override
    public Color getColor(String path, Color def) {
        return config.getColor(path, def);
    }

    /**
     * Checks if the specified path is a Color.
     * <p>
     * If the path exists but is not a Color, this will return false. If the
     * path does not exist, this will return false. If the path does not exist
     * but a default value has been specified, this will check if that default
     * value is a Color and return appropriately.
     *
     * @param path Path of the Color to check.
     * @return Whether or not the specified path is a Color.
     */
    @Override
    public boolean isColor(String path) {
        return config.isColor(path);
    }

    /**
     * Gets the requested ConfigurationSection by path.
     * <p>
     * If the ConfigurationSection does not exist but a default value has been
     * specified, this will return the default value. If the
     * ConfigurationSection does not exist and no default value was specified,
     * this will return null.
     *
     * @param path Path of the ConfigurationSection to get.
     * @return Requested ConfigurationSection.
     */
    @Override
    public ConfigurationSection getConfigurationSection(String path) {
        return config.getConfigurationSection(path);
    }

    /**
     * Checks if the specified path is a ConfigurationSection.
     * <p>
     * If the path exists but is not a ConfigurationSection, this will return
     * false. If the path does not exist, this will return false. If the path
     * does not exist but a default value has been specified, this will check
     * if that default value is a ConfigurationSection and return
     * appropriately.
     *
     * @param path Path of the ConfigurationSection to check.
     * @return Whether or not the specified path is a ConfigurationSection.
     */
    @Override
    public boolean isConfigurationSection(String path) {
        return config.isConfigurationSection(path);
    }

    /**
     * Gets the equivalent {@link ConfigurationSection} from the default
     * {@link Configuration} defined in {@link #getRoot()}.
     * <p>
     * If the root contains no defaults, or the defaults doesn't contain a
     * value for this path, or the value at this path is not a {@link
     * ConfigurationSection} then this will return null.
     *
     * @return Equivalent section in root configuration
     */
    @Override
    public ConfigurationSection getDefaultSection() {
        return config.getDefaultSection();
    }

    /**
     * Sets the default value of the given path as provided.
     * <p>
     * If no source {@link Configuration} was provided as a default
     * collection, then a new {@link MemoryConfiguration} will be created to
     * hold the new default value.
     * <p>
     * If value is null, the value will be removed from the default
     * Configuration source.
     *
     * @param path  Path of the value to set.
     * @param value Value to set the default to.
     * @throws IllegalArgumentException Thrown if path is null.
     */
    @Override
    public void addDefault(String path, Object value) {
        config.addDefault(path, value);
    }

    /**
     * Sets the default values of the given paths as provided.
     * <p>
     * If no source {@link Configuration} was provided as a default
     * collection, then a new {@link MemoryConfiguration} will be created to
     * hold the new default values.
     *
     * @param defaults A map of Path{@literal ->}Values to add to defaults.
     * @throws IllegalArgumentException Thrown if defaults is null.
     */
    @Override
    public void addDefaults(Map<String, Object> defaults) {
        config.addDefaults(defaults);
    }

    /**
     * Sets the default values of the given paths as provided.
     * <p>
     * If no source {@link Configuration} was provided as a default
     * collection, then a new {@link MemoryConfiguration} will be created to
     * hold the new default value.
     * <p>
     * This method will not hold a reference to the specified Configuration,
     * nor will it automatically update if that Configuration ever changes. If
     * you require this, you should set the default source with {@link
     * #setDefaults(Configuration)}.
     *
     * @param defaults A configuration holding a list of defaults to copy.
     * @throws IllegalArgumentException Thrown if defaults is null or this.
     */
    @Override
    public void addDefaults(Configuration defaults) {
        config.addDefaults(defaults);
    }

    /**
     * Sets the source of all default values for this {@link Configuration}.
     * <p>
     * If a previous source was set, or previous default values were defined,
     * then they will not be copied to the new source.
     *
     * @param defaults New source of default values for this configuration.
     * @throws IllegalArgumentException Thrown if defaults is null or this.
     */
    @Override
    public void setDefaults(Configuration defaults) {
        config.setDefaults(defaults);
    }

    /**
     * Gets the source {@link Configuration} for this configuration.
     * <p>
     * If no configuration source was set, but default values were added, then
     * a {@link MemoryConfiguration} will be returned. If no source was set
     * and no defaults were set, then this method will return null.
     *
     * @return Configuration source for default values, or null if none exist.
     */
    @Override
    public Configuration getDefaults() {
        return config.getDefaults();
    }

    /**
     * Gets the {@link ConfigurationOptions} for this {@link Configuration}.
     * <p>
     * All setters through this method are chainable.
     *
     * @return Options for this configuration
     */
    @Override
    public ConfigurationOptions options() {
        return config.options();
    }

}
