// This is a generated file. Not intended for manual editing.
package uk.co.itmoore.intellisubsteps.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import uk.co.itmoore.intellisubsteps.psi.impl.*;

public interface SubstepDefTypes {

  IElementType A_SUBSTEP_DEFINITION = new SubstepDefElementType("A_SUBSTEP_DEFINITION");
  IElementType COMMENT = new SubstepDefElementType("COMMENT");
  IElementType COMMENT_LINE = new SubstepDefElementType("COMMENT_LINE");
  IElementType EOL = new SubstepDefElementType("EOL");
  IElementType LINE_TO_EOL = new SubstepDefElementType("LINE_TO_EOL");
  IElementType PARAM = new SubstepDefElementType("PARAM");
  IElementType SPACE = new SubstepDefElementType("SPACE");
  IElementType STEPLINE = new SubstepDefElementType("STEPLINE");
  IElementType SUBSTEP_DEFINITION_KEYWORD = new SubstepDefElementType("SUBSTEP_DEFINITION_KEYWORD");
  IElementType SUBSTEP_DEFINITION_LINE = new SubstepDefElementType("SUBSTEP_DEFINITION_LINE");
  IElementType WHITE = new SubstepDefElementType("WHITE");

  IElementType STEPLINE = new SubstepDefTokenType("stepline");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == A_SUBSTEP_DEFINITION) {
        return new SubstepDefASubstepDefinitionImpl(node);
      }
      else if (type == COMMENT) {
        return new SubstepDefCommentImpl(node);
      }
      else if (type == COMMENT_LINE) {
        return new SubstepDefCommentLineImpl(node);
      }
      else if (type == EOL) {
        return new SubstepDefEolImpl(node);
      }
      else if (type == LINE_TO_EOL) {
        return new SubstepDefLineToEolImpl(node);
      }
      else if (type == PARAM) {
        return new SubstepDefParamImpl(node);
      }
      else if (type == SPACE) {
        return new SubstepDefSpaceImpl(node);
      }
      else if (type == STEPLINE) {
        return new SubstepDefSteplineImpl(node);
      }
      else if (type == SUBSTEP_DEFINITION_KEYWORD) {
        return new SubstepDefSubstepDefinitionKeywordImpl(node);
      }
      else if (type == SUBSTEP_DEFINITION_LINE) {
        return new SubstepDefSubstepDefinitionLineImpl(node);
      }
      else if (type == WHITE) {
        return new SubstepDefWhiteImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
