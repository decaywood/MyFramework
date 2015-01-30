package DecayMongo.disruptor.dsl;

import DecayMongo.disruptor.EventProcessor;
import DecayMongo.disruptor.RingBuffer;
import DecayMongo.disruptor.Sequence;

/**
 * A factory interface to make it possible to include custom event processors in a chain:
 *
 * <pre><code>
 * disruptor.handleEventsWith(handler1).then((ringBuffer, barrierSequences) -&gt; new CustomEventProcessor(ringBuffer, barrierSequences));
 * </code></pre>
 */
public interface EventProcessorFactory<T>
{
    /**
     * Create a new event processor that gates on <code>barrierSequences</code>.
     *
     * @param barrierSequences the sequences to gate on
     * @return a new EventProcessor that gates on <code>barrierSequences</code> before processing events
     */
    EventProcessor createEventProcessor(RingBuffer<T> ringBuffer, Sequence[] barrierSequences);
}
