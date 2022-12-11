fun readLines(): List<String> {
    val l = mutableListOf<String>()
    while (true) {
        val s = readlnOrNull() ?: break
        l.add(s)
    }
    return l
}

fun solve(ops: List<Operation>): Int {
    val xs = mutableListOf(1)
    var x = 1
    for (op in ops) {
        when (op) {
            Noop -> {
                xs.add(x)
            }

            is Addx -> {
                xs.add(x)
                x += op.v
                xs.add(x)
            }
        }
    }
    fun show() {
        for (i in 0 until 240) {
            val middle = xs[i]
            if (i % 40 in middle - 1..middle + 1) {
                print("#")
            } else {
                print(".")
            }
            if (i % 40 == 39) {
                print("\n")
            }
        }
    }

    show()

    return 1
}

typealias Env = Pair<Int, Int>

sealed interface Operation {
    fun apply(e: Env): Env
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
