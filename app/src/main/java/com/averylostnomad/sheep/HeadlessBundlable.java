package com.averylostnomad.sheep;

import com.watabou.utils.Bundle;

public interface HeadlessBundlable {

    void restoreFromBundle( HeadlessBundle bundle );
    void storeInBundle( HeadlessBundle bundle );

}
