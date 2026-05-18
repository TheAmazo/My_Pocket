package com.thisara.mypocket.data

import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import kotlin.random.Random

object SavingsBoardGenerator {
    const val DEFAULT_CELL_COUNT = 30
    const val MAX_CELL_COUNT = 720

    private val weightedAmounts = listOf(
        20 to 23,
        50 to 21,
        100 to 25,
        500 to 25,
        1000 to 6,
    )

    private val dailyOpenWeightedAmounts = listOf(
        20 to 34,
        50 to 30,
        100 to 23,
        500 to 10,
        1000 to 3,
    )

    val allowedAmounts: Set<Int> = weightedAmounts.map { it.first }.toSet()

    fun generate(
        monthKey: String,
        pocketId: String,
        round: Int = 0,
        count: Int = DEFAULT_CELL_COUNT,
    ): List<Int> {
        require(count > 0) { "Board must contain at least one cell." }

        val totalWeight = weightedAmounts.sumOf { it.second }
        val random = Random("$monthKey:$pocketId:$round".hashCode())

        return List(count) {
            val pick = random.nextInt(totalWeight)
            var running = 0
            weightedAmounts.first { (_, weight) ->
                running += weight
                pick < running
            }.first
        }
    }

    fun dailyOpenAmount(
        monthKey: String,
        pocketId: String,
        dayKey: String,
        cellIndex: Int,
        round: Int = cellIndex / DEFAULT_CELL_COUNT,
    ): Int {
        require(cellIndex >= 0) { "Cell index must be zero or greater." }

        return weightedPick(
            seed = "$monthKey:$pocketId:$dayKey:$round:$cellIndex",
            candidates = dailyOpenWeightedAmounts,
        )
    }

    fun dailyOpenAmountsToCoverTarget(
        monthKey: String,
        pocketId: String,
        dayKey: String,
        targetAmount: Int,
        startIndex: Int,
        maxCount: Int,
    ): List<Int> {
        require(startIndex >= 0) { "Start index must be zero or greater." }
        if (targetAmount <= 0 || maxCount <= 0) return emptyList()

        val amounts = mutableListOf<Int>()
        var remaining = targetAmount
        while (remaining > 0 && amounts.size < maxCount) {
            val cellIndex = startIndex + amounts.size
            val slotsLeft = maxCount - amounts.size
            val minimumNeeded = (remaining + slotsLeft - 1) / slotsLeft
            val candidates = dailyOpenWeightedAmounts
                .filter { (amount, _) -> amount <= remaining && amount >= minimumNeeded }
                .ifEmpty {
                    dailyOpenWeightedAmounts.filter { (amount, _) -> amount <= remaining }
                }
                .ifEmpty {
                    val smallestLargerAmount = dailyOpenWeightedAmounts
                        .map { it.first }
                        .filter { it > remaining }
                        .minOrNull()
                    dailyOpenWeightedAmounts.filter { (amount, _) -> amount == smallestLargerAmount }
                }
            val amount = weightedPick(
                seed = "$monthKey:$pocketId:$dayKey:target:$cellIndex:$remaining",
                candidates = candidates,
            )
            amounts += amount
            remaining -= amount
        }
        return amounts
    }

    fun dailyOpenAmountsForOpenCells(
        cells: List<SavingsCell>,
        monthKey: String,
        pocketId: String,
        dayKey: String,
    ): Map<String, Int> {
        return cells
            .filterNot { it.saved }
            .associate { cell ->
                cell.id to dailyOpenAmount(
                    monthKey = monthKey,
                    pocketId = pocketId,
                    dayKey = dayKey,
                    cellIndex = cell.index,
                )
            }
    }

    fun currentMonthKey(zoneId: ZoneId = ZoneId.systemDefault()): String {
        return YearMonth.now(zoneId).toString()
    }

    fun todayKey(zoneId: ZoneId = ZoneId.systemDefault()): String {
        return LocalDate.now(zoneId).toString()
    }

    fun currentYear(zoneId: ZoneId = ZoneId.systemDefault()): Int {
        return YearMonth.now(zoneId).year
    }

    fun isBeforeMonthEnd(monthKey: String, zoneId: ZoneId = ZoneId.systemDefault()): Boolean {
        val month = YearMonth.parse(monthKey)
        return LocalDate.now(zoneId).dayOfMonth < month.lengthOfMonth()
    }

    private fun weightedPick(seed: String, candidates: List<Pair<Int, Int>>): Int {
        require(candidates.isNotEmpty()) { "At least one amount candidate is required." }

        val totalWeight = candidates.sumOf { it.second }
        val random = Random(seed.hashCode())
        val pick = random.nextInt(totalWeight)
        var running = 0
        return candidates.first { (_, weight) ->
            running += weight
            pick < running
        }.first
    }
}
