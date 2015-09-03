package uk.co.itmoore.intellisubsteps;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.javadoc.PsiDocTagValue;
import com.intellij.psi.javadoc.PsiDocToken;
import com.intellij.util.Processor;
import com.technophobia.substeps.glossary.StepDescriptor;
import com.technophobia.substeps.glossary.StepImplementationsDescriptor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.*;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import com.intellij.openapi.project.Project;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import uk.co.itmoore.intellisubsteps.psi.SubstepsCompletionContributor;

/**
 * A class to load and cache step impl descriptors from jars.  Will need to invalidate the cache when libraries change
 * Used by completions and docs.. maybe more..
 *
 * Created by ian on 07/08/15.
 */
public class SubstepLibraryManager {

    private static final Logger logger = LogManager.getLogger(SubstepLibraryManager.class);

    public static final String STEPIMPLEMENTATIONS_JSON_FILENAME = "stepimplementations.json";


    private Map<String, List<StepImplementationsDescriptor>> descriptorCache = new HashMap<>();

    public static final SubstepLibraryManager INSTANCE = new SubstepLibraryManager();

    private SubstepLibraryManager(){}

    public List<StepImplementationsDescriptor> getDescriptorsForProjectFromLibraries(Module module){

        List<StepImplementationsDescriptor> stepImplementationsDescriptors = descriptorCache.get(module.getModuleFilePath());

        if (stepImplementationsDescriptors != null){
            return stepImplementationsDescriptors;
        }
        else {
            // go off and build
            stepImplementationsDescriptors = getStepImplementationsFromModuleLibraries(ModuleRootManager.getInstance(module));
            descriptorCache.put(module.getModuleFilePath(), stepImplementationsDescriptors);
        }
        return stepImplementationsDescriptors;
    }

    public Set<String> getStepImplClassNamesFromProjectLibraries(Module module){

        List<StepImplementationsDescriptor> descriptors = getDescriptorsForProjectFromLibraries(module);


        Set<String> classNames = new HashSet<>();
        for (StepImplementationsDescriptor des : descriptors){
            classNames.add(des.getClassName());
        }
        return classNames;
    }


    protected List<StepImplementationsDescriptor> getStepImplementationsFromModuleLibraries(ModuleRootManager moduleRootManager) {

        List<StepImplementationsDescriptor> stepImplsInScope = new ArrayList<>();

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

        return stepImplsInScope;
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


    public List<StepImplementationsDescriptor> buildStepImplentationDescriptorsFromJavaSource(PsiJavaFile psiJavaFile) {


        List<StepImplementationsDescriptor> stepImpls = new ArrayList<>();

        // logger.debug("psiJavaFile: "  psiJavaFile.getName() + "\n" + psiJavaFile.getText());

        final PsiClass[] psiClasses = psiJavaFile.getClasses();

        //final PsiClass psiClass = JavaPsiFacade.getInstance(thisProject).findClass(fqn, psiJavaFile.getResolveScope());

        for (PsiClass psiClass : psiClasses) {

            if (SubstepsCompletionContributor.isStepImplementationsClass(psiClass)) {

                StepImplementationsDescriptor stepClassDescriptor = new StepImplementationsDescriptor(psiClass.getQualifiedName());

                stepImpls.add(stepClassDescriptor);

                PsiMethod[] methods = psiClass.getAllMethods();

                for (PsiMethod method : methods) {
                    addStepDescriptorIfApplicable(method, stepClassDescriptor);
                }
            }
        }

        return stepImpls;
    }

    protected void addStepDescriptorIfApplicable(PsiMethod method, StepImplementationsDescriptor stepClassDescriptor) {


        PsiAnnotation[] methodAnnotations = method.getModifierList().getAnnotations();
        for (PsiAnnotation a : methodAnnotations) {
            if (a.getQualifiedName().equals(com.technophobia.substeps.model.SubSteps.Step.class.getCanonicalName())){

                PsiAnnotationParameterList parameterList = a.getParameterList();

                PsiNameValuePair[] attributes = parameterList.getAttributes();
                if (attributes != null) {
                    String src = attributes[0].getValue().getText();

                    String  stepExpression = src.substring(1, src.length() -1);

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

                    PsiDocComment docComment = method.getDocComment();

                    String description = "";
                    String example = "";
                    String section = "";

                    if (docComment != null){
                        PsiDocTag[] tags = docComment.getTags();

                        PsiElement[] descriptionElements = docComment.getDescriptionElements();

                        StringBuilder descriptionBuf = new StringBuilder();

                        for (PsiElement e : descriptionElements){

                            logger.debug("descriptionElement: class " + e.getClass() + " text: " + e.getText());

                            if (e instanceof PsiDocToken){
                                PsiDocToken docs = (PsiDocToken)e;

                                String txt = docs.getText().trim();
                                if (!txt.isEmpty()) {
                                    descriptionBuf.append("<p>").append(txt).append("<p>");
                                }
                            }
                        }

                        PsiDocTag[] paramTags = docComment.findTagsByName("param");
                        if (paramTags != null){

                            descriptionBuf.append("<h3>Parameters:</h3>");
                            for (PsiDocTag paramTag: paramTags){

                                String txt = getStringFromPsiDocTag(paramTag, true).trim();
                                if (!txt.isEmpty()) {
                                    descriptionBuf.append("<p>").append(txt).append("<p>");
                                }
                            }

                        }

                        description = descriptionBuf.toString();



                        PsiDocTag exampleTag = docComment.findTagByName("example");
                        example = getStringFromPsiDocTag(exampleTag, false);

                        PsiDocTag sectionTag = docComment.findTagByName("section");
                        if (sectionTag!= null){
                            section = sectionTag.getValueElement().getText();
                        }


                        // TODO what to do with the parameter tags

                        if (tags != null){

                            for (PsiDocTag tag : tags){
                                String tagName = tag.getName();
//                                String elemText = tag.getContainingComment().getText();
                                String tagVal = tag.getValueElement().getText();

//                                for (PsiElement e : tag.getDataElements()){
//
//                                    logger.debug("tag.getDataElements(): class " + e.getClass() + " text: " + e.getText());

//                                    if (e instanceof PsiDocToken){
//                                        PsiDocToken docs = (PsiDocToken)e;
//                                        description = description + "<p>" +docs.getText()+ "</p>";
//                                    }
//                            description = description + "<p>" +  e.getText() + "</p>";
//                                }



                                logger.debug("psidoctag tagName: " + tagName + " tagVal: " + tagVal );


                            }

                        }
                    }
                    sd.setDescription(description);
                    sd.setExample(example);
                    sd.setSection(section);


                    stepClassDescriptor.addStepTags(sd);
                    break;
                }
            }
        }
    }

    private String getStringFromPsiDocTag(PsiDocTag exampleTag, boolean highlightFirstWord) {
        String docTagString = "";
        if (exampleTag!= null){

            for (PsiElement e : exampleTag.getDataElements()){

                if (e instanceof PsiDocTagValue){
                    if (highlightFirstWord) {
                        docTagString = "<strong>" + e.getText().trim() + "</strong>&nbsp;";
                    }
                    else {
                        docTagString = e.getText();
                    }
                }
                else if ( e instanceof PsiDocToken){
                    docTagString = docTagString + e.getText().trim();
                }
            }
        }
        return docTagString;
    }

}
