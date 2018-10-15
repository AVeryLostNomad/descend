package com.watabou.pixeldungeon.utils

import com.averylostnomad.sheep.HeadlessBundlable
import com.watabou.utils.Bundlable
import java.util.*

class BundleUtils {

    companion object {
        fun <E>castInto(bundle : Collection<Bundlable>) : Collection<E> {
            val toReturn = LinkedList<E>()
            for(b in bundle){
                toReturn.add(b as E)
            }
            return toReturn
        }
        fun <E>castIntoHeadless(bundle : Collection<HeadlessBundlable>) : Collection<E> {
            val toReturn = LinkedList<E>()
            for(b in bundle){
                toReturn.add(b as E)
            }
            return toReturn
        }
    }

}