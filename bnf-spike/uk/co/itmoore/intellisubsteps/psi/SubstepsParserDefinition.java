package uk.co.itmoore.intellisubsteps.psi;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiUtilCore;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.impl.*;

public class SubstepsParserDefinition implements ParserDefinition {
  private static final TokenSet WHITESPACE = TokenSet.create(TokenType.WHITE_SPACE);
  private static final TokenSet COMMENTS = TokenSet.create(SubstepsTokenTypes.COMMENT);

  @NotNull
  public Lexer createLexer(Project project) {
    return new SubstepsLexer(new PlainSubstepsKeywordProvider() );
  }

  public PsiParser createParser(Project project) {
    return new SubstepsParser();
  }

  public IFileElementType getFileNodeType() {
    return SubstepsElementTypes.SUBSTEPS_FILE;
  }

  @NotNull
  public TokenSet getWhitespaceTokens() {
    return WHITESPACE;
  }

  @NotNull
  public TokenSet getCommentTokens() {
    return COMMENTS;
  }

  @NotNull
  public TokenSet getStringLiteralElements() {
    return TokenSet.EMPTY;
  }

  @NotNull
  public PsiElement createElement(ASTNode node) {
    if (node.getElementType() == SubstepsElementTypes.FEATURE) return new SubstepsFeatureImpl(node);
    if (node.getElementType() == SubstepsElementTypes.FEATURE_HEADER) return new SubstepsFeatureHeaderImpl(node);
    if (node.getElementType() == SubstepsElementTypes.SCENARIO) return new SubstepsScenarioImpl(node);
    if (node.getElementType() == SubstepsElementTypes.STEP) return new SubstepsStepImpl(node);
    if (node.getElementType() == SubstepsElementTypes.SCENARIO_OUTLINE) return new SubstepsScenarioOutlineImpl(node);
    if (node.getElementType() == SubstepsElementTypes.EXAMPLES_BLOCK) return new SubstepsExamplesBlockImpl(node);
    if (node.getElementType() == SubstepsElementTypes.TABLE) return new SubstepsTableImpl(node);
    if (node.getElementType() == SubstepsElementTypes.TABLE_ROW) return new SubstepsTableRowImpl(node);
    if (node.getElementType() == SubstepsElementTypes.TABLE_CELL) return new SubstepsTableCellImpl(node);
    if (node.getElementType() == SubstepsElementTypes.TABLE_HEADER_ROW) return new SubstepsTableHeaderRowImpl(node);
    if (node.getElementType() == SubstepsElementTypes.TAG) return new SubstepsTagImpl(node);
    if (node.getElementType() == SubstepsElementTypes.STEP_PARAMETER) return new SubstepsStepParameterImpl(node);

    // TODO
    // if (node.getElementType() == SubstepsElementTypes.SUBSTEP_DEFINITION) return new SubstepsDefinitionImpl(node);

    //if (node.getElementType() == SubstepsElementTypes.PYSTRING) return new SubstepsPystringImpl(node);
    return PsiUtilCore.NULL_PSI_ELEMENT;
  }

  public PsiFile createFile(FileViewProvider viewProvider) {
    return new SubstepsFileImpl(viewProvider);
  }

  public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
    // Line break between line comment and other elements
    final IElementType leftElementType = left.getElementType();
    if (leftElementType == SubstepsTokenTypes.COMMENT) {
      return SpaceRequirements.MUST_LINE_BREAK;
    }
    if (right.getElementType() == SubstepsTokenTypes.EXAMPLES_KEYWORD) {
      return SpaceRequirements.MUST_LINE_BREAK;
    }
    return SpaceRequirements.MAY;
  }
}
