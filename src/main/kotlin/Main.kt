@Suppress("UNUSED_PARAMETER")
fun main(_args: Array<String>) {
    var cs = readln().toCharArray()

    val n = 4
    for (i in 0 until  (cs.size - n + 1) ){
        val targets = cs.drop(i).take(n)
        if (targets.toSet().size == n) {
            println(i + n)
            return
        }
    }
    println("ERROR!")
}
