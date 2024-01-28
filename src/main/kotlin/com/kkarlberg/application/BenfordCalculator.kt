package com.kkarlberg.application

import mu.KotlinLogging
import org.apache.commons.math3.stat.inference.ChiSquareTest
import java.util.*
import kotlin.collections.HashMap

private val logger = KotlinLogging.logger {}

private val BENFORD_PERCENTAGES: Map<Char,Double> = mapOf(
    '1' to 30.10, '2' to 17.61, '3' to 12.49, '4' to 9.69,
    '5' to 7.92, '6' to 6.69, '7' to 5.80, '8' to 5.12, '9' to 4.58)

class BenfordCalculator(private val chiSquareTester: ChiSquareTest) {

    fun processString(inData: String): BenfordSeries {
        if ( Objects.isNull(inData) ) {
            throw IllegalArgumentException("In data is null")
        }
        val numbers = getNumbersAsList(inData)
        if ( numbers.isEmpty() || numbers.size < 500 ) {
            throw IllegalArgumentException("In data has too few values, need at least 500 number to make a sensible calculation")
        }
        logger.info { "Making Benford series of ${numbers.size} numbers" }
        val eachCount = numbers.groupingBy { it.first() }.eachCount().toSortedMap()

        val digitPercentages = getDigitPercentages(eachCount, numbers.size)
        val expectedCounts = getExpectedDigitCountsMap(numbers.size)
        val pValue = chiSquareTester.chiSquareTest(expectedCounts.values.toDoubleArray(), toLongArray(eachCount))
        val result = BenfordSeries(eachCount, expectedCounts, digitPercentages, BENFORD_PERCENTAGES, numbers.size, pValue.toBigDecimal().toPlainString(), pValue<0.05)
        logger.info { "Result:  $result" }
        return result
    }


    private fun getDigitPercentages(eachCount: Map<Char, Int>, size: Int): Map<Char, Double> {
        return eachCount.entries
            .associate {
                it.key to (100.0*it.value/size)
            }
    }

    private fun getExpectedDigitCountsMap(total: Int): Map<Char, Double> {
        return BENFORD_PERCENTAGES.mapValues { it.value/100.0*total }
    }

    private fun toLongArray(eachCount: Map<Char, Int>): LongArray {
        //annoying that I missed all the lovely "convert" things like "toLongArray"
        return eachCount.values.map { it.toLong() }.toLongArray()
    }

    private fun getNumbersAsList(inData: String): List<String> {
        return Regex("[1-9][0-9]*").findAll(inData)
            .map(MatchResult::value)
            .toList()
    }
}

data class BenfordSeries(
    val observedCountsByStartingDigit: Map<Char,Int> = HashMap(),
    val expectedCountsByStartingDigit: Map<Char,Double> = HashMap(),
    val digitPercentages: Map<Char,Double> = HashMap(),
    val bendfordPercentages : Map<Char,Double> = HashMap(), //cannot figure out how (and if) one should / could declare constants in data-classes
    //seems silly to declare the constant on line 10 just to pass it here (and I want it here since I have both expected and observed counts,
    // I want the same for the percentage numbers)
    val totalNumbers: Int = 0,
    val pValue: String = "0.0",
    val isStatisticallySignificant : Boolean
)