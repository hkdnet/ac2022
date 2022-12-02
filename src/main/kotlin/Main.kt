enum class Hand {
    ROCK, PAPER, SCISSORS
}
enum class Result {
    WIN, DRAW, LOSE
}

val Normalized = mapOf(
    "A" to Hand.ROCK,
    "B" to Hand.PAPER,
    "C" to Hand.SCISSORS,
    "X" to Hand.ROCK,
    "Y" to Hand.PAPER,
    "Z" to Hand.SCISSORS
)
val Scores = mapOf(Hand.ROCK to 1, Hand.PAPER to 2, Hand.SCISSORS to 3);

fun vs(op: Hand, me: Hand): Result {
    if (op == me) {
        return Result.DRAW
    }
    if (
        op == Hand.ROCK && me == Hand.PAPER ||
        op == Hand.PAPER && me == Hand.SCISSORS ||
        op == Hand.SCISSORS && me == Hand.ROCK
    ) {
        return Result.WIN
    }
    return Result.LOSE
}

@Suppress("UNUSED_PARAMETER")
fun main(_args: Array<String>) {
    val rounds = mutableListOf<Pair<Hand, Hand>>()
    while (true) {
        val line = readLine()
        if (line == null) {
            break;
        }
        val arr = line.split(" ")
        val h1 = Normalized.getValue(arr[0])
        val h2 = Normalized.getValue(arr[1])
        rounds.add(Pair(h1, h2))
    }
    val points = rounds.map { (opponent, mine) ->
        val result = vs(opponent, mine)
        when (result) {
            Result.DRAW -> 3 + Scores.getValue(mine)
            Result.WIN -> 6 + Scores.getValue(mine)
            Result.LOSE -> 0 + Scores.getValue(mine)
        }
    }

    println(points.sum())
}
