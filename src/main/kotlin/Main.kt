import java.io.File

fun readLines(): List<String> {
    val l = mutableListOf<String>()
    while (true) {
        val s = readlnOrNull() ?: break
        l.add(s)
    }
    return l
}

fun solve(ops: List<Operation>): Int {
    val checkpoints = mutableListOf<Int>()
    var e = Pair(0, 1)
    fun normalizeClock(c: Int): Int {
        return (c + 20) % 40
    }
    for (op in ops) {
        val (oldClock, oldValue) = e
        e = op.apply(e)
        println(e)
        val newClock = e.first
        if (normalizeClock(oldClock) > normalizeClock(newClock)) {
            checkpoints.add(oldValue)
        }
        if (e.first > 220) {
            println("too many operations")
            break
        }
    }
    println("$checkpoints")
    val points = checkpoints.withIndex().map { (idx, v) -> (20 + 40 * idx) * v }
    println("$points")
    return points.sum()
}

typealias Env = Pair<Int, Int>

sealed interface Operation {
    abstract fun apply(e: Env): Env
}

object Noop : Operation {
    override fun apply(e: Env): Env {
        val (clock, register) = e
        return Pair(clock + 1, register)
    }
}

data class Addx(val v: Int) : Operation {
    override fun apply(e: Env): Env {
        val (clock, register) = e
        return Pair(clock + 2, register + v)
    }
}


@Suppress("UNUSED_PARAMETER")
fun main(_args: Array<String>) {
    fun parse(): List<Operation> {
        val lines = readLines()
        return lines.map {
            if (it == "noop") {
                Noop
            } else {
                val v = it.split(" ")[1].toInt()
                Addx(v)
            }
        }
    }
    println(solve(parse()))
}
