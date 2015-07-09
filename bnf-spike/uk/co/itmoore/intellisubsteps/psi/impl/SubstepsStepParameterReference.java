package uk.co.itmoore.intellisubsteps.psi.feature.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;

/**
 * User: Andrey.Vokin
 * Date: 4/13/11
 */
public class SubstepsStepParameterReference extends SubstepsSimpleReference {

  public SubstepsStepParameterReference(SubstepsStepParameter stepParameter) {
    super(stepParameter);
  }

  @Override
  public SubstepsStepParameter getElement() {
    return (SubstepsStepParameter)super.getElement();
  }

  //@Override
  //public TextRange getRangeInElement() {
  //  TextRange superRange = super.getRangeInElement();
  //  return new TextRange(1, superRange.getEndOffset() - 1);
  //}

  @Override
  public PsiElement resolve() {
    final SubstepsScenarioOutline scenario = PsiTreeUtil.getParentOfType(getElement(), SubstepsScenarioOutline.class);
    if (scenario != null) {
      final SubstepsExamplesBlock exampleBlock = PsiTreeUtil.getChildOfType(scenario, SubstepsExamplesBlock.class);
      if (exampleBlock != null) {
        final SubstepsTable table = PsiTreeUtil.getChildOfType(exampleBlock, SubstepsTable.class);
        if (table != null) {
          final SubstepsTableHeaderRowImpl header = PsiTreeUtil.getChildOfType(table, SubstepsTableHeaderRowImpl.class);
          if (header != null) {
            for (PsiElement cell : header.getChildren()) {
              if (cell instanceof SubstepsTableCell) {
                final String cellText = cell.getText();
                if (cellText.equals(getElement().getName())) {
                  return cell;
                }
              }
            }
          }
        }
      }
    }
    return null;
  }
}
