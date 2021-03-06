package cyclops.collections.standard.anyM;

import cyclops.collections.AbstractAnyMSeqTest;
import cyclops.monads.Witness;
import cyclops.monads.Witness.set;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.Test;

import cyclops.monads.AnyM;
import cyclops.collections.mutable.SetX;
import com.aol.cyclops2.types.anyM.AnyMSeq;

import java.util.List;

import static java.util.Comparator.comparing;
import static org.hamcrest.Matchers.equalTo;
import static org.jooq.lambda.tuple.Tuple.tuple;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class SetXTest extends AbstractAnyMSeqTest<set> {

	@Override
	public <T> AnyMSeq<set,T> of(T... values) {
		return AnyM.fromSet(SetX.of(values));
	}

    @Test
    public void testSorted() {


        AnyMSeq<set,Tuple2<Integer, Integer>> t1 = of(tuple(2, 2), tuple(1, 1));

        List<Tuple2<Integer, Integer>> s1 = t1.sorted().toListX().sorted();
        assertEquals(tuple(1, 1), s1.get(0));
        assertEquals(tuple(2, 2), s1.get(1));

        AnyMSeq<set,Tuple2<Integer, String>> t2 = of(tuple(2, "two"), tuple(1, "replaceWith"));
        List<Tuple2<Integer, String>> s2 = t2.sorted(comparing(t -> t.v1())).toListX().sorted();
        assertEquals(tuple(1, "replaceWith"), s2.get(0));
        assertEquals(tuple(2, "two"), s2.get(1));

        AnyMSeq<set,Tuple2<Integer, String>> t3 = of(tuple(2, "two"), tuple(1, "replaceWith"));
        List<Tuple2<Integer, String>> s3 = t3.sorted(t -> t.v1()).toListX().sorted();
        assertEquals(tuple(1, "replaceWith"), s3.get(0));
        assertEquals(tuple(2, "two"), s3.get(1));
    }
	@Test
	public void testRecover1(){
		assertThat(of(1,2,3).map(e->{throw new RuntimeException();}).recover(e->"hello").join(" "),equalTo("hello"));
	}
	@Test
	public void testRecover2(){
		assertThat(of(1,2,3).map(e->{throw new RuntimeException();}).recover(RuntimeException.class,e->"hello").join(" "),equalTo("hello"));
	}
	/* (non-Javadoc)
	 * @see com.aol.cyclops2.function.collections.extensions.AbstractCollectionXTest#zero()
	 */
	@Override
	public <T> AnyMSeq<set,T> empty() {
		return AnyM.fromSet(SetX.empty());
	}
	 /* (non-Javadoc)
     * @see com.aol.cyclops2.function.collections.extensions.AbstractAnyMSeqTest#whenGreaterThan2()
     */
    @Override
    @Test
    public void whenGreaterThan2() {
       
    }

    @Test
    public void testRetry(){
        of(1,2,3).retry(i->i+2).printOut();
    }
}
