/*
 * Copyright 2016 riddles.io (developers@riddles.io)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 *     For the full copyright and license information, please view the LICENSE
 *     file that was distributed with this source code.
 */

package com.aryaha.tictactoe.logic;

import com.aryaha.core.game.commands.ICommand;
import com.aryaha.core.game.logic.ILogicHandler;
import com.aryaha.core.game.player.AbstractPlayer;
import com.aryaha.tictactoe.commands.PlaceCommand;
import com.aryaha.tictactoe.commands.RoundUpdateCommand;
import com.aryaha.tictactoe.board.Board;
import com.aryaha.tictactoe.messages.PlaceMessage;
import com.aryaha.tictactoe.messages.RoundUpdateMessage;
import com.aryaha.tictactoe.moves.Move;
import com.aryaha.tictactoe.player.Player;

import java.util.ArrayList;
import java.util.List;

public class TicTacToeLogic implements ILogicHandler {

    private int mMoveNumber = 1;
    private List<Player> mPlayers;
    private List<Move> mMoves;
    private Board mBoard;
    private int mGameOverByPlayerErrorPlayerId = 0;

    public TicTacToeLogic(List<Player> players, Board board) {
        mPlayers = players;
        mBoard = board;
        mMoves = new ArrayList<>();
    }

    @Override
    public void playRound(int roundNumber) {
        for (Player player : mPlayers) {
            sleep(200);

            if (isGameOver()) {
                return;
            }
            playRoundForPlayer(player, roundNumber);
            mMoveNumber++;
        }
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
        }
    }

    private void playRoundForPlayer(Player player, int roundNumber) {
        sendUpdateToPlayer(player, roundNumber);

        ICommand command = player.requestMove();
        processPlayerMove(player, command);
    }

    private void processPlayerMove(Player player, ICommand command) {
        if (command instanceof PlaceCommand) {
            doPlayerCommand((PlaceCommand) command, player);
        }
    }

    private void sendUpdateToPlayer(Player player, int roundNumber) {
        RoundUpdateMessage roundUpdateMsg = createRoundUpdateMsg(mBoard, mMoveNumber, roundNumber);
        player.sendCommand(new RoundUpdateCommand(roundUpdateMsg));
    }

    private RoundUpdateMessage createRoundUpdateMsg(Board board, int moveNumber, int roundNumber) {
        RoundUpdateMessage msg = new RoundUpdateMessage();
        msg.setRoundNumber(roundNumber);
        msg.setMoveNumber(moveNumber);
        msg.setBoard(board);
        return msg;
    }

    private void doPlayerCommand(PlaceCommand command, Player player) {
        try {
            PlaceMessage placeMessage = (PlaceMessage) command.getMessage();
            mBoard.placeMark(placeMessage.toLocation(), player.getId());
        } catch (Exception e) {
            player.getBot().outputEngineWarning(e.getMessage());
        }
    }

    @Override
    public AbstractPlayer getWinner() {
        int winner = mBoard.getWinnerPlayerId();
        if (mGameOverByPlayerErrorPlayerId > 0) { /* Game over due to too many player errors. Look up the other player, which became the winner */
            for (Player player : mPlayers) {
                if (player.getId() != mGameOverByPlayerErrorPlayerId) {
                    return player;
                }
            }
        }
        if (winner != Board.EMPTY_CELL) {
            for (Player player : mPlayers) {
                if (player.getId() == winner) {
                    return player;
                }
            }
        }
        return null;
    }

    @Override
    public String getPlayedGame() {
        return "";
    }

    @Override
    public boolean isGameOver() {
        return (!mBoard.hasPossibleMove() || getWinner() != null);
    }

    public Board getBoard() {
        return mBoard;
    }
}