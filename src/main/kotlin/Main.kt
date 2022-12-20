import java.time.Duration
import java.time.Instant

@Suppress("UNUSED_PARAMETER")
fun main(_args: Array<String>) {
    val startAt = Instant.now()
    Day17.exec()
    val endAt = Instant.now()
    println("took ${Duration.between(startAt, endAt).toMillis() / 1000.0} seconds")
}

