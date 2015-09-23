package uk.co.itmoore.intellisubsteps.ui;

import com.intellij.execution.Location;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;

public class RootTestInfo extends TestInfo {
  private String myName = SpecialNode.TESTS_IN_PROGRESS;

  public String getComment() {
    return "";
  }

  public String getName() { return myName; }

  public void setName(final String name) { myName = name; }

  public boolean shouldRun() {
    return false;
  }

  public int getTestsCount() {
    return 0;
  }

//  @Override
//  public void readFrom(ObjectReader reader) {
//  }

  public Location getLocation(final Project project, GlobalSearchScope searchScope) {
    return null;
  }

}
