typealias WorkRange = Pair<Int, Int>

fun fullCover(r1: WorkRange, r2: WorkRange): Boolean {
    val (a1, a2) = r1
    val (b1, b2) = r2
    return (a1 <= b1 && b1 <= a2 && a1 <= b2 && b2 <= a2) ||
            (b1 <= a1 && a1 <= b2 && b1 <= a2 && a2 <= b2)
}

@Suppress("UNUSED_PARAMETER")
fun main(_args: Array<String>) {
    val assignments = mutableListOf<Pair<WorkRange, WorkRange>>()
    while (true) {
        val s = readLine();
        if (s == null) {
            break;
        }
        val a = s.split(",").map { it.split("-").map(String::toInt) }
        assignments.add(
            Pair(
                Pair(a[0][0], a[0][1]),
                Pair(a[1][0], a[1][1]),
            )
        )
    }
    val points = assignments.map { (r1, r2) ->
        if (fullCover(r1, r2)) {
            1
        } else {
            0
        }
    }

    println(points.sum())
}
