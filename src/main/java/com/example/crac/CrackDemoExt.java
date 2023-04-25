/*
 * Copyright 2023-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.crac;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.crac.Context;
import org.crac.Core;
import org.crac.Resource;

public class CrackDemoExt {

    private static final Logger logger = LogManager.getLogger(CrackDemoExt.class);

    public static void main(String[] args) {

        logger.info("CRaC Demo of a processor with dependant context");

        ProcessorState processorState = new ProcessorState();
        ProcessorContext processorContext = new ProcessorContext(processorState);
        Processor processor = new Processor(processorState);

        processorContext.start();

        // Use the processor in the [main] thread to print sequential numbers.
        for (int i = 0; i < 100; i++) {
            sleep(1000); // wait before next processing item.
            processor.next(i);
        }
    }

    /**
     * Processor depends on an initialized {@link #CrackDemoExt(ProcessorState)}.
     */
    public static class Processor {

        private ProcessorState state;

        public Processor(ProcessorState state) {
            this.state = state;
        }

        /**
         * Process next processor item.
         */
        public void next(int i) {
            this.state.useState();
            System.out.print(i + ":");
        }
    }

    /**
     * Sate used by the {@link #CrackDemoExt(Processor)}.
     */
    public static class ProcessorState {

        private boolean isStateReady = false;

        public void useState() {
            if (!this.isStateReady) {
                logger.error("ProcessorState is not initialized yet!");
                throw new RuntimeException("ProcessorState is not initialized yet!");
            }
        }

        public void initialize() {
            this.isStateReady = true;
        }

        public void shutdown() {
            this.isStateReady = false;
        }
    }

    /**
     * Processor's State context manager. Responsible to initialize the {@link #CrackDemoExt(ProcessorState)} before the
     * {@link #CrackDemo(Processor)} can use it. The context also can pause/stop and (re)start the context. The
     * processor state is initialized only after the start() method complete.
     */
    public static class ProcessorContext implements Resource {

        private final ProcessorState processorState;

        public ProcessorContext(ProcessorState processorState) {
            this.processorState = processorState;
            Core.getGlobalContext().register(this);
        }

        public void start() {
            // Emulates context start delay. Note that the start delay is
            // longer than the processing loop iterations.
            sleep(3000);

            this.processorState.initialize();

            logger.info("ProcessorContext STARTED!");
        }

        public void stop() {
            sleep(3000); // emulates context stop delay.

            this.processorState.shutdown();

            logger.info("ProcessorContext STOPPED!");
        }

        @Override
        public void beforeCheckpoint(Context<? extends Resource> context) throws Exception {
            logger.info("call 'beforeCheckpoint' \n");
            stop();
        }

        @Override
        public void afterRestore(Context<? extends Resource> context) throws Exception {
            logger.info("call 'afterRestore' \n");
            start();
        }
    }

    private static void sleep(long time) {
        try {
            Thread.sleep(time);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
