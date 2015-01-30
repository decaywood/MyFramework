package DecayMongo.disruptor.dsl;

import java.util.concurrent.Executor;

import DecayMongo.disruptor.Sequence;
import DecayMongo.disruptor.SequenceBarrier;

interface ConsumerInfo
{
    Sequence[] getSequences();

    SequenceBarrier getBarrier();

    boolean isEndOfChain();

    void start(Executor executor);

    void halt();

    void markAsUsedInBarrier();

    boolean isRunning();
}
