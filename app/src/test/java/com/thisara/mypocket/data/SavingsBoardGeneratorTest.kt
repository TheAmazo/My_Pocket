package com.thisara.mypocket.data

import org.junit.Assert.assertEquals
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
}
