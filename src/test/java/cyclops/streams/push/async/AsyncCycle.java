package cyclops.streams.push.async;

import cyclops.collections.mutable.ListX;
import cyclops.stream.ReactiveSeq;
import cyclops.stream.Spouts;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by johnmcclean on 02/02/2017.
 */
public class AsyncCycle {

    protected <U> ReactiveSeq<U> of(U... array){
        int[] index = {0};
        return Spouts.async(s->{
            new Thread(()-> {

                for (U next : array) {
                    s.onNext(next);
                    if(index[0]++>100)
                        break;
                }
                s.onComplete();
            }).start();

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
    public void multicastCycle(){
        final Tuple2<ReactiveSeq<Integer>, ReactiveSeq<Integer>> t = of(1,2,3,4,5,6,7,8).duplicate();

//        t.v1.forEach(e->System.out.println("First " + e));
        //       t.v2.printOut();


        assertThat(t.v1.limit(1).toList(),equalTo(ListX.of(1)));
        System.out.println("Second!");
        assertThat(t.v2.cycle().limit(1).toList(),equalTo(ListX.of(1)));

    }
}
