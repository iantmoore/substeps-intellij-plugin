package uk.co.itmoore.intellisubsteps.psi.feature;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.intellij.analysis.AnalysisScope;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import com.intellij.util.Processor;
import com.technophobia.substeps.glossary.StepDescriptor;
import com.technophobia.substeps.glossary.StepImplementationsDescriptor;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.itmoore.intellisubsteps.psi.SubstepsCompletionContributor;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepDefinitionElementTypes;
import uk.co.itmoore.intellisubsteps.psi.stepdefinition.SubstepsStepDefinitionLanguage;
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
 * Created by ian on 04/07/15.
 */
public class FeatureCompletionContributor extends SubstepsCompletionContributor {



    private static final Logger logger = LogManager.getLogger(FeatureCompletionContributor.class);

    public FeatureCompletionContributor() {


        logger.debug("FeatureCompletionContributor ctor");



        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(FeatureElementTypes.STEP_ELEMENT_TYPE).withLanguage(FeatureLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {

                        buildSuggestionsList(parameters, context, resultSet);
                    }
                }
        );

//        extend(CompletionType.BASIC,
//                PlatformPatterns.psiElement(FeatureElementTypes.SCENARIO_OUTLINE_BLOCK_ELEMENT_TYPE).withLanguage(FeatureLanguage.INSTANCE),
//                new CompletionProvider<CompletionParameters>() {
//                    public void addCompletions(@NotNull CompletionParameters parameters,
//                                               ProcessingContext context,
//                                               @NotNull CompletionResultSet resultSet) {
//
//                        buildSuggestionsList(parameters, context, resultSet);
//                    }
//                }
//        );

    }

}