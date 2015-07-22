package uk.co.itmoore.intellisubsteps.psi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.intellij.analysis.AnalysisScope;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import com.intellij.util.Processor;
import com.technophobia.substeps.glossary.StepDescriptor;
import com.technophobia.substeps.glossary.StepImplementationsDescriptor;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.psi.SubstepsDefinitionFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * Created by ian on 22/07/15.
 */
public abstract class SubstepsCompletionContributor extends CompletionContributor {

    public static final String STEPIMPLEMENTATIONS_JSON_FILENAME = "stepimplementations.json";

    private static final Logger logger = LogManager.getLogger(SubstepsCompletionContributor.class);

    protected void buildSuggestionsFromStepImplementationsInProjectSource(Module module, final List<StepImplementationsDescriptor> stepImplsInScope, final CompletionResultSet resultSet) {
        long start = System.currentTimeMillis();
        AnalysisScope moduleScope = new AnalysisScope(module);
        moduleScope.accept(new PsiRecursiveElementVisitor() {
            @Override
            public void visitFile(final PsiFile file) {

                logger.debug("got src file: " + file.getName() + " filetype: " + file.getFileType());

                if (file instanceof PsiJavaFile) {
                    buildSuggestionsFromJavaSource((PsiJavaFile) file, stepImplsInScope);
                } else if (file instanceof SubstepsDefinitionFile) {

                    buildSuggestionsFromSubstepsSource((SubstepsDefinitionFile) file, resultSet);

                }

                // TODO from scala ??

            }
        });
        long duration = System.currentTimeMillis() - start;
        logger.debug("step implementation descriptors built from code in " + duration + " msecs");
    }

    protected void buildSuggestionsFromSubstepsSource(SubstepsDefinitionFile substepsDefFile, CompletionResultSet resultSet) {

        logger.debug("buildSuggestionsFromSubstepsSource for file: " + substepsDefFile.getName());


        // TODO - think this is probably the way it *should* be done - but not implemented the psi classes correctly so that it actually works :-(
//        SubstepDefinition[] substepDefs = substepsDefFile.getSubstepDefinitions();
//        for (SubstepDefinition sd : substepDefs){
//            ....
//        }

        String[] lines = substepsDefFile.getText().split("\n");

        for (String line: lines){
            if (line.trim().startsWith("Define:")){

                String def = StringUtils.stripStart(line.trim(), "Define:").trim();

                LookupElementBuilder builder = LookupElementBuilder.create(def);
                resultSet.addElement(builder);

            }
        }
    }

    protected void buildSuggestionsFromJavaSource(PsiJavaFile psiJavaFile, List<StepImplementationsDescriptor> stepImplsInScope) {

        logger.trace("looking at java source file: " + psiJavaFile.getName());
        // logger.debug("psiJavaFile: "  psiJavaFile.getName() + "\n" + psiJavaFile.getText());

        final PsiClass[] psiClasses = psiJavaFile.getClasses();

        //final PsiClass psiClass = JavaPsiFacade.getInstance(thisProject).findClass(fqn, psiJavaFile.getResolveScope());

        for (PsiClass psiClass : psiClasses) {

            if (isStepImplementationsClass(psiClass)) {

                StepImplementationsDescriptor stepClassDescriptor = new StepImplementationsDescriptor(psiClass.getQualifiedName());

                stepImplsInScope.add(stepClassDescriptor);

                PsiMethod[] methods = psiClass.getAllMethods();

                for (PsiMethod method : methods) {
                    addStepDescriptorIfApplicable(method, stepClassDescriptor);
                }
            }
        }
    }

    protected void addStepDescriptorIfApplicable(PsiMethod method, StepImplementationsDescriptor stepClassDescriptor) {

        PsiAnnotation[] methodAnnotations = method.getModifierList().getAnnotations();
        for (PsiAnnotation a : methodAnnotations) {
            if (a.getQualifiedName().equals(com.technophobia.substeps.model.SubSteps.Step.class.getCanonicalName())){

                PsiAnnotationParameterList parameterList = a.getParameterList();

                PsiNameValuePair[] attributes = parameterList.getAttributes();
                if (attributes != null) {
                    String src = attributes[0].getValue().getText();

                    String  stepExpression = src.substring(1, src.length() -2);

                    StepDescriptor sd = new StepDescriptor();

                    PsiParameter[] parameters = method.getParameterList().getParameters();
                    if (parameters != null){
                        for (PsiParameter p : parameters){
                            stepExpression = stepExpression.replaceFirst("\\([^\\)]*\\)", "<" + p.getName() + ">");
                        }
                    }
                    stepExpression = stepExpression.replaceAll("\\?", "");
                    stepExpression = stepExpression.replaceAll("\\\\", "");

                    sd.setExpression(stepExpression);
                    stepClassDescriptor.addStepTags(sd);
                    break;
                }
            }
        }
    }


    protected boolean isStepImplementationsClass(PsiClass psiClass) {

        PsiAnnotation[] classAnnotations = psiClass.getModifierList().getAnnotations();
        for (PsiAnnotation a : classAnnotations){

            if (a.getQualifiedName().equals(com.technophobia.substeps.model.SubSteps.StepImplementations.class.getCanonicalName())){
                return true;
            }
        }
        return false;
    }


    protected void addStepImplementationsFromModuleLibraries(ModuleRootManager moduleRootManager, List<StepImplementationsDescriptor> stepImplsInScope) {
        final List<Library> libraries = new ArrayList<>();

        moduleRootManager.orderEntries().forEachLibrary(new Processor<Library>() {
            @Override
            public boolean process(Library library) {

                libraries.add(library);
                return true;
            }
        });


        for (Library lib : libraries){
            logger.debug("looking for stepImplementations in " + lib.getName());

            VirtualFile[] vLibFiles = lib.getFiles(OrderRootType.CLASSES);

//            for (VirtualFile vf  : vLibFiles){
//                logger.debug("virtual file canonical path: " + vf.getCanonicalPath() + " path: " + vf.getPath());
//            }
            String libraryPath = StringUtils.substringBefore(vLibFiles[0].getPath(), "!/");
            stepImplsInScope.addAll(findStepImplementationDescriptorsForDependency(libraryPath));
        }
    }

    protected void buildSuggestionsFromStepDescriptions(List<StepImplementationsDescriptor> stepImplsInScope, @NotNull CompletionResultSet resultSet) {
        for (StepImplementationsDescriptor descriptor : stepImplsInScope){

            for (StepDescriptor stepDescriptor: descriptor.getExpressions()){

                LookupElementBuilder builder = LookupElementBuilder.create(stepDescriptor.getExpression());

                if (stepDescriptor.getExample() != null && stepDescriptor.getExample().isEmpty()){

                    builder.withPresentableText(stepDescriptor.getExample());
                }
                resultSet.addElement(builder);
            }

        }
    }


    private List<StepImplementationsDescriptor> findStepImplementationDescriptorsForDependency(final String path) {
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(new File(path));


            final List<StepImplementationsDescriptor> stepImplementationDescriptors = loadJsonStepImplementationsDescriptorFromJar(jarFile);

            return stepImplementationDescriptors != null ? stepImplementationDescriptors : Collections.<StepImplementationsDescriptor>emptyList();
        }
        catch (final IOException ex) {

            logger.warn("Could not open jar file " + path, ex);
        }
        finally {
            try {
                if (jarFile != null) {
                    jarFile.close();
                }
            } catch (final IOException e) {
                logger.warn("Could not close jar file " + path);
            }
        }

        return Collections.<StepImplementationsDescriptor> emptyList();
    }

    protected List<StepImplementationsDescriptor> loadJsonStepImplementationsDescriptorFromJar(JarFile jarFile){
        Gson gson = new GsonBuilder().create();

        List<StepImplementationsDescriptor> classStepTagList = null;

        final ZipEntry entry = jarFile.getEntry(STEPIMPLEMENTATIONS_JSON_FILENAME);

        if (entry != null) {

            try {
                final InputStream is = jarFile.getInputStream(entry);

                Type datasetListType = new TypeToken<Collection<StepImplementationsDescriptor>>() {}.getType();

                classStepTagList = gson.fromJson(new InputStreamReader(is), datasetListType);


            } catch (final IOException e) {
                logger.error("Error loading from jarfile: ", e);
            }
        } else {
            logger.error("couldn't locate file in jar: " + STEPIMPLEMENTATIONS_JSON_FILENAME);
        }

        return classStepTagList;

    }


    public void fillCompletionVariants(CompletionParameters params, CompletionResultSet result){
        super.fillCompletionVariants(params, result);
    }



    protected void buildSuggestionsList(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
        final Project thisProject = parameters.getEditor().getProject();
        VirtualFile vFile = parameters.getOriginalFile().getVirtualFile();

        Module module = ModuleUtil.findModuleForFile(vFile, thisProject);
        String moduleName = module == null ? "Module not found" : module.getName();

        logger.debug("building completion contributions for module name: " + moduleName);

        final List<StepImplementationsDescriptor> stepImplsInScope = new ArrayList<>();

        ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);

        VirtualFile[] srcRoots = moduleRootManager.getSourceRoots(true);

        for (VirtualFile vf : srcRoots) {
            logger.debug("src root: " + vf.getCanonicalPath());

        }
        buildSuggestionsFromStepImplementationsInProjectSource(module, stepImplsInScope, resultSet);

        addStepImplementationsFromModuleLibraries(moduleRootManager, stepImplsInScope);

        logger.debug("completion processing ctx to string:\n" + context.toString());


        buildSuggestionsFromStepDescriptions(stepImplsInScope, resultSet);
    }

}
