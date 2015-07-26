package uk.co.itmoore.intellisubsteps.psi;

import com.intellij.lang.ASTNode;
import com.intellij.pom.PomTarget;
import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.itmoore.intellisubsteps.AbstractStepDefinition;
import uk.co.itmoore.intellisubsteps.SubstepsBundle;
import uk.co.itmoore.intellisubsteps.psi.*;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author yole
 */
public interface SubstepsStep extends SubstepsPsiElement, SubstepsSuppressionHolder, PomTarget, PsiNamedElement {
  SubstepsStep[] EMPTY_ARRAY = new SubstepsStep[0];
  /**
   * Message to display if step can't be renamed. (to be used as result of {@link #isRenameAllowed(String)} with null argument)
   *
   * @see #isRenameAllowed(String)
   */
  String RENAME_DISABLED_MESSAGE = SubstepsBundle.message("cucumber.refactor.rename.disabled");

  /**
   * Message to display if step can't be renamed due to bad symbols. (to be used as result of {@link #isRenameAllowed(String)} with name argument)
   *
   * @see #isRenameAllowed(String)
   */
  String RENAME_BAD_SYMBOLS_MESSAGE = SubstepsBundle.message("cucumber.refactor.rename.bad_symbols");

  ASTNode getKeyword();

  String getStepName();

  @Nullable
  SubstepsTable getTable();

//  @Nullable
//  SubstepsPystring getPystring();

  SubstepsStepsHolder getStepHolder();

  /**
   * @return List with not empty unique possible substitutions names
   */
  List<String> getParamsSubstitutions();

  @Nullable
  String getSubstitutedName();

  @NotNull
  Set<String> getSubstitutedNameList();



  @NotNull
  Collection<AbstractStepDefinition> findDefinitions();


  /**
   * Checks if step can be renamed (actually, all definitions are asked).
   *
   * Show {@link #RENAME_DISABLED_MESSAGE} or {@link #RENAME_BAD_SYMBOLS_MESSAGE}
   *
   * @param newName new name (to check if renaming to it is supported) or null to check if step could be renamed at all.
   *                StepsHolder with out of defintiions can't be renamed as well.
   * @return true it could be
   * @see #RENAME_BAD_SYMBOLS_MESSAGE
   * @see #RENAME_DISABLED_MESSAGE
   */
  boolean isRenameAllowed(@Nullable String newName);
}
