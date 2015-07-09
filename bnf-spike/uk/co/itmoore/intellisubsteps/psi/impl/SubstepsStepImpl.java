package uk.co.itmoore.intellisubsteps.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiCheckedRenameElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.TokenType;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Function;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.HashSet;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import uk.co.itmoore.intellisubsteps.AbstractStepDefinition;
import uk.co.itmoore.intellisubsteps.SubstepsChangeUtil;
import uk.co.itmoore.intellisubsteps.psi.*;

import uk.co.itmoore.intellisubsteps.psi.SubstepsElementTypes;
import uk.co.itmoore.intellisubsteps.psi.SubstepsTokenTypes;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SubstepsStepImpl extends SubstepsPsiElementBase implements  PsiCheckedRenameElement {

  private static final TokenSet TEXT_FILTER = TokenSet
    .create(SubstepsTokenTypes.TEXT, SubstepsElementTypes.STEP_PARAMETER, TokenType.WHITE_SPACE, SubstepsTokenTypes.STEP_PARAMETER_TEXT,
            SubstepsTokenTypes.STEP_PARAMETER_BRACE);

  private static final Pattern PARAMETER_SUBSTITUTION_PATTERN = Pattern.compile("<([^>\n\r]+)>");
  private final Object LOCK = new Object();

  private List<String> mySubstitutions;

  public SubstepsStepImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public String toString() {
    return "SubstepsStep:" + getStepName();
  }

  @Nullable
  public ASTNode getKeyword() {
    return getNode().findChildByType(SubstepsTokenTypes.STEP_KEYWORD);
  }

  @Nullable
  public String getStepName() {
    return getElementText();
  }

  @Override
  @NotNull
  protected String getElementText() {
    final ASTNode node = getNode();
    final ASTNode[] children = node.getChildren(TEXT_FILTER);
    return StringUtil.join(children, new Function<ASTNode, String>() {
      public String fun(ASTNode astNode) {
        return astNode.getText();
      }
    }, "").trim();
  }

//  @Nullable
//  public SubstepsPystring getPystring() {
//    return PsiTreeUtil.findChildOfType(this, SubstepsPystring.class);
//  }

  @Nullable
  public SubstepsTableImpl getTable() {
    final ASTNode tableNode = getNode().findChildByType(SubstepsElementTypes.TABLE);
    return tableNode == null ? null : (SubstepsTableImpl)tableNode.getPsi();
  }

  @Override
  protected String getPresentableText() {
    final ASTNode keywordNode = getKeyword();
    final String prefix = keywordNode != null ? keywordNode.getText() + ": " : "Step: ";
    return prefix + getStepName();
  }

  @NotNull
  @Override
  public PsiReference[] getReferences() {
    return ReferenceProvidersRegistry.getReferencesFromProviders(this, SubstepsStepImpl.class);
  }

  protected void acceptSubsteps(SubstepsElementVisitor SubstepsElementVisitor) {
    SubstepsElementVisitor.visitStep(this);
  }

  @NotNull
  public List<String> getParamsSubstitutions() {
    synchronized (LOCK) {
      if (mySubstitutions == null) {
        final ArrayList<String> substitutions = new ArrayList<String>();


        // step name
        final String text = getStepName();
        if (StringUtil.isEmpty(text)) {
          return Collections.emptyList();
        }
        addSubstitutionFromText(text, substitutions);

        // pystring
//        final SubstepsPystring pystring = getPystring();
//        String pystringText = pystring != null ? pystring.getText() : null;
//        if (!StringUtil.isEmpty(pystringText)) {
//          addSubstitutionFromText(pystringText, substitutions);
//        }

        // table
        final SubstepsTableImpl table = getTable();
        final String tableText = table == null ? null : table.getText();
        if (tableText != null) {
          addSubstitutionFromText(tableText, substitutions);
        }

        mySubstitutions = substitutions.isEmpty() ? Collections.<String>emptyList() : substitutions;
      }
      return mySubstitutions;
    }
  }

  private static void addSubstitutionFromText(String text, ArrayList<String> substitutions) {
    final Matcher matcher = PARAMETER_SUBSTITUTION_PATTERN.matcher(text);
    boolean result = matcher.find();
    if (!result) {
      return;
    }

    do {
      final String substitution = matcher.group(1);
      if (!StringUtil.isEmpty(substitution) && !substitutions.contains(substitution)) {
        substitutions.add(substitution);
      }
      result = matcher.find();
    }
    while (result);
  }

  @Override
  public void subtreeChanged() {
    super.subtreeChanged();
    clearCaches();
  }

  @Nullable
  public SubstepsStepsHolder getStepHolder() {
    final PsiElement parent = getParent();
    return parent != null ? (SubstepsStepsHolder)parent : null;
  }

  private void clearCaches() {
    synchronized (LOCK) {
      mySubstitutions = null;
    }
  }

  @Nullable
  public String getSubstitutedName() {
    List<String> sustitutedNameList = new ArrayList<String>(getSubstitutedNameList(1));
    return sustitutedNameList.size() > 0 ? sustitutedNameList.get(0) : getStepName();
  }

  public Set<String> getSubstitutedNameList(int maxCount) {
    final Set<String> result = new HashSet<String>();
    final SubstepsStepsHolder holder = getStepHolder();
    final String stepName = getStepName();
    if (stepName != null) {
      if (holder instanceof SubstepsScenarioOutlineImpl) {
        final SubstepsScenarioOutlineImpl outline = (SubstepsScenarioOutlineImpl)holder;
        final List<SubstepsExamplesBlock> examplesBlocks = outline.getExamplesBlocks();
        for (SubstepsExamplesBlock examplesBlock : examplesBlocks) {
          final SubstepsTable table = examplesBlock.getTable();
          if (table != null) {
            final List<SubstepsTableRow> rows = table.getDataRows();
            for (SubstepsTableRow row : rows) {
              result.add(substituteText(stepName, table.getHeaderRow(), row));
              if (result.size() == maxCount) {
                return result;
              }
            }
          }
        }
      }
    }
    if (result.size() == 0 && stepName != null) {
      result.add(stepName);
    }
    return result;
  }

  @NotNull
  public Set<String> getSubstitutedNameList() {
    return getSubstitutedNameList(Integer.MAX_VALUE);
  }

  private static String substituteText(String stepName, SubstepsTableRow headerRow,
                                       SubstepsTableRow row) {
    final List<SubstepsTableCell> headerCells = headerRow.getPsiCells();
    final List<SubstepsTableCell> dataCells = row.getPsiCells();
    for (int i = 0, headerCellsNumber = headerCells.size(), dataCellsNumber = dataCells.size();
         i < headerCellsNumber && i < dataCellsNumber;
         i++) {
      final String cellText = headerCells.get(i).getText().trim();
      stepName = stepName.replace("<" + cellText + ">", dataCells.get(i).getText().trim());
    }
    return stepName;
  }

  @Override
  public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
    SubstepsStep newStep = SubstepsChangeUtil.createStep(getKeyword().getText() + " " + name, getProject());
    replace(newStep);
    return newStep;
  }

  @Override
  public String getName() {
    final ASTNode keyword = getKeyword();
    final int keywordLength = keyword != null ? keyword.getTextLength() : 0;
    return getText().substring(keywordLength).trim();
  }


  @NotNull
  //@Override
  public Collection<AbstractStepDefinition> findDefinitions() {

    return Collections.emptyList();
//    final List<AbstractStepDefinition> result = new ArrayList<AbstractStepDefinition>();
//    for (final PsiReference reference : getReferences()) {
//      if (reference instanceof CucumberStepReference) {
//        result.addAll(((CucumberStepReference)reference).resolveToDefinitions());
//      }
//    }
//    return result;
  }


 // @Override
  public boolean isRenameAllowed(@Nullable final String newName) {
    final Collection<AbstractStepDefinition> definitions = findDefinitions();
    if (definitions.isEmpty()) {
      return false; // No sense to rename step with out of definitions
    }
    for (final AbstractStepDefinition definition : definitions) {
      if (!definition.supportsRename(newName)) {
        return false; //At least one definition does not support renaming
      }
    }
    return true; // Nothing prevents us from renaming
  }

  @Override
  public void checkSetName(final String name) {
    if (!isRenameAllowed(name)) {
      throw new IncorrectOperationException("RENAME_BAD_SYMBOLS_MESSAGE");
    }
  }
}
