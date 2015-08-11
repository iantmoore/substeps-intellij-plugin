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
import com.intellij.util.Processor;
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

}
