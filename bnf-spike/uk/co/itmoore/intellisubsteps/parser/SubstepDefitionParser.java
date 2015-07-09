// This is a generated file. Not intended for manual editing.
package uk.co.itmoore.intellisubsteps.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static uk.co.itmoore.intellisubsteps.psi.SubstepDefTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class SubstepDefitionParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    if (t == A_SUBSTEP_DEFINITION) {
      r = a_substep_definition(b, 0);
    }
    else if (t == COMMENT) {
      r = comment(b, 0);
    }
    else if (t == COMMENT_LINE) {
      r = comment_line(b, 0);
    }
    else if (t == EOL) {
      r = eol(b, 0);
    }
    else if (t == LINE_TO_EOL) {
      r = line_to_eol(b, 0);
    }
    else if (t == PARAM) {
      r = param(b, 0);
    }
    else if (t == SPACE) {
      r = space(b, 0);
    }
    else if (t == STEPLINE) {
      r = stepline(b, 0);
    }
    else if (t == SUBSTEP_DEFINITION_KEYWORD) {
      r = substep_definition_keyword(b, 0);
    }
    else if (t == SUBSTEP_DEFINITION_LINE) {
      r = substep_definition_line(b, 0);
    }
    else if (t == WHITE) {
      r = white(b, 0);
    }
    else {
      r = parse_root_(t, b, 0);
    }
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return a_substep_definition_file(b, l + 1);
  }

  /* ********************************************************** */
  // substep_definition_line (stepline)*
  public static boolean a_substep_definition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "a_substep_definition")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<a substep definition>");
    r = substep_definition_line(b, l + 1);
    r = r && a_substep_definition_1(b, l + 1);
    exit_section_(b, l, m, A_SUBSTEP_DEFINITION, r, false, null);
    return r;
  }

  // (stepline)*
  private static boolean a_substep_definition_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "a_substep_definition_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!a_substep_definition_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "a_substep_definition_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // (stepline)
  private static boolean a_substep_definition_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "a_substep_definition_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = stepline(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (a_substep_definition)*
  static boolean a_substep_definition_file(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "a_substep_definition_file")) return false;
    int c = current_position_(b);
    while (true) {
      if (!a_substep_definition_file_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "a_substep_definition_file", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // (a_substep_definition)
  private static boolean a_substep_definition_file_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "a_substep_definition_file_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = a_substep_definition(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (comment_line white)*
  public static boolean comment(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "comment")) return false;
    Marker m = enter_section_(b, l, _NONE_, "<comment>");
    int c = current_position_(b);
    while (true) {
      if (!comment_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "comment", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, l, m, COMMENT, true, false, null);
    return true;
  }

  // comment_line white
  private static boolean comment_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "comment_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = comment_line(b, l + 1);
    r = r && white(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // space* '#' space* line_to_eol
  public static boolean comment_line(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "comment_line")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<comment line>");
    r = comment_line_0(b, l + 1);
    r = r && consumeToken(b, "#");
    r = r && comment_line_2(b, l + 1);
    r = r && line_to_eol(b, l + 1);
    exit_section_(b, l, m, COMMENT_LINE, r, false, null);
    return r;
  }

  // space*
  private static boolean comment_line_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "comment_line_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!space(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "comment_line_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // space*
  private static boolean comment_line_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "comment_line_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!space(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "comment_line_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // '\r'? '\n'
  public static boolean eol(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "eol")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<eol>");
    r = eol_0(b, l + 1);
    r = r && consumeToken(b, "\\n");
    exit_section_(b, l, m, EOL, r, false, null);
    return r;
  }

  // '\r'?
  private static boolean eol_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "eol_0")) return false;
    consumeToken(b, "\\r");
    return true;
  }

  /* ********************************************************** */
  // (!eol)*
  public static boolean line_to_eol(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "line_to_eol")) return false;
    Marker m = enter_section_(b, l, _NONE_, "<line to eol>");
    int c = current_position_(b);
    while (true) {
      if (!line_to_eol_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "line_to_eol", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, l, m, LINE_TO_EOL, true, false, null);
    return true;
  }

  // !eol
  private static boolean line_to_eol_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "line_to_eol_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_, null);
    r = !eol(b, l + 1);
    exit_section_(b, l, m, null, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '<([^>].*)>'
  public static boolean param(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "param")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<param>");
    r = consumeToken(b, "<([^>].*)>");
    exit_section_(b, l, m, PARAM, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ' ' | '\t'
  public static boolean space(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "space")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<space>");
    r = consumeToken(b, " ");
    if (!r) r = consumeToken(b, "\\t");
    exit_section_(b, l, m, SPACE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '([^#]#*?.*)' eol
  public static boolean stepline(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stepline")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<stepline>");
    r = consumeToken(b, "([^#]#*?.*)");
    r = r && eol(b, l + 1);
    exit_section_(b, l, m, STEPLINE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'Define:'
  public static boolean substep_definition_keyword(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "substep_definition_keyword")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<substep definition keyword>");
    r = consumeToken(b, "Define:");
    exit_section_(b, l, m, SUBSTEP_DEFINITION_KEYWORD, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // substep_definition_keyword stepline
  public static boolean substep_definition_line(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "substep_definition_line")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<substep definition line>");
    r = substep_definition_keyword(b, l + 1);
    r = r && stepline(b, l + 1);
    exit_section_(b, l, m, SUBSTEP_DEFINITION_LINE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (space | eol)*
  public static boolean white(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "white")) return false;
    Marker m = enter_section_(b, l, _NONE_, "<white>");
    int c = current_position_(b);
    while (true) {
      if (!white_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "white", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, l, m, WHITE, true, false, null);
    return true;
  }

  // space | eol
  private static boolean white_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "white_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = space(b, l + 1);
    if (!r) r = eol(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

}
