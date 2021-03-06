package cyclops.collections.standard;

import org.jooq.lambda.Collectable;

import cyclops.collections.mutable.ListX;
import com.aol.cyclops2.react.lazy.sequence.CollectableTest;

public class ListXCollectableTest extends CollectableTest {

    @Override
    public <T> Collectable<T> of(T... values) {
       return  ListX.of(values).collectors();
    }

}
