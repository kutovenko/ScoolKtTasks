package coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/*
Практическое задание 2:

Написать актор, который будет принимать сообщения-лямбды вида: `(T) -> T`
и менять текущее состояние, актора выполняя лямбду над текущим состоянием
и используя результат как новое состояние
 */

@ObsoleteCoroutinesApi
fun main() = runBlocking<Unit> {

    val counter = counterActor()

    val job = launch {
        counter.send { x -> x + 2 }
        counter.send { x -> x - 2 }
    }

    job.join()
    counter.close()
}

typealias Message<T> = (T) -> T

@ObsoleteCoroutinesApi
fun CoroutineScope.counterActor() = actor<Message<Int>> {
    var state = 0
    for (message in channel) {
        state = message(state)
        println(state)
    }
}