package me.rerere.tempfly.storage.engine

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import me.rerere.tempfly.TempFlyPlugin
import me.rerere.tempfly.core.PlayerData
import me.rerere.tempfly.storage.IStorageEngine
import java.util.*
import kotlin.time.Duration.Companion.seconds

class MysqlEngine : IStorageEngine {
    private lateinit var dataSource: HikariDataSource

    override fun init() {
        val config = TempFlyPlugin.config
        val hikariConfig = HikariConfig().apply {
            jdbcUrl =
                "jdbc:mysql://${config.getString("database.mysql.url")}/${config.getString("database.mysql.database")}"
            username = config.getString("database.mysql.username")
            password = config.getString("database.mysql.password")

            addDataSourceProperty("cachePrepStmts", "true")
            addDataSourceProperty("prepStmtCacheSize", "250")
            addDataSourceProperty("prepStmtCacheSqlLimit", "2048")

            connectionTimeout = 10.seconds.inWholeMilliseconds
        }
        dataSource = HikariDataSource(hikariConfig)
        this.createTable()
    }

    override fun close() {
        dataSource.close()
    }

    private fun createTable() {
        dataSource.connection.use { connection ->
            connection.createStatement().use { statement ->
                statement.execute(
                    """
                    create table if not exists `$TableName`(
                        `uuid` varchar(255) not null,
                        `name` varchar(16) not null default '',
                        `time` bigint default 0,
                        `bonus` varchar(20) not null default '',
                        primary key (`uuid`)
                    )
                """.trimIndent()
                )
            }
        }
    }

    override fun load(uuid: UUID, defaultData: PlayerData): PlayerData = dataSource.connection.use { connection ->
        connection.prepareStatement(
            "select * from `$TableName` where `uuid`=?"
        ).use { preparedStatement ->
            preparedStatement.setString(1, uuid.toString())

            preparedStatement.executeQuery().use { resultSet ->
                if (resultSet.next()) {
                    PlayerData(
                        uuid = uuid,
                        name = resultSet.getString("name"),
                        time = resultSet.getLong("time"),
                        lastBonusDay = resultSet.getString("bonus")
                    )
                } else {
                    defaultData
                }
            }
        }
    }

    override fun save(playerData: PlayerData) {
        dataSource.connection.use { connection ->
            connection.prepareStatement(
                "insert into $TableName (uuid, name, time, bonus) values (?,?,?,?) on duplicate key update name=values(name),time=values(time),bonus=values(bonus)"
            ).use { preparedStatement ->
                preparedStatement.setString(1, playerData.uuid.toString())
                preparedStatement.setString(2, playerData.name)
                preparedStatement.setLong(3, playerData.time)
                preparedStatement.setString(4, playerData.lastBonusDay)

                preparedStatement.executeUpdate()
            }
        }
    }

    companion object {
        private const val TableName = "tempfly_players"
    }
}