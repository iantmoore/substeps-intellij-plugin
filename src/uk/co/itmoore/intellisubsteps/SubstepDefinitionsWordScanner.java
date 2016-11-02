package uk.co.itmoore.intellisubsteps;

import com.intellij.lang.cacheBuilder.WordOccurrence;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.util.Processor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Created by ian on 30/10/16.
 */
public class SubstepDefinitionsWordScanner implements WordsScanner {

    private static final Logger log = LogManager.getLogger(SubstepDefinitionsWordScanner.class);

    @Override
    public void processWords(CharSequence fileText, Processor<WordOccurrence> processor) {

        // fileText is the substep def file

        log.debug("scanner processing words processor class: " + processor.getClass() + " fileText: " + fileText);


    }
}
