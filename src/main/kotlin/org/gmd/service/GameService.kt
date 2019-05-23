package org.gmd.service

import org.gmd.Algorithm
import org.gmd.model.Game
import org.gmd.model.MemberMetrics
import org.gmd.model.Metric
import org.gmd.model.Score


interface GameService {

    fun listGames(account: String): List<Game>

    fun addGame(account: String, game: Game): Game

    fun computeTournamentMemberScores(account: String, tournament: String, alg: Algorithm = Algorithm.SUM): List<Score>

    fun computeTournamentMemberMetrics(account: String, tournament: String): List<MemberMetrics>
}