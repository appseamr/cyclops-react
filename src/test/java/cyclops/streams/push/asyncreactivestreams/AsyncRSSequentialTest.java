package cyclops.streams.push.asyncreactivestreams;

import com.aol.cyclops2.streams.BaseSequentialTest;
import cyclops.async.adapters.Topic;
import cyclops.collections.mutable.ListX;
import cyclops.control.Maybe;
import cyclops.stream.ReactiveSeq;
import cyclops.stream.Spouts;
import cyclops.stream.Streamable;
import org.hamcrest.Matchers;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;
import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Thread.sleep;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by johnmcclean on 14/01/2017.
 */
public class AsyncRSSequentialTest extends BaseSequentialTest {

    @Override
    protected <U> ReactiveSeq<U> of(U... array){

        return Spouts.from(Flux.just(array).subscribeOn(Schedulers.fromExecutor(ForkJoinPool.commonPool())));

    }
    @Test
    public void testLimitUntilWithNulls() {


        assertThat(of(1, 2, 3, 4, 5).limitUntil(i -> false).toList(), equalTo(asList(1, 2,3, 4, 5)));
    }
    @Test
    public void flatMapStreamFilter() {
        assertThat(of(1, 2, 3).flatMap(i -> ReactiveSeq.of(i).filter(Objects::nonNull))
                        .collect(Collectors.toList()),
                Matchers.equalTo(Arrays.asList(1, 2, 3)));
    }
    @Test
    public void flatMapStream() {
        for (int i = 0; i < ITERATIONS; i++) {
            assertThat(of(1, 2, 3).flatMap(Stream::of)
                            .collect(Collectors.toList()),
                    Matchers.equalTo(Arrays.asList(1, 2, 3)));
        }
    }
    @Test
    public void flatMapIMaybe() {
        assertThat(of(1, 2, 3).flatMapI(Maybe::ofNullable)
                        .collect(Collectors.toList()),
                Matchers.equalTo(Arrays.asList(1, 2, 3)));
    }
    @Test
    public void flatMapIStream() {
        assertThat(of(1, 2, 3).flatMapI(i -> ReactiveSeq.of(i).filter(Objects::nonNull))
                        .collect(Collectors.toList()),
                Matchers.equalTo(Arrays.asList(1, 2, 3)));
    }
    @Test
    public void spoutsCollect(){
        Integer[] array = new Integer[100];
        for(int i=0;i<array.length;i++) {
            array[i]=i;
        }
        for(int i=0;i<100;i++) {
            List<Integer> list = of(array).collect(Collectors.toList());
            assertThat(list.size(),equalTo(array.length));
        }
    }
    @Test
    public void broadcastTest(){
        Topic<Integer> topic = of(1,2,3)
                .broadcast();


        ReactiveSeq<Integer> stream1 = topic.stream();
        ReactiveSeq<Integer> stream2 = topic.stream();
        assertThat(stream1.toListX(), Matchers.equalTo(ListX.of(1,2,3)));
        assertThat(stream2.stream().toListX(), Matchers.equalTo(ListX.of(1,2,3)));

    }
    @Test
    public void mergePTest(){
        for(int i=0;i<100;i++) {
            System.out.println("*********************ITERATION " + i);
            System.out.println("*********************ITERATION " + i);
            System.out.println("*********************ITERATION " + i);
            System.out.println("*********************ITERATION " + i);
            System.out.println("*********************ITERATION " + i);
            System.out.println("*********************ITERATION " + i + "********************");
            ListX<Integer> list = of(3, 6, 9).mergeP(of(2, 4, 8), of(1, 5, 7)).toListX();

            assertThat("List is " + list,list, hasItems(1, 2, 3, 4, 5, 6, 7, 8, 9));
            assertThat("List is " + list,list.size(), Matchers.equalTo(9));
        }
    }
    @Test
    public void prependPlay(){
        System.out.println(of(1,2,3).prepend(100,200,300).collect(Collectors.toList()));


    }
    @Test
    public void splitAtExp(){
        of(1, 2, 3).peek(e->System.out.println("Peeking! " +e)).splitAt(0).map( (a,b)->{
            a.printOut();
            b.printOut();
            return null;
        });

    }



    @Test
    public void testCycleAsync() {
      //  of(1, 2).collectStream(CyclopsCollectors.listX())
        //        .flatMapI(i->i.cycle(3)).printOut();

       // of(1, 2).cycle().limit(6).forEach(n->System.out.println("Next " + n));


        assertEquals(asList(1, 2, 1, 2, 1, 2),of(1, 2).cycle().limit(6).toList());
        assertEquals(asList(1, 2, 3, 1, 2, 3), of(1, 2, 3).cycle().limit(6).toList());

    }

    @Test
    public void multicast(){
        final Tuple2<ReactiveSeq<Integer>, ReactiveSeq<Integer>> t = of(1,2,3,4,5,6,7,8).duplicate();

//        t.v1.forEach(e->System.out.println("First " + e));
 //       t.v2.printOut();


        assertThat(t.v1.limit(1).toList(),equalTo(ListX.of(1)));
        System.out.println("Second!");
        assertThat(t.v2.limit(1).toList(),equalTo(ListX.of(1)));

    }
    @Test
    public void multicastCycle(){
        final Tuple2<ReactiveSeq<Integer>, ReactiveSeq<Integer>> t = of(1,2,3,4,5,6,7,8).duplicate();

//        t.v1.forEach(e->System.out.println("First " + e));
        //       t.v2.printOut();


        assertThat(t.v1.limit(1).toList(),equalTo(ListX.of(1)));
        System.out.println("Second!");
        assertThat(t.v2.cycle().limit(1).toList(),equalTo(ListX.of(1)));

    }
    @Test
    public void duplicateReplay(){
        final Tuple2<ReactiveSeq<Integer>, ReactiveSeq<Integer>> t = of(1).duplicate();
        assertThat(t.v1.limit(1).toList(),equalTo(ListX.of(1)));
//        assertThat(t.v1.limit(1).toList(),equalTo(ListX.of(1)));
    }
    @Test
    public void limitSkip(){
        ReactiveSeq<Integer> stream = of(1);
        Tuple2<ReactiveSeq<Integer>, ReactiveSeq<Integer>> dup = stream.duplicate();


        assertThat(Streamable.fromStream(dup.v1.limit(1)).toList(),equalTo(ListX.of(1)));
        assertThat(Streamable.fromStream(dup.v2.skip(1)).toList(),equalTo(ListX.of()));

    }@Test
    public void limitSkip2(){
        ReactiveSeq<Integer> stream = of(1);
        Tuple2<ReactiveSeq<Integer>, ReactiveSeq<Integer>> dup = stream.duplicate();
        assertThat(dup.v1.limit(1).toList(),equalTo(ListX.of(1)));
        assertThat(dup.v2.skip(1).toList(),equalTo(ListX.of()));




    }


    @Test
    public void splitAtHeadImpl(){

        final Tuple2<ReactiveSeq<Integer>, ReactiveSeq<Integer>> t = of(1).duplicate();



        Tuple2<ReactiveSeq<Integer>, ReactiveSeq<Integer>> dup = new Tuple2(
                t.v1.limit(1), t.v2.skip(1));
        assertThat(t.v1.limit(1).toList(),equalTo(ListX.of(1)));
        assertThat(t.v2.skip(1).toList(),equalTo(ListX.of()));


    }
    @Test
    public void splitAtHeadImpl2(){

        final Tuple2<ReactiveSeq<Integer>, ReactiveSeq<Integer>> t = of(1).duplicate();



        Tuple2<ReactiveSeq<Integer>, ReactiveSeq<Integer>> dup = new Tuple2(
                t.v1.limit(1), t.v2.skip(1));
        assertThat(t.v1.limit(1).toList(),equalTo(ListX.of(1)));
        assertThat(t.v2.skip(1).toList(),equalTo(ListX.of()));


    }
    @Test
    public void splitLimit(){

        ReactiveSeq<Integer> stream = of(1);
        final Tuple2<ReactiveSeq<Integer>, ReactiveSeq<Integer>> t = stream.duplicate();
        assertThat(stream.limit(1).toList(),equalTo(ListX.of(1)));
        assertThat(t.v1.limit(1).toList(),equalTo(ListX.of(1)));

    }
    @Test
    public void splitLimit2(){
        ReactiveSeq<Integer> stream = of(1);
        final Tuple2<ReactiveSeq<Integer>, ReactiveSeq<Integer>> t = stream.duplicate();
        assertThat(stream.limit(1).toList(),equalTo(ListX.of(1)));

        assertThat(t.v1.limit(1).toList(),equalTo(ListX.of(1)));
    }

    @Test
    public void duplicateDuplicate(){
        for(int k=0;k<ITERATIONS;k++) {
            assertThat(of(1, 2, 3).duplicate()
                    .v1.duplicate().v1.duplicate().v1.toListX(), equalTo(ListX.of(1, 2, 3)));
        }

    }
    @Test
    public void duplicateDuplicateDuplicate(){
        for(int k=0;k<ITERATIONS;k++) {
            assertThat(of(1, 2, 3).duplicate()
                    .v1.duplicate().v1.duplicate().v1.duplicate().v1.toListX(), equalTo(ListX.of(1, 2, 3)));
        }

    }
    @Test
    public void skipDuplicateSkip() {
        assertThat(of(1, 2, 3).duplicate().v1.skip(1).duplicate().v1.skip(1).toListX(), equalTo(ListX.of(3)));
        assertThat(of(1, 2, 3).duplicate().v2.skip(1).duplicate().v2.skip(1).toListX(), equalTo(ListX.of(3)));
    }
    @Test
    public void skipLimitDuplicateLimitSkip() {
        Tuple3<ReactiveSeq<Integer>, ReactiveSeq<Integer>,ReactiveSeq<Integer>> dup = of(1, 2, 3).triplicate();
        Optional<Integer> head1 = dup.v1.limit(1).to().optional().flatMap(l -> {
            return l.size() > 0 ? Optional.of(l.get(0)) : Optional.empty();
        });
        Tuple3<ReactiveSeq<Integer>, ReactiveSeq<Integer>,ReactiveSeq<Integer>> dup2 = dup.v2.skip(1).triplicate();
        Optional<Integer> head2 = dup2.v1.limit(1).to().optional().flatMap(l -> {
            return l.size() > 0 ? Optional.of(l.get(0)) : Optional.empty();
        });
       assertThat(dup2.v2.skip(1).toListX(),equalTo(ListX.of(3)));

        assertThat(of(1, 2, 3).duplicate().v1.skip(1).duplicate().v1.skip(1).toListX(), equalTo(ListX.of(3)));
    }


    @Test
    public void splitThenSplit(){
        assertThat(of(1,2,3).to().optional(),equalTo(Optional.of(ListX.of(1,2,3))));
       // System.out.println(of(1, 2, 3).splitAtHead().v2.listX());
        System.out.println("split " + of(1, 2, 3).splitAtHead().v2.splitAtHead().v2.toListX());
        assertEquals(Optional.of(3), of(1, 2, 3).splitAtHead().v2.splitAtHead().v2.splitAtHead().v1);
    }
    @Test
    public void testSplitAtHead() {

        assertEquals(Optional.empty(), of().splitAtHead().v1);
        assertEquals(asList(), of().splitAtHead().v2.toList());

        assertEquals(Optional.of(1), of(1).splitAtHead().v1);
        assertEquals(asList(), of(1).splitAtHead().v2.toList());

        assertEquals(Optional.of(1), of(1, 2).splitAtHead().v1);
        assertEquals(asList(2), of(1, 2).splitAtHead().v2.toList());

        assertEquals(Optional.of(1), of(1, 2, 3).splitAtHead().v1);
        assertEquals(Optional.of(2), of(1, 2, 3).splitAtHead().v2.splitAtHead().v1);
        assertEquals(Optional.of(3), of(1, 2, 3).splitAtHead().v2.splitAtHead().v2.splitAtHead().v1);
        assertEquals(asList(2, 3), of(1, 2, 3).splitAtHead().v2.toList());
         assertEquals(asList(3), of(1, 2, 3).splitAtHead().v2.splitAtHead().v2.toList());
        assertEquals(asList(), of(1, 2, 3).splitAtHead().v2.splitAtHead().v2.splitAtHead().v2.toList());
    }



    @Test
    public void subscribe() throws InterruptedException {
        List<Integer> result = new ArrayList<>();
        Subscription s= of(1,2,3).forEachSubscribe(i->result.add(i));
        s.request(1l);
        sleep(10);
        assertThat(result.size(), Matchers.equalTo(1));
        s.request(1l);
        sleep(10);

        assertThat(result.size(), Matchers.equalTo(2));
        s.request(1l);
        sleep(10);
        assertThat(result.size(), Matchers.equalTo(3));
        assertThat(result,hasItems(1,2,3));
    }
    @Test
    public void subscribe3() throws InterruptedException {
        List<Integer> result = new ArrayList<>();
        Subscription s= of(1,2,3).forEachSubscribe(i->result.add(i));
        s.request(3l);
        sleep(10);
        assertThat(result.size(), Matchers.equalTo(3));
        assertThat(result,hasItems(1,2,3));
    }
    @Test
    public void subscribeErrorEmpty() throws InterruptedException {
        List result = new ArrayList<>();
        Subscription s= of().forEachSubscribe(i->result.add(i), e->e.printStackTrace());
        s.request(1l);
        sleep(10);
        assertThat(result.size(), Matchers.equalTo(0));
        s.request(1l);
        sleep(10);
        assertThat(result.size(), Matchers.equalTo(0));
        s.request(1l);
        sleep(10);
        assertThat(result.size(), Matchers.equalTo(0));

    }
    @Test
    public void subscribeError() throws InterruptedException {
        List<Integer> result = new ArrayList<>();
        Subscription s= of(1,2,3).forEachSubscribe(i->result.add(i), e->e.printStackTrace());
        s.request(1l);
        sleep(10);
        assertThat(result.size(), Matchers.equalTo(1));
        s.request(1l);
        sleep(10);
        assertThat(result.size(), Matchers.equalTo(2));
        s.request(1l);
        sleep(10);
        assertThat(result.size(), Matchers.equalTo(3));
        assertThat(result,hasItems(1,2,3));
    }
    @Test
    public void subscribe3Error() throws InterruptedException {
        List<Integer> result = new ArrayList<>();
        Subscription s= of(1,2,3).forEachSubscribe(i->result.add(i), e->e.printStackTrace());
        s.request(3l);
        sleep(10);
        assertThat(result.size(), Matchers.equalTo(3));
        assertThat(result,hasItems(1,2,3));
    }
    @Test
    public void subscribeErrorEmptyOnComplete() throws InterruptedException {
        List result = new ArrayList<>();
        AtomicBoolean onComplete = new AtomicBoolean(false);
        Subscription s= of().forEachSubscribe(i->result.add(i), e->e.printStackTrace(),()->onComplete.set(true));
        s.request(1l);
        sleep(10);
        assertThat(onComplete.get(), Matchers.equalTo(true));
        assertThat(result.size(), Matchers.equalTo(0));
        s.request(1l);
        sleep(10);
        assertThat(result.size(), Matchers.equalTo(0));
        s.request(1l);
        sleep(10);
        assertThat(result.size(), Matchers.equalTo(0));

    }
    @Test
    public void subscribeErrorOnComplete() throws InterruptedException {
        List<Integer> result = new ArrayList<>();
        AtomicBoolean onComplete = new AtomicBoolean(false);
        Subscription s= of(1,2,3).forEachSubscribe(i->result.add(i), e->e.printStackTrace(),()->onComplete.set(true));

        assertThat(onComplete.get(), Matchers.equalTo(false));
        s.request(1l);
        sleep(10);
        assertThat(result.size(), Matchers.equalTo(1));
        assertThat(onComplete.get(), Matchers.equalTo(false));
        s.request(1l);
        sleep(10);
        assertThat(result.size(), Matchers.equalTo(2));
        assertThat(onComplete.get(), Matchers.equalTo(false));
        s.request(1l);
        sleep(10);
        assertThat(result.size(), Matchers.equalTo(3));
        assertThat(result,hasItems(1,2,3));
        s.request(1l);
        sleep(10);
        assertThat(onComplete.get(), Matchers.equalTo(true));
    }
    @Test
    public void subscribe3ErrorOnComplete() throws InterruptedException {
        List<Integer> result = new ArrayList<>();
        AtomicBoolean onComplete = new AtomicBoolean(false);
        Subscription s= of(1,2,3).forEachSubscribe(i->result.add(i), e->e.printStackTrace(),()->onComplete.set(true));
        assertThat(onComplete.get(), Matchers.equalTo(false));
        s.request(4l);
        sleep(10);
        assertThat(onComplete.get(), Matchers.equalTo(true));

        assertThat(result.size(), Matchers.equalTo(3));
        assertThat(result,hasItems(1,2,3));

        assertThat(onComplete.get(), Matchers.equalTo(true));
    }

}
