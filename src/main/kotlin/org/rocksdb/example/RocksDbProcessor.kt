package org.rocksdb.example

import org.rocksdb.Options
import org.rocksdb.RocksDB
import oshi.SystemInfo
import oshi.hardware.GlobalMemory
import oshi.util.FormatUtil
import java.io.File
import java.nio.file.Files

fun main(args: Array<String>) {
    val count = try {
        args[0].toInt()
    } catch (e: Exception) {
        100
    }
    RocksDbProcessor().testMemoryCleanup(count)
}

class RocksDbProcessor {

    private val rocksDbStore = hashMapOf<String, RocksDB>()
    private val si = SystemInfo()

    fun testMemoryCleanup(count: Int = 100) {
        createSampleRocksDb()
        println("memory usage before testing, usage=${si.hardware.memory.getUsedMemoryStr()}")
        println("start opening $count times of rocksdb...")
        openRocksDbs(count)
        Thread.sleep(2500)
        println("memory usage after opening $count times of rocksdb, usage=${si.hardware.memory.getUsedMemoryStr()}")
        closeRocksDbs(count)
        Thread.sleep(2500)
        println("memory usage after close all rocksdb, usage=${si.hardware.memory.getUsedMemoryStr()}")
        cleanup()
    }

    private fun openRocksDbs(count: Int) {
        for (i in 1..count) {
            RocksDB.openReadOnly(buildRocksDBOptions(), ROCKSDB_PATH).also { db ->
                rocksDbStore["$i"] = db
            }
        }
    }

    private fun closeRocksDbs(count: Int) {
        for (i in 1..count) {
            rocksDbStore.remove("$i").also { db ->
                db?.close()
            }
        }
    }

    private fun createSampleRocksDb() {
        println("creating rocksdb for testing...")
        val rocksDb = File(ROCKSDB_PATH).also {
            Files.createDirectories(it.absoluteFile.toPath())
        }

        val options = buildRocksDBOptions()
        RocksDB.open(options, rocksDb.absolutePath).use {
            for (i in 1..200_000) {
                it.put(
                    "$i".toByteArray(),
                    ("Jon Jones won the UFC heavyweight title in some style on Saturday, " +
                            "defeating France’s Ciryl Gane with a first-round submission following a " +
                            "three-year absence and a step up in weight division.\n").toByteArray()
                )
            }
        }
        options.close()
        println("rocksdb has been created")
    }

    private fun buildRocksDBOptions(): Options {
        return Options().apply {
            setCreateIfMissing(true)
        }
    }

    private fun cleanup() {
        File(ROCKSDB_PATH).deleteRecursively()
    }

    private fun GlobalMemory.getUsedMemoryStr(): String {
        return FormatUtil.formatBytes(total - available)
    }

    companion object {
        val ROCKSDB_PATH = "${System.getProperty("user.home")}/db/rocksDb"
    }
}
