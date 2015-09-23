
package uk.co.itmoore.intellisubsteps.ui.events;


import uk.co.itmoore.intellisubsteps.ui.SubstepsTestProxy;

public class NewChildEvent extends TestEvent {
  private final SubstepsTestProxy myChild;

  public SubstepsTestProxy getChild() {
    return myChild;
  }

  public NewChildEvent(final SubstepsTestProxy parent, final SubstepsTestProxy child) {
    super(parent);
    myChild = child;
  }

  public boolean equals(final Object obj) {
    return super.equals(obj) && ((NewChildEvent) obj).myChild == myChild;
  }

  public int hashCode() {
    return super.hashCode() ^ myChild.hashCode();
  }

  public SubstepsTestProxy getTestSubtree() {
    return getSource();
  }
}
