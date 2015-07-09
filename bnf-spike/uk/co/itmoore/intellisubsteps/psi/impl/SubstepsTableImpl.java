package uk.co.itmoore.intellisubsteps.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.itmoore.intellisubsteps.psi.SubstepsElementTypes;
import uk.co.itmoore.intellisubsteps.psi.SubstepsElementVisitor;
import uk.co.itmoore.intellisubsteps.psi.SubstepsTable;
import uk.co.itmoore.intellisubsteps.psi.SubstepsTableRow;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yole
 */
public class SubstepsTableImpl extends SubstepsPsiElementBase implements SubstepsTable {
  private static final TokenSet HEADER_ROW_TOKEN_SET = TokenSet.create(SubstepsElementTypes.TABLE_HEADER_ROW);
  private static final TokenSet ROW_TOKEN_SET = TokenSet.create(SubstepsElementTypes.TABLE_ROW);

  public SubstepsTableImpl(@NotNull final ASTNode node) {
    super(node);
  }

  @Override
  protected void acceptSubsteps(SubstepsElementVisitor SubstepsElementVisitor) {
    SubstepsElementVisitor.visitTable(this);
  }

  @Nullable
  public SubstepsTableRow getHeaderRow() {
    final ASTNode node = getNode();

    final ASTNode tableNode = node.findChildByType(HEADER_ROW_TOKEN_SET);
    return tableNode == null ? null : (SubstepsTableRow)tableNode.getPsi();
  }

  public List<SubstepsTableRow> getDataRows() {
    List<SubstepsTableRow> result = new ArrayList<SubstepsTableRow>();
    final SubstepsTableRow[] rows = PsiTreeUtil.getChildrenOfType(this, SubstepsTableRow.class);
    if (rows != null) {
      for (SubstepsTableRow row : rows) {
        if (!(row instanceof SubstepsTableHeaderRowImpl)) {
          result.add(row);
        }
      }
    }
    return result;
  }

  public int getColumnWidth(int columnIndex) {
    int result = 0;
    final SubstepsTableRow headerRow = getHeaderRow();
    if (headerRow != null) {
      result = headerRow.getColumnWidth(columnIndex);
    }
    for (SubstepsTableRow row : getDataRows()) {
      result = Math.max(result, row.getColumnWidth(columnIndex));
    }
    return result;
  }

  @Override
  public String toString() {
    return "SubstepsTable";
  }
}
