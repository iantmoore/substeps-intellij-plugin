package uk.co.itmoore.intellisubsteps.psi;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author yole
 */
public interface SubstepsScenarioOutline extends SubstepsStepsHolder {
  @NotNull
  List<SubstepsExamplesBlock> getExamplesBlocks();
}
