package uk.co.itmoore.intellisubsteps.test;

import com.google.gson.Gson;
import com.intellij.openapi.util.IconLoader;
import com.technophobia.substeps.glossary.StepDescriptor;
import com.technophobia.substeps.glossary.StepImplementationsDescriptor;
import com.technophobia.substeps.glossary.XMLSubstepsGlossarySerializer;
import org.junit.Assert;
import org.junit.Test;

import javax.swing.*;
import java.io.File;
import java.util.List;
import java.util.jar.JarFile;

public class SomeTests {


    @Test
    public void testLoadOfSubstepsMetaInfo() throws Exception {

        XMLSubstepsGlossarySerializer serializer = new XMLSubstepsGlossarySerializer();

        String path = "/home/ian/.m2/repository/com/technophobia/substeps/webdriver-substeps/1.1.2/webdriver-substeps-1.1.2.jar";

        JarFile jarFile = new JarFile(new File(path));


        final List<StepImplementationsDescriptor> stepImplementationDescriptors = serializer
                .loadStepImplementationsDescriptorFromJar(jarFile);

        for (StepImplementationsDescriptor d : stepImplementationDescriptors){


            for (StepDescriptor expr : d.getExpressions()){
                System.out.println("expression: " + expr.getExpression() + " description: " + expr.getDescription());
            }
        }

        Gson gson = new Gson();

        System.out.println("gson\n" +
                gson.toJson(stepImplementationDescriptors));

    }

    @Test
    public void aTest() {

        Icon icon = IconLoader.getIcon("/icons/cucumber.png", SomeTests.class);
        Assert.assertNotNull(icon);

    }
}
