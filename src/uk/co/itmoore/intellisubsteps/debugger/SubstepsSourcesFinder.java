package uk.co.itmoore.intellisubsteps.debugger;

import com.intellij.debugger.engine.SourcesFinder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class SubstepsSourcesFinder<Scope> implements SourcesFinder<Scope>{
    private static final Logger log = LogManager.getLogger(SubstepsSourcesFinder.class);


    @Nullable
    @Override
    public PsiFile findSourceFile(String relPath, Project project, Scope scope) {

        log.debug("findSourceFile: " + relPath);

        return null;
    }
}
