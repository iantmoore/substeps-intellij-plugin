package uk.co.itmoore.intellisubsteps.psi.stepdefinition;

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
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.impl.*;

/**
 * Created by ian on 05/07/15.
 */
public class SubstepDefinitionParserDefinition implements ParserDefinition {

    private static final Logger logger = LogManager.getLogger(SubstepDefinitionParserDefinition.class);


    private static final TokenSet WHITESPACE = TokenSet.create(TokenType.WHITE_SPACE);
    private static final TokenSet COMMENTS = TokenSet.create(SubstepDefinitionTokenTypes.COMMENT_TOKEN);


    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new SubstepDefinitionLexer2();
    }

    @Override
    public PsiParser createParser(Project project) {
        return new SubstepDefinitionParser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return SubstepDefinitionElementTypes.SUBSTEP_DEFINITION_FILE;
    }

    @NotNull
    @Override
    public TokenSet getWhitespaceTokens() {
        return WHITESPACE;
    }

    @NotNull
    @Override
    public TokenSet getCommentTokens() {
        return COMMENTS;
    }

    @NotNull
    @Override
    public TokenSet getStringLiteralElements() {
        return TokenSet.EMPTY;
    }

    @NotNull
    @Override
    public PsiElement createElement(ASTNode astNode) {

        // original pre 29.10.2016
//        if (astNode.getElementType() == SubstepDefinitionElementTypes.SUBSTEP_DEFINITION_ELEMENT_TYPE) return new SubstepDefinitionImpl(astNode);
//        if (astNode.getElementType() == SubstepDefinitionElementTypes.SUBSTEP_DEFINITION_STEP_ELEMENT_TYPE) return new SubstepDefinitionStepImpl(astNode);
//        if (astNode.getElementType() == SubstepDefinitionElementTypes.SUBSTEP_DEFINITION_DEFINE_BLOCK_ELEMENT_TYPE) return new SubstepDefinitionDefineBlockImpl(astNode);

//        if (astNode.getElementType() == SubstepDefinitionElementTypes.SUBSTEP_DEFINITION_ELEMENT_TYPE) return new SubstepDefinitionImpl(astNode);
        if (astNode.getElementType() == SubstepDefinitionElementTypes.SUBSTEP_DEFINITION_NAME_ELEMENT_TYPE) return new SubstepDefinitionNameImpl(astNode);
        if (astNode.getElementType() == SubstepDefinitionElementTypes.SUBSTEP_DEFINITION_STEP_ELEMENT_TYPE) return new SubstepStep2Impl(astNode);
        if (astNode.getElementType() == SubstepDefinitionElementTypes.SUBSTEP_DEFINITION_DEFINE_BLOCK_ELEMENT_TYPE) return new SubstepDefinition2Impl(astNode);


        return PsiUtilCore.NULL_PSI_ELEMENT;
    }

    @Override
    public PsiFile createFile(FileViewProvider fileViewProvider) {

        return new SubstepDefinitionsFileImpl(fileViewProvider);
    }

    @Override
    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {

        // line break between step defs ?

//        final IElementType leftElementType = left.getElementType();
//        if (leftElementType == GherkinTokenTypes.COMMENT) {
//            return SpaceRequirements.MUST_LINE_BREAK;
//        }
//        if (right.getElementType() == GherkinTokenTypes.EXAMPLES_KEYWORD) {
//            return SpaceRequirements.MUST_LINE_BREAK;
//        }

        return SpaceRequirements.MAY;

    }
}
