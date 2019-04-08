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

/*
Отрефакторил. Вопрос: что лучше использовать в данном случае - withContext(Dispatchers.Default) или просто coroutineScope?
 */
suspend fun getCounters(): List<Int> {
    val timeout = 5000L
    val results = mutableListOf<Int>()

    try {
        withContext(Dispatchers.Default) {
            for (id in 1..3) {
                launch {
                    val response = async { getCounter(id) }
                    results.add(
                        try {
                            println("in thread ${Thread.currentThread().name}") // For debug
                            withTimeout(timeout) { response.await() }
                        } catch (e: TimeoutCancellationException) {
                            println("[TimeoutCancellationException in $id] ${e.message}")
                            coroutineContext.cancel()
                            0
                        } catch (e: RuntimeException) {
                            coroutineContext.cancel()
                            println("[RuntimeException in $id] ${e.message}")
                            0
                        }
                    )
                }
            }
        }
    } catch (e: CancellationException){
        println("At least one task failed")
    } finally {
        return results
    }
}


suspend fun getCounter(id: Int): Int {
    val duration = Random.nextLong(0, 10000)
    if (duration < 1000) throw RuntimeException("Duration less than 1000ms")
    delay(duration)
    return id * 3
}
