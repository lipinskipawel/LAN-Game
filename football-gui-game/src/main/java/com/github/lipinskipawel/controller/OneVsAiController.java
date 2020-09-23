package com.github.lipinskipawel.controller;

import com.github.lipinskipawel.BruteForceThinking;
import com.github.lipinskipawel.board.ai.bruteforce.MiniMaxAlphaBeta;
import com.github.lipinskipawel.board.ai.bruteforce.SmartBoardEvaluator;
import com.github.lipinskipawel.board.engine.Boards;
import com.github.lipinskipawel.board.engine.Player;
import com.github.lipinskipawel.gui.GameDrawer;
import com.github.lipinskipawel.gui.Table;
import kotlin.Unit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.github.lipinskipawel.board.engine.Player.FIRST;
import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

final class OneVsAiController {

    private GameFlowController gameFlowController;
    private final Table table;

    private AtomicBoolean canHumanMove;
    private BruteForceThinking bruteForce;
    private final Logger logger = LoggerFactory.getLogger(OneVsAiController.class);

    public OneVsAiController(final Table table) {
        this.table = table;
        this.gameFlowController = new GameFlowController();
        this.canHumanMove = new AtomicBoolean(true);
    }

    void onClick(final MouseEvent e, final Object src) {
        if (isRightMouseButton(e)) {

            if (this.canHumanMove.get()) {
                this.gameFlowController = this.gameFlowController.undoOnlyCurrentPlayerMove();
//                this.gameFlowController = this.gameFlowController.undoPlayerMove(
//                        () -> {
//                            if (this.bruteForce != null) {
//                                this.bruteForce.cancel(true);
//                            }
//                            this.bruteForce = null;
//                            this.canHumanMove = new AtomicBoolean(false);
//                            return null;
//                        }
//                );
                this.table.drawBoard(this.gameFlowController.board(), FIRST);
            }
            // if you decided to implement undo when ai thinks, watch out on
            // this.canHumanMove
            // this.bruteForce -- managing the state by operations on null

            // use this.bruteForce.cancel(true);

        } else if (isLeftMouseButton(e)) {
            logger.info("canHumanMove : " + this.canHumanMove + ", player : " + this.gameFlowController.player());

            if (this.canHumanMove.get()) {
                // here it is save to get move from worker and update board and draw it one more time
                try {
                    if (bruteForce != null) {
                        var aiMove = bruteForce.get();
                        this.gameFlowController = this.gameFlowController.makeAMove(aiMove);
                        this.gameFlowController.onPlayerHitTheCorner(() -> {
                            this.table.drawBoard(this.gameFlowController.board(), this.gameFlowController.player().opposite());
                            JOptionPane.showMessageDialog(null, "You won the game!!!");
                            this.canHumanMove.set(false);
                            final var dataObject = new QuestionService(new InMemoryQuestions()).displayAiQuestion();
                            new HerokuService().send(dataObject);
                            return null;
                        });
                        this.table.drawBoard(this.gameFlowController.board(), this.gameFlowController.player());
                        this.bruteForce = null;
                        logger.info("redundant update board");
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                }

                GameDrawer.PointTracker pointTracker = (GameDrawer.PointTracker) e.getSource();

                try {
                    this.gameFlowController = this.gameFlowController.makeAMove(
                            pointTracker.getPosition(),
                            () -> {
                                this.canHumanMove.set(false);
                                return null;
                            }
                    );
                    this.table.drawBoard(this.gameFlowController.board(), FIRST);
                    this.gameFlowController.onWinner(this::winningMessage);

                } catch (CantMakeAMove ee) {
                    return;
                }
            } else {
                JOptionPane.showMessageDialog(null, "There is AI to move");
            }
        }
        if (!canHumanMove.get()) {
            // need to check that null because ai can think long, and the human could click and trigger another thead
            if (this.bruteForce == null) {
                this.bruteForce = new BruteForceThinking(
                        new MiniMaxAlphaBeta(new SmartBoardEvaluator()),
                        this.gameFlowController.board(),
                        3,
                        this.table.gameDrawer(),
                        this.canHumanMove
                );
                bruteForce.execute();
            }
        }
    }

    private Unit winningMessage(final Player winner) {
        JOptionPane.showMessageDialog(null, "Player " + winner + " won he game.");
        return null;
    }

    void reset() {
        this.gameFlowController = new GameFlowController(Boards.immutableBoard(), false);
        this.table.drawBoard(this.gameFlowController.board(), this.gameFlowController.player());
        this.table.activePlayer(this.gameFlowController.player());
        this.canHumanMove = new AtomicBoolean(true);
        if (this.bruteForce != null) {
            this.bruteForce.cancel(true);
            this.bruteForce = null;
        }
    }
}
