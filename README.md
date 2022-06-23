# EasyListener

[![version](https://img.shields.io/github/v/release/CarmJos/EasyListener)](https://github.com/CarmJos/EasyListener/releases)
[![License](https://img.shields.io/github/license/CarmJos/EasyListener)](https://opensource.org/licenses/MIT)
[![workflow](https://github.com/CarmJos/EasyListener/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/CarmJos/EasyListener/actions/workflows/maven.yml)
![CodeSize](https://img.shields.io/github/languages/code-size/CarmJos/EasyListener)
![](https://visitor-badge.glitch.me/badge?page_id=EasyListener.readme)

轻松(做)监听，简单快捷的通用Bukkit插件监听器类库。

> 本项目由 舰长 @tdiant 委托本人开发。

### [开发示例](src/test/java/DemoPlugin.java)

您可以点击这里访问项目的 [JavaDoc](https://carmjos.github.io/EasyListener) 。

```java

public class DemoPlugin extends JavaPlugin {

    protected final EasyListener listeners = EasyListener.create(this);

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

        listeners  // 额外提供的快捷方法
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
```

### 依赖方式

<details>
<summary>展开查看Maven依赖方式</summary>

```xml

<project>
    <repositories>

        <repository>
            <!--采用Maven中心库，安全稳定，但版本更新需要等待同步-->
            <id>maven</id>
            <name>Maven Central</name>
            <url>https://repo1.maven.org/maven2</url>
        </repository>

        <repository>
            <!--采用github依赖库，实时更新，但需要配置 (推荐) -->
            <id>EasyListener</id>
            <name>GitHub Packages</name>
            <url>https://maven.pkg.github.com/CarmJos/EasyListener</url>
        </repository>

        <repository>
            <!--采用我的私人依赖库，简单方便，但可能因为变故而无法使用-->
            <id>carm-repo</id>
            <name>Carm's Repo</name>
            <url>https://repo.carm.cc/repository/maven-public/</url>
        </repository>

    </repositories>

    <dependencies>

        <dependency>
            <groupId>cc.carm.lib</groupId>
            <artifactId>easylistener</artifactId>
            <version>[LATEST RELEASE]</version>
            <scope>compile</scope>
        </dependency>

    </dependencies>

</project>
```

</details>

<details>
<summary>展开查看Gradle依赖方式</summary>

```groovy
repositories {

    // 采用Maven中心库，安全稳定，但版本更新需要等待同步
    mavenCentral()

    // 采用github依赖库，实时更新，但需要配置 (推荐)
    maven { url 'https://maven.pkg.github.com/CarmJos/EasyListener' }

    // 采用我的私人依赖库，简单方便，但可能因为变故而无法使用
    maven { url 'https://repo.carm.cc/repository/maven-public/' }
}

dependencies {
    api "cc.carm.lib:easylistener:[LATEST RELEASE]"
}
```

</details>

## 支持与捐赠

若您觉得本插件做的不错，您可以通过捐赠支持我！

感谢您对开源项目的支持！

<img height=25% width=25% src="https://raw.githubusercontent.com/CarmJos/CarmJos/main/img/donate-code.jpg"  alt=""/>

## 开源协议

本项目源码采用 [GNU LESSER GENERAL PUBLIC LICENSE](https://www.gnu.org/licenses/lgpl-3.0.html) 开源协议。
