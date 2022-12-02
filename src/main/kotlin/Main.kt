enum class Hand {
    ROCK, PAPER, SCISSORS
}
enum class Result {
    WIN, DRAW, LOSE
}

val NormalizedHand = mapOf(
    "A" to Hand.ROCK,
    "B" to Hand.PAPER,
    "C" to Hand.SCISSORS,
)
val NormalizedResult = mapOf(
    "X" to Result.LOSE,
    "Y" to Result.DRAW,
    "Z" to Result.WIN,
)
val Scores = mapOf(Hand.ROCK to 1, Hand.PAPER to 2, Hand.SCISSORS to 3);

val handTable = mapOf(
    Hand.ROCK to mapOf(
        Result.DRAW to Hand.ROCK,
        Result.WIN  to Hand.PAPER,
        Result.LOSE to Hand.SCISSORS,
    ),
    Hand.PAPER to mapOf(
        Result.DRAW to Hand.PAPER,
        Result.WIN  to Hand.SCISSORS,
        Result.LOSE to Hand.ROCK,
    ),
    Hand.SCISSORS to mapOf(
        Result.DRAW to Hand.SCISSORS,
        Result.WIN  to Hand.ROCK,
        Result.LOSE to Hand.PAPER,
    ),
)
fun myHand(op: Hand, res: Result): Hand {
    return handTable.getValue(op).getValue(res)
}

@Suppress("UNUSED_PARAMETER")
fun main(_args: Array<String>) {
    val rounds = mutableListOf<Pair<Hand, Result>>()
    while (true) {
        val line = readLine()
        if (line == null) {
            break;
        }
        val arr = line.split(" ")
        val h = NormalizedHand.getValue(arr[0])
        val r = NormalizedResult.getValue(arr[1])
        rounds.add(Pair(h, r))
    }
    val points = rounds.map { (opponent, result) ->
        val mine = myHand(opponent, result)
        when (result) {
            Result.DRAW -> 3 + Scores.getValue(mine)
            Result.WIN -> 6 + Scores.getValue(mine)
            Result.LOSE -> 0 + Scores.getValue(mine)
        }
    }

    println(points.sum())
}
