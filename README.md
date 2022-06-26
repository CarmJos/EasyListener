# EasyListener

[![version](https://img.shields.io/github/v/release/CarmJos/EasyListener)](https://github.com/CarmJos/EasyListener/releases)
[![License](https://img.shields.io/github/license/CarmJos/EasyListener)](https://opensource.org/licenses/MIT)
[![workflow](https://github.com/CarmJos/EasyListener/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/CarmJos/EasyListener/actions/workflows/maven.yml)
![CodeSize](https://img.shields.io/github/languages/code-size/CarmJos/EasyListener)
![](https://visitor-badge.glitch.me/badge?page_id=EasyListener.readme)

轻松(做)监听，简单快捷的通用Bukkit插件监听器类库。

### [开发示例](src/test/java/DemoPlugin.java)

相关开发示例请 [点击这里](src/test/java/DemoPlugin.java)，您也可以直接访问项目的 [JavaDoc](https://carmjos.github.io/EasyListener) 。

```java

public class DemoPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        EasyListener listeners = EasyListener.create(this);
        // listeners...; // Do something...
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
