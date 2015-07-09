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
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.impl.SubstepDefinitionDefineBlockImpl;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.impl.SubstepDefinitionImpl;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.impl.SubstepDefinitionStepImpl;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.impl.SubstepDefinitionsFileImpl;

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
        return new SubstepStepDefinitionLexer();
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

        if (astNode.getElementType() == SubstepDefinitionElementTypes.SUBSTEP_DEFINITION_ELEMENT_TYPE) return new SubstepDefinitionImpl(astNode);
        if (astNode.getElementType() == SubstepDefinitionElementTypes.SUBSTEP_DEFINITION_STEP_ELEMENT_TYPE) return new SubstepDefinitionStepImpl(astNode);
        if (astNode.getElementType() == SubstepDefinitionElementTypes.SUBSTEP_DEFINITION_DEFINE_BLOCK_ELEMENT_TYPE) return new SubstepDefinitionDefineBlockImpl(astNode);
        // TODO need to sort out the model here - this is why it's barfing

//        if (node.getElementType() == GherkinElementTypes.FEATURE) return new GherkinFeatureImpl(node);
//        if (node.getElementType() == GherkinElementTypes.FEATURE_HEADER) return new GherkinFeatureHeaderImpl(node);
//        if (node.getElementType() == GherkinElementTypes.SCENARIO) return new GherkinScenarioImpl(node);
//        if (node.getElementType() == GherkinElementTypes.STEP) return new GherkinStepImpl(node);
//        if (node.getElementType() == GherkinElementTypes.SCENARIO_OUTLINE) return new GherkinScenarioOutlineImpl(node);
//        if (node.getElementType() == GherkinElementTypes.EXAMPLES_BLOCK) return new GherkinExamplesBlockImpl(node);
//        if (node.getElementType() == GherkinElementTypes.TABLE) return new GherkinTableImpl(node);
//        if (node.getElementType() == GherkinElementTypes.TABLE_ROW) return new GherkinTableRowImpl(node);
//        if (node.getElementType() == GherkinElementTypes.TABLE_CELL) return new GherkinTableCellImpl(node);
//        if (node.getElementType() == GherkinElementTypes.TABLE_HEADER_ROW) return new GherkinTableHeaderRowImpl(node);
//        if (node.getElementType() == GherkinElementTypes.TAG) return new GherkinTagImpl(node);
//        if (node.getElementType() == GherkinElementTypes.STEP_PARAMETER) return new GherkinStepParameterImpl(node);
//        if (node.getElementType() == GherkinElementTypes.PYSTRING) return new GherkinPystringImpl(node);
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
