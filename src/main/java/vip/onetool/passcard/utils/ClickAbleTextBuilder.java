package vip.onetool.passcard.utils;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.*;

/**
 * @author mcard
 */
public class ClickAbleTextBuilder {

    private static final Pattern patternMatchCommand = Pattern.compile("(/[a-zA-Z0-9 ]+)");

    public static TextComponent fastBuildClickAbleCommand(String str) {
        Matcher matcher = patternMatchCommand.matcher(str);
        if (matcher.find()) {
            String command = matcher.group(1).trim();
            TextComponent left = new TextComponent(str.split(command, 2)[0]);
            TextComponent right = new TextComponent(str.split(command, 2)[1]);
            TextComponent click = new TextComponent(command);
            click.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
            command = "§6§l" + command + "\n§8点击输入";
            click.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
                    new TextComponent(command)
            }));
            left.addExtra(click);
            left.addExtra(right);
            return left;
        } else {
            return new TextComponent(str);
        }
    }


    public static void send(CommandSender sender, TextComponent str) {
        //如果是玩家展示更加丰富的文本内容，否则仅展示文本
        if (sender instanceof Player) {
            ((Player) sender).spigot().sendMessage(str);
        } else {
            sender.sendMessage(str.getText());
        }
    }

}
