import cc.carm.lib.easylistener.EasyListener;
import cc.carm.lib.easylistener.defaults.CommonListeners;
import cc.carm.lib.easylistener.defaults.EventFilters;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

public class DemoPlugin extends JavaPlugin {

    protected final EasyListener source = EasyListener.create(this);

    @Override
    public void onEnable() {
        // 基本用法
        source.handle(PlayerInteractAtEntityEvent.class, (event) -> {
                    Entity clicked = event.getRightClicked();
                    Player player = event.getPlayer();

                    if (clicked instanceof Player) {
                        player.sendMessage("你点了 " + clicked.getName() + " 一下！");
                    }

                }) // 处理一个事件
                .cancel(PlayerPickupArrowEvent.class) // 取消一个事件
                .cancel(
                        EntityDamageEvent.class, EventPriority.HIGHEST,
                        (event) -> event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK
                ); // 有条件的取消一个事件


        // Functional 用法
        source.handleEvent(PlayerInteractAtEntityEvent.class)
                .filter(e -> e.getRightClicked() instanceof Player)
                .filter(EventFilters.playerHasPerm("yc.admin"))
                .handle(e -> {
                    Player player = e.getPlayer();
                    player.sendMessage("你点了 " + e.getRightClicked().getName() + " 一下！");
                });

        source.handleEvents(PlayerEvent.class)
                .from(PlayerJoinEvent.class)
                .from(PlayerQuitEvent.class)
                .filter(EventFilters.playerHasPerm("yc.admin"))
                .handle(playerEvent -> System.out.println(playerEvent.getPlayer().getName()));

        source.handleBundle(Player.class)
                .from(PlayerJoinEvent.class, PlayerEvent::getPlayer)
                .from(PlayerInteractEvent.class, PlayerEvent::getPlayer)
                .filter(p -> p.hasPermission("yc.admin"))
                .handle((p, e) -> p.sendMessage("hi!"));


        // 预设的快捷方法
        CommonListeners.cancelDeath(source, null); // 所有玩家取消死亡
        CommonListeners.cancelBreak(source, player -> !player.isOp()); // 禁止非OP玩家破坏方块/接水或岩浆
        CommonListeners.cancelPlace(source, player -> !player.isOp()); // 禁止非OP玩家建造方块/接水或岩浆

        CommonListeners.cancelPVP(source, (attacker, victim) -> !attacker.isOp()); // 禁止非op玩家攻击别人
        CommonListeners.cancelWeatherChange(source); // 取消天气变化
        CommonListeners.cancelJoinMessage(source); // 取消加入消息
//        CommonListeners.cancelQuitMessage(listeners);
//        CommonListeners.handleJoinMessage(listeners, player -> "玩家 " + player.getName() + " 加入了服务器。");
        CommonListeners.handleQuitMessage(source, player -> "玩家 " + player.getName() + " 退出了服务器。"); // 设定退出消息

    }


}
