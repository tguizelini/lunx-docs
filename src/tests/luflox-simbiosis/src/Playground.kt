import java.math.BigDecimal

fun main() {
    val sample = listOf<RawTxn>(
        RawTxn("t1", "m1", BigDecimal("100.00")),
        RawTxn("t2", "m1", BigDecimal("50.00")),
        RawTxn("t3", "m2", BigDecimal("200.00")),
        RawTxn("", "m3", BigDecimal("10.00")), // return Result.Error - id is empty - Reason.BAD_ID
        RawTxn("t5", null, BigDecimal("15.00")), // return Result.Error - merchantId is null - Reason.BAD_MERCHANT
        RawTxn("t6", "m4", BigDecimal("-5.00")) // // return Result.Error - amount <= 0 - Reason.BAD_AMOUNT
    )

    val result = process(sample)
    println("result: $result")
}

fun process(txns: List<RawTxn>): List<Result> {
    val list = mutableListOf<Result>()

    txns.forEach { it ->
        var hasError = false
        if (it.id.isEmpty()) {
            hasError = true
            list.add(Result.Error(id = it.id, reason = Reason.BAD_ID))
        }
        if (it.merchantId == null) {
            hasError = true
            list.add(Result.Error(id = it.id, reason = Reason.BAD_MERCHANT))
        }
        if (it.amount == null) {
            hasError = true
            list.add(Result.Error(id = it.id, reason = Reason.BAD_AMOUNT))
        }
        if (it.amount != null && it.amount <= BigDecimal("0.00")) {
            hasError = true
            list.add(Result.Error(id = it.id, reason = Reason.BAD_AMOUNT))
        }

        if (!hasError) {
            val previousMerchantSummary = list
                .filterIsInstance<Result.Summary>()
                .firstOrNull() { previous -> previous.merchantId == it.merchantId }

            if (previousMerchantSummary !== null) {
                list.remove(previousMerchantSummary)

                list.add(Result.Summary(
                    merchantId = it.merchantId!!,
                    total = (previousMerchantSummary as Result.Summary).total + it.amount!!,
                    count = (previousMerchantSummary as Result.Summary).count + 1
                ))
            } else {
                list.add(
                    Result.Summary(
                        merchantId = it.merchantId!!,
                        total = it.amount!!,
                        count = 1
                    )
                )
            }
        }
    }

    val listSummary = list
        .filterIsInstance<Result.Summary>()
        .sortedByDescending { (it as Result.Summary).total }

    val listErrors = list
        .filterIsInstance<Result.Error>()

    return (listSummary + listErrors)
}
/*
/ Kotlin Live Coding Challenge
// ------------------------------------------------------
// EXERCISE 1 / Transaction Processing
// ------------------------------------------------------
/*
Implement the function below that validates, groups, and summarizes
a list of transactions.

VALIDATION RULES:
 */
    - id is blank. Reason.BAD_ID
    - merchantId is null or blank. Reason.BAD_MERCHANT
    - amount is null or ≤ 0. Reason.BAD_AMOUNT

VALID TXN RULES:
    1. Group by merchantId
    2. Produce a Summary(total = BigDecimal sum, count = number of items)
    3. Sort summaries by total descending

FINAL OUTPUT ORDER:
    1. Summaries first (sorted)
    2. Errors after (any order)
*/

data class RawTxn(
    val id: String,
    val merchantId: String?,
    val amount: BigDecimal?
)

sealed class Result {
    data class Summary(
        val merchantId: String,
        val total: BigDecimal,
        val count: Int
    ) : Result()

    data class Error(
        val id: String,
        val reason: Reason
    ) : Result()
}

enum class Reason { BAD_ID, BAD_MERCHANT, BAD_AMOUNT }
