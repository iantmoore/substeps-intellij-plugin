package uk.co.itmoore.intellisubsteps.psi.feature;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by ian on 04/07/15.
 */
public class SubstepFeatureLexer extends LexerBase {
    @Override
    public void start(CharSequence charSequence, int i, int i1, int i2) {


    }

    @Override
    public int getState() {
        return 0;
    }

    @Nullable
    @Override
    public IElementType getTokenType() {


        return null;
    }

    @Override
    public int getTokenStart() {
        return 0;
    }

    @Override
    public int getTokenEnd() {
        return 0;
    }

    @Override
    public void advance() {

    }

    @NotNull
    @Override
    public CharSequence getBufferSequence() {

        return "feature who knows?";


    }

    @Override
    public int getBufferEnd() {
        return 0;
    }
}
