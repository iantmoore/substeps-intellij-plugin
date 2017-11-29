package uk.co.itmoore.intellisubsteps.debugger;

import com.intellij.psi.PsiFile;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class JavaDebugAware extends com.intellij.debugger.engine.JavaDebugAware{

    private static final Logger logger = LogManager.getLogger(JavaDebugAware.class);

    @Override
    public boolean isBreakpointAware(@NotNull PsiFile psiFile) {

        logger.debug("isBreakpointAware: " + psiFile.getName());
        return true;
    }
}
