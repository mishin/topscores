package org.gmd.repository.jdbc

import org.gmd.model.Game
import org.gmd.repository.GameRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import java.sql.Timestamp
import java.time.Instant

@Component
open class JdbcGameRepository : GameRepository {

    companion object {
        private const val MODEL_VERSION = 2
        private val ACCOUNT_PATTERN = Regex("\\w+")
    }

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    override fun listGames(account: String, maxElements: Int): List<Pair<Instant, Game>> {
        val tableName = withTableForAccount(account)
        return jdbcTemplate.query("select created_at, content from $tableName order by created_at desc limit ?", 
                arrayOf(maxElements), GameRowMapper())

    }

    override fun listGames(account: String, tournament: String, maxElements: Int): List<Pair<Instant, Game>> {
        val tableName = withTableForAccount(account)
        return jdbcTemplate.query("select created_at, content from $tableName where tournament = ? order by created_at desc limit ?",
                arrayOf(tournament, maxElements), GameRowMapper())
    }

    override fun addGame(account: String, game: Game): Game {
        val tableName = withTableForAccount(account)
        jdbcTemplate.update("INSERT INTO $tableName(tournament, content) VALUES (?,?)", game.tournament, game.toJsonBytes())
        return game
    }

    override fun deleteGame(account: String, tournament: String, createdAt: Instant): Boolean {
        val tableName = withTableForAccount(account)
        val deleted = jdbcTemplate.update("DELETE FROM $tableName where tournament = ? and created_at = ?",
                tournament, Timestamp.from(createdAt))
        return deleted > 0
    }

    override fun listTournaments(account: String): List<String> {
        val tableName = withTableForAccount(account)
        return jdbcTemplate.queryForList("select distinct tournament from $tableName", String::class.java)
    }

    private fun withTableForAccount(account: String): String {
        assert(ACCOUNT_PATTERN.matches(account), { -> "Account $account is not valid" })
        val tableName = "${account}_game_$MODEL_VERSION"
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS $tableName " +
                "(tournament TEXT NOT NULL, created_at TIMESTAMP DEFAULT NOW(), content bytea NOT NULL, PRIMARY KEY (tournament, created_at))")
        return tableName
    }
}