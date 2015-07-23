package uk.co.itmoore.intellisubsteps.psi;

import com.intellij.lang.Commenter;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

/**
 * Created by ian on 23/07/15.
 */
public class SubstepsCommenter implements Commenter {
    @NonNls
    private static final String LINE_COMMENT_PREFIX = "#";

    @Nullable
    public String getLineCommentPrefix() {
        return LINE_COMMENT_PREFIX;
    }

    @Nullable
    public String getBlockCommentPrefix() {
        // N/A
        return null;
    }

    @Nullable
    public String getBlockCommentSuffix() {
        // N/A
        return null;
    }

    public String getCommentedBlockCommentPrefix() {
        return null;
    }

    public String getCommentedBlockCommentSuffix() {
        return null;
    }
}
