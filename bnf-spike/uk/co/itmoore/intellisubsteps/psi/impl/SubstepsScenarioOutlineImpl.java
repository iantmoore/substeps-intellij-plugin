package uk.co.itmoore.intellisubsteps.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yole
 */
public class SubstepsScenarioOutlineImpl extends SubstepsStepsHolderBase implements SubstepsScenarioOutline {
  private static final TokenSet EXAMPLES_BLOCK_FILTER = TokenSet.create(SubstepsElementTypes.EXAMPLES_BLOCK);

  public SubstepsScenarioOutlineImpl(@NotNull final ASTNode node) {
    super(node);
  }

  @Override
  public String toString() {
    return "SubstepsScenarioOutline:" + getElementText();
  }

  @Override
  protected String getPresentableText() {
    return buildPresentableText("Scenario Outline");
  }

  protected void acceptSubsteps(SubstepsElementVisitor SubstepsElementVisitor) {
    SubstepsElementVisitor.visitScenarioOutline(this);
  }

  @NotNull
  public List<SubstepsExamplesBlock> getExamplesBlocks() {
    List<SubstepsExamplesBlock> result = new ArrayList<SubstepsExamplesBlock>();
    final ASTNode[] nodes = getNode().getChildren(EXAMPLES_BLOCK_FILTER);
    for (ASTNode node : nodes) {
      result.add((SubstepsExamplesBlock) node.getPsi());
    }
    return result;
  }
}
