package uk.co.itmoore.intellisubsteps;

import com.intellij.analysis.AnalysisScope;
import com.intellij.find.FindManager;
import com.intellij.find.FindModel;
import com.intellij.find.impl.FindInProjectUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.usages.*;
import com.technophobia.substeps.model.ParentStep;
import com.technophobia.substeps.model.PatternMap;
import com.technophobia.substeps.model.Step;
import com.technophobia.substeps.parser.FileContents;
import com.technophobia.substeps.runner.syntax.DefaultSyntaxErrorReporter;
import com.technophobia.substeps.runner.syntax.SubStepDefinitionParser;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.itmoore.intellisubsteps.psi.feature.Feature;
import uk.co.itmoore.intellisubsteps.psi.feature.FeatureFile;
import uk.co.itmoore.intellisubsteps.psi.feature.StepsHolder;
import uk.co.itmoore.intellisubsteps.psi.feature.impl.ScenarioStepImpl;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.impl.SubstepDefinitionNameImpl;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.impl.SubstepStep2Impl;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.psi.SubstepsDefinitionFile;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ian on 30/10/16.
 */
public class FindSubstepUsagesAction extends BaseSubstepsEditorAction {

    private static final Logger log = LogManager.getLogger(FindSubstepUsagesAction.class);

    private List<Usage> getUsagesInProjectSource(Project project, SubstepsSyntaxBuilder syntaxBuilder, String text){
        List<Usage> usages = new ArrayList<>();

        List<SubstepsSyntaxBuilder.PsiMethodAndClass> psiMethodAndClasses = syntaxBuilder.resolveToProjectJavaSource(text);

        for (SubstepsSyntaxBuilder.PsiMethodAndClass methodAndClass : psiMethodAndClasses){

            // convert to a usage

            PsiFile containingFile = methodAndClass.psiClass.getContainingFile();
            VirtualFile virtualFile = containingFile.getVirtualFile();

            OpenFileDescriptor ofd = new OpenFileDescriptor(project, virtualFile, methodAndClass.method.method.getTextOffset());

            usages.add(new MyUsage("in java class", methodAndClass.method.method.getText(), project, virtualFile, ofd, containingFile));

        }
        return usages;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        final Editor editor = getEditor(anActionEvent);
        final PsiFile psiFile = getPsiFile(anActionEvent);

        PsiElement psiElement = getPsiElement(anActionEvent);
        final Project project = getProject(anActionEvent);

        List<Usage> usages = new ArrayList<>();
        if (psiElement != null){
            String text = psiElement.getText();

            log.debug("find usages of [" + text + "] psiElement: " + psiElement.getClass());
            SubstepsSyntaxBuilder syntaxBuilder = new SubstepsSyntaxBuilder(project);

            if (psiElement instanceof SubstepDefinitionNameImpl){

                // Define: xxxxxxx
                // look in substeps steps and scenario steps - it won't be in code..
                String patternText = text.replaceAll("(<[^>]*>)", "\"?([^\"]*)\"?");

                log.debug("looking for usages of step definition text: " + text + " pattern: " + patternText);

                Pattern pattern = Pattern.compile(patternText);
                usages = getMatchingProjectFiles(pattern, project, false, text, syntaxBuilder);

            }
            else if (psiElement instanceof SubstepStep2Impl || psiElement instanceof ScenarioStepImpl){

                // this is plain text - it might include params - need to resolve to either a substep def or a java method
                List<ParentStep> parentSteps = syntaxBuilder.resolveToSubstepDefinition(text);
                if (!parentSteps.isEmpty()){
                    // this text maps to a define: ....
                    log.debug("usage expression matches a substep def - define:xxxx");
                    for (ParentStep p : parentSteps){

                        Pattern pattern = Pattern.compile(p.getParent().getPattern());
                        usages.addAll(getMatchingProjectFiles(pattern, project, true, text, syntaxBuilder));
                    }
                }
                else {
                    // could be a reference to project code.

                    usages.addAll( getUsagesInProjectSource(project, syntaxBuilder, text));

                }
            }
            else {
                log.debug("unknown element");
            }

            if (usages != null){
                UsageViewManager usageViewManager = UsageViewManager.getInstance(project);

                FindManager findManager = FindManager.getInstance(project);

                FindModel findModel = (FindModel)findManager.getFindInProjectModel().clone();
                findModel.setStringToFind(text);
                findModel.setReplaceState(false);
                findModel.setOpenInNewTabVisible(true);
                findModel.setOpenInNewTabEnabled(true);
                findModel.setOpenInNewTab(true);
                findModel.setFileFilter("substeps");
                findModel.setMultiline(true);
                findModel.setMultipleFiles(true);

                findManager.getFindInProjectModel().copyFrom(findModel);
                final FindModel findModelCopy = (FindModel)findModel.clone();

                UsageViewPresentation presentation = FindInProjectUtil.setupViewPresentation(findModel.isOpenInNewTabEnabled(), findModelCopy);

                FindModel featureFindModel = findModel.clone();
                featureFindModel.setFileFilter("feature");

                UsageTarget[] usageTargets = new UsageTarget[]{
                        new FindInProjectUtil.StringUsageTarget(project, findModel)
                };
                Usage[] foundUsages = usages.toArray(new Usage[usages.size()]);

                usageViewManager.showUsages(usageTargets, foundUsages, presentation);

            }

        }






//        if (editor == null || psiFile == null ){
//            log.debug("no editor or psiFile");
//        }
//        else if (psiFile instanceof SubstepsDefinitionFile || psiFile instanceof FeatureFile) {
//
//            PsiElement elementAt = psiFile.findElementAt(editor.getCaretModel().getOffset());
//            log.debug("actionPerformed for a substeps or feature file elementAt.getText(): " + elementAt.getText());
//
//            log.debug("psiElement text: " + psiElement.getText() + " class: " +
//            psiElement.getClass());
//
//            // uk.co.itmoore.intellisubsteps.psi.stepdefinition.impl.SubstepStep2Impl
//            // uk.co.itmoore.intellisubsteps.psi.stepdefinition.impl.SubstepDefinitionNameImpl
//            // uk.co.itmoore.intellisubsteps.psi.feature.impl.ScenarioStepImpl
//
//            String stepText = elementAt.getText();
//
//            log.debug("find usages of: " + stepText);
//
//
//            /*
//            FileBasedIndex.getInstance().getFilesWithKey()
//
//            ID = SubstepsFileBasedIndexExtension.NAME
//
//    getValues() allows to obtain all values associated with a specific key but not the files in which they were found.
//    getContainingFiles() allows to obtain all files in which a specific key was encountered.
//    processValues() allows to iterate though all files in which a specific key was encountered and to access the associated values at the same time.
//
//
//              */
//
//
//            UsageViewManager usageViewManager = UsageViewManager.getInstance(project);
//
//
//            FindManager findManager = FindManager.getInstance(project);
//
//
//
//            FindModel findModel = (FindModel)findManager.getFindInProjectModel().clone();
//            findModel.setStringToFind(stepText);
//            findModel.setReplaceState(false);
//            findModel.setOpenInNewTabVisible(true);
//            findModel.setOpenInNewTabEnabled(true);
//            findModel.setOpenInNewTab(true);
//            findModel.setFileFilter("substeps");
//            findModel.setMultiline(true);
//            findModel.setMultipleFiles(true);
//
//            findManager.getFindInProjectModel().copyFrom(findModel);
//            final FindModel findModelCopy = (FindModel)findModel.clone();
//
//            UsageViewPresentation presentation = FindInProjectUtil.setupViewPresentation(findModel.isOpenInNewTabEnabled(), findModelCopy);
////            boolean showPanelIfOnlyOneUsage = true;
////            final FindUsagesProcessPresentation processPresentation =
////                    FindInProjectUtil.setupProcessPresentation(project, showPanelIfOnlyOneUsage, presentation);
//
//
//            // TODO = UsageTarget = searched for
//
//            FindModel featureFindModel = findModel.clone();
//            featureFindModel.setFileFilter("feature");
//
//            UsageTarget[] usageTargets = new UsageTarget[]{
//                    new FindInProjectUtil.StringUsageTarget(project, findModel)//,
////                    new FindInProjectUtil.StringUsageTarget(project, featureFindModel)
//            };
//
//                /* TODO  I wonder if I could use this - sourceElement - is this something like the psi file ?  targets are all the find results in that file ?
//
//            FindUtil.showInUsageView(@Nullable PsiElement sourceElement, NotNull PsiElement[] targets, @NotNull String title, @NotNull final Project project) {
//            */
//
//            List<Usage> usageList = getMatchingProjectFiles(stepText, project);
//
////            if (matchingProjectFiles != null){
////
////                for (GotoStepDefinitionAction.PsiClassAndExpression  match : matchingProjectFiles){
////
////                    PsiFile referencedPsiFile = match.psiFile;
////
////                    VirtualFile virtualFile = referencedPsiFile.getContainingFile().getVirtualFile();
////
////                    int offset = referencedPsiFile.getText().indexOf(match.expression);
////
////                    OpenFileDescriptor ofd = new OpenFileDescriptor(project, virtualFile, offset);
////
////                    usageList.add(new MyUsage(stepText, project, virtualFile, ofd));
////                }
////            }
//
//            Usage[] foundUsages = usageList.toArray(new Usage[usageList.size()]);
//
//            usageViewManager.showUsages(usageTargets, foundUsages, presentation);
//
////            usageViewManager.searchAndShowUsages(new UsageTarget[]{usageTarget}, new Factory<UsageSearcher>() {
////                @Override
////                public UsageSearcher create() {
////                    return new UsageSearcher() {
////                        @Override
////                        public void generate(@NotNull final Processor<Usage> processor) {
////                            AdapterProcessor<UsageInfo, Usage> consumer =
////                                    new AdapterProcessor<UsageInfo, Usage>(processor, UsageInfo2UsageAdapter.CONVERTER);
////                            //noinspection ConstantConditions
////                            FindInProjectUtil.findUsages(findModelCopy, null, project, consumer, processPresentation);
////                        }
////                    };
////                }
////            }, processPresentation, presentation, null);
//
//            // TODO - not sur ehow to drive this bit
//
////            usageViewManager.showUsages(new UsageTarget[])
////
////            @NotNull UsageTarget[] searchedFor, @NotNull Usage[] foundUsages, @NotNull UsageViewPresentation presentation
////            usageViewManager.searchAndShowUsages()
//        }
    }

    private List<Usage> getMatchingProjectFiles(final Pattern pattern, Project project, boolean searchJavaSource, String text, SubstepsSyntaxBuilder syntaxBuilder){

        AnalysisScope moduleScope = new AnalysisScope(project);

        final List<Usage> references = new ArrayList<>();

        moduleScope.accept(new PsiRecursiveElementVisitor() {
            @Override
            public void visitFile(final PsiFile file) {

                //log.debug("got src file: " + file.getName() + " filetype: " + file.getFileType());

               // String expression = null;
//                if (file instanceof PsiJavaFile) {
//
//                    //     references.addAll(getReferencesFromPsiJavaFile((PsiJavaFile) file, text));
//
//                } else
                if (file instanceof SubstepsDefinitionFile) {

                    references.addAll(getReferencesFromSubstepDefs(file, pattern, project));
                }
                else if (file instanceof FeatureFile){
                    FeatureFile fFile = (FeatureFile)file;
                    references.addAll(getReferencesFromFeatureFile(fFile, pattern));
                }

//                if (expression != null) {
//                    GotoStepDefinitionAction.PsiClassAndExpression result = new GotoStepDefinitionAction.PsiClassAndExpression();
//
//                    result.psiFile = file;
//                    result.expression = expression;
//
//                    filesWithMatchingStep.add(result);
//                }

            }
        });

        if (searchJavaSource){
            references.addAll( getUsagesInProjectSource(project, syntaxBuilder, text));
        }


        return references;

    }


//    private List<Usage> getMatchingProjectFiles(final String text, Project project){
//
//        AnalysisScope moduleScope = new AnalysisScope(project);
//
//        final List<Usage> references = new ArrayList<>();
//
//        moduleScope.accept(new PsiRecursiveElementVisitor() {
//            @Override
//            public void visitFile(final PsiFile file) {
//
//                log.debug("got src file: " + file.getName() + " filetype: " + file.getFileType());
//
//                String expression = null;
//                if (file instanceof PsiJavaFile) {
//
//               //     references.addAll(getReferencesFromPsiJavaFile((PsiJavaFile) file, text));
//
//                } else if (file instanceof SubstepsDefinitionFile) {
//
//                    //references.addAll(getReferencesFromSubstepDefs(file, text, project));
//                }
//                else if (file instanceof FeatureFile){
//     //               references.addAll(getReferencesFromFeatureFile(file, text));
//                }
//
////                if (expression != null) {
////                    GotoStepDefinitionAction.PsiClassAndExpression result = new GotoStepDefinitionAction.PsiClassAndExpression();
////
////                    result.psiFile = file;
////                    result.expression = expression;
////
////                    filesWithMatchingStep.add(result);
////                }
//
//            }
//        });
//
//        return references;
//
//    }

    private List<Usage> getReferencesFromFeatureFile(FeatureFile file, Pattern pattern) {

        List<Usage> usages = new ArrayList<>();

        Feature f = file.getFeature();

        StepsHolder[] scenarios = f.getScenarios();

        for (StepsHolder stepHolder : scenarios){

            for (uk.co.itmoore.intellisubsteps.psi.feature.Step step : stepHolder.getSteps()){
                String stepText = step.getText();

                if (pattern.matcher(stepText).matches()){
                    VirtualFile virtualFile = file.getContainingFile().getVirtualFile();

                    OpenFileDescriptor ofd = new OpenFileDescriptor(file.getProject(), virtualFile, step.getTextOffset());

                    usages.add(new MyUsage("used in feature", stepText, file.getProject(), virtualFile, ofd, file));
                }
            }
        }
        return usages;
    }

    private List<Usage> getReferencesFromSubstepDefs(PsiFile file, Pattern pattern, Project project) {

        List<Usage> usages = new ArrayList<>();

        SubStepDefinitionParser subStepParser = new SubStepDefinitionParser(true, new DefaultSyntaxErrorReporter());

        String[] lines = file.getText().split("\n");

        FileContents contents = new FileContents(Arrays.asList(lines), new File(file.getVirtualFile().getCanonicalPath()));

        PatternMap<ParentStep> parentMap = subStepParser.parseSubstepFileContents(contents);

        Collection<ParentStep> substepDefs = parentMap.values();

        List<ParentStep> definitions = new ArrayList<>();

        for (ParentStep substepDef : substepDefs){
            // test each pf the substep defs children, do any of the steps match up ?

            if (substepDef.getParent().getPattern().equals(pattern.pattern())){
                definitions.add(substepDef);
            }

            for (Step s : substepDef.getSteps()){


                if (pattern.matcher(s.getLine()).matches()){
                    VirtualFile virtualFile = file.getContainingFile().getVirtualFile();
                    OpenFileDescriptor ofd = new OpenFileDescriptor(project, virtualFile, s.getSourceLineNumber() -1, 0);

                    usages.add(new MyUsage("used in substep definition", s.getLine(), project, virtualFile, ofd, file));

                }

                // create a usage for each match step - needs to be hierarchichal
                // File
                    // Define: blah
                        // line
                    /// Define: blah
                        // line
                // File
                    // ...

            }

        }
        for (ParentStep substepDef : definitions) {
            VirtualFile virtualFile = file.getContainingFile().getVirtualFile();
            OpenFileDescriptor ofd = new OpenFileDescriptor(project, virtualFile, substepDef.getParent().getSourceLineNumber() -1, 0);

            usages.add(new MyUsage("defined", substepDef.getParent().getLine(), project, virtualFile, ofd, file));

        }


        return usages;
    }

//    private List<Usage> getReferencesFromPsiJavaFile(PsiJavaFile psiJavaFile, String text) {
//
//        log.debug("looking at java source file: " + psiJavaFile.getName());
//
//        final PsiClass[] psiClasses = psiJavaFile.getClasses();
//
//        for (PsiClass psiClass : psiClasses) {
//
//            if (SubstepsCompletionContributor.isStepImplementationsClass(psiClass)) {
//
//                PsiMethod[] methods = psiClass.getAllMethods();
//
//                for (PsiMethod method : methods) {
//                    String expression = getMatchingStepExpression(method, text);
//                    if (expression != null){
//                        return expression;
//                    }
//                }
//            }
//            else {
//
//                log.debug("not step impls class");
//            }
//        }
//
//        return null;
//    }

//    private enum MatchResult {
//        NO_MATCH,
//        EXACT_MATCH,
//        FUZZY_MATCH
//    }


//    private String getMatchingStepExpression(PsiMethod method, final String stepText) {
//
//        log.debug("checking method: " + method.getName());
//
//        PsiAnnotation[] methodAnnotations = method.getModifierList().getAnnotations();
//        for (PsiAnnotation a : methodAnnotations) {
//            if (a.getQualifiedName().equals(com.technophobia.substeps.model.SubSteps.Step.class.getCanonicalName())){
//
//                PsiAnnotationParameterList parameterList = a.getParameterList();
//
//                PsiNameValuePair[] attributes = parameterList.getAttributes();
//                if (attributes != null) {
//                    String src = attributes[0].getValue().getText();
//
//                    String stepExpression = src.substring(1, src.length() - 1);
//                    log.debug("found step expression: " + stepExpression);
//
//                    if (stepTextMatchesExpression(stepText, stepExpression) == MatchResult.EXACT_MATCH){
//
//                        log.debug("found a match with stepEpression: " + stepExpression + " and text: " + stepText + " method: " + method.getName());
//                        return stepExpression;
//                    }
//                    else {
//
//                        // TODO - check the capture patterns in the regex, if the pattern is capturing (//d+) then convert the stepText accordingly
//
//                        log.debug("no match");
//                    }
//                }
//            }
//        }
//        return null;
//    }


//    private MatchResult stepTextMatchesExpression(String text, String expression){
//
//        String parameterRegEx = ".*(<[^>]*>).*";
//        Pattern paramPattern = Pattern.compile(parameterRegEx);
//
//        if(paramPattern.matcher(text).matches()){
//            String text2 = text.replaceAll("<[^>]*>", "");
//
//            String expression2 = expression.replaceAll("\\([^\\)]*\\)", "");
//
//            if(text2.equals(expression2)){
//                return MatchResult.EXACT_MATCH;
//            }
//            else {
//                int idx = StringUtils.indexOfAny(expression2, new String[]{"Given", "When", "Then", "And"});
//                if (idx ==0){
//                    String expression3 = replaceKeywords(expression2);
//                    String text3 = replaceKeywords(text2);
//                    if (text3.equals(expression3)){
//                        return MatchResult.FUZZY_MATCH;
//                    }
//                }
//                return MatchResult.NO_MATCH;
//            }
//
//        }
//        else {
//            Pattern p = Pattern.compile(expression);
//            Matcher matcher = p.matcher(text);
//
//            if (matcher.matches()) {
//                return MatchResult.EXACT_MATCH;
//            }
//            else {
//                String expression2 = replaceKeywords(expression);
//                String text2 = replaceKeywords(text);
//
//                p = Pattern.compile(expression2);
//                matcher = p.matcher(text2);
//
//                if (matcher.matches()) {
//                    return MatchResult.FUZZY_MATCH;
//                }
//            }
//            return MatchResult.NO_MATCH;
//        }
//    }

//    private String replaceKeywords(String src){
//        return StringUtils.removeStart(StringUtils.removeStart(StringUtils.removeStart(StringUtils.removeStart(src, "Given"), "When"), "Then"), "And").trim();
//    }




    public static class MyUsage implements Usage, FileEditorLocation {

        private String text ;
        private FileEditor editor;
        private OpenFileDescriptor openFileDescriptor;
        private  Project project;

        public MyUsage(String prefix, String text, Project project, VirtualFile vFile, OpenFileDescriptor openFileDescriptor, PsiFile file){

            // TODO not sure how to apply the grouping..

            if (openFileDescriptor.getLine() == -1) {
                this.text = prefix + " : " + text + " : " + openFileDescriptor.getFile().getName();
            }
            else {
                this.text = prefix + " : " + text + " : " + openFileDescriptor.getFile().getName() + " : " + (openFileDescriptor.getLine() + 1);
            }
            this.project = project;

            FileEditor[] editors = FileEditorManager.getInstance(project).getEditors(vFile);
            editor = editors[0];

            this.openFileDescriptor = openFileDescriptor;// = new OpenFileDescriptor(project, vFile, 5, 0);
        }

        @NotNull
        @Override
        public UsagePresentation getPresentation() {
            return new UsagePresentation() {
                @Override
                @NotNull
                public TextChunk[] getText() {
                    return new TextChunk[] {
                            new TextChunk(SimpleTextAttributes.REGULAR_ATTRIBUTES.toTextAttributes(), text)
                    };
                }
                @Override
                @Nullable
                public Icon getIcon() {
                    return null;
                }

                @Override
                public String getTooltipText() {
                    return "tooltip";
                }

                @Override
                @NotNull
                public String getPlainText() {
                    return "plain text";
                }
            };


        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public boolean isReadOnly() {
            return false;
        }

        @Nullable
        @Override
        public FileEditorLocation getLocation() {

            return this;
        }

        @Override
        public void selectInEditor() {
            openFileDescriptor.navigateInEditor(project, true);
        }

        @Override
        public void highlightInEditor() {

        }

        @Override
        public void navigate(boolean requestFocus) {
            openFileDescriptor.navigate(requestFocus);
        }

        @Override
        public boolean canNavigate() {
            return true;
        }

        @Override
        public boolean canNavigateToSource() {
            return true;
        }

        @NotNull
        @Override
        public FileEditor getEditor() {
            return editor;
        }

        @Override
        public int compareTo(@NotNull FileEditorLocation o) {
            return 0;
        }
    }

}

