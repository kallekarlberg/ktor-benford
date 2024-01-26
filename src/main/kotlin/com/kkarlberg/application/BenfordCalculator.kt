package com.kkarlberg.application

import mu.KotlinLogging
import org.apache.commons.math3.stat.inference.ChiSquareTest

private val logger = KotlinLogging.logger {}

class BenfordCalculator {

    val BENFORD_PERCENTAGES: DoubleArray = doubleArrayOf(30.10, 17.61, 12.49, 9.69, 7.92, 6.69, 5.80, 5.12, 4.58)

    @Throws(Exception::class)
    fun processString(inData: String): BenfordSeries {
        val numbers = getNumbersAsList(inData)
        logger.info { "Making Benford series of ${numbers.size} numbers" }
        val eachCount = numbers.groupingBy { it.first() }.eachCount()

        val digitPercentages = getDigitPercentages(eachCount, numbers.size)
        val expectedCounts = getExpectedDigitCounts(eachCount)
        val chiSquareTest = ChiSquareTest().chiSquareTest(expectedCounts, toLongArray(eachCount))
        return BenfordSeries(eachCount, digitPercentages, numbers.size, chiSquareTest )
    }

    private fun getDigitPercentages(eachCount: Map<Char, Int>, size: Int): Map<Char, Double> {
        return eachCount.entries.associate { it.key to (100.0*it.value/size) }
    }

    private fun getExpectedDigitCounts(eachCount: Map<Char, Int>): DoubleArray {
        var j = 0;
        val result = DoubleArray(9);
        eachCount.forEach {
            result[j] = it.value * BENFORD_PERCENTAGES[it.key.digitToInt()-1];
            j++
        }
        return result;
    }

    private fun toLongArray(eachCount: Map<Char, Int>): LongArray {
        val result = LongArray(9);
        var i =0;
        eachCount.values.forEach {
            result[i] = it.toLong()
            i++;
        }
        return result;
    }

    private fun getNumbersAsList(inData: String): List<String> {
        return Regex("[1-9][0-9]*").findAll(inData)
            .map(MatchResult::value)
            .toList()
    }
}

data class BenfordSeries(
    val digitCounters: Map<Char,Int> = HashMap(),
    val digitPercentages: Map<Char,Double> = HashMap(),
    val totalDigits: Int = 0,
    val chiSquareTest: Double = 0.0
)