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
