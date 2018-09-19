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

package com.theaigames.core.game;

import com.theaigames.core.game.logic.LogicHandler;
import com.theaigames.core.Engine;
import com.theaigames.core.io.IOPlayer;

/**
 * abstract class AbstractGame
 * <p>
 * DO NOT EDIT THIS FILE
 * <p>
 * Extend this class with your main method. In the main method, create an
 * instance of your IGame and run setupEngine() and run()
 *
 * @author Jim van Eeden <jim@starapple.nl>
 */

public abstract class AbstractGame implements IGame {

    protected Engine engine;
    protected LogicHandler processor;

    private int maxRounds;

    public AbstractGame() {
        maxRounds = -1; // set this later if there is a maximum amount of rounds for this game
        engine = new Engine();
    }

    /**
     * Set logic in the engine and start it to run the game
     */
    public void run() throws Exception {
        engine.setGame(this);
        engine.start();
    }

    /**
     * @return : True when the game is over
     */
    @Override
    public boolean isGameOver() {
        return this.processor.isGameOver()
                || (this.maxRounds >= 0 && this.processor.getRoundNumber() > this.maxRounds);
    }

    /**
     * Play one round of the game
     *
     * @param roundNumber : round number
     */
    @Override
    public void playRound(int roundNumber) {
        for (IOPlayer ioPlayer : this.engine.getPlayers())
            ioPlayer.addToDump(String.format("Round %d", roundNumber));

        this.processor.playRound(roundNumber);
    }

    /**
     * close the bot processes, save, exit program
     */
    @Override
    public void finish() throws Exception {
        // stop the bots
        this.engine.getPlayers().forEach(IOPlayer::finish);
        Thread.sleep(100);

        try {
            this.saveGame();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Done.");

        System.exit(0);
    }

    /**
     * Does everything that is needed to store the output of a game
     */
    public void saveGame() {
        System.out.println("winner: " + this.processor.getWinner().getName());

        // save results to file here
        String playedGame = this.processor.getPlayedGame();
        System.out.println(playedGame);
    }
}