package uk.co.itmoore.intellisubsteps.psi;

import com.intellij.lexer.LexerBase;
import com.intellij.util.ArrayUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import uk.co.itmoore.intellisubsteps.psi.feature.FeatureLexer;

/**
 * Created by ian on 16/01/17.
 */
public abstract class AbstractSubstepsLexer extends LexerBase {

    private static final Logger log = LogManager.getLogger(AbstractSubstepsLexer.class);


    protected int myStartOffset = 0;
    protected int myEndOffset = 0;
    protected int myPosition;
    protected CharSequence myBuffer = ArrayUtil.EMPTY_CHAR_SEQUENCE;
    protected String bufString = null;


    protected abstract void setStartState(int initialState);

    @Override
    public void start(CharSequence buffer, int startOffset, int endOffset, int initialState) {

        // initially called with 0, end and 0
        bufString = buffer.toString();

        String sample = buffer.toString().substring(startOffset);//, buffer.length() > 40 ? startOffset + 40 : buffer.length());

        if (sample.length() > 40){
            sample = sample.substring(0, 40);
        }

        log.trace("start buffer: " +
                sample + " ....startOffset: " +
                startOffset + " endOffset: " + endOffset + " initialState: " + initialState);

        myBuffer = buffer;
        myStartOffset = startOffset;
        myEndOffset = endOffset;
        myPosition = startOffset;

        setStartState(initialState);

        advance();
    }

    protected void advanceToEOL() {
        log.trace("advanceToEOL");
        myPosition++;
        int mark = myPosition;

        boolean inQuotes = false;
        boolean commentFound = false;
        while (myPosition < myEndOffset && myPosition < myBuffer.length() && myBuffer.charAt(myPosition) != '\n') {
            char c = myBuffer.charAt(myPosition);
            if (c == '\'' || c == '"') {
                if (inQuotes) {
                    inQuotes = false;
                }
                else {
                    inQuotes = true;
                }
            }
            if (c == '#' && !inQuotes){
                commentFound = true;
                break;
            }

            myPosition++;
        }

        if (!commentFound) {
            returnWhitespace(mark);
        }
    }

    protected void returnWhitespace(int mark) {
        while(myPosition > mark && Character.isWhitespace(myBuffer.charAt(myPosition - 1))) {

            log.trace("returnWhitespace re-winding");
            myPosition--;
        }
    }

}
