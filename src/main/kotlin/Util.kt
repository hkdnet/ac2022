fun readLines(): List<String> {
    val l = mutableListOf<String>()
    while (true) {
        val s = readlnOrNull() ?: break
        l.add(s)
    }
    return l
}


fun modinv(a: ULong, m: ULong): ULong {
    var a = a
    var b = m
    var u = 1UL
    var v = 0UL

    while (b != 0UL) {
        val t = a / b;
        a -= t * b
        var tmp = a
        a = b
        b = tmp
        u -= t * v
        tmp = u
        u = v
        v = tmp
    }
    u %= m
    if (u < 0UL) u += m;
    return u
}

fun gcd(a: Int, b: Int): Int {
    if (a < b) {
        return gcd(b, a)
    }
    var x = a
    var y = b
    var r = x % y;
    while (r != 0) {
        x = y;
        y = r;
        r = x % y;
    }
    return y
}

fun lcm(a: Int, b: Int): Int {
    return a * b / gcd(a, b)
}

data class Vec2(val x: Int, val y: Int) {
    operator fun plus(o: Vec2): Vec2 {
        return Vec2(x + o.x, y + o.y)
    }

    operator fun times(o: Int): Vec2 {
        return Vec2(x * o, y * o)
    }
}
