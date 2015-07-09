package uk.co.itmoore.intellisubsteps.psi;

import com.intellij.psi.PsiElementVisitor;
import uk.co.itmoore.intellisubsteps.psi.impl.*;

public abstract class SubstepsElementVisitor extends PsiElementVisitor {
  public void visitFeature(SubstepsFeature feature) {
    visitElement(feature);
  }

  public void visitFeatureHeader(SubstepsFeatureHeaderImpl header) {
    visitElement(header);
  }

  public void visitScenario(SubstepsScenario scenario) {
    visitElement(scenario);
  }

  public void visitScenarioOutline(SubstepsScenarioOutline outline) {
    visitElement(outline);
  }

  public void visitExamplesBlock(SubstepsExamplesBlockImpl block) {

    visitElement(block);
  }

  public void visitStep(SubstepsStepImpl step) {
    visitElement(step);
  }

  public void visitTable(SubstepsTableImpl table) {
    visitElement(table);
  }

  public void visitTableRow(SubstepsTableRowImpl row) {
    visitElement(row);
  }

  public void visitTableHeaderRow(SubstepsTableHeaderRowImpl row) {
    visitElement(row);
  }

  public void visitTag(SubstepsTagImpl SubstepsTag) {
    visitElement(SubstepsTag);
  }

  public void visitStepParameter(SubstepsStepParameterImpl SubstepsStepParameter) {
    visitElement(SubstepsStepParameter);
  }

  public void visitSubstepsTableCell(final SubstepsTableCell cell) {
    visitElement(cell);
  }

//   public void visitPystring(final SubstepsPystring phstring) {
//    visitElement(phstring);
//  }
}
