package com.thisara.mypocket.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SavingsBoardGeneratorTest {
    @Test
    fun generatedBoardHasThirtyAllowedAmounts() {
        val board = SavingsBoardGenerator.generate("2026-05", "pocket-a")

        assertEquals(30, board.size)
        assertTrue(board.all { it in SavingsBoardGenerator.allowedAmounts })
    }

    @Test
    fun generatedBoardIsStableForSamePocketAndMonth() {
        val first = SavingsBoardGenerator.generate("2026-05", "pocket-a")
        val second = SavingsBoardGenerator.generate("2026-05", "pocket-a")

        assertEquals(first, second)
    }

    @Test
    fun generatedBoardChangesWhenRoundChanges() {
        val firstRound = SavingsBoardGenerator.generate("2026-05", "pocket-a", round = 0)
        val secondRound = SavingsBoardGenerator.generate("2026-05", "pocket-a", round = 1)

        assertTrue(firstRound != secondRound)
    }

    @Test
    fun generatedBoardChangesWhenMonthChanges() {
        val may = SavingsBoardGenerator.generate("2026-05", "pocket-a")
        val june = SavingsBoardGenerator.generate("2026-06", "pocket-a")

        assertTrue(may != june)
    }

    @Test
    fun dailyOpenAmountIsStableForSameDay() {
        val first = SavingsBoardGenerator.dailyOpenAmount(
            monthKey = "2026-05",
            pocketId = "pocket-a",
            dayKey = "2026-05-17",
            cellIndex = 4,
        )
        val second = SavingsBoardGenerator.dailyOpenAmount(
            monthKey = "2026-05",
            pocketId = "pocket-a",
            dayKey = "2026-05-17",
            cellIndex = 4,
        )

        assertEquals(first, second)
    }

    @Test
    fun dailyOpenAmountsChangeWhenDayChanges() {
        val today = List(30) { index ->
            SavingsBoardGenerator.dailyOpenAmount("2026-05", "pocket-a", "2026-05-17", index)
        }
        val tomorrow = List(30) { index ->
            SavingsBoardGenerator.dailyOpenAmount("2026-05", "pocket-a", "2026-05-18", index)
        }

        assertTrue(today != tomorrow)
    }

    @Test
    fun dailyOpenAmountsAreAllowedAmounts() {
        val amounts = List(60) { index ->
            SavingsBoardGenerator.dailyOpenAmount("2026-05", "pocket-a", "2026-05-17", index)
        }

        assertTrue(amounts.all { it in SavingsBoardGenerator.allowedAmounts })
    }

    @Test
    fun dailyOpenAmountsExcludeSavedCells() {
        val cells = listOf(
            SavingsCell(id = "00", index = 0, amount = 500, saved = true),
            SavingsCell(id = "01", index = 1, amount = 1000, saved = false),
            SavingsCell(id = "02", index = 2, amount = 100, saved = true),
            SavingsCell(id = "03", index = 3, amount = 50, saved = false),
        )

        val dailyAmounts = SavingsBoardGenerator.dailyOpenAmountsForOpenCells(
            cells = cells,
            monthKey = "2026-05",
            pocketId = "pocket-a",
            dayKey = "2026-05-17",
        )

        assertFalse(dailyAmounts.containsKey("00"))
        assertTrue(dailyAmounts.containsKey("01"))
        assertFalse(dailyAmounts.containsKey("02"))
        assertTrue(dailyAmounts.containsKey("03"))
    }

    @Test
    fun targetAmountsCoverRequestedRemainingValue() {
        val amounts = SavingsBoardGenerator.dailyOpenAmountsToCoverTarget(
            monthKey = "2026-05",
            pocketId = "pocket-a",
            dayKey = "2026-05-17",
            targetAmount = 1470,
            startIndex = 0,
            maxCount = 720,
        )

        assertTrue(amounts.sum() >= 1470)
        assertTrue(amounts.all { it in SavingsBoardGenerator.allowedAmounts })
    }

    @Test
    fun targetAmountsAreStableForSameDay() {
        val first = SavingsBoardGenerator.dailyOpenAmountsToCoverTarget(
            monthKey = "2026-05",
            pocketId = "pocket-a",
            dayKey = "2026-05-17",
            targetAmount = 2000,
            startIndex = 4,
            maxCount = 720,
        )
        val second = SavingsBoardGenerator.dailyOpenAmountsToCoverTarget(
            monthKey = "2026-05",
            pocketId = "pocket-a",
            dayKey = "2026-05-17",
            targetAmount = 2000,
            startIndex = 4,
            maxCount = 720,
        )

        assertEquals(first, second)
    }
}
