package coroutines
import kotlinx.coroutines.*
import kotlin.random.Random

/*
 * Вариации задания:

1. Отменять все задания если одно из заданий упало
2. Отменять все задания если одно из заданий выполняется дольше 5000мс
3. Если задание упало - возвращать дефолтовое значение (например 0)
4. Если задание выполняется больше 5000мс - возвращать дефолтовое значение
*/

fun main() = runBlocking{
    println(getCounters().toString())
}

suspend fun getCounters(): List<Int> {
    val timeout = 5000L
    val results = mutableListOf<Int>()
    var res1 = 0
    var res2 = 0
    var res3 = 0

    try {
        coroutineScope {
            val response1 = GlobalScope.async { getCounter(1) }
            val response2 = GlobalScope.async { getCounter(2) }
            val response3 = GlobalScope.async { getCounter(3) }

            try {
                withTimeout(timeout) { res1 = response1.await() }
            } catch (e: TimeoutCancellationException) {
                println("[TimeoutCancellationException] ${e.message}")
                coroutineContext.cancel()
            } catch (e: RuntimeException) {
                coroutineContext.cancel()
                println("[RuntimeException] ${e.message}")
            }

            try {
                withTimeout(timeout) { res2 = response2.await() }
            } catch (e: TimeoutCancellationException) {
                println("[TimeoutCancellationException] ${e.message}")
                coroutineContext.cancel()
            } catch (e: RuntimeException) {
                coroutineContext.cancel()
                println("[RuntimeException] ${e.message}")
            }

            try {
                withTimeout(timeout) { res3 = response3.await() }
            } catch (e: TimeoutCancellationException) {
                println("[TimeoutCancellationException] ${e.message}")
                coroutineContext.cancel()
            } catch (e: RuntimeException) {
                coroutineContext.cancel()
                println("[RuntimeException] ${e.message}")
            }
        }
    } catch (e: CancellationException){
        println("At least one task failed")
    } finally {
        results.add(res1)
        results.add(res2)
        results.add(res3)
    }
    return results
}

suspend fun getCounter(id: Int): Int {
    val duration = Random.nextLong(0, 10000)
    if (duration < 1000) throw RuntimeException("Duration less than 1000ms")
    delay(duration)
    return id * 3
}