package uk.co.itmoore.intellisubsteps;

import com.intellij.analysis.AnalysisScope;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import uk.co.itmoore.intellisubsteps.psi.SubstepsCompletionContributor;
import uk.co.itmoore.intellisubsteps.psi.feature.FeatureFile;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.psi.SubstepsDefinitionFile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ian on 25/07/15.
 */
public class GotoStepDefinitionAction extends AnAction {

    private static final Logger log = LogManager.getLogger(GotoStepDefinitionAction.class);

    public static Project getProject(AnActionEvent event)
    {
        return PlatformDataKeys.PROJECT.getData(event.getDataContext());
    }

    public static PsiElement getPsiElement(AnActionEvent event)
    {
        return LangDataKeys.PSI_ELEMENT.getData(event.getDataContext());
    }

    public static Editor getEditor(AnActionEvent event)
    {
        return PlatformDataKeys.EDITOR.getData(event.getDataContext());
    }

    public static PsiFile getPsiFile(AnActionEvent event)
    {
        return LangDataKeys.PSI_FILE.getData(event.getDataContext());
    }

    public static VirtualFile getVirtualFile(AnActionEvent event)
    {
        return PlatformDataKeys.VIRTUAL_FILE.getData(event.getDataContext());
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        log.debug("actionPerformed");


        Editor editor = getEditor(anActionEvent);
        if (editor == null){
            log.debug("no editor");
        }

        final PsiFile psiFile = getPsiFile(anActionEvent);
        if (psiFile == null ){
            log.debug("no psi file");
        }

        else if (psiFile instanceof SubstepsDefinitionFile || psiFile instanceof FeatureFile) {

            PsiElement elementAt = psiFile.findElementAt(editor.getCaretModel().getOffset());
            log.debug("elementAt.getText(): " + elementAt.getText());

            String stepText = elementAt.getText();

            final Project project = getProject(anActionEvent);

//            GlobalSearchScope scope = GlobalSearchScope.allScope(project);
            //PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass("im.StepImplentations", scope);

            // this works loading up a class via the decompiler
//        PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass("com.technophobia.webdriver.substeps.impl.ActionWebDriverSubStepImplementations", scope);

            List<PsiClassAndExpression> matchingProjectFiles = getMatchingProjectFiles(stepText, project);

            if (!matchingProjectFiles.isEmpty()) {

                if (matchingProjectFiles.size() > 1) {
                    log.error("found too many matching step impls");
                }

                PsiFile referencedPsiFile = matchingProjectFiles.get(0).psiFile;

                VirtualFile virtualFile = referencedPsiFile.getContainingFile().getVirtualFile();

                int offset = referencedPsiFile.getText().indexOf(matchingProjectFiles.get(0).expression);

                new OpenFileDescriptor(project, virtualFile, offset).navigateInEditor(project, true);

            }

        }
        else {
            log.debug("not a substep feature or substep def file");
        }
    }

    // get a vfile:   https://confluence.jetbrains.com/display/IDEADEV/IntelliJ+IDEA+Architectural+Overview


        // TODO accessing a substep file...

//            VirtualFile vFile = LocalFileSystem.getInstance().findFileByIoFile(new File("/home/ian/projects/IdeaProjects/untitled/src/y.substeps"));
//            int logicalLine = 2;
//            int logicalColumn = 2;
//            int offset = 2;
//            boolean persistent = true;
//
//            OpenFileDescriptor od = new OpenFileDescriptor(project, vFile, logicalLine, logicalColumn);
//
//        List<FileEditor> fileEditors = fileEditorManager.openEditor(od, true);
//
//        for (FileEditor fe : fileEditors){
//            log.debug("file editor name: " +
//            fe.getName());
//
//        }
//

//        } else {
//            //handle the class not found
//        }


    // if only java had tuples :-)
    public static class PsiClassAndExpression{
        String expression;
        PsiFile psiFile;
    }

    private List<PsiClassAndExpression> getMatchingProjectFiles(final String text, Project project){

        AnalysisScope moduleScope = new AnalysisScope(project);

        final List<PsiClassAndExpression> filesWithMatchingStep = new ArrayList<>();

        moduleScope.accept(new PsiRecursiveElementVisitor() {
            @Override
            public void visitFile(final PsiFile file) {

                log.debug("got src file: " + file.getName() + " filetype: " + file.getFileType());

                String expression = null;
                if (file instanceof PsiJavaFile) {

                    expression = psiJavaFileContainsSubstep((PsiJavaFile) file, text);

                } else if (file instanceof SubstepsDefinitionFile) {

                    expression = getMatchingSubstepDefExpression(file, text);
                }
                if (expression != null) {
                    PsiClassAndExpression result = new PsiClassAndExpression();

                    result.psiFile = file;
                    result.expression = expression;

                    filesWithMatchingStep.add(result);
                }

            }
        });

        return filesWithMatchingStep;

    }

    private String getMatchingSubstepDefExpression(PsiFile file, String text) {

        // TODO take parameters into account

        String[] lines = file.getText().split("\n");

        for (String line: lines){
            if (line.trim().startsWith("Define:")){

                String def = StringUtils.stripStart(line.trim(), "Define:").trim();

                // does this def match the text
                Pattern stepDefPattern = Pattern.compile(def.replaceAll("(<[^>]*>)", "\"?([^\"]*)\"?"));
                if (stepDefPattern.matcher(text).matches()){
                    return def;
                }
            }
        }


        return null;
    }

    private String psiJavaFileContainsSubstep(PsiJavaFile psiJavaFile, String text) {

            log.debug("looking at java source file: " + psiJavaFile.getName());

            final PsiClass[] psiClasses = psiJavaFile.getClasses();

            for (PsiClass psiClass : psiClasses) {

                if (SubstepsCompletionContributor.isStepImplementationsClass(psiClass)) {

                    PsiMethod[] methods = psiClass.getAllMethods();

                    for (PsiMethod method : methods) {
                        String expression = getMatchingStepExpression(method, text);
                        if (expression != null){
                            return expression;
                        }
                    }
                }
            }

        return null;
    }


    private enum MatchResult {
        NO_MATCH,
        EXACT_MATCH,
        FUZZY_MATCH
    }

    private MatchResult stepTextMatchesExpression(String text, String expression){

        String parameterRegEx = ".*(<[^>]*>).*";
        Pattern paramPattern = Pattern.compile(parameterRegEx);

        if(paramPattern.matcher(text).matches()){
            String text2 = text.replaceAll("<[^>]*>", "");

            String expression2 = expression.replaceAll("\\([^\\)]*\\)", "");

            if(text2.equals(expression2)){
                return MatchResult.EXACT_MATCH;
            }
            else {
                int idx = StringUtils.indexOfAny(expression2, new String[]{"Given", "When", "Then", "And"});
                if (idx ==0){
                    String expression3 = replaceKeywords(expression2);
                    String text3 = replaceKeywords(text2);
                    if (text3.equals(expression3)){
                        return MatchResult.FUZZY_MATCH;
                    }
                }
                return MatchResult.NO_MATCH;
            }

        }
        else {
            Pattern p = Pattern.compile(expression);
            Matcher matcher = p.matcher(text);

            if (matcher.matches()) {
                return MatchResult.EXACT_MATCH;
            }
            else {
                String expression2 = replaceKeywords(expression);
                String text2 = replaceKeywords(text);

                p = Pattern.compile(expression2);
                matcher = p.matcher(text2);

                if (matcher.matches()) {
                    return MatchResult.FUZZY_MATCH;
                }
            }
            return MatchResult.NO_MATCH;
        }
    }

    private String replaceKeywords(String src){
        return StringUtils.removeStart(StringUtils.removeStart(StringUtils.removeStart(StringUtils.removeStart(src, "Given"), "When"), "Then"), "And").trim();
    }

    private String getMatchingStepExpression(PsiMethod method, final String stepText) {

        log.debug("checking method: " + method.getName());

        PsiAnnotation[] methodAnnotations = method.getModifierList().getAnnotations();
        for (PsiAnnotation a : methodAnnotations) {
            if (a.getQualifiedName().equals(com.technophobia.substeps.model.SubSteps.Step.class.getCanonicalName())){

                PsiAnnotationParameterList parameterList = a.getParameterList();

                PsiNameValuePair[] attributes = parameterList.getAttributes();
                if (attributes != null) {
                    String src = attributes[0].getValue().getText();

                    String stepExpression = src.substring(1, src.length() - 1);
                    log.debug("found step expression: " + stepExpression);

                    if (stepTextMatchesExpression(stepText, stepExpression) == MatchResult.EXACT_MATCH){

                        log.debug("found a match with stepEpression: " + stepExpression + " and text: " + stepText + " method: " + method.getName());
                        return stepExpression;
                    }
                    else {

                        // TODO - check the capture patterns in the regex, if the pattern is capturing (//d+) then convert the stepText accordingly

                        log.debug("no match");
                    }
                }
            }
        }
        return null;
    }
}
