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

public class CrackDemo {

    private static final Logger logger = LogManager.getLogger(CrackDemo.class);

    public static void main(String[] args) {

        logger.info("CRaC Demo of a processor with dependant context");

        ProcessorContext processorContext = new ProcessorContext();
        Processor processor = new Processor(processorContext);

        Core.getGlobalContext().register(new CRaCResource(processorContext));

        processorContext.startContext();

        // Use the processor in the [main] thread to print sequential numbers.
        for (int i = 0; i < 100; i++) {
            sleep(1000); // wait before next processing item.
            processor.next(i);
        }
    }

    /**
     * Processor depends on an initialized {@link #CrackDemo(ProcessorContext)}.
     */
    public static class Processor {

        private ProcessorContext context;

        public Processor(ProcessorContext context) {
            this.context = context;
        }

        /**
         * Process next processor item. An initialized {@link #CrackDemo(ProcessorContext)} is required.
         */
        public void next(int i) {
            if (!this.context.isRunning()) {
                logger.error("ProcessorContext not initialized yet! [i =" + i + "]");
                throw new RuntimeException("ProcessorContext not initialized yet!");
            }

            System.out.print(i + ":");
        }
    }

    /**
     * Processor's context. Must be initialized before the processor can be used.
     */
    public static class ProcessorContext {

        private boolean isInitialized = false;

        public void startContext() {
            // Emulates context start delay. Note that the start delay is
            // longer than the processing loop iterations.
            sleep(3000);
            this.isInitialized = true;
            logger.info("ProcessorContext STARTED!");
        }

        public void stopContext() {
            sleep(3000); // emulates context stop delay.
            this.isInitialized = false;
            logger.info("ProcessorContext STOPPED!");
        }

        public boolean isRunning() {
            return this.isInitialized;
        }
    }

    /**
     * CRaC Resource that gracefully stops the context on checkpoint and restores it on checkpoint restart.
     */
    public static class CRaCResource implements Resource {

        private ProcessorContext context;

        public CRaCResource(ProcessorContext context) {
            this.context = context;
        }

        @Override
        public void beforeCheckpoint(Context<? extends Resource> context) throws Exception {
            logger.info("call 'beforeCheckpoint' \n");
            this.context.stopContext();
        }

        @Override
        public void afterRestore(Context<? extends Resource> context) throws Exception {
            logger.info("call 'afterRestore' \n");
            this.context.startContext();
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
