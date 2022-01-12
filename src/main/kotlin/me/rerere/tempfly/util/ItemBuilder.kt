package me.rerere.tempfly.util

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

/**
 * 构建一个新的ItemStack
 *
 * @param material 物品材质
 * @param modifier 构建lambda
 * @return 构建完的ItemStack
 */
inline fun itemStackOf(material: Material, modifier: ItemBuilder.() -> Unit = {}): ItemStack {
    val builder = ItemBuilder(ItemStack(material))
    builder.modifier()
    return builder.itemStack
}

/**
 * 使用一个ItemStack为基础构建一个新的ItemStack
 *
 * @param itemStack 基础的ItemStack
 * @param modifier 构建lambda
 * @return 构建完的ItemStack
 */
inline fun itemStackOf(itemStack: ItemStack, modifier: ItemBuilder.() -> Unit): ItemStack {
    val builder = ItemBuilder(itemStack)
    builder.modifier()
    return builder.itemStack
}


// 不建议直接调用
@JvmInline
value class ItemBuilder(val itemStack: ItemStack) {
    var amount: Int
        get() = itemStack.amount
        set(amount) {
            itemStack.amount = amount
        }

    var displayName: String
        get() = itemStack.itemMeta?.displayName ?: itemStack.type.name
        set(value) {
            meta {
                setDisplayName(value.color())
            }
        }

    fun enchant(type: Enchantment, level: Int): ItemBuilder {
        itemStack.addUnsafeEnchantment(type, level)
        return this
    }

    inline fun meta(modifier: ItemMeta.() -> Unit): ItemBuilder {
        val meta = this.itemStack.itemMeta
        meta?.modifier()
        itemStack.itemMeta = meta
        return this
    }

    fun lore(lore: List<String>): ItemBuilder {
        meta {
            this.lore = lore
        }
        return this
    }

    fun addLore(line: String) : ItemBuilder {
        meta {
            this.lore = this.lore?.apply {
                add(line)
            } ?: listOf(line)
        }
        return this
    }
}