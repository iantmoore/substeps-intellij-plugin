package uk.co.itmoore.intellisubsteps.psi;

import java.util.List;

/**
 * @author yole
 */
public interface SubstepsTableRow extends SubstepsPsiElement {
  SubstepsTableRow[] EMPTY_ARRAY = new SubstepsTableRow[0];

  List<SubstepsTableCell> getPsiCells();

  int getColumnWidth(int columnIndex);

  void deleteCell(int columnIndex);
}
