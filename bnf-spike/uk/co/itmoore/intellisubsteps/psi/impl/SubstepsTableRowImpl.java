package uk.co.itmoore.intellisubsteps.psi.feature.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.SubstepsElementVisitor;
import uk.co.itmoore.intellisubsteps.psi.SubstepsTableCell;
import uk.co.itmoore.intellisubsteps.psi.SubstepsTableRow;
import uk.co.itmoore.intellisubsteps.psi.SubstepsTokenTypes;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author yole
 */
public class SubstepsTableRowImpl extends SubstepsPsiElementBase implements SubstepsTableRow {
  public SubstepsTableRowImpl(@NotNull final ASTNode node) {
    super(node);
  }

  @Override
  protected void acceptSubsteps(SubstepsElementVisitor SubstepsElementVisitor) {
    SubstepsElementVisitor.visitTableRow(this);
  }

  @Override
  public String toString() {
    return "SubstepsTableRow";
  }

  // ToDo: Andrey Vokin, remove code duplication
  @NotNull
  public static <T extends PsiElement> List<T> getChildrenByFilter(final PsiElement psiElement, final Class<T> c) {
    LinkedList<T> list = new LinkedList<T>();
    for (PsiElement element : psiElement.getChildren()) {
      if (c.isInstance(element)) {
        //noinspection unchecked
        list.add((T)element);
      }
    }

    return list.isEmpty() ? Collections.<T>emptyList() : list;
  }

  @NotNull
  public List<SubstepsTableCell> getPsiCells() {

    return getChildrenByFilter(this, SubstepsTableCell.class);
  }

  public int getColumnWidth(int columnIndex) {
    final List<SubstepsTableCell> cells = getPsiCells();
    if (cells.size() <= columnIndex) {
      return 0;
    }

    final PsiElement cell = cells.get(columnIndex);
    if (cell != null && cell.getText() != null) {
      return cell.getText().trim().length();
    }
    return 0;
  }

  public void deleteCell(int columnIndex) {
    final List<SubstepsTableCell> cells = getPsiCells();
    if (columnIndex < cells.size()) {
      PsiElement cell = cells.get(columnIndex);
      PsiElement nextPipe = cell.getNextSibling();
      if (nextPipe instanceof PsiWhiteSpace) {
        nextPipe = nextPipe.getNextSibling();
      }
      if (nextPipe != null && nextPipe.getNode().getElementType() == SubstepsTokenTypes.PIPE) {
        nextPipe.delete();
      }
      cell.delete();
    }
  }
}
