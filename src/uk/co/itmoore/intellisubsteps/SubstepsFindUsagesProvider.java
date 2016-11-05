package uk.co.itmoore.intellisubsteps;


import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.psi.SubstepDefinitionName;

/**
 * Created by ian on 26/10/16.
 */
public class SubstepsFindUsagesProvider implements FindUsagesProvider {

    private static final Logger log = LogManager.getLogger(SubstepsFindUsagesProvider.class);

    public SubstepsFindUsagesProvider(){
        log.debug("SubstepsFindUsagesProvider created");
    }

    @Nullable
    @Override
    public WordsScanner getWordsScanner() {

//        return new SimpleWordsScanner();

        return new SubstepDefinitionsWordScanner();

//        return new DefaultWordsScanner(new SubstepDefinitionLexer2(),
//                TokenSet.create(SubstepDefinitionElementTypes.SUBSTEP_DEFINITION_DEFINE_BLOCK_ELEMENT_TYPE,
//                        SubstepDefinitionElementTypes.SUBSTEP_DEFINITION_STEP_ELEMENT_TYPE,
//                        SubstepDefinitionElementTypes.SUBSTEP_DEFINITION_NAME_ELEMENT_TYPE,
//                        SubstepDefinitionTokenTypes.SUBSTEP_DEFINITION_TOKEN),
//                TokenSet.create(SubstepTokenTypes.COMMENT_TOKEN),
//                TokenSet.EMPTY);
    }

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {

        boolean can = psiElement instanceof SubstepDefinitionName;

        log.debug("canFindUsagesFor: text: " + psiElement.getText() + " class: " + psiElement.getClass() + " returning: " + can);

        return can;
    }

    @Nullable
    @Override
    public String getHelpId(@NotNull PsiElement psiElement) {
        return null;
    }

    @NotNull
    @Override
    public String getType(@NotNull PsiElement element) {

        log.debug("getType: " + element.toString());

//        if (element instanceof SimpleProperty) {
//            return "simple property";
//        } else {
            return "";
//        }
    }

    @NotNull
    @Override
    public String getDescriptiveName(@NotNull PsiElement element) {

        log.debug("getDescriptiveName: " + element.toString());

//        if (element instanceof SimpleProperty) {
//            return ((SimpleProperty) element).getKey();
//        } else {
            return "";
//        }
    }

    @NotNull
    @Override
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {

        log.debug("getNodeText: " + element.toString());

//        if (element instanceof SimpleProperty) {
//            return ((SimpleProperty) element).getKey() + ":" + ((SimpleProperty) element).getValue();
//        } else {
            return "";
//        }
    }

}
