package com.example.riccardo.lomux;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by Stefano on 08/10/2017.
 */
public interface ThreadCompleteListener {
    void notifyOfThreadComplete(final Thread thread);
}
