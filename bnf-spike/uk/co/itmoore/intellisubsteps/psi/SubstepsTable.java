package uk.co.itmoore.intellisubsteps.psi;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author yole
 */
public interface SubstepsTable extends SubstepsPsiElement {
  @Nullable
  SubstepsTableRow getHeaderRow();
  List<SubstepsTableRow> getDataRows();

  int getColumnWidth(int columnIndex);
}
