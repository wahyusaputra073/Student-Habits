package com.wahyusembiring.habit

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*
import kotlin.coroutines.coroutineContext
import kotlin.system.measureTimeMillis

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun myTest() {
        runBlocking {
            val time = measureTimeMillis {
                val delay3 = async {
                    delay(3000L)
                    3
                }
                val delay5 = async {
                    delay(5000L)
                    5
                }
                val delay10 = async {
                    delay(10000L)
                    10
                }
                val delay2 = async {
                    delay(2000L)
                    2
                }
                val delay1 = async {
                    delay(1000L)
                    1
                }
                val a = delay3.await()
                val b = delay10.await()
                val c = delay3.await()
                println(a + b + c)
            }
            println("Total time: $time")
        }
    }
}