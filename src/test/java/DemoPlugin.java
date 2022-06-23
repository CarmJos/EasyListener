import cc.carm.lib.easylistener.EasyListener;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class DemoPlugin extends JavaPlugin {

    protected final EasyListener listeners = new EasyListener(this);

    @Override
    public void onEnable() {

        listeners // 基本用法
                .handle(PlayerInteractAtEntityEvent.class, (event) -> {
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

        listeners // 额外提供的快捷方法
                .cancelDeath(null) // 所有玩家取消死亡
                .cancelBreak(player -> !player.isOp()) // 禁止非OP玩家破坏方块/接水或岩浆
                .cancelPlace(player -> !player.isOp()) // 禁止非OP玩家放置方块/放水或岩浆
                .cancelPVP((attacker, victim) -> !attacker.isOp()) // 禁止非op玩家PVP
                .cancelWeatherChange() // 取消天气变更
                .cancelJoinMessage() // 取消加入消息
//                .cancelQuitMessage()
//                .handleJoinMessage(player -> "玩家 " + player.getName() + " 加入了服务器。")
                .handleQuitMessage(player -> "玩家 " + player.getName() + " 退出了服务器。"); // 设定退出消息

    }


}
