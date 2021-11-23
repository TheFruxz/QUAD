//[JET-Minecraft](../../../index.md)/[de.jet.minecraft.tool.data](../index.md)/[JetJsonFile](index.md)

# JetJsonFile

[jvm]\
interface [JetJsonFile](index.md) : [JetFile](../-jet-file/index.md)

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [jvm]<br>object [Companion](-companion/index.md) |

## Functions

| Name | Summary |
|---|---|
| [contains](../-jet-file/contains.md) | [jvm]<br>abstract fun [contains](../-jet-file/contains.md)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [get](get.md) | [jvm]<br>abstract operator override fun &lt;[T](get.md)&gt; [get](get.md)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [T](get.md)? |
| [load](load.md) | [jvm]<br>abstract override fun [load](load.md)() |
| [save](save.md) | [jvm]<br>abstract override fun [save](save.md)() |
| [set](set.md) | [jvm]<br>abstract operator override fun &lt;[T](set.md)&gt; [set](set.md)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), value: [T](set.md)) |

## Properties

| Name | Summary |
|---|---|
| [file](file.md) | [jvm]<br>abstract override val [file](file.md): [Path](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Path.html) |