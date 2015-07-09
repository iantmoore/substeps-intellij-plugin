package uk.co.itmoore.intellisubsteps.psi;

/**
 * User: Andrey Vokin
 * Date: 11/28/11
 */
public interface SubstepsTag extends SubstepsPsiElement {
  SubstepsTag[] EMPTY_ARRAY = new SubstepsTag[0];

  public String getName();
}
