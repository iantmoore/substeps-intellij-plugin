package uk.co.itmoore.intellisubsteps.test

import java.util

import com.technophobia.substeps.glossary.{StepDescriptor, StepImplementationsDescriptor}
import org.scalatest.{FlatSpec, Matchers}
import uk.co.itmoore.intellisubsteps.StepValidatorAnnotator

/**
  * Created by ian on 23/01/17.
  */
class StepValidatorAnnotationTest extends FlatSpec with Matchers{

  val stepValidator = new StepValidatorAnnotator

  "StepValidatorAnnotator" should "return not implemented" in {

    val substepDefinitions = new util.ArrayList[String]

    substepDefinitions.add("Substep def one")

    val stepImplsInScope = new util.ArrayList[StepImplementationsDescriptor]
    val stepImplDescriptor = new StepImplementationsDescriptor("MyClass")

    val sd = new StepDescriptor
    sd.setExpression("""WithParams "([^"]*)" "([^"]*)"""")
    stepImplDescriptor.addStepTags(sd)

    stepImplsInScope.add(stepImplDescriptor)
    val notImplemented: StepValidatorAnnotator.ErrorAnnotation = stepValidator.validate("text to be validated", substepDefinitions, stepImplsInScope)

    Option(notImplemented) shouldBe defined

    notImplemented.msg should be ("Unimplemented substep definition")

  }

  it should "return null when a single instance found" in {

    val substepDefinitions = new util.ArrayList[String]

    substepDefinitions.add("Substep def one")

    val stepImplsInScope = new util.ArrayList[StepImplementationsDescriptor]
    val stepImplDescriptor = new StepImplementationsDescriptor("MyClass")

    val sd = new StepDescriptor
    sd.setExpression("""WithParams "([^"]*)" "([^"]*)"""")
    stepImplDescriptor.addStepTags(sd)

    stepImplsInScope.add(stepImplDescriptor)
    val noError: StepValidatorAnnotator.ErrorAnnotation = stepValidator.validate("Substep def one", substepDefinitions, stepImplsInScope)

    Option(noError) shouldBe None

    val noError2: StepValidatorAnnotator.ErrorAnnotation = stepValidator.validate("""WithParams "wahoo" "something else"""", substepDefinitions, stepImplsInScope)

    Option(noError2) shouldBe None
  }


  it should "return null when a single instance fuzzily found" in {

    val substepDefinitions = new util.ArrayList[String]

    substepDefinitions.add("Given a substep def one")

    val stepImplsInScope = new util.ArrayList[StepImplementationsDescriptor]
    val stepImplDescriptor = new StepImplementationsDescriptor("MyClass")

    val sd = new StepDescriptor
    sd.setExpression("""Then WithParams "([^"]*)" "([^"]*)"""")
    stepImplDescriptor.addStepTags(sd)

    stepImplsInScope.add(stepImplDescriptor)
    val noError: StepValidatorAnnotator.ErrorAnnotation = stepValidator.validate("Then a substep def one", substepDefinitions, stepImplsInScope)

    Option(noError) shouldBe None

    val noError2: StepValidatorAnnotator.ErrorAnnotation = stepValidator.validate("""Given WithParams "wahoo" "something else"""", substepDefinitions, stepImplsInScope)

    Option(noError2) shouldBe None
  }

  it should "return duplicates when defined in substeps files and step impls" in {

    val substepDefinitions = new util.ArrayList[String]

    substepDefinitions.add("Substep def one")
    substepDefinitions.add("Substep def one")
    substepDefinitions.add("""WithParams "<paramone>" "<paramtwo>"""")

    val stepImplsInScope = new util.ArrayList[StepImplementationsDescriptor]
    val stepImplDescriptor = new StepImplementationsDescriptor("MyClass")

    val sd = new StepDescriptor
    sd.setExpression("""WithParams "([^"]*)" "([^"]*)"""")
    stepImplDescriptor.addStepTags(sd)

    stepImplsInScope.add(stepImplDescriptor)
    val notImplemented: StepValidatorAnnotator.ErrorAnnotation = stepValidator.validate("Substep def one", substepDefinitions, stepImplsInScope)

    Option(notImplemented) shouldBe defined

    notImplemented.msg should be ("Duplicated substep definition")


    val duplicated2: StepValidatorAnnotator.ErrorAnnotation = stepValidator.validate("""WithParams "wahoo" "something else"""", substepDefinitions, stepImplsInScope)

    Option(duplicated2) shouldBe defined

    duplicated2.msg should be ("Duplicated substep definition")

  }

}
