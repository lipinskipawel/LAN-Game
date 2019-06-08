package io.lipinski.player.ai;

import io.lipinski.board.Direction;
import io.lipinski.board.engine.BoardInterface2;
import io.lipinski.board.engine.ImmutableBoard;
import io.lipinski.board.engine.Player;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Testing brute force - minimax")
class MiniMaxTest {

    private MoveStrategy bruteForce;
    private BoardInterface2 board;


    @BeforeEach
    void setUp() {
        this.bruteForce = new MiniMax(new DummyBoardEvaluator());
        this.board = new ImmutableBoard();
    }

    @Nested
    @DisplayName("Dummy evaluator")
    class DummyEvaluator {

        @Test
        @DisplayName("Should score the goal at 1 deep level")
        void scoreAGoal() {

            //Given:
            final var after4Moves = board
                    .executeMove(Direction.N)
                    .executeMove(Direction.N)
                    .executeMove(Direction.N)
                    .executeMove(Direction.N);

            //When:
            final var bestMove = bruteForce.execute(after4Moves, 1);
            final var afterAiMove = after4Moves.executeMove(bestMove);

            //Then:
            assertAll(
                    () -> Assertions.assertTrue(afterAiMove.isGoal()),
                    () -> Assertions.assertSame(Player.FIRST, afterAiMove.getPlayer())
            );
        }
    }
}