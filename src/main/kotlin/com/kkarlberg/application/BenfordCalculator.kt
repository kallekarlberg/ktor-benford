package com.kkarlberg.application

import mu.KotlinLogging
import org.apache.commons.math3.stat.inference.ChiSquareTest
import java.util.*
import kotlin.collections.HashMap

private val logger = KotlinLogging.logger {}

class BenfordCalculator {

    // TODO 6. not following naming conventions
    private val BENFORD_PERCENTAGES: DoubleArray =
        doubleArrayOf(30.10, 17.61, 12.49, 9.69, 7.92, 6.69, 5.80, 5.12, 4.58)

    fun processString(inData: String): BenfordSeries {
        if (Objects.isNull(inData)) {
            throw IllegalArgumentException("Indata is null")
        }
        val numbers = getNumbersAsList(inData)
        if (numbers.isEmpty() || numbers.size < 500) {
            throw IllegalArgumentException("Indata has too few values, need at least 500 number to make a sensible calculation")
        }
        logger.info { "Making Benford series of ${numbers.size} numbers" }
        val eachCount = numbers.groupingBy { it.first() }.eachCount().toSortedMap()

        val digitPercentages = getDigitPercentages(eachCount, numbers.size)
        val expectedCounts = getExpectedDigitCounts(numbers.size)
        // TODO 8. ChiSquareTest is instantiated here, it should be a dependency,
        // so your tests could be decoupled from the library,
        // currently you are also testing the lib functionality
        val chiSquareTest = ChiSquareTest().chiSquareTest(expectedCounts, toLongArray(eachCount))
        val result = BenfordSeries(eachCount, digitPercentages, numbers.size, chiSquareTest)
        // TODO 7. redundant curly braces
        logger.info { "Result:  ${result}" }
        return result
    }


    private fun getDigitPercentages(eachCount: Map<Char, Int>, size: Int): Map<Char, Double> {
        return eachCount.entries
            .associate {
                it.key to (100.0 * it.value / size)
            }
    }

    // TODO 4. could you please try to apply immutability here, using stream api,
    // maybe fluent api calls (instead of foreach maybe a map)
    // this method can be replaced by BENFORD_PERCENTAGES.map {}.something
    private fun getExpectedDigitCounts(total: Int): DoubleArray {
        var j = 0
        val result = DoubleArray(9)
        // this way of calculating is not nice
        // the variable you are doing the foreach on, is not used ('it') (you also have 'j')
        BENFORD_PERCENTAGES.forEach {
            result[j] = BENFORD_PERCENTAGES[j] / 100.0 * total
            j++
        }
        return result
    }

    // TODO 5. the method could be simplified (stream, map, fluent api, immutability)
    private fun toLongArray(eachCount: Map<Char, Int>): LongArray {
        val result = LongArray(9)
        var i = 0
        eachCount.values.forEach {
            result[i] = it.toLong()
            i++
        }
        return result
    }

    private fun getNumbersAsList(inData: String): List<String> {
        return Regex("[1-9][0-9]*").findAll(inData)
            .map(MatchResult::value)
            .toList()
    }
}

// TODO 9. to make it more user-friendly,
// it could explain better why the decision was made (
// e.g. ('1', 300) ('2', 180) ... (observed)
//      ('1', 303.28) ('2', 167.3) (expected)
//      like this it is visible that the number are matching
//      chiSquareTest - instead of this you could call it at least p-value,
//      with that name the user would know what that number is (if it knows what a p-value is)
//      format could be changed from floating point to something more readable
// ),
// and what the decision is (from this number 2.220446049250313E-16 it is difficult to understand,
// if the books are cooked or not)
data class BenfordSeries(
    // I think you don't need the default values
    val digitCounters: Map<Char, Int> = HashMap(),
    val digitPercentages: Map<Char, Double> = HashMap(),
    val totalDigits: Int = 0,
    val chiSquareTest: Double = 0.0
)