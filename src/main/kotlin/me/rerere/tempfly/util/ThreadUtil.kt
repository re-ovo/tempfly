package me.rerere.tempfly.util

import me.rerere.tempfly.TempFlyPlugin
import org.bukkit.Bukkit

/**
 * 异步运行任务
 */
fun async(runnable: () -> Unit) = Bukkit.getScheduler().runTaskAsynchronously(TempFlyPlugin, runnable)

fun sync(runnable: () -> Unit) = Bukkit.getScheduler().runTask(TempFlyPlugin, runnable)