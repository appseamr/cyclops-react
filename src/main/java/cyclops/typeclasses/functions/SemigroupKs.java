package cyclops.typeclasses.functions;

import com.aol.cyclops2.data.collections.extensions.CollectionX;
import com.aol.cyclops2.data.collections.extensions.FluentCollectionX;
import com.aol.cyclops2.hkt.Higher;
import com.aol.cyclops2.types.Zippable;
import com.aol.cyclops2.types.futurestream.EagerFutureStreamFunctions;
import com.aol.cyclops2.types.futurestream.SimpleReactStream;
import cyclops.async.Future;
import cyclops.collections.immutable.*;
import cyclops.collections.mutable.*;
import cyclops.companion.CompletableFutures;
import cyclops.companion.Optionals;
import cyclops.companion.Optionals.OptionalKind;
import cyclops.companion.Semigroups;
import cyclops.companion.Streams;
import cyclops.control.Ior;
import cyclops.control.Maybe;
import cyclops.control.Try;
import cyclops.control.Xor;
import cyclops.function.Semigroup;
import cyclops.monads.Witness.*;
import cyclops.monads.Witness.list;
import cyclops.monads.Witness.optional;
import cyclops.stream.FutureStream;
import cyclops.stream.ReactiveSeq;
import cyclops.stream.Spouts;
import org.jooq.lambda.Seq;
import org.pcollections.PCollection;
import org.reactivestreams.Publisher;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.stream.Stream;


public class SemigroupKs{



    public static <T>  SemigroupK<optional,T> optionalPresent() {
        return (a, b) -> OptionalKind.narrowK(a).isPresent() ? a : b;
    }
    public static <T> SemigroupK<list,T> listXConcat() {
        return (a, b) -> ListX.narrowK(a).plusAll(ListX.narrowK(b));
    }




    /**
     * @return A combiner for SetX (concatenates two SetX into a singleUnsafe SetX)
     */
    static <T> SemigroupK<set,T> setXConcat() {
        return (a, b) -> SetX.narrowK(a).plusAll(SetX.narrowK(b));
    }

    /**
     * @return A combiner for SortedSetX (concatenates two SortedSetX into a singleUnsafe SortedSetX)

    static <T> SemigroupK<sortedSet,T> sortedSetXConcat() {
        return (a, b) -> SortedSetX.narrowK(a).plusAll(SortedSetX.narrowK(b));
    }*/

    /**
     * @return A combiner for QueueX (concatenates two QueueX into a singleUnsafe QueueX)
     */
    static <T> SemigroupK<queue,T> queueXConcat() {
        return (a, b) -> QueueX.narrowK(a).plusAll(QueueX.narrowK(b));
    }

    /**
     * @return A combiner for DequeX (concatenates two DequeX into a singleUnsafe DequeX)
     */
    static <T> SemigroupK<deque,T> dequeXConcat() {
        return (a, b) -> DequeX.narrowK(a).plusAll(DequeX.narrowK(b));
    }

    /**
     * @return A combiner for LinkedListX (concatenates two LinkedListX into a singleUnsafe LinkedListX)
     */
    static <T> SemigroupK<linkedListX,T> linkedListXConcat() {
        return (a, b) -> LinkedListX.narrowK(a).plusAll(LinkedListX.narrowK(b));
    }

    /**
     * @return A combiner for VectorX (concatenates two VectorX into a singleUnsafe VectorX)
     */
    static <T> SemigroupK<vectorX,T> vectorXConcat() {
        return (a, b) -> VectorX.narrowK(a).plusAll(VectorX.narrowK(b));
    }

    /**
     * @return A combiner for PersistentSetX (concatenates two PersistentSetX into a singleUnsafe PersistentSetX)

    static <T> SemigroupK<persistentSetX,T> persistentSetXConcat() {
        return (a, b) -> PersistentSetX.narrowK(a).plusAll(PersistentSetX.narrowK(b));
    }
     */
    /**
     * @return A combiner for OrderedSetX (concatenates two OrderedSetX into a singleUnsafe OrderedSetX)

    static <T> SemigroupK<OrderedsetX,T> orderedSetXConcat() {
        return (a, b) -> OrderedSetX.narrowK(a).plusAll(OrderedSetX.narrowK(b));
    }*/

    /**
     * @return A combiner for PersistentQueueX (concatenates two PersistentQueueX into a singleUnsafe PersistentQueueX)
     */
    static <T> SemigroupK<persistentQueueX,T> persistentQueueXConcat() {
        return (a, b) -> PersistentQueueX.narrowK(a).plusAll(PersistentQueueX.narrowK(b));
    }






    /**
     * @return Combination of two ReactiveSeq Streams b is appended to a
     */
    static <T> SemigroupK<reactiveSeq,T> combineReactiveSeq() {
        return (a, b) -> ReactiveSeq.narrowK(a).appendS(ReactiveSeq.narrowK(b));
    }

    static <T> SemigroupK<reactiveSeq,T> firstNonEmptyReactiveSeq() {
        return (a, b) -> ReactiveSeq.narrowK(a).onEmptySwitch(()->ReactiveSeq.narrowK(b));
    }
    static <T> SemigroupK<reactiveSeq,T> ambReactiveSeq() {
        return (a,b)->Spouts.amb(ReactiveSeq.narrowK(a),ReactiveSeq.narrowK(b));
    }

    static <T> SemigroupK<reactiveSeq,T> mergeLatestReactiveSeq() {
        return (a,b) -> Spouts.mergeLatest(ReactiveSeq.narrowK(a),ReactiveSeq.narrowK(b));
    }
    


    /**
     * @return Combination of two Stream's : b is appended to a
     */
    static <T> SemigroupK<stream,T> combineStream() {
        return (a, b) ->  Streams.StreamKind.widen(Stream.concat(Streams.StreamKind.narrow(a), Streams.StreamKind.narrow(b)));
    }


    /**
     * @return Combine two CompletableFuture's by taking the first present
     */
    static <T> SemigroupK<completableFuture,T> firstCompleteCompletableFuture() {
        return (a, b) -> {
            CompletableFuture x = CompletableFuture.anyOf(CompletableFutures.CompletableFutureKind.<T>narrowK(a), CompletableFutures.CompletableFutureKind.<T>narrowK(b));
            return CompletableFutures.CompletableFutureKind.widen(x);
        };
    }
    /**
     * @return Combine two Future's by taking the first result
     */
    static <T> SemigroupK<future,T> firstCompleteFuture() {
        return (a, b) -> Future.anyOf(Future.narrowK(a),Future.narrowK(b));
    }


    /**
     * @return Combine two Future's by taking the first successful
     */
    static <T> SemigroupK<future,T> firstSuccessfulFuture() {
        return (a, b) -> Future.firstSuccess(Future.narrowK(a),Future.narrowK(b));
    }
    /**
     * @return Combine two Xor's by taking the first primary
     */
    static <ST,PT> SemigroupK<Higher<xor,ST>,PT> firstPrimaryXor() {
        return  (a, b) -> Xor.narrowK(a).isPrimary() ? a : b;
    }
    /**
     * @return Combine two Xor's by taking the first secondary
     */
    static <ST,PT> SemigroupK<Higher<xor,ST>,PT> firstSecondaryXor() {
        return  (a, b) -> Xor.narrowK(a).isSecondary() ? a : b;
    }
    /**
     * @return Combine two Xor's by taking the last primary
     */
    static <ST,PT> SemigroupK<Higher<xor,ST>,PT> lastPrimaryXor() {
        return  (a, b) -> Xor.narrowK(b).isPrimary() ? b : a;
    }
    /**
     * @return Combine two Xor's by taking the last secondary
     */
    static <ST,PT> SemigroupK<Higher<xor,ST>,PT> lastSecondaryXor() {
        return  (a, b) -> Xor.narrowK(b).isSecondary() ? b : a;
    }
    /**
     * @return Combine two Try's by taking the first primary
     */
    static <T,X extends Throwable> SemigroupK<Higher<tryType,X>,T> firstTrySuccess() {
        return  (a, b) -> Try.narrowK(a).isSuccess() ? a : b;
    }
    /**
     * @return Combine two Try's by taking the first secondary
     */
    static <T,X extends Throwable> SemigroupK<Higher<tryType,X>,T> firstTryFailure() {
        return  (a, b) -> Try.narrowK(a).isFailure() ? a : b;
    }
    /**
     * @return Combine two Tryr's by taking the last primary
     */
    static<T,X extends Throwable> SemigroupK<Higher<tryType,X>,T> lastTrySuccess() {
        return  (a, b) -> Try.narrowK(b).isSuccess() ? b : a;
    }
    /**
     * @return Combine two Try's by taking the last secondary
     */
    static <T,X extends Throwable> SemigroupK<Higher<tryType,X>,T> lastTryFailure() {
        return  (a, b) -> Try.narrowK(b).isFailure() ? b : a;
    }
    /**
     * @return Combine two Ior's by taking the first primary
     */
    static <ST,PT> SemigroupK<Higher<ior,ST>,PT> firstPrimaryIor() {
        return  (a, b) -> Ior.narrowK(a).isPrimary() ? a : b;
    }
    /**
     * @return Combine two Ior's by taking the first secondary
     */
    static <ST,PT> SemigroupK<Higher<ior,ST>,PT> firstSecondaryIor() {
        return  (a, b) -> Ior.narrowK(a).isSecondary() ? a : b;
    }
    /**
     * @return Combine two Ior's by taking the last primary
     */
    static <ST,PT> SemigroupK<Higher<ior,ST>,PT> lastPrimaryIor() {
        return  (a, b) -> Ior.narrowK(b).isPrimary() ? b : a;
    }
    /**
     * @return Combine two Ior's by taking the last secondary
     */
    static <ST,PT> SemigroupK<Higher<ior,ST>,PT> lastSecondaryIor() {
        return  (a, b) -> Ior.narrowK(b).isSecondary() ? b : a;
    }

    /**
     * @return Combine two Maybe's by taking the first present
     */
    static <T> SemigroupK<maybe,T> firstPresentMaybe() {
        return (a, b) -> Maybe.narrowK(a).isPresent() ? a : b;
    }

    /**
     * @return Combine two optionals by taking the first present
     */
    static <T> SemigroupK<optional,T> firstPresentOptional() {
        return (a, b) -> OptionalKind.narrowK(a).isPresent() ? a : b;
    }

    /**
     * @return Combine two Maybes by taking the last present
     */
    static <T> SemigroupK<maybe,T> lastPresentMaybe() {
        return (a, b) -> Maybe.narrowK(b).isPresent() ? b : a;
    }

    /**
     * @return Combine two optionals by taking the last present
     */
    static <T> SemigroupK<optional,T> lastPresentOptional() {
        return (a, b) -> OptionalKind.narrowK(b).isPresent() ? b : a;
    }
}
