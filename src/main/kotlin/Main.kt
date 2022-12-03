fun intersect(s1: String, s2: String, s3: String): Char {
    val cs1 = s1.toCharArray().toSet()
    val cs2 = s2.toCharArray().toSet()
    val cs3 = s3.toCharArray().toSet()
    for (c in cs1) {
        if (cs2.contains(c) && cs3.contains(c)) {
            return c
        }
    }
    1 / 0 // unreachable!!!
    return '-'
}

fun toPoint(c: Char): Int {
    return if (c >= 'a') {
            c.code - 'a'.code + 1
        } else {
            c.code - 'A'.code + 27
        }
}

@Suppress("UNUSED_PARAMETER")
fun main(_args: Array<String>) {
    val l = mutableListOf<Char>()
    while (true) {
        val s1 = readLine();
        if (s1 == null) {
            break;
        }
        val s2 = readLine();
        if (s2 == null) {
            break;
        }
        val s3 = readLine();
        if (s3 == null) {
            break;
        }
        val c = intersect(s1, s2, s3)
        l.add(c)
    }
    val points = l.map { toPoint(it) }

    println(points.sum())
}
