package uk.co.itmoore.intellisubsteps.debugger;

import com.intellij.debugger.PositionManager;
import com.intellij.debugger.PositionManagerFactory;
import com.intellij.debugger.engine.DebugProcess;
import com.intellij.debugger.engine.JSR45PositionManager;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SubstepsPositionManagerFactory extends PositionManagerFactory {

    private static final Logger logger = LogManager.getLogger(SubstepsPositionManagerFactory.class);

    @Nullable
    @Override
    public PositionManager createPositionManager(@NotNull DebugProcess debugProcess) {

        logger.debug("createPositionManager process is attached : " + debugProcess.isAttached());

      //  Scope s = null;
        final String stratumId;
        final LanguageFileType[] acceptedFileTypes = new LanguageFileType[]{JavaFileType.INSTANCE};
       // final SubstepsSourcesFinder<Scope> sourcesFinder;

//        return new JSR45PositionManager<>(debugProcess, s, "stratumId", acceptedFileTypes, new SubstepsSourcesFinder());

        return new SubstepsPositionManager();
    }
}
